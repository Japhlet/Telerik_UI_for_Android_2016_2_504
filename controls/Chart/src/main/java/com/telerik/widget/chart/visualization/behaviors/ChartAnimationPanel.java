package com.telerik.widget.chart.visualization.behaviors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.telerik.android.common.CollectionChangeListener;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.behaviors.animations.ChartAnimation;
import com.telerik.widget.chart.visualization.behaviors.animations.ChartAnimationFinishedListener;
import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationViewport;
import com.telerik.widget.chart.visualization.common.ChartElementPresenter;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.PresenterCollection;
import com.telerik.widget.chart.visualization.common.PropertyChangedListener;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChartAnimationPanel extends FrameLayout implements ChartSeriesModel.DataPointsChangedListener, ChartAnimationFinishedListener, PropertyChangedListener {
    private ArrayList<ChartAnimation> animations = new ArrayList<>();
    private RectF plotClip;
    private SeriesAnimationViewport viewport;
    private RadChartViewBase<ChartSeries> chart;
    private HashMap<ChartSeries, SeriesAnimationView> seriesViews = new HashMap<>();
    private CollectionChangeListener<ChartSeries> collectionChangeListener = new CollectionChangeListener<ChartSeries>() {
        @Override
        public void collectionChanged(CollectionChangedEvent<ChartSeries> info) {
            onCollectionChanged(info);
        }
    };
    private int runningAnimations;
    private boolean chartClickable;
    private boolean animationsStarted;

    public ChartAnimationPanel(Context context) {
        this(context, null);
    }

    public ChartAnimationPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        viewport = new SeriesAnimationViewport(context);
        this.addView(viewport);

        this.setWillNotDraw(false);
    }

    private SeriesAnimationView createSeriesView(ChartSeries series) {
        if(seriesViews.containsKey(series)) {
            return null;
        }

        SeriesAnimationView newView = new SeriesAnimationView(this.getContext(), series);
        seriesViews.put(series, newView);

        return newView;
    }

    public RadChartViewBase getChart() {
        return this.chart;
    }

    public void setChart(RadChartViewBase value) {
        if (value != null){
            this.addView(value, 0);
        }
    }

    private void initChart(RadChartViewBase chart) {
        chart.getSeries().addCollectionChangeListener(collectionChangeListener);
        for(Object series : chart.getSeries()) {
            ChartSeries typedSeries = (ChartSeries)series;
            typedSeries.model().setDataPointsChangedListener(this);
            typedSeries.addPropertyChangedListener(this);
            createSeriesView(typedSeries);
        }

        resetViewPort();

        viewport.setChart(chart);
    }

    private void resetViewPort() {
        viewport.removeAllViews();
        ArrayList<SeriesAnimationView> views = getSortedAnimationViews();
        for(SeriesAnimationView view : views) {
            viewport.addView(view);
        }
    }

    private ArrayList<SeriesAnimationView> getSortedAnimationViews() {
        ArrayList<SeriesAnimationView> result = new ArrayList<>();
        for(SeriesAnimationView view : seriesViews.values()) {
            result.add(view);
        }

        Collections.sort(result, new Comparator<SeriesAnimationView>() {
            @Override
            public int compare(SeriesAnimationView lhs, SeriesAnimationView rhs) {
                Integer left = lhs.getSeries().getCollectionIndex();
                Integer right = rhs.getSeries().getCollectionIndex();
                return left.compareTo(right);
            }
        });

        return result;
    }

    private void removePreviousChart(RadChartViewBase chart) {
        if(chart == null) {
            return;
        }

        chart.getSeries().removeCollectionChangeListener(collectionChangeListener);
        for(Object series : chart.getSeries()) {
            ChartSeries typedSeries = (ChartSeries)series;
            typedSeries.removePropertyChangedListener(this);
            ChartSeriesModel model = typedSeries.model();
            if(model.getDataPointsChangedListener() == this) {
                model.setDataPointsChangedListener(null);
            }
            seriesViews.clear();
            viewport.removeAllViews();
        }

        this.removeView(chart);
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if(child instanceof RadChartViewBase) {
            removePreviousChart(chart);
            chart = (RadChartViewBase) child;
            initChart(chart);
            super.addView(child, 0, params);
        } else {
            super.addView(child, index, params);
        }
    }

    public void addAnimation(@NonNull ChartAnimation animation) {
        this.animations.add(animation);
        animation.addAnimationFinishedListener(this);

        ChartSeries series = animation.getSeries();
        if(series == null) {
            throw new IllegalArgumentException("Top level animations must have chart series associated with them.");
        }
    }

    @SuppressWarnings("unused")
    public boolean removeAnimation(@NonNull ChartAnimation animation) {
        animation.removeAnimationFinishedListener(this);
        ChartSeriesModel model = animation.getSeries().model();
        if(model.getDataPointsChangedListener() == this) {
            model.setDataPointsChangedListener(null);
        }

        return  this.animations.remove(animation);
    }

    public void startAllAnimations() {
        if(animations.size() == 0 || animationsStarted) {
            return;
        }

        chartClickable = chart.isClickable();
        chart.setClickable(false);
        setSeriesVisibility(false);
        setAnimationViewsAlpha(1);

        runningAnimations = this.animations.size();
        for(ChartAnimation animation : this.animations) {
            SeriesAnimationView viewToAnimate = seriesViews.get(animation.getSeries());
            animation.setInitialValues(viewToAnimate);
            animation.start(viewToAnimate).start();
        }

        animationsStarted = true;
    }

    private void startAnimationsForSeries(final ChartSeries series) {
        if(animationsStarted) {
            return;
        }

        chartClickable = chart.isClickable();
        chart.setClickable(false);
        setSeriesVisibility(false);
        setAnimationViewsAlpha(1);
        chart.invalidate();

        this.post(new Runnable() {
            @Override
            public void run() {
                List<ChartAnimation> seriesAnimations = findAnimationsForSeries(series);
                SeriesAnimationView seriesView = seriesViews.get(series);
                seriesView.invalidate();
                runningAnimations = seriesAnimations.size();
                for(ChartAnimation animation : seriesAnimations) {
                    animation.setInitialValues(seriesView);
                    animation.start(seriesView).start();
                }
            }
        });


        animationsStarted = true;
    }

    private void setSeriesVisibility(boolean visible) {
        PresenterCollection<?> seriesCollection = chart.getSeries();
        for(ChartElementPresenter series : seriesCollection) {
            series.setVisible(visible, false);
        }
    }

    private void setAnimationViewsAlpha(float alpha) {
        for(SeriesAnimationView view : seriesViews.values()) {
            ViewCompat.setAlpha(view, alpha);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(chart == null) {
            return;
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        chart.measure(width, height);

        plotClip = Util.convertToRectF(chart.getPlotAreaClip());
        width = Math.round(plotClip.width());
        height = Math.round(plotClip.height());
        viewport.measure(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(chart == null) {
            return;
        }

        viewport.layout(Math.round(plotClip.left), Math.round(plotClip.top), Math.round(plotClip.right), Math.round(plotClip.bottom));

        this.animationsStarted = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.startAllAnimations();
    }

    @Override
    public void onPointAdded(int index, DataPoint point) {
        ChartSeries series = (ChartSeries) point.getParent().getPresenter();
        this.animationsStarted = false;
        startAnimationsForSeries(series);
    }

    @Override
    public void onPointRemoved(int index, DataPoint point) {
        ChartSeries series = (ChartSeries) point.getParent().getPresenter();
        this.animationsStarted = false;
        startAnimationsForSeries(series);
    }

    private List<ChartAnimation> findAnimationsForSeries(ChartSeries series) {
        ArrayList<ChartAnimation> result = new ArrayList<>();
        for(ChartAnimation animation : animations) {
            if(animation.getSeries() == series) {
                result.add(animation);
            }
        }
        return result;
    }

    @Override
    public void onAnimationFinished(ChartAnimation animation, SeriesAnimationView animatedView) {
        runningAnimations--;
        if(runningAnimations == 0) {
            chart.setClickable(chartClickable);
            setSeriesVisibility(true);
            setAnimationViewsAlpha(0);
            chart.invalidate();
        }
    }

    public void onCollectionChanged(CollectionChangedEvent<ChartSeries> info) {
        switch (info.action()) {
            case ADD:
                for(ChartSeries series : info.getNewItems()) {
                    createSeriesView((series));
                }
                resetViewPort();
                for(ChartSeries series : info.getNewItems()) {
                    this.animationsStarted = false;
                    startAnimationsForSeries((series));
                }
                break;
            case REMOVE:
                for(ChartSeries series : info.getOldItems()) {
                    seriesViews.remove(series);
                }
                break;
            case RESET:
                seriesViews.clear();
                PresenterCollection<? extends ChartSeries> allSeries = chart.getSeries();
                for(ChartSeries series : allSeries) {
                    createSeriesView(series);
                }
                resetViewPort();
                break;
        }
    }

    @Override
    public void onPropertyChanged(Object sender, String propertyName, Object propertyValue) {
        if(propertyName.equals("Data")) {
            this.animationsStarted = false;
            startAnimationsForSeries((ChartSeries)sender);
        }
    }
}
