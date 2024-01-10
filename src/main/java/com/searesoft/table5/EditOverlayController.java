package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import com.searesoft.table5.menu.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller for the Edit overlay dialog
 */
public class EditOverlayController extends BaseMenuController {
    @FXML
    GridPane root;
    @FXML
    VBox vBoxDialogRoot;
    @FXML
    Label labelPrice;
    @FXML
    ImageView imagePlus;
    @FXML
    ImageView imageMinus;
    @FXML
    ImageView imageRemove;
    @FXML
    ImageView imageOrder;
    @FXML
    VBox vBoxImage;
    @FXML
    Label labelCount;
    @FXML
    GridPane gridPaneUpdate;
    @FXML
    GridPane gridPaneDetails;

    @FXML
    Text textDescription;
    @FXML
    Label labelBasePrice;

    Order.Item orderItem;
    OrderItemController orderItemController;

    /**
     * I don't know how to hide this from the child class so just don't use it!!
     *
     * @param menuItem the MenuItem associated with this controller
     */
    @Override
    public void init(MenuItem menuItem) {
        throw new RuntimeException("Don't use this!!");
    }

    /**
     * Initialize the controller
     *
     * @param orderItemController the order item associated with this controller
     */
    public void init(OrderItemController orderItemController) {
        this.orderItemController = orderItemController;
        this.orderItem = orderItemController.orderItem();
        this.menuItem = App.menu.menuItemFromName(orderItem.name);

        //display the name, description and price
        labelName.setText(menuItem.name());
        textDescription.setText(menuItem.description());

        //display the price per item
        labelBasePrice.setText(String.format("£%.2f", orderItem.price / orderItem.count));

        labelCount.setText(String.valueOf(orderItem.count));

        vBoxImage.managedProperty().bind(vBoxImage.visibleProperty());

        updatePrice();

        //use the plus/minus buttons as a simple spinner for the item count
        imageMinus.setOnMouseClicked(event -> {
            int val = Integer.parseInt(labelCount.getText());
            if (val > 1) {
                val--;
                labelCount.setText(String.valueOf(val));
                updatePrice();
            }
        });
        imagePlus.setOnMouseClicked(event -> {
            int val = Integer.parseInt(labelCount.getText());
            if (val < 9) {
                val++;
                labelCount.setText(String.valueOf(val));
                updatePrice();
            }
        });
        //the bin/remove button sets the count to zero
        imageRemove.setOnMouseClicked(event -> {
            labelCount.setText("0");
            updatePrice();
        });
    }

    /**
     * Update the price from the order item and new count
     */
    public void updatePrice() {
        double price = orderItem.price / orderItem.count * Integer.parseInt(labelCount.getText());
        labelPrice.setText(String.format("£%.2f", price));
    }

    /**
     * Update the dialog size
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        //    vBoxDialogRoot.setPrefHeight(height * 0.75);
    }
}
