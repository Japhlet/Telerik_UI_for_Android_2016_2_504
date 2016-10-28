//package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;
//
//import android.content.Context;
//
//import com.telerik.widget.chart.engine.series.rangeSeries.RangeSeriesBaseModel;
//import com.telerik.widget.chart.visualization.common.FilledSeries;
//import com.telerik.widget.chart.visualization.common.StrokedSeries;
//import com.telerik.widget.palettes.ChartPalette;
//
///// <summary>
///// Represents a chart range area series.
///// </summary>
//public class RangeSeries extends RangeSeriesBase implements FilledSeries, StrokedSeries {
//
//    //private RangeRenderer rangeRenderer;
//
//    protected RangeSeries(Context context) {
//        super(context);
//    }
////
////        /// <summary>
////        /// Initializes a new instance of the <see cref="RangeSeries" /> class.
////        /// </summary>
////        public RangeSeries()
////        {
////            this.rangeRenderer = new RangeRenderer();
////            this.rangeRenderer.model = this.Model;
////        }
////
//    /// <summary>
//    /// Gets or sets the style used to draw the <see cref="Polyline"/> shape.
//    /// </summary>
//    @Override
//    public int getFillColor() {
//        return 0;
//    }
//
//    public void setFillColor(int value) {
//    }
//
//    public int getStrokeColor(){
//        return 0;
//    }
//
//    public void setStrokeColor(int value){
//
//    }
//
//    @Override
//    protected int getLegendFillColor(){
//        return this.getFillColor();
//    }
//
//    @Override
//    protected int getLegendStrokeColor(){
//        return this.getStrokeColor();
//    }
//
//    //
////        /// <summary>
////        /// Gets a value indicating whether the <see cref="Fill"/> property has been set locally.
////        /// </summary>
////        /// <value></value>
////        bool FilledSeries.IsFillSetLocally
////        {
////            get
////            {
////                return this.IsLocalValueSet(FillProperty);
////            }
////        }
////
////        /// <summary>
////        /// Gets a value indicating whether the <see cref="Stroke"/> property has been set locally.
////        /// </summary>
////        /// <value></value>
////        bool StrokedSeries.IsStrokeSetLocally
////        {
////            get
////            {
////                return this.IsLocalValueSet(StrokeProperty);
////            }
////        }
////
////        /// <summary>
////        /// Gets or setst the <see cref="Brush"/> instance that defines the stroke of the area shape.
////        /// </summary>
////        public Brush Stroke
////        {
////            get
////            {
////                return this.getValue(StrokeProperty) as Brush;
////            }
////            set
////            {
////                this.setValue(StrokeProperty, value);
////            }
////        }
////
////        /// <summary>
////        /// Gets or sets the thickness of the line used to present the series.
////        /// </summary>
////        public double StrokeThickness
////        {
////            get
////            {
////                return this.rangeRenderer.strokeShape.StrokeThickness;
////            }
////            set
////            {
////                this.setValue(StrokeThicknessProperty, value);
////            }
////        }
////
////        /// <summary>
////        /// Gets or sets the style applied to the stroke shape.
////        /// </summary>
////        public Style StrokeShapeStyle
////        {
////            get
////            {
////                return (Style)this.getValue(StrokeShapeStyleProperty);
////            }
////            set
////            {
////                this.setValue(StrokeShapeStyleProperty, value);
////            }
////        }
////
////        /// <summary>
////        /// Gets or sets the dash pattern to be applied to the <see cref="Polyline"/> shape used to render the series.
////        /// </summary>
////        public DoubleCollection dashArray
////        {
////            get
////            {
////                return this.getValue(DashArrayProperty) as DoubleCollection;
////            }
////            set
////            {
////                this.setValue(DashArrayProperty, value);
////            }
////        }
////
////        /// <summary>
////        /// Gets or sets the mode that defines how the area is stroked.
////        /// </summary>
////        public RangeSeriesStrokeMode StrokeMode
////        {
////            get
////            {
////                return this.rangeRenderer.strokeMode;
////            }
////            set
////            {
////                if (this.rangeRenderer.strokeMode == value)
////                {
////                    return;
////                }
////
////                this.rangeRenderer.strokeMode = value;
////                this.requestRender();
////            }
////        }
////
////        /// <summary>
////        /// Gets or sets the style applied to the area shape.
////        /// </summary>
////        public Style AreaShapeStyle
////        {
////            get
////            {
////                return (Style)this.getValue(AreaShapeStyleProperty);
////            }
////            set
////            {
////                this.setValue(AreaShapeStyleProperty, value);
////            }
////        }
////
//    @Override
//    public String paletteFamily() {
//        return ChartPalette.AREA_FAMILY;
//    }
//
//    //
////        /// <summary>
////        /// When overridden in a derived class, is invoked whenever application code or internal processes (such as a rebuilding layout pass) call <see cref="M:System.Windows.Controls.Control.ApplyTemplate"/>. In simplest terms, this means the method is called just before a UI element displays in an application. For more information, see Remarks.
////        /// </summary>
////        public override void onApplyTemplate()
////        {
////            base.onApplyTemplate();
////
////            this.renderSurface.Children.ADD(this.rangeRenderer.strokeShape.DisconnectIfChildOfAnotherCanvas());
////            this.renderSurface.Children.ADD(this.rangeRenderer.areaShape.DisconnectIfChildOfAnotherCanvas());
////
////            // area is below the stroke
////            Canvas.SetZIndex(this.rangeRenderer.areaShape, -1);
////        }
////
//    @Override
//    protected RangeSeriesBaseModel createModel() {
//        return new RangeSeriesBaseModel();
//    }
////
////        internal override void applyPaletteCore()
////        {
////            base.applyPaletteCore();
////
////            this.rangeRenderer.applyPalette();
////        }
////
////        internal override void updateUICore(ChartLayoutContext context)
////        {
////            base.updateUICore(context);
////
////            this.rangeRenderer.layoutContext = context;
////            this.rangeRenderer.render();
////        }
////
////        private static void OnFillChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
////        {
////            RangeSeries series = d as RangeSeries;
////            series.rangeRenderer.areaShape.Fill = e.NewValue as Brush;
////
////            if (series.isPaletteApplied)
////            {
////                series.updatePalette(true);
////            }
////        }
////
////        private static void OnStrokeChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
////        {
////            RangeSeries series = d as RangeSeries;
////            series.rangeRenderer.strokeShape.Stroke = e.NewValue as Brush;
////
////            if (series.isPaletteApplied)
////            {
////                series.updatePalette(true);
////            }
////        }
////
////        private static void OnStrokeThicknessChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
////        {
////            RangeSeries series = d as RangeSeries;
////            series.rangeRenderer.strokeShape.StrokeThickness = (double)e.NewValue;
////        }
////
////        private static void OnStrokeShapeStylePropertyChanged(DependencyObject target, DependencyPropertyChangedEventArgs args)
////        {
////            RangeSeries series = target as RangeSeries;
////            series.rangeRenderer.strokeShape.Style = (Style)args.NewValue;
////        }
////
////        private static void OnDashArrayChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
////        {
////            RangeSeries series = d as RangeSeries;
////            ApplyDashArray(series.rangeRenderer.strokeShape, e.NewValue as DoubleCollection);
////        }
////
////        private static void OnAreaShapeStylePropertyChanged(DependencyObject target, DependencyPropertyChangedEventArgs args)
////        {
////            RangeSeries series = target as RangeSeries;
////            series.rangeRenderer.areaShape.Style = (Style)args.NewValue;
////        }
//}

