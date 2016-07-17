package com.jueggs.podcaster.data.repo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import java.io.Serializable;

@SuppressLint("ParcelCreator") public class DataResultReceiver extends ResultReceiver
{
    public static final String EXTRA_RECEIVER = "com.jueggs.podcaster.EXTRA_RECEIVER";
    public static final String EXTRA_DATA = "com.jueggs.podcaster.EXTRA_DATA";
    public static final String EXTRA_LANGUAGE = "com.jueggs.podcaster.EXTRA_LANGUAGE";
    public static final String EXTRA_ID = "com.jueggs.podcaster.EXTRA_ID";
    public static final String EXTRA_TYPE = "com.jueggs.podcaster.EXTRA_TYPE";
    public static final int RESULT_CODE = 0;

    private Receiver receiver;

    public DataResultReceiver(Handler handler, Receiver receiver)
    {
        super(handler);
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == RESULT_CODE)
            receiver.onReceiveResult(resultData.getSerializable(EXTRA_DATA));
    }

    public interface Receiver
    {
        void onReceiveResult(Serializable result);
    }
}
