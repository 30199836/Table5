package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * Controller for the product image overlay dialog
 */
public class ImageOverlayController extends BaseMenuController {
    @FXML
    ImageView image;

    /**
     * Initialize the controller
     *
     * @param menuItem the MenuItem associated with this controller
     */
    @Override
    public void init(MenuItem menuItem) {
        super.init(menuItem);
        //display the name
        labelName.setText(menuItem.name());
        //load the product image if available
        loadImage(image);
    }

    /**
     * Update the dialog size
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        double size = Math.round(Math.min(width, height) * 0.75);
        image.setFitWidth(size);
        image.setFitHeight(size);
    }
}
