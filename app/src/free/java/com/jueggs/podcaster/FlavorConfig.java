package com.jueggs.podcaster;

import android.content.Context;
import android.provider.Settings;
import android.view.View;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jueggs.utils.SecurityUtils;
import com.jueggs.utils.Utils;

import static com.jueggs.utils.SecurityUtils.*;

public class FlavorConfig
{
    public static void initializeMobileAds(Context context,View view)
    {
        MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");

        AdView adView = (AdView) view.findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getDeviceId(context)).build();
        adView.loadAd(adRequest);
    }
}
