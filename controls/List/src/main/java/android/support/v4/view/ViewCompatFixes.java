package android.support.v4.view;

import android.view.View;

public class ViewCompatFixes {
    public static void setPivotY(View view, float value) {
        ViewCompat.IMPL.setPivotY(view, value);
    }
}
