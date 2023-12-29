package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.InputStream;

/**
 * Base class for overlay controllers that have a MenuItem association
 */
public abstract class BaseMenuController {
    @FXML
    ImageView imageClose;
    @FXML
    GridPane root;
    @FXML
    Label labelName;
    protected MenuItem menuItem;


    public Node mouseDownNode = null;

    /**
     * Read only access to menuItem
     *
     * @return the MenuItem associated with this controller
     */
    public MenuItem menuItem() {
        return menuItem;
    }

    /**
     * Initialize the controller and set the menuItem
     *
     * @param menuItem the MenuItem associated with this controller
     */
    public void init(MenuItem menuItem) {
        this.menuItem = menuItem;

    }

    /**
     * Abstract method for subclasses to update controls when the window size changes
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    public abstract void updateSize(double width, double height);

    /**
     * Set the base or final price text on the label
     *
     * @param label the label to set the text on
     */
    public void setLabelBasePrice(Label label) {
        //check if this is a base or final price
        double min = Double.MAX_VALUE;
        int j = 0;
        for (int i = 0; i < menuItem.options().size(); i++) {
            double price = menuItem.options().get(i).price();
            if (price < min) min = price;
            j++;
        }
        //if there's more than 1 price it's a base price, eg. From £2.95
        label.setText(String.format("%s£%.2f", j > 1 ? "From " : "", min));
    }

    /**
     * Load the product image to an ImageView
     *
     * @param image The imageview to load the image into
     */
    public void loadImage(ImageView image){
        InputStream stream = App.class.getResourceAsStream("/images/menu/" + menuItem.name() + ".png");
     //   InputStream stream = App.class.getResourceAsStream("/images/Table5.gif");
        if (stream == null) {
            image.setImage(null);
        } else {
            image.setImage(new Image(stream));
        }
    }
}
