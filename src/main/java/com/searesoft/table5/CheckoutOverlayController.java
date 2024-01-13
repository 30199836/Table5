package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import com.searesoft.table5.menu.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the checkout overlay dialog
 */
public class CheckoutOverlayController extends BaseMenuController {
    @FXML
    VBox vBoxDialogRoot;

    @FXML
    ImageView imageClose;

    @FXML
    Label labelOrderID;

    @FXML
    Label labelTime;

    @FXML
    GridPane root;

    @FXML
    ScrollPane scrollPaneOrder;
    @FXML
    VBox vBoxOrderRoot;
    @FXML
    VBox vBoxDetails;
    @FXML
    VBox vBoxLogo;

    @FXML
    ImageView imageOrder;
    @FXML
    ImageView imagePrice;
    @FXML
    GridPane gridPaneConfirm;

    private String orderID;

    public String orderID() {
        return orderID;
    }

    private Order order;

    /**
     * I don't know how to hide this from the child class so just don't use it!!
     *
     * @param menuItem not used, should be null
     */
    @Override
    public void init(MenuItem menuItem) {
        throw new RuntimeException("Don't use this!!");
    }

    /**
     * Initialize the controller
     *
     * @param order the order associated with the controller
     */
    public void init(Order order) {
        this.order = order;
        vBoxLogo.managedProperty().bind(vBoxLogo.visibleProperty());
        vBoxLogo.setVisible(false);
        vBoxDetails.managedProperty().bind(vBoxDetails.visibleProperty());
        vBoxDetails.setVisible(false);
    }

    /**
     * Update the orderID and details labels
     */
    public void updateDetails() {
        orderID = String.valueOf(order.nextID());
        labelOrderID.setText("Order ID: " + orderID);
        labelTime.setText("Time ordered: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }
    /**
     * Hide the details gui controls when displaying the receipt
     */
    public void hideDetails() {
        vBoxLogo.setVisible(false);
        vBoxDetails.setVisible(false);
    }
    /**
     * Show the details gui controls when printing the receipt
     */
    public void showDetails() {
        vBoxLogo.setVisible(true);
        vBoxDetails.setVisible(true);
    }
    /**
     * Update the dialog size
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        // dialogRoot.setPrefWidth(width * 0.75);

        vBoxDialogRoot.setPrefHeight(height * 0.75);
    }
}
