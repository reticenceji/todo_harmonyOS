package com.example.backup;

import ohos.ace.ability.AceInternalAbility;
import ohos.data.distributed.common.*;
import ohos.data.distributed.user.SingleKvStore;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.utils.zson.ZSONObject;

import java.lang.reflect.Array;
import java.util.*;

/**
 * AceInternalAbility Represents internal abilities that are integrated into the JS UI framework.
 */
public class TodoServiceAbility extends AceInternalAbility {
    // 常量
    private static final String BUNDLE_NAME = "com.example.backup";
    private static final String ABILITY_NAME = "com.example.backup.TodoServiceAbility";
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int SELECT_TODOS = 1001;
    public static final int DELETE_TODOS = 1002;
    public static final int UPDATE_TODOS = 1003;
    public static final int INSERT_TODOS = 1004;
    public static final int SELECT_TODO = 1005;
    // 定义日志标签
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "TodoDatabase");
    // TodoServiceAbility 类的一个实例，所以从语义上他只能被初始化一次
    private static TodoServiceAbility instance;
    // Context
    private MainAbility abilityContext;

    /**
     * 如果多个Ability实例都需要注册当前InternalAbility实例，需要更改构造函数，设定自己的bundleName和abilityName
     * 但是在这里我们的InternalAbility只会实例化一次
     */
    public TodoServiceAbility() {
        super(BUNDLE_NAME, ABILITY_NAME);
    }

    /**
     * 关键的接口，JS端携带的操作请求业务码以及业务数据，业务执行完后，返回响应给JS端。开发者需要继承RemoteObject类并重写该方法
     * @param code Js端发送的业务请求编码 PA端定义需要与Js端业务请求码保持一致
     * @param data Js端发送的MessageParcel对象，当前仅支持String格式
     * @param reply 将本地业务响应返回给Js端的MessageParcel对象，当前仅支持String格式
     * @param option 指示操作是同步还是异步的方式，但是我这里只有同步处理方式
     * @return 不知道有什么用
     */
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        SingleKvStore db = abilityContext.getSingleKvStore();

        Map<String, String> result = new HashMap<>();
        Map<String, String> ret = new HashMap<>();
        HiLog.debug(LABEL, "onRemoteRequest");

        // 基本思路-解析code-解析data-业务逻辑-构造reply
        switch (code) {
            case DELETE_TODOS: {
                String dataStr = data.readString();
                TodoRequestParam param = new TodoRequestParam();
                try {
                    param = ZSONObject.stringToClass(dataStr, TodoRequestParam.class);
                } catch (RuntimeException e) {
                    HiLog.error(LABEL, "convert failed.");
                }
                try {
                    db.delete(param.id + " title");
                } catch (KvStoreException k){
                    HiLog.debug(LABEL,"no");
                };
                try {
                    db.delete(param.id + " text");
                } catch (KvStoreException k){
                    HiLog.debug(LABEL,"no");
                };
                try {
                    db.delete(param.id + " date");
                } catch (KvStoreException k){
                    HiLog.debug(LABEL,"no");
                };
                try {
                    db.delete(param.id + " ddl");
                } catch (KvStoreException k){
                    HiLog.debug(LABEL,"no");
                };
                HiLog.debug(LABEL, "insert or update success");
                break;
            }
            case UPDATE_TODOS:
            case INSERT_TODOS:{
                String dataStr = data.readString();
                TodoRequestParam param = new TodoRequestParam();
                try {
                    param = ZSONObject.stringToClass(dataStr, TodoRequestParam.class);
                } catch (RuntimeException e) {
                    HiLog.error(LABEL, "convert failed.");
                }
                if (param.title!=null) db.putString(param.id+" title",param.title);
                if (param.date!=null) db.putString(param.id+" date",param.date);
                if (param.text!=null) db.putString(param.id+" text",param.text);
                if (param.ddl!=null) db.putString(param.id+" ddl",param.ddl);
                HiLog.debug(LABEL, "insert or update success");
                break;
            }
            case SELECT_TODOS:{
                // 返回结果当前仅支持String，对于复杂结构可以序列化为ZSON字符串上报
                HiLog.debug(LABEL, "Database Return All Result");
                String condition = data.readString();
                HiLog.debug(LABEL, "condition="+condition);
                Set<String> indexes = new HashSet<>();
                for (Entry i: db.getEntries("")){
                    indexes.add(i.getKey().split(" ")[0]);
                }

                for (String i: indexes){
                    String title,date,ddl;
                    try {
                        title = db.getString(i+" title");
                        if (title.contains(condition))
                            result.put("title",title);
                        else
                            continue;
                    }
                    catch (KvStoreException k){
                        title = "";
                    };
                    try {
                        date = db.getString(i+" date");
                        result.put("date",date);
                    }
                    catch (KvStoreException k){
                        date = "";
                    };
                    try {
                        ddl = db.getString(i+" ddl");
                        result.put("ddl",ddl);
                    }
                    catch (KvStoreException k){
                        ddl = "";
                    };
//                    HiLog.debug(LABEL, String.valueOf(result));
                    String rString = ZSONObject.toZSONString(result);
                    ret.put(i,rString);
                }
                reply.writeString(ZSONObject.toZSONString(ret));
                HiLog.debug(LABEL, "select all success");
                break;
            }
            case SELECT_TODO: {
                String dataStr = data.readString();
                TodoRequestParam param = new TodoRequestParam();
                try {
                    param = ZSONObject.stringToClass(dataStr, TodoRequestParam.class);
                } catch (RuntimeException e) {
                    HiLog.error(LABEL, "convert failed.");
                }
                String r;
                try {
                    r = db.getString(param.id+" text");
                } catch (KvStoreException k) {
                    r = "";
                }
                reply.writeString(r);
                HiLog.debug(LABEL, "select one text success");
                break;
            }
            default: {
                HiLog.debug(LABEL, "unknown code");
                return false;
            }
        };
        // 分布式数据库的同步
        abilityContext.syncContact();
        return true;
    }

    /**
     * Internal ability 注册接口
     */
    public static void register(MainAbility abilityContext) {
        instance = new TodoServiceAbility();
        instance.onRegister(abilityContext);
    }

    private void onRegister(MainAbility abilityContext) {
        this.abilityContext = abilityContext;
        this.setInternalAbilityHandler(this::onRemoteRequest);
        HiLog.info(LABEL, "jgq TodoServiceAbility onRegister");
    }

    /**
     * Internal ability 注销接口。
     */
    public static void unregister() {
        instance.onUnregister();
    }

    private void onUnregister() {
        abilityContext = null;
        this.setInternalAbilityHandler(null);
        HiLog.info(LABEL, "jgq TodoServiceAbility onUnregister");
    }
}