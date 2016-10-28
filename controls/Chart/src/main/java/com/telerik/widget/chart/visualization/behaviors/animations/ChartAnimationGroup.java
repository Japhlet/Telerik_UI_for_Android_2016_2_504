package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.animation.Interpolator;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

import java.util.ArrayList;

public class ChartAnimationGroup implements ChartAnimation, Runnable {
    private ChartSeries series;
    private ArrayList<ChartAnimation> children = new ArrayList<ChartAnimation>();
    private ChartAnimationSequenceMode mode = ChartAnimationSequenceMode.CONCURRENT;
    private long duration = 1000;
    private Interpolator interpolator;
    private long initialDelay;
    private int sequenceIndex;
    private SeriesAnimationView viewToAnimate;
    private ArrayList<ChartAnimationFinishedListener> finishedListeners = new ArrayList<ChartAnimationFinishedListener>();

    public ChartAnimationGroup(ChartSeries series) {
        if(series == null) {
            throw new IllegalArgumentException("series cannot be null.");
        }

        series.setVisible(false);
        this.series = series;
    }

    public void addAnimation(ChartAnimation animation) {
        children.add(animation);
        animation.setDuration(getChildDuration());
    }

    public void removeAnimation(ChartAnimation animation) {
        children.remove(animation);
    }

    public void setSequenceMode(ChartAnimationSequenceMode value) {
        if(mode == value) {
            return;
        }

        this.mode = value;
        this.setDuration(this.duration);
    }

    protected long getChildDuration() {
        if(mode == ChartAnimationSequenceMode.SEQUENTIAL) {
            return duration / Math.max(children.size(), 1);
        } else {
            return duration;
        }
    }

    @Override
    public ViewPropertyAnimatorCompat start(SeriesAnimationView viewToAnimate) {
        viewToAnimate.incrementAnimationCount();
        this.viewToAnimate = viewToAnimate;
        if(mode == ChartAnimationSequenceMode.CONCURRENT) {
            for(ChartAnimation animation : children) {
                if(isLast(animation)) {
                    return animation.start(viewToAnimate).withEndAction(this);
                } else if(isFirst(animation)) {
                    animation.start(viewToAnimate).setStartDelay(this.initialDelay);
                } else {
                    animation.start(viewToAnimate);
                }
            }
        } else {
            return children.get(0).start(viewToAnimate).withEndAction(this);
        }

        return null;
    }

    @Override
    public void setInitialValues(SeriesAnimationView view) {
        for(ChartAnimation animation : children) {
            animation.setInitialValues(view);
        }
    }

    private boolean isLast(ChartAnimation animation) {
        return animation == children.get(children.size() - 1);
    }

    private boolean isFirst(ChartAnimation animation) {
        return animation == children.get(0);
    }

    @Override
    public void setDuration(long value) {
        this.duration = value;
        long childDuration = getChildDuration();

        for(ChartAnimation animation : children) {
            animation.setDuration(childDuration);
        }
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setInterpolator(Interpolator value) {
        interpolator = value;
        for(ChartAnimation animation : children) {
            animation.setInterpolator(interpolator);
        }
    }

    @Override
    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    @Override
    public void setInitialDelay(long value) {
        initialDelay = value;
    }

    @Override
    public long getInitialDelay() {
        return initialDelay;
    }

    @Override
    public ChartSeries getSeries() {
        return this.series;
    }

    public void addAnimationFinishedListener(ChartAnimationFinishedListener listener) {
        this.finishedListeners.add(listener);
    }

    public void removeAnimationFinishedListener(ChartAnimationFinishedListener listener) {
        this.finishedListeners.remove(listener);
    }

    private void notifyListeners(SeriesAnimationView animatedView) {
        for(ChartAnimationFinishedListener listener : finishedListeners) {
            listener.onAnimationFinished(this, animatedView);
        }
    }

    @Override
    public void run() {
        if (mode == ChartAnimationSequenceMode.CONCURRENT || sequenceIndex == children.size() - 1) {
            viewToAnimate.decrementAnimationCount();
            notifyListeners(viewToAnimate);
            viewToAnimate = null;
            sequenceIndex = 0;
        } else {
            sequenceIndex++;
            children.get(sequenceIndex).start(viewToAnimate).withEndAction(this);
        }
    }
}
