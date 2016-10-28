package com.telerik.android.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.telerik.android.common.exceptions.MissingLayoutPartException;
import com.telerik.android.common.exceptions.WrongLayoutPartTypeException;
import com.telerik.android.common.math.RadRect;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class with helper methods.
 */
public class Util {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(5000);

    private static final String BELOW_LINE_CHARS = "qypgjущфдцр";

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.equals("");
    }

    public static int generateViewId() {
        // Copy/Paste from the android source code because this API is not present and the compat library
        // does not have it. Naturally.
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static boolean Equals(Object obj1, Object obj2) {
        if(obj1 == obj2) {
            return true;
        }

        if(obj1 != null) {
            return obj1.equals(obj2);
        }

        return false;
    }

    public static String toString(Object object) {
        if (object == null) {
            return "";
        }

        return object.toString();
    }

    /**
     * Converts the given RectF to Rect by rounding its members.
     * @param rect The rect to convert.
     * @return Returns the converted rect.
     */
    public static Rect RectFToRect(RectF rect) {
        return new Rect(Math.round(rect.left), Math.round(rect.top), Math.round(rect.right), Math.round(rect.bottom));
    }

    /**
     * Converts the provided value in dp to a value in physical pixels using the provided context.
     * @param type The type of dimension.
     * @param dimen The dimension to get.
     * @return Returns a value in physical pixels.
     */
    public static float getDimen(int type, float dimen) {
        return TypedValue.applyDimension(type, dimen, Resources.getSystem().getDisplayMetrics());
    }

    public static float getDP(float dp) {
        return getDimen(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    /**
     * Converts a {@link RadRect} instance to {@link RectF}.
     *
     * @param rect The rectangle to convert.
     */
    public static RectF convertToRectF(RadRect rect) {
        return new RectF((float)rect.getX(), (float)rect.getY(), (float)rect.getRight(), (float)rect.getBottom());
    }

    /**
     * Gets an xml layout part.
     *
     * @param parent     The View inside which the part resides.
     * @param partId     The id of the part to get.
     * @param resultType The class of the part to get.
     * @param <T>        The type of the part to get.
     * @return Returns a View of a given type with a given id from the children of the parent.
     */
    public static <T> T getLayoutPart(View parent, int partId, Class<T> resultType) {
        return getLayoutPart(parent, partId, resultType, true);
    }

    /**
     * Gets an xml layout part.
     *
     * @param parent         The View inside which the part resides.
     * @param partId         The id of the part to get.
     * @param resultType     The class of the part to get.
     * @param <T>            The type of the part to get.
     * @param throwException <code>true</code> if a {@link com.telerik.android.common.exceptions.MissingLayoutPartException} should be thrown
     *                       if the layout part is not found and <code>false</code>otherwise.
     * @return Returns a View of a given type with a given id from the children of the parent.
     */
    public static <T> T getLayoutPart(View parent, int partId, Class<T> resultType, boolean throwException) {

        View view = parent.findViewById(partId);

        String resourceName = parent.getResources().getResourceName(partId);

        if (view == null && throwException) {
            throw new MissingLayoutPartException(String.format("Layout part with id %s is missing from the specified layout.", resourceName));
        }

        T result;
        try {
            result = resultType.cast(view);
        } catch (ClassCastException ex) {
            throw new WrongLayoutPartTypeException(String.format("Layout part with id %s is of the wrong type. It should be of type %s.", resourceName, resultType.getName()));
        }
        return result;
    }

    /**
     * Gets an xml layout part.
     *
     * @param parent     The View inside which the part resides.
     * @param partId     The id of the part to get.
     * @param resultType The class of the part to get.
     * @param <T>        The type of the part to get.
     * @return Returns a View of a given type with a given id from the children of the parent.
     */
    public static <T> T getLayoutPart(Activity parent, int partId, Class<T> resultType) {
        return getLayoutPart(parent, partId, resultType, true);
    }

    /**
     * Gets an xml layout part.
     *
     * @param parent         The View inside which the part resides.
     * @param partId         The id of the part to get.
     * @param resultType     The class of the part to get.
     * @param <T>            The type of the part to get.
     * @param throwException <code>true</code> if a {@link com.telerik.android.common.exceptions.MissingLayoutPartException} should be thrown
     *                       if the layout part is not found and <code>false</code>otherwise.
     * @return Returns a View of a given type with a given id from the children of the parent.
     */
    public static <T> T getLayoutPart(Activity parent, int partId, Class<T> resultType, boolean throwException) {

        View view = parent.findViewById(partId);

        String resourceName = parent.getResources().getResourceName(partId);

        if (view == null && throwException) {
            throw new MissingLayoutPartException(String.format("Layout part with id %s is missing from the specified layout.", resourceName));
        }

        T result;
        try {
            result = resultType.cast(view);
        } catch (ClassCastException ex) {
            throw new WrongLayoutPartTypeException(String.format("Layout part with id %s is of the wrong type. It should be of type %s.", resourceName, resultType.getName()));
        }
        return result;
    }

    /**
     * Creates a view from the given xml resource id.
     * @param layoutId The id of the xml resource.
     * @param resultType The type of view declared in the xml resource.
     * @param context A context from which a LayoutInflater will be obtained.
     * @param <T> The result type.
     * @return A an instance of the result type created from the given xml resource.
     */
    public static <T> T createViewFromXML(int layoutId, Class<T> resultType, Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        if (resultType == null) {
            throw new IllegalArgumentException("resultType cannot be null");
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            return resultType.cast(inflater.inflate(layoutId, null));
        } catch (ClassCastException ex) {
            throw new WrongLayoutPartTypeException(String.format("Layout root is of the wrong type. It should be of type %s.", resultType.getName()));
        }
    }

    /**
     * Generates dummy text avoiding any below line characters.
     *
     * @param text the original text.
     * @return the dummy text.
     */
    public static String generateDummyText(final String text) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0, len = text.length(); i < len; i++) {
            Character ch = text.charAt(i);
            if (Character.isDigit(ch) || !Character.isLetter(ch)) {
                sb.append(ch);
            } else {
                if (BELOW_LINE_CHARS.contains(ch.toString())) {
                    sb.append('a');
                } else
                    sb.append(ch);
            }
        }

        return sb.toString();
    }
}
