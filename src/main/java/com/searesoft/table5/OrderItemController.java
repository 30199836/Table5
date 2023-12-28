package com.searesoft.table5;

import com.searesoft.table5.menu.MenuOption;
import com.searesoft.table5.menu.Order;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Controller that handles an item in the order menu
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
    Order.Item item;

    /**
     * Initialize the controller
     *
     * @param item the item associated with this controller
     */
    public void init(Order.Item item) {
        this.item = item;
        //display the basic info, name, price and count
        labelName.setText(item.name);
        labelPrice.setText(String.format("£%.2f", item.price));
        labelCount.setText(String.format("%dx", item.count));

        //remove any exisiting option and choices (if the item was edited)
        for (int i = vboxOrderRoot.getChildren().size() - 1; i > 1; i--) {
            vboxOrderRoot.getChildren().remove(i);
        }

        //don't show the separator at the top
        if (item.order().items.size() == 1) {
            separator.setPrefHeight(0);
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
}
