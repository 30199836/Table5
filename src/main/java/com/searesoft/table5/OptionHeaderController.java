package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Controller for the add/edit overlay option header
 */
public class OptionHeaderController {
    @FXML
    Label labelName;
    @FXML
    Label labelRequired;
    @FXML
    GridPane root;

    private MenuItem menuItem;

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
}
