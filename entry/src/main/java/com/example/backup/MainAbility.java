package com.example.backup;

import ohos.aafwk.ability.IAbilityContinuation;
import ohos.ace.ability.AceAbility;
import ohos.aafwk.content.Intent;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbility extends AceAbility implements IAbilityContinuation {
    static public Preferences preferences;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        // 开发者显示声明需要使用的权限
        requestPermissionsFromUser(new String[]{"ohos.permission.DISTRIBUTED_DATASYNC"}, 0);
        Context context = this.getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String filename = "todoDB";
        preferences = databaseHelper.getPreferences(filename);
        // 需要注册
        TodoServiceAbility.register(this);
    }

    @Override
    public void onStop() {
        // 取消注册
        TodoServiceAbility.unregister();
        super.onStop();
    }


}
