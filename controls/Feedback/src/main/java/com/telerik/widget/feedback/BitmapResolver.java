package com.telerik.widget.feedback;

import android.graphics.Bitmap;
import android.view.View;

/**
 * An interface whose implementors should provide a Bitmap based on a passed View.
 */
public interface BitmapResolver {
    Bitmap getBitmapFromView(View view);
}
