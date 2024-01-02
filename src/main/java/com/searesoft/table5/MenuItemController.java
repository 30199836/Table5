package com.searesoft.table5;

import com.searesoft.lib.FXUtils;
import com.searesoft.table5.menu.MenuItem;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.InputStream;

/**
 * Controller for  each MenuITem
 */
public class MenuItemController extends BaseMenuController {
    @FXML
    GridPane root;
    @FXML
    Label labelName;
    @FXML
    Text textDescription;
    @FXML
    Label labelPrice;
    @FXML
    ImageView image;
    @FXML
    ImageView imageMagnify;
    @FXML
    Separator separator;

    //simple linked list to iterate the menuItems
    private MenuItemController next = null;
    private MenuItemController prev = null;

    public MenuItemController prev() {
        return prev;
    }

    public MenuItemController next() {
        return next;
    }
    //

    /**
     * Constructor
     *
     * @param prev the previous menuItem in the list
     */
    MenuItemController(MenuItemController prev) {
        //attach this instance to the end of the linked list
        this.prev = prev;
        if (prev != null) prev.next = this;
    }

    /**
     * Initialize the controller
     *
     * @param menuItem the MenuItem associated with this controller
     */
    public void init(MenuItem menuItem) {
        super.init(menuItem);
        //handle the magnify icon visible on mouse over
        imageMagnify.setOpacity(0);
        imageMagnify.setOnMouseEntered(event -> imageMagnify.setOpacity(0.75));

        imageMagnify.setOnMouseExited(event -> imageMagnify.setOpacity(0));

        //display the name, description and price
        labelName.setText(menuItem.name());
        textDescription.setText(menuItem.description());
        setLabelBasePrice(labelPrice);
        //load the product image if available
        loadImage(image);
    }

    /**
     * Set the description text wrap from the window width
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        if (textDescription == null) return;

        //the container is set to use 60% width so the text wrap width is...
        //(width * 60%) - (image container width + half image width) - margin
        int size = (int) Math.round(((width / 100.0) * 60.0) - (132 + 64.0) - 24);

        // if (size > 300)
        textDescription.setWrappingWidth(size);
        //   labelName.setText(String.valueOf(size));
    }
}

