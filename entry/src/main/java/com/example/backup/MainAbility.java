package com.example.backup;

import ohos.aafwk.ability.IAbilityContinuation;
import ohos.ace.ability.AceAbility;
import ohos.aafwk.content.Intent;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.distributed.common.*;
import ohos.data.distributed.device.DeviceFilterStrategy;
import ohos.data.distributed.device.DeviceInfo;
import ohos.data.distributed.user.SingleKvStore;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainAbility extends AceAbility implements IAbilityContinuation {
//    static public Preferences preferences;
    static public String storeID = "TodoDistributeDatabase";
    private SingleKvStore singleKvStore;
    private KvManager kvManager;
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0, "TodoDatabase");
    private boolean dbbugfix;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        // 开发者显示声明需要使用的权限
        requestPermissionsFromUser(new String[]{"ohos.permission.DISTRIBUTED_DATASYNC"}, 0);
        Context context = this.getContext();
//        DatabaseHelper databaseHelper = new DatabaseHelper(context);
//        String filename = "todoDB";
//        preferences = databaseHelper.getPreferences(filename);

        // 需要注册
        TodoServiceAbility.register(this);
        // 分布式数据库,不知道是不是写在这里,注册
        KvManagerConfig config = new KvManagerConfig(context);
        kvManager = KvManagerFactory.getInstance().createKvManager(config);
        // 创建数据库
        Options CREATE = new Options();
        CREATE.setCreateIfMissing(true).setEncrypt(false).setKvStoreType(KvStoreType.SINGLE_VERSION);
        singleKvStore = kvManager.getKvStore(CREATE, storeID);
        // 订阅
        KvStoreObserver kvStoreObserverClient = new KvStoreObserverClient();
        singleKvStore.subscribe(SubscribeType.SUBSCRIBE_TYPE_ALL, kvStoreObserverClient);
        dbbugfix = true;
    }

    public SingleKvStore getSingleKvStore() {
        return singleKvStore;
    }

    @Override
    public void onStop() {
        // 取消注册
        TodoServiceAbility.unregister();
        super.onStop();
        dbbugfix = false;
    }

    public void syncContact() {
        List<DeviceInfo> deviceInfoList = kvManager.getConnectedDevicesInfo(DeviceFilterStrategy.NO_FILTER);
        List<String> deviceIdList = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            deviceIdList.add(deviceInfo.getId());
        }

        if (deviceIdList.size() == 0) {
//            showTip("组网失败");
            HiLog.info(LABEL,"同步失败");
            return;
        }
        singleKvStore.registerSyncCallback(new SyncCallback() {
            @Override
            public void syncCompleted(Map<String, Integer> map) {
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
//                        queryContact();
//                        showTip("同步成功");
                        HiLog.info(LABEL,"同步成功");
                    }
                });
                singleKvStore.unRegisterSyncCallback();
            }
        });
        singleKvStore.sync(deviceIdList, SyncMode.PUSH_PULL);
    }
}
