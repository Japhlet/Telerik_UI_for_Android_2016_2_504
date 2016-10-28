package com.telerik.android.common.licensing;

import android.content.Context;

public class TelerikLicense
{
  private static boolean trialMessageShown = false;

  public static boolean licenseRequired() {
    return true;
  }

  public static void verify(Context context)
  {
  }
}