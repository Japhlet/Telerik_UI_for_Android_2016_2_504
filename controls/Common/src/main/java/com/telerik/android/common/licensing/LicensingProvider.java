package com.telerik.android.common.licensing;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LicensingProvider {

    public static void verify(Context context) {
        try {
            Class telerikLicenseClass = Class.forName("com.telerik.android.common.licensing.TelerikLicense");
            Method verifyMethod = telerikLicenseClass.getMethod("verify", Context.class);
            verifyMethod.invoke(null, context);
        } catch (Exception e) {
            // Do nothing here. We are licensed version.
        }
    }
}
