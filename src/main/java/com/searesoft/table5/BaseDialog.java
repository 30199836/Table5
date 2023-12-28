package com.searesoft.table5;

import javafx.stage.Stage;

/**
 * Base class for dialogs
 */
public abstract class BaseDialog {
    public Stage stage = null;

    /**
     * Constructor
     *
     * @param stage The stage associated with this dialog
     */
    BaseDialog(Stage stage) {
        this.stage = stage;
    }

    /**
     * Abstract init method
     */
    public abstract void init();

    /**
     * Center a child dialog on a parent
     * Must be called after the dialog is visible
     *
     * @param child  The child to center
     * @param parent The patent to center the child on
     */
    public static void centerChild(BaseDialog child, BaseDialog parent) {
        double x = parent.stage.getX() + parent.stage.getWidth() * 0.5 - child.stage.getWidth() * 0.5;
        double y = parent.stage.getY() + parent.stage.getHeight() * 0.5 - child.stage.getHeight() * 0.5;
        child.stage.setX(x > 0 ? x : 0);
        child.stage.setY(y > 0 ? y : 0);
    }

    /**
     * Center a child dialog on a parent
     *
     * @param child  The child to center
     * @param parent  The patent to center the child on
     * @param width The known child dialog width
     * @param height The known child dialog height
     */
    public static void centerChild(BaseDialog child, BaseDialog parent,int width,int height) {
        double x = parent.stage.getX() + parent.stage.getWidth() * 0.5 - width * 0.5;
        double y = parent.stage.getY() + parent.stage.getHeight() * 0.5 - height * 0.5;
        child.stage.setX(x > 0 ? x : 0);
        child.stage.setY(y > 0 ? y : 0);
    }
}
