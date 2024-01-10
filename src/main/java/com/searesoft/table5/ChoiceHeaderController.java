package com.searesoft.table5;

import com.searesoft.table5.menu.MenuChoice;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Controller for the Menu Choice Header
 */
public class ChoiceHeaderController {
    @FXML
    Label labelName;
    @FXML
    Label labelRequired;
    @FXML
    GridPane root;
    private MenuChoice choice;

    /**
     * Read access to choice
     * @return the MenuChoice associated with this controller
     */
    public MenuChoice choice() {
        return choice;
    }

    /**
     * Initialize the controller
     *
     * @param choice the MenuChoice associated with this controller
     */
    public void init(MenuChoice choice) {
        this.choice = choice;
        //display the choice name with " Options" concatenated eg. Salad Options, Drink Options
        if (choice.allowedCount() == 0) {
            labelName.setText(choice.name() + " Options");
        } else {
            labelName.setText(choice.name() + " Options (Max " + choice.allowedCount() + ")");
        }
        //hide the "Required label" for optional choices
        if (choice.requiredCount() == 0) labelRequired.setOpacity(0);
    }
}
