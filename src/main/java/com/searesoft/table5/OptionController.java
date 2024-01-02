package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import com.searesoft.table5.menu.MenuOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.InputStream;

/**
 * Controller for each Menu Option
 */
public class OptionController {
    @FXML
    RadioButton radioButton;
    @FXML
    Label label;
    @FXML
    GridPane root;
    private MenuOption option;

    /**
     * Read access to option
     *
     * @return the MenuOption associated with this controller
     */
    public MenuOption option() {
        return option;
    }

    /**
     * Initialize the controller
     *
     * @param option the MenuOption associated with this controller
     */
    public void init(MenuOption option) {
        this.option = option;
        radioButton.setText(option.name());
        String str = String.format("£%.2f", option.price());
        if (option.price() != 0) label.setText(str);
    }
}
