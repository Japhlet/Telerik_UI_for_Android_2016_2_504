package com.telerik.widget.feedback;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * A base implementation of the BitmapResolver interface.
 */
public class BitmapResolverBase implements BitmapResolver {
    @Override
    public Bitmap getBitmapFromView(View view) {
        Bitmap screenShot = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        Canvas drawingBoard = new Canvas(screenShot);
        view.draw(drawingBoard);
        return screenShot;
    }
}
