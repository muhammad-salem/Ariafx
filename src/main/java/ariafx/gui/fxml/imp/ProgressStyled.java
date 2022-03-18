package ariafx.gui.fxml.imp;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ProgressBar;


public class ProgressStyled extends ProgressBar {

    ChangeListener<Number> listener = (e, l, v) -> {
        if (Double.isNaN((double) v)) {
            return;
        }
        double value = v == null ? 0 : v.doubleValue();
        if (value > 0.2 && value < 0.4) {
            setStyleClass(StyleProgress.Yellow);
        } else if (value > 0.4 && value < 0.6) {
            setStyleClass(StyleProgress.Light_Green);
        } else if (value > 0.6 && value < 0.8) {
            setStyleClass(StyleProgress.Amber);
        } else if (value > 0.8 && value < 1.0) {
            setStyleClass(StyleProgress.Orange);
        }
    };

    public ProgressStyled(double progress) {
        super(progress);
        getStylesheets().add(getClass().getResource("progressPar.css").toExternalForm());
    }

    public ProgressStyled() {
        this(INDETERMINATE_PROGRESS);
    }

    public ProgressStyled(StyleProgress styleProgress) {
        this(INDETERMINATE_PROGRESS);
        setStyleClass(styleProgress);
    }

    public ProgressStyled(double progress, StyleProgress styleProgress) {
        this(progress);
        setStyleClass(styleProgress);
    }

    public static ProgressBar CreateProgressFlat() {
        return CreateProgressFlat(INDETERMINATE_PROGRESS);
    }

    public static ProgressBar CreateProgressFlat(double progress) {
        ProgressBar styled = new ProgressBar(progress);
        styled.getStylesheets().add(ProgressStyled.class.getResource("FlatProgress.css").toExternalForm());
        return styled;
    }

    public static ProgressBar CreateProgressFlat(StyleProgress styleProgress) {
        ProgressBar styled = CreateProgressFlat();
        styled.getStyleClass().removeAll(StyleProgress.Styles);
        styled.getStyleClass().add(styleProgress.toString());
        return styled;
    }

    public static ProgressBar CreateProgressFlat(double progress, StyleProgress styleProgress) {
        ProgressBar styled = CreateProgressFlat(styleProgress);
        styled.setProgress(progress);
        return styled;
    }

    public void addStyleListener() {
        progressProperty().addListener(listener);
    }

    public void removeStyleListener() {
        progressProperty().removeListener(listener);
    }

    public void setStyleClass(StyleProgress style) {
        getStyleClass().removeAll(StyleProgress.Styles);
        getStyleClass().add(style.toString());
    }

    public enum StyleProgress {
        DEFAULT,
        PRIMARY,
        STRIPED,
        DANGER,
        SUCCESS,
        INFO,
        WARNING,
        Red,
        Pink,
        Purple,
        Deep_purple,
        Indigo,
        Blue,
        Light_blue,
        Cyan,
        Teal,
        Green,
        Light_Green,
        Lime,
        Yellow,
        Amber,
        Orange,
        Deep_Orange,
        Brown,
        Grey,
        Blue_Grey;

        public static String[] Styles;

        static {
            Styles = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                Styles[i] = values()[i].toString();
            }
        }

        @Override

        public String toString() {
            return "bar-" + this.name().toLowerCase();
        }

    }

}









