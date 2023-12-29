package com.searesoft.table5;

import com.searesoft.table5.menu.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Add/Edit overlay dialog
 */
public class CheckoutOverlayController extends BaseMenuController {
    @FXML
    VBox dialogRoot;

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
    VBox vboxOrderRoot;

    @FXML
    VBox vboxPrice;

    @FXML
    VBox vboxProducts;

    @FXML
    VBox vboxQuantity;

    @FXML
    VBox vboxTotal;

    @FXML
    ImageView imageOrder;
    @FXML
    ImageView imagePrice;

    @FXML GridPane gridPanePrint;

    public String OrderID;


    /**
     * Initialize the controller
     *
     * @param order the order associated with the controller
     */
    public void init(Order order) {
        OrderID = String.valueOf(Math.round(100 + (Math.random() * 20)));
        labelOrderID.setText("Order ID: "+OrderID);
        labelTime.setText("Time ordered: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));


//        //display the name, description and price
//        labelName.setText(menuItem.name());
//        textDescription.setText(menuItem.description());
//        setLabelBasePrice(labelBasePrice);
//        //use the plus/minus buttons as a simple spinner for the item count
//        imageMinus.setOnMouseClicked(event -> {
//            int val = Integer.parseInt(labelCount.getText());
//            if (val > 1) {
//                val--;
//                labelCount.setText(String.valueOf(val));
//                updateChoices();
//            }
//        });
//        imagePlus.setOnMouseClicked(event -> {
//            int val = Integer.parseInt(labelCount.getText());
//            if (val < 9) {
//                val++;
//                labelCount.setText(String.valueOf(val));
//                updateChoices();
//            }
//        });
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
        dialogRoot.setPrefHeight(height * 0.75);
    }
}
