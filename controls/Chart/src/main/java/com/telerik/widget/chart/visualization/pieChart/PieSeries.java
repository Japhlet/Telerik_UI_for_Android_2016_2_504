package com.telerik.widget.chart.visualization.pieChart;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;

import com.telerik.android.common.ObservableCollection;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadPolarCoordinates;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.chartAreas.AngleRange;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.PieSeriesDataSource;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.PieSeriesModel;
import com.telerik.widget.chart.visualization.behaviors.DataPointInfo;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;
import com.telerik.widget.primitives.legend.LegendItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a special type of {@link ChartSeries}
 * which is used in {@link RadPieChartView}.
 * <p/>
 * Each data items provided through PieSeries is visually represented by a pie slice. The angle
 * of each slice is calculated depending on the value of the data item that it represents
 * in a way that all slices together form a pie.
 */
public class PieSeries extends ChartSeries {

    public static final String ARC_STROKE_WIDTH_KEY = "ArcStrokeWidth";

    protected PieUpdateContext updateContext;

    private static final double ARC_PADDING = 2;
    private static final double DEFAULT_RADIUS_FACTOR = 1;
    public static final double DEFAULT_SELECTION_OFFSET = 0.15F;
    private static final double DEFAULT_SLICE_OFFSET = 2;
    private static final double DEFAULT_LABEL_OFFSET = 40;

    private List<SliceStyle> sliceStyles;
    private ArrayList<PieSegment> segments = new ArrayList<PieSegment>();

    private DataPointBinding valueBinding;
    private double radiusFactor = DEFAULT_RADIUS_FACTOR;
    private double labelOffset = DEFAULT_LABEL_OFFSET;
    private double selectedPointOffsetCache = DEFAULT_SELECTION_OFFSET;
    private double sliceOffset = DEFAULT_SLICE_OFFSET;

    private PieSeriesModel model;
    private ArrayList<LegendItem> legendItems;

    /**
     * Initializes a new instance of the {@link PieSeries} class.
     */
    public PieSeries() {

        this.model = this.model();
        this.sliceStyles = new ArrayList<SliceStyle>();
        this.setClipToPlotArea(false);

        this.legendItems = new ArrayList<LegendItem>();
    }

    /**
     * Gets the data points that are visualized by this instance.
     * The data that needs to be visualized in
     * {@link RadPieChartView} can be passed
     * in one of the following ways: manually or with data binding. With the manual approach
     * is selected then the dataPoints collection can be used to manually add
     * new instances of type {@link PieDataPoint}
     * which represent the data. The other option is to set a collection of data items
     * through the <code>setData</code> method.
     *
     * @return the collection of data points used for the visualization of the data
     * @see #setData(Iterable) to provide the data through data binding
     */
    public ElementCollection<PieDataPoint> dataPoints() {
        return this.model.dataPoints();
    }

    @Override
    public void setData(Iterable value) {
        this.legendItems.clear();
        for (Object dataItem : value) {
            this.legendItems.add(new LegendItem());
        }

        super.setData(value);
    }

    /**
     * Gets the {@link DataPointBinding}
     * object that defines the way the value of a data point is calculated
     * depending on the business object that it represents.
     *
     * @return the value binding definition
     */
    public DataPointBinding getValueBinding() {
        return this.valueBinding;
    }

    /**
     * Sets a {@link DataPointBinding}
     * object that defines the way the value of a data point will be calculated
     * depending on the values of the business object that it will represent.
     *
     * @param value the value binding definition
     */
    public void setValueBinding(DataPointBinding value) {
        this.valueBinding = value;
        ((PieSeriesDataSource) this.dataSource()).setValueBinding(value);
    }

    /**
     * Gets a {@link java.util.List} of
     * {@link SliceStyle} objects
     * that define how the different slices in a pie will be visualized.
     *
     * @return a collection of slice styles
     * @see SliceStyle
     */
    public List<SliceStyle> getSliceStyles() {
        return this.sliceStyles;
    }

    /**
     * Sets a {@link java.util.List} of
     * {@link SliceStyle} objects
     * that define how the different slices in a pie will be visualized.
     *
     * @param value a collection of slice styles
     * @see SliceStyle
     */
    public void setSliceStyles(List<SliceStyle> value) {
        this.sliceStyles = value;
    }

    /**
     * Gets the value of the radius factor.
     * {@link RadPieChartView} calculates the necessary radius in order to consume the whole
     * available space. When this value is calculated, the radius factor is applied.
     * The default value is <code>1.0</code>. This means that the radius will not be changed.
     * If the value is less than <code>1.0</code>, the radius will be decreased, so the
     * whole available space will not be taken.
     * If the value is more than <code>1.0</code>, the radius will be increased, so the
     * chart will not entirely fit in the available space.
     *
     * @return the radius factor
     */
    public double getRadiusFactor() {
        return this.radiusFactor;
    }

    /**
     * Gets the value of the radius factor.
     * {@link RadPieChartView} calculates the necessary radius in order to consume the whole
     * available space. When this value is calculated, the radius factor is applied.
     * The default value is <code>1.0</code>. This means that the radius will not be changed.
     * If the value is less than <code>1.0</code>, the radius will be decreased, so the
     * whole available space will not be taken.
     * If the value is more than <code>1.0</code>, the radius will be increased, so the
     * chart will not entirely fit in the available space.
     *
     * @param value the radius factor, the value must be positive
     */
    public void setRadiusFactor(double value) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    "The specified radius factor is not valid. The value should be positive.");
        }
        this.radiusFactor = value;
    }

    /**
     * Gets the format that is applied to the percent value when creating labels for the
     * data points. The default value is <code>%d %%</code>.
     *
     * @return the format of the labels
     */
    public String getLabelFormat() {
        return this.model.getLabelFormat();
    }

    /**
     * Sets a format that will be applied to the percent value when creating labels for the
     * data points. The default value is <code>%d %%</code>.
     *
     * @param value the format of the labels
     */
    public void setLabelFormat(String value) {
        this.model.setLabelFormat(value);
    }

    /**
     * Gets the offset in pixels that is applied to the labels relative to the arc of the
     * data point.
     * The default value is <code>10.0</code>.
     * The value is negative when the label is positioned out of the pie.
     *
     * @return the offset of the labels
     */
    public double getLabelOffset() {
        return this.labelOffset;
    }

    /**
     * Gets the offset in pixels that is applied to the labels relative to the arc of the
     * data point.
     * The default value is <code>10.0</code>.
     * If the value is negative, the label will be positioned out of the pie.
     *
     * @param value the offset of the labels
     */
    public void setLabelOffset(double value) {
        this.labelOffset = value;
    }

    /**
     * Gets the offset in pixels that is applied to the slices relative to the center of the pie.
     * The default value is <code>2.0</code>.
     * The value can be <code>0.0</code>, if the slices will have no empty space among them.
     * The value can be negative, if the slices are overlapping.
     *
     * @return the offset of the slices
     */
    public double getSliceOffset() {
        return this.sliceOffset;
    }

    /**
     * Gets the offset in pixels that is applied to the slices relative to the center of the pie.
     * The default value is <code>2.0</code>.
     * If the value is <code>0.0</code>, the slices will have no empty space among them.
     * If the value is negative, the slices be overlapping.
     *
     * @param value the offset of the slices
     */
    public void setSliceOffset(double value) {
        this.sliceOffset = value;
    }

    /**
     * Gets the {@link com.telerik.widget.chart.engine.chartAreas.AngleRange} that is
     * used to visualize the data points in this instance.
     * The default value is a range which is starting at <code>0</code> and
     * is <code>360</code> degrees large.
     *
     * @return the angle range used to visualize the data points
     */
    public AngleRange getAngleRange() {
        return this.model.getRange();
    }

    /**
     * Sets the {@link com.telerik.widget.chart.engine.chartAreas.AngleRange} that is
     * used to visualize the data points in this instance.
     * The default value is a range which is starting at <code>0</code> and
     * is <code>360</code> degrees large.
     */
    public void setAngleRange(final AngleRange value) {
        if (this.model.getRange().equals(value)) {
            return;
        }

        this.model.setRange(value);
    }

    @Override
    protected void onUIUpdated() {
        super.onUIUpdated();

        updateLegendItems();
    }

    protected void updateLegendItems() {
        for (int i = 0, len = dataPoints().size(); i < len; i++) {
            PieDataPoint point = dataPoints().get(i);
            LegendItem item = this.legendItems.get(i);

            item.setFillColor(getDataPointColor(point.index()));
            item.setTitle(getLegendTitle(point));
        }
    }

    protected String getLegendTitle(PieDataPoint point) {
        return Double.toString(point.getValue());
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        if (this.isVisibleInLegend)
            for (LegendItem legendItem : this.legendItems)
                getChart().getLegendInfos().add(legendItem);
    }

    @Override
    protected void onIsVisibleInLegendChanged(boolean value) {
        RadChartViewBase chart = this.getChart();

        if (chart != null) {
            if (value) {
                for (LegendItem legendItem : this.legendItems)
                    chart.getLegendInfos().add(legendItem);
            } else {
                for (LegendItem legendItem : this.legendItems)
                    chart.getLegendInfos().remove(legendItem);
            }
        }
    }

    @Override
    public PieSeriesModel model() {
        if (this.model == null) {
            this.model = (PieSeriesModel) this.createModel();
        }

        return this.model;
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.PIE_FAMILY;
    }

    @Override
    public DataPointInfo findClosestPoint(Point location) {
        if (this.dataPoints().size() == 0) {
            return null;
        }

        RadPoint center = this.getChart().chartAreaModel().getPlotArea().getLayoutSlot().getCenter();
        RadPolarCoordinates polarCoordinates = RadMath.getPolarCoordinates(new RadPoint(location.x, location.y), center);

        double tapLocationAngle = polarCoordinates.angle;
        tapLocationAngle = PieSegment.SEGMENT_MAX_ANGLE - tapLocationAngle;

        for (PieDataPoint dataPoint : this.dataPoints()) {
            if (dataPoint.isEmpty) {
                continue;
            }

            double sliceStartAngle = dataPoint.startAngle();
            double sliceEndAnge = sliceStartAngle + dataPoint.sweepAngle();

            if ((sliceStartAngle <= tapLocationAngle) && (tapLocationAngle < sliceEndAnge)) {
                DataPointInfo closestDataPoint = new DataPointInfo();
                closestDataPoint.setDataPoint(dataPoint);
                closestDataPoint.setSeriesModel(this.model());
                Point dataPointLocation = new Point((int) dataPoint.getCenterX(), (int) dataPoint.getCenterY());
                closestDataPoint.setDistanceToTouchLocation(RadMath.getPointDistance(dataPointLocation.x, location.x, dataPointLocation.y, location.y));

                return closestDataPoint;
            }
        }

        return null;
    }

    public int getDataPointColor(int dataPointIndex) {
        return segments.get(dataPointIndex).fillPaint.getColor();
    }

    @Override
    protected void initDataBinding() {
        ((PieSeriesDataSource) this.dataSource()).setValueBinding(this.getValueBinding());
        super.initDataBinding();
    }

    /**
     * Setups the update context. This context will be used to pass update information to
     * the data points in order to be visualized correctly depending on the available size.
     *
     * @param availableSize the size that is available for this pie
     */
    protected void setupUpdateContext(final RadRect availableSize) {

        if (this.updateContext == null) {
            this.updateContext = this.createUpdateContext();
        }

        RadSize updatedAvailableSize = this.getUpdatedSize(new RadSize(availableSize.getWidth(), availableSize.getHeight()));
        this.updateContext.diameter = Math.min(updatedAvailableSize.getWidth(), updatedAvailableSize.getHeight()) * this.getRadiusFactor();
        this.updateContext.radius = this.updateContext.diameter / 2;
        this.updateContext.center = new RadPoint(availableSize.getX() + (availableSize.getWidth() / 2), availableSize.getY() + (availableSize.getHeight() / 2));
        this.updateContext.startAngle = this.model.getRange().getStartAngle();
    }

    /**
     * Creates a new update context that will be setup later in <code>setupUpdateContext</code>.
     * This update context will be used to pass update information to the data points
     * in order to be visualized correctly.
     *
     * @return a new update context
     * @see #setupUpdateContext(com.telerik.android.common.math.RadRect)
     */
    protected PieUpdateContext createUpdateContext() {
        return new PieUpdateContext();
    }

    /**
     * Creates a new segment that holds the information necessary for a slice in the pie.
     *
     * @return a new segment
     */
    protected PieSegment createSegment() {
        return new PieSegment(this);
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);

        RadRect layoutSlot = this.getChart().chartAreaModel().getLayoutSlot();
        RadSize zoom = this.getChart().getZoom();
        double width = layoutSlot.getWidth() * zoom.getWidth();
        double height = layoutSlot.getHeight() * zoom.getHeight();

        this.setupUpdateContext(new RadRect(layoutSlot.getX(), layoutSlot.getY(), width, height));
        this.updateSegments();
    }

    @Override
    protected void drawCore(Canvas canvas) {
        for (PieSegment segment : this.segments) {
            segment.draw(canvas);
        }

        super.drawCore(canvas);
    }

    @Override
    protected ChartSeriesModel createModel() {
        this.model = new PieSeriesModel();
        return this.model;
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new PieSeriesDataSource(this.model());
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new PieSeriesLabelRenderer(this);
    }

    @Override
    public DataPoint hitTestDataPoint(PointF touchLocation) {
        for (PieSegment segment : this.segments) {
            if (segment.hitTest(touchLocation)) {
                return segment.point;
            }
        }

        return null;
    }

    public void setSelectedPointOffset(double value) {
        if(value < 0) {
            throw new IllegalArgumentException("value cannot be less than 0.");
        }

        this.selectedPointOffsetCache = value;
    }

    public double getSelectedPointOffset() {
        return this.selectedPointOffsetCache;
    }

    protected void onDataPointSelectionChanged(DataPoint point) {
        this.isPaletteApplied = false;
        ((PieDataPoint) point).setRelativeOffsetFromCenter(point.getIsSelected() ? this.selectedPointOffsetCache : 0);
        this.updateSegments();
        super.onDataPointSelectionChanged(point);
    }

    private RadSize getUpdatedSize(final RadSize availableSize) {

        double radiusFactor = this.getRadiusFactor();

        // do not apply automatic offset if a radius factor is applied
        double relativeOffsetFromCenter = radiusFactor == 1.0 ? this.model.maxRelativeOffsetFromCenter() : 0;

        double updateRatio = 1 + relativeOffsetFromCenter;
        double width = availableSize.getWidth() / updateRatio;
        double height = availableSize.getHeight() / updateRatio;

        double doublePadding = 2 * ARC_PADDING;
        width -= doublePadding;
        height -= doublePadding;

        return new RadSize(width, height);
    }

    public RadPoint getPointLocation(PieDataPoint point) {
        PieSegment segment = this.findSegment(point);
        if (segment == null) {
            return null;
        }

        return segment.getLocation();
    }

    private void updateSegments() {
        this.segments.clear();
        int index = 0;

        for (PieDataPoint point : this.dataPoints()) {
            PieSegment segment = this.getSegment(point);
            this.updateSegmentStyles(segment, index);
            segment.updatePaths(point, this.updateContext);

            index++;

            LegendItem segmentLegendItem = segment.getLegendItem();
            ObservableCollection<LegendItem> legendInfos = this.getChart().getLegendInfos();
            if (segment.getIsVisibleInLegend() && !legendInfos.contains(segmentLegendItem)) {
                legendInfos.add(segmentLegendItem);
            } else if (legendInfos.contains(segmentLegendItem)) {
                legendInfos.remove(segmentLegendItem);
            }
        }
    }

    private void updateSegmentStyles(PieSegment segment, int index) {
        List<SliceStyle> styles = this.getSliceStyles();
        if (styles != null) {
            int stylesSize = styles.size();
            if (stylesSize > 0) {
                int sliceIndex = index % stylesSize;
                SliceStyle sliceStyle = sliceStyles.get(sliceIndex);
                if (sliceStyle != null) {
                    segment.applySliceStyle(sliceStyle);

                    return;
                }
            }
        }

        PaletteEntry entry = new PaletteEntry();
        ChartPalette palette = this.getPalette();

        if (segment.point.getIsSelected() && this.getChart().getSelectionPalette() != null) {
            palette = this.getChart().getSelectionPalette();
        }

        if (palette != null) {
            entry = palette.getEntry(this.getPaletteFamilyCore(), segment.point.collectionIndex());
            getLabelRenderer().applyPalette(palette);
        }

        if (entry != null) {
            segment.applyPaletteStyle(entry);
        }
    }

    private PieSegment getSegment(PieDataPoint point) {
        PieSegment segment = this.createSegment();
        this.segments.add(segment);
        segment.point = point;

        return segment;
    }

    private PieSegment findSegment(DataPoint point) {
        if (this.segments.size() == 0) {
            return null;
        }

        for (PieSegment segment : this.segments) {
            if (segment.point == point) {
                return segment;
            }
        }

        return null;
    }
}