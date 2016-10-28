package com.telerik.widget.chart.visualization.pieChart;

import android.graphics.Paint;
import android.graphics.Rect;

import com.telerik.android.common.Function;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.chart.engine.series.PieSeriesModel;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Label renderer handling the labels in the {@link PieSeries} instances.
 */
public class PieSeriesLabelRenderer extends BaseLabelRenderer {

    /**
     * Setting this color as fill color will reset the default palette behavior for fill colors.
     */
    public static final int RESET_COLOR = -2;

    /**
     * Creates a new instance of the {@link PieSeriesLabelRenderer} class.
     *
     * @param owner the chart series owning this renderer instance.
     */
    public PieSeriesLabelRenderer(PieSeries owner) {
        super(owner);
        this.labelFillColor = RESET_COLOR;
    }

    @Override
    public void applyPalette(ChartPalette palette) {
        PaletteEntry entry = null;
        if (palette != null) {
            entry = palette.getEntry(PALETTE_FAMILY, owner.getCollectionIndex());
        }

        if (entry != null) {
            this.setValue(LABEL_STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
            this.setValue(LABEL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        }
    }

    @Override
    protected Paint getLabelFillPaint(int dataPointIndex) {
        if (labelFillColor == RESET_COLOR) {
            labelFillPaint.setColor(((PieSeries) owner).getDataPointColor(dataPointIndex));

            return labelFillPaint;
        }

        return super.getLabelFillPaint(dataPointIndex);
    }

    @Override
    protected String getLabelText(DataPoint dataPoint) {
        final PieDataPoint point = (PieDataPoint) dataPoint;

        Function<Object, String> converter = getLabelValueToStringConverter();
        if(converter != null) {
            return converter.apply(point.getValue());
        }

        double labelValue = point.percent();

        String format;
        PieSeriesModel model = ((PieSeries) this.owner).model();
        if (model != null) {
            format = model.getLabelFormat();
        } else {
            format = PieSeriesModel.DEFAULT_LABEL_FORMAT;
        }

        return String.format(format, labelValue);
    }

    @Override
    protected RadPoint calculateLabelPoint(DataPoint point, Rect textSize) {
        PieSeries series = (PieSeries) this.owner;
        PieDataPoint dataPoint = (PieDataPoint) point;

        RadSize labelSize = new RadSize(textSize.width(), textSize.height());
        return this.calculateLabelPointCore(
                labelSize, dataPoint, series.updateContext.radius, series.updateContext.radius * dataPoint.getRelativeOffsetFromCenter(), series.getLabelOffset());
    }

    private RadPoint calculateLabelPointCore(RadSize size, PieDataPoint pieDataPoint, double radius, double offsetFromCenterInPixels, double labelOffset) {
        double middleAngle = pieDataPoint.startAngle() + (pieDataPoint.sweepAngle() / 2);
        middleAngle = middleAngle % PieSegment.SEGMENT_MAX_ANGLE;

        double angleInRad = Math.toRadians(middleAngle);

        double calculatedValueCache = Math.sin(angleInRad);
        double heightCoefficient = calculatedValueCache * calculatedValueCache;

        calculatedValueCache = Math.cos(angleInRad);
        double widthCoefficient = calculatedValueCache * calculatedValueCache;

        double labelDistance = heightCoefficient * (size.halfHeight()) + widthCoefficient * (size.halfWidth());

        double labelRadius;

        if (labelOffset >= 0) {
            labelRadius = Math.max(0, radius - labelOffset - labelDistance);
        } else {
            labelRadius = Math.max(0, radius - labelOffset + labelDistance);
        }

        RadPoint centerWithOffset = ((PieSeries) this.owner).updateContext.getCenterWithOffset(offsetFromCenterInPixels, middleAngle);
        RadPoint labelPoint = RadMath.getArcPoint(middleAngle, centerWithOffset, labelRadius);

        double x = labelPoint.getX() - size.halfWidth();
        double y = labelPoint.getY() + size.halfHeight();

        return new RadPoint(x, y);
    }
}
