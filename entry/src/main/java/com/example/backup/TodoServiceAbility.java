package com.example.backup;

import ohos.ace.ability.AceInternalAbility;
import ohos.agp.components.DependentLayout;
import ohos.app.AbilityContext;
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

    // 定义日志标签
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "TodoDatabase");

    private static TodoServiceAbility instance;
    private AbilityContext abilityContext;

    // 如果多个Ability实例都需要注册当前InternalAbility实例，需要更改构造函数，设定自己的bundleName和abilityName

    public TodoServiceAbility() {
        super(BUNDLE_NAME, ABILITY_NAME);
    }

    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
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
                MainAbility.preferences.delete(param.id+" title");
                MainAbility.preferences.delete(param.id+" date");
                MainAbility.preferences.delete(param.id+" text");
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
                MainAbility.preferences.putString(param.id+" title",param.title);
                MainAbility.preferences.putString(param.id+" date",param.date);
                MainAbility.preferences.putString(param.id+" text",param.text);
                HiLog.debug(LABEL, "insert or update success");
                break;
            }
            case SELECT_TODOS:
            default: {
                break;
            }
        }
        // 返回结果当前仅支持String，对于复杂结构可以序列化为ZSON字符串上报
        HiLog.debug(LABEL, "Database Return All Result");
        int size = MainAbility.preferences.getAll().size()/3;
//        String[] rString = new String[size];
        for (int i=0;i<size;i++){
            String title = MainAbility.preferences.getString(String.format("%1$d title",i ),"");
            result.put("title",title);
            String date = MainAbility.preferences.getString(String.format("%1$d date",i ),"");
            result.put("date",date);
            String text = MainAbility.preferences.getString(String.format("%1$d text",i ),"");
            result.put("text",text);
            HiLog.debug(LABEL, String.valueOf(result));
            String rString = ZSONObject.toZSONString(result);
            ret.put(String.format("%1$d",i),rString);
        }
        reply.writeString(ZSONObject.toZSONString(ret));
        // SYNC
//        if (option.getFlags() == MessageOption.TF_SYNC) {

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
    public static void register(AbilityContext abilityContext) {
        instance = new TodoServiceAbility();
        instance.onRegister(abilityContext);
    }

    private void onRegister(AbilityContext abilityContext) {
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
}