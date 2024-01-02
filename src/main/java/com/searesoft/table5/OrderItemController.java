package com.searesoft.table5;

import com.searesoft.table5.menu.Order;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Controller for each Order Item
 */
public class OrderItemController {
    @FXML
    Label labelCount;
    @FXML
    Label labelName;
    @FXML
    Label labelPrice;
    @FXML
    GridPane root;
    @FXML
    Separator separator;
    @FXML
    VBox vboxOrderRoot;
    @FXML
    GridPane gridPaneSeparator;
    @FXML
    GridPane gridPaneDetails;
    private Order.Item orderItem;

    public Order.Item orderItem() {
        return orderItem;
    }

    private boolean separatorHidden;

    /**
     * Initialize the controller
     *
     * @param item the order item associated with this controller
     */
    public void init(Order.Item item) {
        gridPaneDetails.managedProperty().bind(gridPaneDetails.visibleProperty());
        gridPaneSeparator.managedProperty().bind(gridPaneSeparator.visibleProperty());

        this.orderItem = item;
        //display the basic info, name, price and count
        labelName.setText(item.name);
        labelPrice.setText(String.format("£%.2f", item.price));
        labelCount.setText(String.format("%dx", item.count));

        //remove any existing option and choices (if the item was edited)
        for (int i = vboxOrderRoot.getChildren().size() - 1; i > 1; i--) {
            vboxOrderRoot.getChildren().remove(i);
        }

        //don't show the separator at the top
        separatorHidden = item.order().items.size() == 1;
        if (separatorHidden) {
            gridPaneSeparator.setVisible(false);

           //vboxOrderRoot.getChildren().remove(gridPaneSeparator);
            //  separator.setOpacity(0);
        }

        Label label;

        //display the selected option
        if (!item.option.equals("default")) {
            label = new Label();
            vboxOrderRoot.getChildren().add(label);
            label.setText("Option");
            label.setPadding(new Insets(0, 0, 0, 8));
            label.setFont(Font.font(12));

            label = new Label();
            vboxOrderRoot.getChildren().add(label);
            label.setText(item.option);
            label.setPadding(new Insets(0, 0, 0, 32));
            label.setFont(Font.font(14));
        }

        //display the selected choices
        for (Order.Choice choice : item.choices) {
            label = new Label();
            vboxOrderRoot.getChildren().add(label);
            label.setText(choice.name);
            label.setPadding(new Insets(0, 0, 0, 8));
            label.setFont(Font.font(12));
            for (String choiceItem : choice.items) {
                label = new Label();
                vboxOrderRoot.getChildren().add(label);
                label.setText(choiceItem);
                label.setPadding(new Insets(0, 0, 0, 32));
                label.setFont(Font.font(14));
            }
        }
    }

    /**
     * Show the details pane
     */
    public void showDetails(){
        gridPaneDetails.setVisible(true);
        if (!separatorHidden) gridPaneSeparator.setVisible(true);
    }

    /**
     * Hide the details pane when taking a snapshot
     */
    public void hideDetails(){
        gridPaneDetails.setVisible(false);
        if (!separatorHidden) gridPaneSeparator.setVisible(false);
    }
}
