package com.jueggs.podcaster.helper;

import android.content.Context;
import android.text.TextUtils;
import com.jueggs.utils.NetUtils;

import java.io.IOException;
import java.net.UnknownHostException;

import static android.text.TextUtils.*;
import static com.jueggs.utils.NetUtils.*;

public class NetworkValidator
{
    private Context context;

    public NetworkValidator(Context context)
    {
        this.context = context;
    }

    public int validate(String data)
    {
        if (!isEmpty(data))
            return Result.SUCCESS;
        else
        {
            if (isNetworkAvailable(context))
                return Result.SERVER_DOWN;
            else
                return Result.NO_NETWORK;
        }
    }

    public int validate(Exception e)
    {
        if (e instanceof UnknownHostException)
        {
            if (isNetworkAvailable(context))
                return Result.SERVER_DOWN;
            else
                return Result.NO_NETWORK;
        }
        else
            return Result.UNKNOWN;
    }
}
