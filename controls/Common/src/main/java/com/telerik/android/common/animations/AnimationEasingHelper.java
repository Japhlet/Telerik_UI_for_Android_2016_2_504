package com.telerik.android.common.animations;

/**
 * Helper for adding easing to animations.
 */
public class AnimationEasingHelper {

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a linear algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double linear(double currentTime, double startValue, double totalValueChange, double endTime) {
        return totalValueChange * currentTime / endTime + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quadratic ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quadraticEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        return totalValueChange * currentTime * currentTime + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quadratic ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quadraticEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        return -totalValueChange * currentTime * (currentTime - 2) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quadratic ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quadraticEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime / 2;
        if (currentTime < 1)
            return totalValueChange / 2 * currentTime * currentTime + startValue;
        currentTime -= 1;
        return -totalValueChange / 2 * (currentTime * (currentTime - 2) - 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a cubic ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double cubicEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        return totalValueChange * currentTime * currentTime * currentTime + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a cubic ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double cubicEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        currentTime -= 1;
        return totalValueChange * (currentTime * currentTime * currentTime + 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a cubic ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double cubicEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime / 2;
        if (currentTime < 1)
            return totalValueChange / 2 * currentTime * currentTime * currentTime + startValue;
        currentTime -= 2;
        return totalValueChange / 2 * (currentTime * currentTime * currentTime + 2) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quartic ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quarticEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        return totalValueChange * currentTime * currentTime * currentTime * currentTime + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quartic ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quarticEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        currentTime -= 1;
        return -totalValueChange * (currentTime * currentTime * currentTime * currentTime - 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quartic ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quarticEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime / 2;
        if (currentTime < 1)
            return totalValueChange / 2 * currentTime * currentTime * currentTime * currentTime + startValue;
        currentTime -= 2;
        return -totalValueChange / 2 * (currentTime * currentTime * currentTime * currentTime - 2) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quintic ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quinticEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        return totalValueChange * currentTime * currentTime * currentTime * currentTime * currentTime + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quintic ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quinticEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        currentTime -= 1;
        return totalValueChange * (currentTime * currentTime * currentTime * currentTime * currentTime + 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a quintic ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double quinticEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime / 2;
        if (currentTime < 1)
            return totalValueChange / 2 * currentTime * currentTime * currentTime * currentTime * currentTime + startValue;
        currentTime -= 2;
        return totalValueChange / 2 * (currentTime * currentTime * currentTime * currentTime * currentTime + 2) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a sinusoidal ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double sinusoidalEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        return -totalValueChange * Math.cos(currentTime / endTime * (Math.PI / 2)) + totalValueChange + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a sinusoidal ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double sinusoidalEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        return totalValueChange * Math.sin(currentTime / endTime * (Math.PI / 2)) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a sinusoidal ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double sinusoidalEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        return -totalValueChange / 2 * (Math.cos(Math.PI * currentTime / endTime) - 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a exponential ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double exponentialEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        return totalValueChange * Math.pow(2, 10 * (currentTime / endTime - 1)) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a exponential ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double exponentialEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        return totalValueChange * (-Math.pow(2, -10 * currentTime / endTime) + 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a exponential ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double exponentialEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime / 2;
        if (currentTime < 1)
            return totalValueChange / 2 * Math.pow(2, 10 * (currentTime - 1)) + startValue;
        currentTime -= 1;
        return totalValueChange / 2 * (-Math.pow(2, -10 * currentTime) + 2) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a circular ease in algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double circularEaseIn(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        return -totalValueChange * (Math.sqrt(1 - currentTime * currentTime) - 1) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a circular ease out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double circularEaseOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime;
        currentTime -= 1;
        return totalValueChange * Math.sqrt(1 - currentTime * currentTime) + startValue;
    }

    /**
     * Used to calculate a single step of an animation based on a starting value, total value change, current time and ending time using a circular ease in/out algorithm.
     * The starting value is the value at the beginning of the animation. The total change value is the total change to be applied during the animation. For example if
     * the start value is 100 and the desired value at the end of the animation is 0 then the total value change will be -100.
     * The current time and the end time can be frames or milliseconds.
     *
     * @param currentTime      the current time or frame.
     * @param startValue       the value at the beginning of the animation.
     * @param totalValueChange the value to be applied during the animation.
     * @param endTime          the end time or frame.
     * @return the calculated animation step.
     */
    public static double circularEaseInOut(double currentTime, double startValue, double totalValueChange, double endTime) {
        currentTime /= endTime / 2;
        if (currentTime < 1)
            return -totalValueChange / 2 * (Math.sqrt(1 - currentTime * currentTime) - 1) + startValue;
        currentTime -= 2;
        return totalValueChange / 2 * (Math.sqrt(1 - currentTime * currentTime) + 1) + startValue;
    }
}
