package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

import java.util.ArrayList;

public abstract class ChartAnimationBase implements ChartAnimation, Runnable {
    private ChartSeries series;
    private long duration = 500;
    private Interpolator interpolator = new DecelerateInterpolator(2);
    private long initialDelay;
    private ArrayList<ChartAnimationFinishedListener> finishedListeners = new ArrayList<ChartAnimationFinishedListener>();
    private SeriesAnimationView animatedView;

    public ChartAnimationBase() {
    }

    public ChartAnimationBase(ChartSeries series) {
        if(series == null) {
            throw new IllegalArgumentException("series cannot be null.");
        }

        this.series = series;
    }

    public ChartSeries getSeries() {
        return this.series;
    }

    private ViewPropertyAnimatorCompat animateView(SeriesAnimationView view) {
        animatedView = view;
        view.incrementAnimationCount();

        ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);
        animator.setDuration(this.getDuration()).setStartDelay(this.getInitialDelay()).setInterpolator(this.getInterpolator()).withEndAction(this);
        this.animateViewCore(animator);
        return animator;
    }

    protected abstract void animateViewCore(ViewPropertyAnimatorCompat animator);

    @Override
    public void setDuration(long value) {
        this.duration = value;
    }

    @Override
    public void setInterpolator(Interpolator value) {
        this.interpolator = value;
    }

    @Override
    public void setInitialDelay(long value) {
        this.initialDelay = value;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    @Override
    public long getInitialDelay() {
        return this.initialDelay;
    }

    @Override
    public ViewPropertyAnimatorCompat start(SeriesAnimationView viewToAnimate) {
        return this.animateView(viewToAnimate);
    }

    public void run() {
        this.onAnimationFinished();
    }

    public void addAnimationFinishedListener(ChartAnimationFinishedListener listener) {
        this.finishedListeners.add(listener);
    }

    public void removeAnimationFinishedListener(ChartAnimationFinishedListener listener) {
        this.finishedListeners.remove(listener);
    }

    protected void onAnimationFinished() {
        animatedView.decrementAnimationCount();
        notifyListeners(animatedView);
        animatedView = null;
    }

    private void notifyListeners(SeriesAnimationView animatedView) {
        for(ChartAnimationFinishedListener listener : finishedListeners) {
            listener.onAnimationFinished(this, animatedView);
        }
    }
}
