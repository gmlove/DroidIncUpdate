package com.comeplus.droidincupdate;

import java.net.URLEncoder;
import java.util.Locale;
import java.util.TimeZone;
import com.comeplus.droidincupdate.utils.MD5;
import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class SystemInfo {

    public SystemInfo() {
    }

    @SuppressLint("NewApi")
    public static String asUrlParam(Context context) {
        Locale l = Locale.getDefault();
        StringBuilder sb = new StringBuilder();
        sb.append("cc=");
        sb.append(URLEncoder.encode(l.getISO3Country()));
        sb.append("&lc=");
        sb.append(URLEncoder.encode(l.getISO3Language()));
        sb.append("&tz=");
        sb.append(URLEncoder.encode(TimeZone.getDefault().getID()));
        // READ_PHONE_STATE
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sb.append("&did=");
        sb.append(URLEncoder.encode(MD5.caclStringMd5(tm.getDeviceId())));
        sb.append("&m=");
        sb.append(URLEncoder.encode(android.os.Build.MODEL));
        sb.append("&vr=");
        sb.append(URLEncoder.encode(android.os.Build.VERSION.RELEASE));
        sb.append("&vsdk=");
        sb.append(URLEncoder.encode(android.os.Build.VERSION.SDK_INT + ""));
        sb.append("&sc=");
        DisplayMetrics dm = context.getResources().getDisplayMetrics(); 
        sb.append(URLEncoder.encode(dm.widthPixels + "x" + dm.heightPixels));
        return sb.toString();
    }
}
