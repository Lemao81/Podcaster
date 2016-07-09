package com.jueggs.podcaster.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service
{
    private Object lock=new Object();
    private SyncAdapter syncAdapter;

    @Override
    public void onCreate()
    {
        synchronized (lock)
        {
            if (syncAdapter == null)
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return syncAdapter.getSyncAdapterBinder();
    }
}
