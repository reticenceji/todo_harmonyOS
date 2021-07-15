package com.example.backup;

import ohos.ace.ability.AceInternalAbility;
import ohos.agp.components.DependentLayout;
import ohos.app.AbilityContext;
import ohos.data.distributed.common.*;
import ohos.data.distributed.device.DeviceFilterStrategy;
import ohos.data.distributed.device.DeviceInfo;
import ohos.data.distributed.user.SingleKvStore;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.RequestParam;
import ohos.rpc.IRemoteObject;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.rpc.RemoteException;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

import java.lang.reflect.Array;
import java.util.*;

public class TodoServiceAbility extends AceInternalAbility {
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

    private static TodoServiceAbility instance;
    private MainAbility abilityContext;

    // 如果多个Ability实例都需要注册当前InternalAbility实例，需要更改构造函数，设定自己的bundleName和abilityName

    public TodoServiceAbility() {
        super(BUNDLE_NAME, ABILITY_NAME);
    }

    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        SingleKvStore db = abilityContext.getSingleKvStore();

        Map<String, String> result = new HashMap<>();
        Map<String, String> ret = new HashMap<>();
        HiLog.debug(LABEL, "onRemoteRequest");

        switch (code) {
            case DELETE_TODOS: {
                String dataStr = data.readString();
                TodoRequestParam param = new TodoRequestParam();
                try {
                    param = ZSONObject.stringToClass(dataStr, TodoRequestParam.class);
                } catch (RuntimeException e) {
                    HiLog.error(LABEL, "convert failed.");
                }
                db.delete(param.id+" title");
                db.delete(param.id+" date");
                db.delete(param.id+" text");
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
                db.putString(param.id+" title",param.title);
                db.putString(param.id+" date",param.date);
                db.putString(param.id+" text",param.text);
                HiLog.debug(LABEL, "insert or update success");
                break;
            }
            case SELECT_TODOS:{
                // 返回结果当前仅支持String，对于复杂结构可以序列化为ZSON字符串上报
                HiLog.debug(LABEL, "Database Return All Result");
                Set<String> indexes = new HashSet<>();
                for (Entry i: db.getEntries("")){
                    indexes.add(i.getKey().split(" ")[0]);
                }

                for (String i: indexes){
                    String title = db.getString(i+" title");
                    result.put("title",title);
                    String date = db.getString(i+" date");
                    result.put("date",date);
                    // 不需要回传text
//                    String text = MainAbility.preferences.getString(String.format("%1$d text",i ),"");
//                    result.put("text",text);
                    HiLog.debug(LABEL, String.valueOf(result));
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
                return false;
            }
        }
//        MainAbility.singleKvStore.flush();
//        KvStoreObserver kvStoreObserverClient = new KvStoreObserverClient();
//        db.subscribe(SubscribeType.SUBSCRIBE_TYPE_ALL, kvStoreObserverClient);
        abilityContext.syncContact();
        // SYNC
//        if (option.getFlags() == MessageOption.TF_SYNC) {
//             ...
//        } else {
//            // ASYNC
//            MessageParcel responseData = MessageParcel.obtain();
//            responseData.writeString(ZSONObject.toZSONString(result));
//            IRemoteObject remoteReply = reply.readRemoteObject();
//            try {
//                remoteReply.sendRequest(0, responseData, MessageParcel.obtain(), new MessageOption());
//            } catch (RemoteException exception) {
//                return false;
//            } finally {
//                responseData.reclaim();
//            }
//        }
        return true;
    }

    /**
     * Internal ability 注册接口。
     */
    public static void register(MainAbility abilityContext) {
        instance = new TodoServiceAbility();
        instance.onRegister(abilityContext);
    }

    private void onRegister(MainAbility abilityContext) {
        this.abilityContext = abilityContext;
        this.setInternalAbilityHandler(this::onRemoteRequest);
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
    }

    /**
     * 数据同步
     */

}