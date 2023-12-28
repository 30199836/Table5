package com.searesoft.table5;

import com.searesoft.table5.menu.MenuChoice;
import com.searesoft.table5.menu.MenuItem;
import com.searesoft.table5.menu.MenuOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Controller for the Add/Edit overlay dialog
 */
public class AddOverlayController extends BaseMenuController {
    @FXML
    GridPane root;
    @FXML
    VBox dialogRoot;
    @FXML
    Text textDescription;
    @FXML
    Label labelBasePrice;
    @FXML
    Label labelPrice;
    @FXML
    VBox vboxOptionsRoot;
    @FXML
    ImageView imagePlus;
    @FXML
    ImageView imageMinus;
    @FXML
    Label labelCount;
    @FXML
    GridPane gridPaneAdd;
    @FXML
    Label labelAdd;

    /**
     * Initialize the controller
     *
     * @param menuItem the MenuItem associated with this controller
     */
    @Override
    public void init(MenuItem menuItem) {
        super.init(menuItem);
        //display the name, description and price
        labelName.setText(menuItem.name());
        textDescription.setText(menuItem.description());
        setLabelBasePrice(labelBasePrice);
        //use the plus/minus buttons as a simple spinner for the item count
        imageMinus.setOnMouseClicked(event -> {
            int val = Integer.parseInt(labelCount.getText());
            if (val > 1) {
                val--;
                labelCount.setText(String.valueOf(val));
                updateChoices();
            }
        });
        imagePlus.setOnMouseClicked(event -> {
            int val = Integer.parseInt(labelCount.getText());
            if (val < 9) {
                val++;
                labelCount.setText(String.valueOf(val));
                updateChoices();
            }
        });
    }

    /**
     * Update the price and "add button" from the selected option and choices
     */
    public void updateChoices() {
        double price = 0;
        if (menuItem.selectedIndex() == -1) {
            //no option selected
            labelAdd.setText("Select an option");
        } else {
            int count = 0;
            MenuOption option = menuItem.options().get(menuItem.selectedIndex());
            //count how many required choices there are
            for (MenuChoice choice : option.choices()) {
                if (choice.selectedIndices().size() < choice.requiredCount()) count++;
            }
            if (count == 0) {
                //only optional or all required choices selected
                labelAdd.setText("Add to Order");
            } else if (count == 1) {
                //1 required choice remaining
                labelAdd.setText("1 choice is required");
            } else {
                //multiple required choices remaining
                labelAdd.setText(count + " choices are required");
            }
            //base price for this option (eg. large pizza, burger meal)
            price = option.price();
            //add any additional charges for things like pizza toppings
            for (MenuChoice choice : option.choices()) {
                for (int i = 0; i < choice.selectedIndices().size(); i++) {
                    ArrayList<Double> prices = choice.menuList().items().get(choice.selectedIndices().get(i)).prices();
                    if (menuItem.selectedIndex() < prices.size()) {
                        price += prices.get(menuItem.selectedIndex());
                    }
                }
            }
        }
        //multiply by the number of items
        price *= Integer.parseInt(labelCount.getText());
        //display the price with all the options added
        labelPrice.setText(String.format("£%.2f", price));
    }

    /**
     * Update the dialog size
     *
     * @param width new width of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        dialogRoot.setPrefWidth(width * 0.5);
        if (vboxOptionsRoot.getChildren().size() > 1) {
            dialogRoot.setPrefHeight(height * 0.75);
        } else {
            dialogRoot.setPrefHeight(220);
        }
    }
}
