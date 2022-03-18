package ariafx.gui.fxml.imp;

import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.css.converters.SizeConverter;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgressSkin extends BehaviorSkinBase<ProgressCircle, BehaviorBase<ProgressCircle>> {

    /***************************************************************************
     *                                                                         *
     * CSS properties                                                          *
     *                                                                         *
     **************************************************************************/

    public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;    /**
     * The colour of the progress segment.
     */
    private final ObjectProperty<Paint> progressColor = new StyleableObjectProperty<Paint>(null) {
        @Override
        protected void invalidated() {
            final Paint value = get();
            if (value != null && !(value instanceof Color)) {
                if (isBound()) {
                    unbind();
                }
                set(null);
                throw new IllegalArgumentException("Only Color objects are supported");
            }
            if (spinner != null) spinner.setFillOverride(value);
            if (determinateIndicator != null) determinateIndicator.setFillOverride(value);
        }

        @Override
        public Object getBean() {
            return ProgressSkin.this;
        }

        @Override
        public String getName() {
            return "progressColorProperty";
        }

        @Override
        public CssMetaData<ProgressIndicator, Paint> getCssMetaData() {
            return PROGRESS_COLOR;
        }
    };
    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final String DONE = ControlResources.getString("ProgressIndicator.doneString");
    /**
     * doneText is just used to know the size of done as that is the biggest text we need to allow for
     */
    private static final Text doneText = new Text(DONE);    /**
     * The number of segments in the spinner.
     */
    private final IntegerProperty indeterminateSegmentCount = new StyleableIntegerProperty(8) {
        @Override
        protected void invalidated() {
            if (spinner != null) spinner.rebuild();
        }

        @Override
        public Object getBean() {
            return ProgressSkin.this;
        }

        @Override
        public String getName() {
            return "indeterminateSegmentCount";
        }

        @Override
        public CssMetaData<ProgressIndicator, Number> getCssMetaData() {
            return INDETERMINATE_SEGMENT_COUNT;
        }
    };
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final CssMetaData<ProgressIndicator, Paint> PROGRESS_COLOR =
            new CssMetaData<ProgressIndicator, Paint>("-fx-progress-color",
                    PaintConverter.getInstance(), null) {

                @Override
                public boolean isSettable(ProgressIndicator n) {
                    final ProgressSkin skin = (ProgressSkin) n.getSkin();
                    return skin.progressColor == null ||
                            !skin.progressColor.isBound();
                }

                @Override
                public StyleableProperty<Paint> getStyleableProperty(ProgressIndicator n) {
                    final ProgressSkin skin = (ProgressSkin) n.getSkin();
                    return (StyleableProperty<Paint>) skin.progressColor;
                }
            };    /**
     * True if the progress indicator should rotate as well as animate opacity.
     */
    private final BooleanProperty spinEnabled = new StyleableBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (spinner != null) spinner.setSpinEnabled(get());
        }

        @Override
        public CssMetaData<ProgressIndicator, Boolean> getCssMetaData() {
            return SPIN_ENABLED;
        }

        @Override
        public Object getBean() {
            return ProgressSkin.this;
        }

        @Override
        public String getName() {
            return "spinEnabled";
        }
    };
    private static final CssMetaData<ProgressIndicator, Number> INDETERMINATE_SEGMENT_COUNT =
            new CssMetaData<ProgressIndicator, Number>("-fx-indeterminate-segment-count",
                    SizeConverter.getInstance(), 8) {

                @Override
                public boolean isSettable(ProgressIndicator n) {
                    final ProgressSkin skin = (ProgressSkin) n.getSkin();
                    return skin.indeterminateSegmentCount == null ||
                            !skin.indeterminateSegmentCount.isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(ProgressIndicator n) {
                    final ProgressSkin skin = (ProgressSkin) n.getSkin();
                    return (StyleableProperty<Number>) skin.indeterminateSegmentCount;
                }
            };
    private static final CssMetaData<ProgressIndicator, Boolean> SPIN_ENABLED =
            new CssMetaData<ProgressIndicator, Boolean>("-fx-spin-enabled", BooleanConverter.getInstance(), Boolean.FALSE) {

                @Override
                public boolean isSettable(ProgressIndicator node) {
                    final ProgressSkin skin = (ProgressSkin) node.getSkin();
                    return skin.spinEnabled == null || !skin.spinEnabled.isBound();
                }

                @Override
                public StyleableProperty<Boolean> getStyleableProperty(ProgressIndicator node) {
                    final ProgressSkin skin = (ProgressSkin) node.getSkin();
                    return (StyleableProperty<Boolean>) skin.spinEnabled;
                }
            };

    static {
        doneText.getStyleClass().add("text");
    }

    static {
        final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
        styleables.add(PROGRESS_COLOR);
        styleables.add(INDETERMINATE_SEGMENT_COUNT);
        styleables.add(SPIN_ENABLED);
        STYLEABLES = Collections.unmodifiableList(styleables);
    }

    /***************************************************************************
     *                                                                         *
     * IndeterminateSpinner                                                    *
     *                                                                         *
     **************************************************************************/

    protected final Duration CLIPPED_DELAY = new Duration(300);
    protected final Duration UNCLIPPED_DELAY = new Duration(0);
    protected Animation indeterminateTransition;
    private IndeterminateSpinner spinner;
    private DeterminateIndicator determinateIndicator;
    private ProgressCircle control;
    private final InvalidationListener progressListener = valueModel -> updateProgress();

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public ProgressSkin(ProgressCircle control) {
        super(control, new BehaviorBase<ProgressCircle>(control, Collections.emptyList()));

        this.control = control;
        this.control.indeterminateProperty().addListener(indeterminateListener);
        this.control.progressProperty().addListener(progressListener);

        initialize();
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return STYLEABLES;
    }

    Paint getProgressColor() {
        return progressColor.get();
    }    /***************************************************************************
     *                                                                         *
     * Listeners                                                               *
     *                                                                         *
     **************************************************************************/

    // Listen to ProgressIndicator indeterminateProperty
    private final InvalidationListener indeterminateListener = valueModel -> initialize();

    /***************************************************************************
     *                                                                         *
     * API (for ProgressBarSkin)                                               *
     *                                                                         *
     **************************************************************************/

    @SuppressWarnings("deprecation")
    protected void initialize() {
        boolean isIndeterminate = control.isIndeterminate();
        if (isIndeterminate) {
            // clean up determinateIndicator
            determinateIndicator = null;

            // create spinner
            spinner = new IndeterminateSpinner(spinEnabled.get(), progressColor.get());
            getChildren().setAll(spinner);
            if (control.impl_isTreeVisible()) {
                if (indeterminateTransition != null) {
                    indeterminateTransition.play();
                }
            }
        } else {
            // clean up after spinner
            if (spinner != null) {
                if (indeterminateTransition != null) {
                    indeterminateTransition.stop();
                }
                spinner = null;
            }

            // create determinateIndicator
            determinateIndicator = new DeterminateIndicator(control, this, progressColor.get());
            getChildren().setAll(determinateIndicator);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (indeterminateTransition != null) {
            indeterminateTransition.stop();
            indeterminateTransition = null;
        }

        if (spinner != null) {
            spinner = null;
        }

        control.indeterminateProperty().removeListener(indeterminateListener);
        control.progressProperty().removeListener(progressListener);
        control = null;
    }    protected final InvalidationListener treeVisibleListener = observable -> {
        @SuppressWarnings("deprecation") final boolean isTreeVisible = getSkinnable().impl_isTreeVisible();
        if (indeterminateTransition != null) {
            pauseTimeline(!isTreeVisible);
        } else if (isTreeVisible) {
            createIndeterminateTimeline();
        }
    };

    protected void updateProgress() {
        if (determinateIndicator != null) {
            determinateIndicator.updateProgress(control.getProgress());
        }
    }

    protected void createIndeterminateTimeline() {
        if (spinner != null) {
            spinner.rebuildTimeline();
        }
    }

    protected void pauseTimeline(boolean pause) {
        if (getSkinnable().isIndeterminate()) {
            if (indeterminateTransition == null) {
                createIndeterminateTimeline();
            }
            if (pause) {
                indeterminateTransition.pause();
            } else {
                indeterminateTransition.play();
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * Layout                                                                  *
     *                                                                         *
     **************************************************************************/

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        if (spinner != null && control.isIndeterminate()) {
            spinner.layoutChildren();
            spinner.resizeRelocate(0, 0, w, h);
        } else if (determinateIndicator != null) {
            determinateIndicator.layoutChildren();
            determinateIndicator.resizeRelocate(0, 0, w, h);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    /***************************************************************************
     *                                                                         *
     * DeterminateIndicator                                                    *
     *                                                                         *
     **************************************************************************/

    private class DeterminateIndicator extends Region {
        DecimalFormat dec = new DecimalFormat("0.000");
        private final double textGap = 2.0F;
        // only update progress text on whole percentages
        private String intProgress;
        // only update pie arc to nearest degree
        private int degProgress;
        private final Text text;
        private final StackPane indicator;
        private final StackPane progress;
        private final StackPane tick;
        private final Arc arcShape;
        private final Circle indicatorCircle;
        private final Circle opacityCircle;

        public DeterminateIndicator(ProgressCircle control, ProgressSkin s, Paint fillOverride) {

            getStyleClass().add("determinate-indicator");

            intProgress = dec.format(control.getProgress() * 100.0);
            degProgress = (int) (360 * control.getProgress());

            getChildren().clear();

            text = new Text((control.getProgress() >= 1) ? (control.getDoneText()) : ("" + intProgress + "%"));
            text.setTextOrigin(VPos.TOP);
            text.getStyleClass().setAll("text", "percentage");

            // The circular background for the progress pie piece
            indicator = new StackPane();
            indicator.setScaleShape(false);
            indicator.setCenterShape(false);
            indicator.getStyleClass().setAll("indicator");
            indicatorCircle = new Circle();
            indicator.setShape(indicatorCircle);

            // The shape for our progress pie piece
            arcShape = new Arc();
            arcShape.setType(ArcType.ROUND);
            arcShape.setStartAngle(90.0F);

            // Our progress pie piece
            progress = new StackPane();
            progress.getStyleClass().setAll("progress");
            progress.setScaleShape(false);
            progress.setCenterShape(false);
            progress.setShape(arcShape);
            progress.getChildren().clear();
            setFillOverride(fillOverride);

            // The check mark that's drawn at 100%
            tick = new StackPane();
            tick.getStyleClass().setAll("tick");


            opacityCircle = new Circle();
            opacityCircle.getStyleClass().setAll("opacity-circle");
            opacityCircle.setFill(Color.web("#fff8dcf0"));
            opacityCircle.setStyle("-fx-stroke: #1aa2faf0; -fx-stroke-width: 0.12em;-fx-stroke-dash-array: 0.22em;");
//            opacityCircle.setOpacity(0.7);

            progress.getChildren().addAll(opacityCircle, tick, text);

            getChildren().setAll(indicator, progress);
            updateProgress(control.getProgress());
        }

        private void setFillOverride(Paint fillOverride) {
            if (fillOverride instanceof Color) {
                Color c = (Color) fillOverride;
                progress.setStyle("-fx-background-color: rgba(" + ((int) (255 * c.getRed())) + "," + ((int) (255 * c.getGreen())) + "," + ((int) (255 * c.getBlue())) + "," + c.getOpacity() + ");");
            } else {
                progress.setStyle(null);
            }
        }

        @Override
        public boolean usesMirroring() {
            // This is used instead of setting NodeOrientation,
            // allowing the Text node to inherit the current
            // orientation.
            return false;
        }

        private void updateProgress(double progress) {
//            intProgress = (int) Math.round(progress * 100.0) ;
            intProgress = dec.format(control.getProgress() * 100.0);
            text.setText((progress >= 1) ? (control.getDoneText()) : ("" + intProgress + "%"));

            degProgress = (int) (360 * progress);
            arcShape.setLength(-degProgress);
            requestLayout();
        }

        @Override
        protected void layoutChildren() {
            // Position and size the circular background
//            double doneTextHeight = doneText.getLayoutBounds().getHeight();
            final double left = control.snappedLeftInset();
            final double right = control.snappedRightInset();
            final double top = control.snappedTopInset();
            final double bottom = control.snappedBottomInset();

            /*
             ** use the min of width, or height, keep it a circle
             */
            final double areaW = control.getWidth() - left - right;
            final double areaH = control.getHeight() - top - bottom - textGap;
            final double radiusW = areaW / 2;
            final double radiusH = areaH / 2;
            final double radius = Math.floor(Math.min(radiusW, radiusH));
            final double centerX = snapPosition(left + radiusW);
            final double centerY = snapPosition(top + radius);

            // find radius that fits inside radius - insetsPadding
            final double iLeft = indicator.snappedLeftInset();
            final double iRight = indicator.snappedRightInset();
            final double iTop = indicator.snappedTopInset();
            final double iBottom = indicator.snappedBottomInset();
            final double progressRadius = snapSize(Math.min(
                    Math.min(radius - iLeft, radius - iRight),
                    Math.min(radius - iTop, radius - iBottom)));

            indicatorCircle.setRadius(radius);

            opacityCircle.setRadius(radius - 12);

            indicator.setLayoutX(centerX);
            indicator.setLayoutY(centerY);


            arcShape.setRadiusX(progressRadius);
            arcShape.setRadiusY(progressRadius);
            progress.setLayoutX(centerX);
            progress.setLayoutY(centerY);


            // find size of spare box that fits inside indicator radius

            tick.resize(progress.getWidth(), progress.getHeight());
            tick.setVisible(control.getProgress() >= 1);


            // if the % text can't fit anywhere in the bounds then don't display it
            double textWidth = text.getLayoutBounds().getWidth();
            double textHeight = text.getLayoutBounds().getHeight();
            if (control.getWidth() >= textWidth && control.getHeight() >= textHeight) {
                if (!text.isVisible()) text.setVisible(true);
            } else {
                if (text.isVisible()) text.setVisible(false);
            }
        }

        @Override
        protected double computePrefWidth(double height) {
            final double left = control.snappedLeftInset();
            final double right = control.snappedRightInset();
            final double iLeft = indicator.snappedLeftInset();
            final double iRight = indicator.snappedRightInset();
            final double iTop = indicator.snappedTopInset();
            final double iBottom = indicator.snappedBottomInset();
            final double indicatorMax = snapSize(Math.max(Math.max(iLeft, iRight), Math.max(iTop, iBottom)));
            final double pLeft = progress.snappedLeftInset();
            final double pRight = progress.snappedRightInset();
            final double pTop = progress.snappedTopInset();
            final double pBottom = progress.snappedBottomInset();
            final double progressMax = snapSize(Math.max(Math.max(pLeft, pRight), Math.max(pTop, pBottom)));
            final double indicatorWidth = indicatorMax + progressMax + progressMax + indicatorMax;
            return left + indicatorWidth + right;
        }

        @Override
        protected double computePrefHeight(double width) {
            final double top = control.snappedTopInset();
            final double bottom = control.snappedBottomInset();
            final double iLeft = indicator.snappedLeftInset();
            final double iRight = indicator.snappedRightInset();
            final double iTop = indicator.snappedTopInset();
            final double iBottom = indicator.snappedBottomInset();
            final double indicatorMax = snapSize(Math.max(Math.max(iLeft, iRight), Math.max(iTop, iBottom)));
            final double pLeft = progress.snappedLeftInset();
            final double pRight = progress.snappedRightInset();
            final double pTop = progress.snappedTopInset();
            final double pBottom = progress.snappedBottomInset();
            final double progressMax = snapSize(Math.max(Math.max(pLeft, pRight), Math.max(pTop, pBottom)));
            final double indicatorHeight = indicatorMax + progressMax + progressMax + indicatorMax;
            return top + indicatorHeight + bottom;
        }

        @Override
        protected double computeMaxWidth(double height) {
            return computePrefWidth(height);
        }

        @Override
        protected double computeMaxHeight(double width) {
            return computePrefHeight(width);
        }
    }

    private final class IndeterminateSpinner extends Region {
        private final List<Double> opacities = new ArrayList<>();
        private final IndicatorPaths pathsG;
        private boolean spinEnabled = false;
        private Paint fillOverride = null;

        @SuppressWarnings("deprecation")
        private IndeterminateSpinner(boolean spinEnabled, Paint fillOverride) {
            // does not need to be a weak listener since it only listens to its own property
            impl_treeVisibleProperty().addListener(treeVisibleListener);
            this.spinEnabled = spinEnabled;
            this.fillOverride = fillOverride;

            setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            getStyleClass().setAll("spinner");

            pathsG = new IndicatorPaths();
            getChildren().add(pathsG);
            rebuild();

            rebuildTimeline();

        }

        public void setFillOverride(Paint fillOverride) {
            this.fillOverride = fillOverride;
            rebuild();
        }

        public void setSpinEnabled(boolean spinEnabled) {
            this.spinEnabled = spinEnabled;
            rebuildTimeline();
        }

        private void rebuildTimeline() {
            if (spinEnabled) {
                if (indeterminateTransition == null) {
                    indeterminateTransition = new Timeline();
                    indeterminateTransition.setCycleCount(Timeline.INDEFINITE);
                    indeterminateTransition.setDelay(UNCLIPPED_DELAY);
                } else {
                    indeterminateTransition.stop();
                    ((Timeline) indeterminateTransition).getKeyFrames().clear();
                }
                final ObservableList<KeyFrame> keyFrames = FXCollections.observableArrayList();

                keyFrames.add(new KeyFrame(Duration.millis(1), new KeyValue(pathsG.rotateProperty(), 360)));
                keyFrames.add(new KeyFrame(Duration.millis(3900), new KeyValue(pathsG.rotateProperty(), 0)));

                for (int i = 100; i <= 3900; i += 100) {
                    keyFrames.add(new KeyFrame(Duration.millis(i), event -> shiftColors()));
                }

                ((Timeline) indeterminateTransition).getKeyFrames().setAll(keyFrames);
                indeterminateTransition.playFromStart();
            } else {
                if (indeterminateTransition != null) {
                    indeterminateTransition.stop();
                    ((Timeline) indeterminateTransition).getKeyFrames().clear();
                    indeterminateTransition = null;
                }
            }
        }

        @Override
        protected void layoutChildren() {
            final double w = control.getWidth() - control.snappedLeftInset() - control.snappedRightInset();
            final double h = control.getHeight() - control.snappedTopInset() - control.snappedBottomInset();
            final double prefW = pathsG.prefWidth(-1);
            final double prefH = pathsG.prefHeight(-1);
            double scaleX = w / prefW;
            double scale = scaleX;
            if ((scaleX * prefH) > h) {
                scale = h / prefH;
            }
            double indicatorW = prefW * scale;
            double indicatorH = prefH * scale;
            pathsG.resizeRelocate((w - indicatorW) / 2, (h - indicatorH) / 2, indicatorW, indicatorH);
        }

        private void rebuild() {
            // update indeterminate indicator
            final int segments = indeterminateSegmentCount.get();
            opacities.clear();
            pathsG.getChildren().clear();
            final double step = 0.8 / (segments - 1);
            for (int i = 0; i < segments; i++) {
                Region region = new Region();
                region.setScaleShape(false);
                region.setCenterShape(false);
                region.getStyleClass().addAll("segment", "segment" + i);
                if (fillOverride instanceof Color) {
                    Color c = (Color) fillOverride;
                    region.setStyle("-fx-background-color: rgba(" + ((int) (255 * c.getRed())) + "," + ((int) (255 * c.getGreen())) + "," + ((int) (255 * c.getBlue())) + "," + c.getOpacity() + ");");
                } else {
                    region.setStyle(null);
                }
                pathsG.getChildren().add(region);
                opacities.add(Math.max(0.1, (1.0 - (step * i))));
            }
        }

        private void shiftColors() {
            if (opacities.size() <= 0) return;
            final int segments = indeterminateSegmentCount.get();
            Collections.rotate(opacities, -1);
            for (int i = 0; i < segments; i++) {
                pathsG.getChildren().get(i).setOpacity(opacities.get(i));
            }
        }

        private class IndicatorPaths extends Pane {
            @Override
            protected double computePrefWidth(double height) {
                double w = 0;
                for (Node child : getChildren()) {
                    if (child instanceof Region) {
                        Region region = (Region) child;
                        if (region.getShape() != null) {
                            w = Math.max(w, region.getShape().getLayoutBounds().getMaxX());
                        } else {
                            w = Math.max(w, region.prefWidth(height));
                        }
                    }
                }
                return w;
            }

            @Override
            protected double computePrefHeight(double width) {
                double h = 0;
                for (Node child : getChildren()) {
                    if (child instanceof Region) {
                        Region region = (Region) child;
                        if (region.getShape() != null) {
                            h = Math.max(h, region.getShape().getLayoutBounds().getMaxY());
                        } else {
                            h = Math.max(h, region.prefHeight(width));
                        }
                    }
                }
                return h;
            }

            @Override
            protected void layoutChildren() {
                // calculate scale
                double scale = getWidth() / computePrefWidth(-1);
                for (Node child : getChildren()) {
                    if (child instanceof Region) {
                        Region region = (Region) child;
                        if (region.getShape() != null) {
                            region.resize(
                                    region.getShape().getLayoutBounds().getMaxX(),
                                    region.getShape().getLayoutBounds().getMaxY()
                            );
                            region.getTransforms().setAll(new Scale(scale, scale, 0, 0));
                        } else {
                            region.autosize();
                        }
                    }
                }
            }
        }
    }










}
