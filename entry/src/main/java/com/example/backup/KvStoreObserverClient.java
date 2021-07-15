package com.example.backup;

import ohos.data.distributed.common.ChangeNotification;
import ohos.data.distributed.common.Entry;
import ohos.data.distributed.common.KvStoreObserver;

import java.util.List;

class KvStoreObserverClient implements KvStoreObserver {
    @Override
    public void onChange(ChangeNotification notification) {
//        List<Entry> insertEntries = notification.getInsertEntries();
//        List<Entry> updateEntries = notification.getUpdateEntries();
//        List<Entry> deleteEntries = notification.getDeleteEntries();

    }
}

