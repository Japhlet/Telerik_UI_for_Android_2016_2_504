package com.telerik.android.primitives.widget.sidedrawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v8.renderscript.RenderScript;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v8.renderscript.*;
import android.widget.ImageView;

import com.telerik.android.common.Util;
import com.telerik.android.primitives.R;

public class BlurFadeLayer extends DrawerFadeLayerBase {
    RenderScript renderContext;
    ImageView imageView;
    ScriptIntrinsicBlur blur;

    public BlurFadeLayer(Context context, RenderScript renderScriptContext) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.drawer_blur_fade_layer, this);
        imageView = Util.getLayoutPart(this, R.id.imageView, ImageView.class);

        renderContext = renderScriptContext;

        blur = ScriptIntrinsicBlur.create(renderContext, Element.RGBA_8888(renderContext));
        blur.setRadius(5);
    }

    @Override
    public void show() {
        super.show();

        ViewGroup parent = (ViewGroup)this.getParent();
        View mainContent = parent.getChildAt(0);


        Bitmap input = this.snapshotView(mainContent);
        Bitmap output = this.blurBitmap(input);

        this.imageView.setImageBitmap(output);
    }

    @Override
    public void hide() {
        super.hide();

        ViewGroup parent = (ViewGroup)this.getParent();
        View mainContent = parent.getChildAt(0);
    }

    protected Bitmap blurBitmap(Bitmap input) {
        Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);

        Allocation inputAlloc = Allocation.createFromBitmap(renderContext, input);
        Allocation outputAlloc = Allocation.createFromBitmap(renderContext, result);

        blur.setInput(inputAlloc);
        blur.forEach(outputAlloc);

        outputAlloc.copyTo(result);

        input.recycle();
        return result;
    }

    protected Bitmap snapshotView(View view) {
        Bitmap viewImage = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewImage);
        view.draw(canvas);

        Bitmap result = Bitmap.createScaledBitmap(viewImage, viewImage.getWidth() / 5, viewImage.getHeight() / 5, true);
        viewImage.recycle();

        return result;
    }
}
