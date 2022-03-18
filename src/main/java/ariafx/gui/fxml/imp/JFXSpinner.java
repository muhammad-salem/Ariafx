/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package ariafx.gui.fxml.imp;

import com.sun.javafx.css.converters.SizeConverter;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXSpinner is the material design implementation of a loading spinner.
 *
 * @author Bashir Elias & Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXSpinner extends ProgressIndicator {

    public static final double INDETERMINATE_PROGRESS = -1;
    /**
     * Initialize the style class to 'jfx-spinner'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-spinner";
    /**
     * specifies the radius of the spinner node, by default it's set to -1 (USE_COMPUTED_SIZE)
     */
    private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
            StyleableProperties.RADIUS,
            JFXSpinner.this,
            "radius",
            Region.USE_COMPUTED_SIZE
    );
    /**
     * specifies from which angle the spinner should start spinning
     */
    private final StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(
            StyleableProperties.STARTING_ANGLE,
            JFXSpinner.this,
            "starting_angle",
            360 - Math.random() * 720
    );

    public JFXSpinner() {
        this(INDETERMINATE_PROGRESS);
    }

    public JFXSpinner(double progress) {
        super(progress);
        init();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }

    private void init() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXSpinnerSkin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("jfx-spinner.css").toExternalForm();
    }

    public final StyleableDoubleProperty radiusProperty() {
        return this.radius;
    }

    public final double getRadius() {
        return this.radiusProperty().get();
    }

    public final void setRadius(final double radius) {
        this.radiusProperty().set(radius);
    }

    public final StyleableDoubleProperty startingAngleProperty() {
        return this.startingAngle;
    }

    public final double getStartingAngle() {
        return this.startingAngleProperty().get();
    }

    public final void setStartingAngle(final double startingAngle) {
        this.startingAngleProperty().set(startingAngle);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXSpinner, Number> RADIUS =
                new CssMetaData<JFXSpinner, Number>("-jfx-radius",
                        SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
                    @Override
                    public boolean isSettable(JFXSpinner control) {
                        return control.radius == null || !control.radius.isBound();
                    }

                    @Override
                    public StyleableDoubleProperty getStyleableProperty(JFXSpinner control) {
                        return control.radius;
                    }
                };

        private static final CssMetaData<JFXSpinner, Number> STARTING_ANGLE =
                new CssMetaData<JFXSpinner, Number>("-jfx-starting-angle", SizeConverter.getInstance(), 360 - Math.random() * 720) {
                    @Override
                    public boolean isSettable(JFXSpinner control) {
                        return control.startingAngle == null || !control.startingAngle.isBound();
                    }

                    @Override
                    public StyleableDoubleProperty getStyleableProperty(JFXSpinner control) {
                        return control.startingAngle;
                    }
                };


        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(ProgressIndicator.getClassCssMetaData());
            Collections.addAll(
                    styleables,
                    RADIUS,
                    STARTING_ANGLE
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }


}
