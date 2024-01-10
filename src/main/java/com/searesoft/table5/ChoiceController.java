package com.searesoft.table5;

import com.searesoft.table5.menu.MenuChoice;
import com.searesoft.table5.menu.MenuListItem;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;

/**
 * Controller for each Menu Choice
 */
public class ChoiceController {
    @FXML
    CheckBox checkBox;
    @FXML
    RadioButton radioButton;
    @FXML
    Label label;
    @FXML
    GridPane root;

    private MenuChoice choice;

    /**
     * Read access to choice
     *
     * @return MenuChoice associated with this controller
     */
    public MenuChoice choice() {
        return choice;
    }

    private MenuListItem listItem;

    /**
     * Read access to listItem
     *
     * @return MenuListItem associated with this controller
     */
    public MenuListItem listItem() {
        return listItem;
    }

    private int priceIndex;

    /**
     * Read access to priceIndex
     *
     * @return the priceIndex, which is equivalent to the option index chosen by the user
     */
    public int priceIndex() {
        return priceIndex;
    }

    /**
     * Initialize the controller
     *
     * @param choice     the choice associated with this controller
     * @param listItem   the listItem associated with this controller
     * @param priceIndex the priceIndex used by this controller
     */
    public void init(MenuChoice choice, MenuListItem listItem, int priceIndex) {
        this.choice = choice;
        this.listItem = listItem;
        this.priceIndex = priceIndex;

        //display the name
        //checkbox is null if we're part of a radio group
        if (checkBox != null) {
            checkBox.setText(listItem.name());
        } else {
            radioButton.setText(listItem.name());
        }
        //if priceIndex is valid and the price is more than zero, display the price
        if (listItem.prices().size() > priceIndex && listItem.prices().get(priceIndex) != 0) {
            label.setText(String.format("£%.2f", listItem.prices().get(priceIndex)));
        } else {
            label.setText("");
        }
    }
}
