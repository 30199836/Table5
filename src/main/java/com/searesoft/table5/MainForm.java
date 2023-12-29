package com.searesoft.table5;

import com.searesoft.table5.menu.*;
import com.searesoft.lib.*;
import com.searesoft.table5.menu.MenuItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

public class MainForm extends BaseDialog {

    @FXML
    private GridPane gridPaneAdd;

    @FXML
    private Label labelDiscount;

    @FXML
    private Label labelName;

    @FXML
    private Label labelSubTotal;

    @FXML
    private Label labelTotal;

    @FXML
    private ListView listViewCategories;

    @FXML
    private GridPane root;

    @FXML
    GridPane gridPaneCheckout;

    @FXML
    private ScrollPane scrollPaneMenu;

    @FXML
    private ScrollPane scrollPaneOrder;

    @FXML
    private VBox vBoxMain;

    @FXML
    private VBox vBoxMenu;

    @FXML
    private VBox vboxOrderRoot;

    @FXML
    GridPane gridPaneOrderPrice;

    private MenuItemController firstMenuItem;
    private BaseMenuController overlay;
    private Label[] headers;
    private ArrayList<ChoiceHeaderController> requiredChoiceHeaderControllers = new ArrayList<>();
    private double width, height;
    private boolean sizeChanged = false;
    private final Order order = new Order();
    double orderVScrollPos = 1.0;

    IntroOverlayController introOverlay;

    //keep track of the window width change, run later so both the width and height have been updated
    //update the menu text wrap and resize the overlay dialogs
    ChangeListener<Number> widthChangedListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            width = t1.doubleValue();

            //update the menu text wrap
            MenuItemController controller = firstMenuItem;
            while (controller != null) {
                controller.updateSize(width, height);
                controller = controller.next();
            }

            if (introOverlay.root.isVisible() && !sizeChanged) {
                sizeChanged = true;
                Platform.runLater(() -> {
                    introOverlay.updateSize(width, height);
                    sizeChanged = false;
                });
            }

            //resize the overlay dialog
            if (overlay != null && !sizeChanged) {
                sizeChanged = true;
                Platform.runLater(() -> {
                    overlay.updateSize(width, height);
                    sizeChanged = false;
                });
            }
        }
    };

    //keep track of the window height change, run later so both the width and height have been updated
    ChangeListener<Number> heightChangedListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            height = t1.doubleValue();

            if (introOverlay.root.isVisible() && !sizeChanged) {
                sizeChanged = true;
                Platform.runLater(() -> {
                    introOverlay.updateSize(width, height);
                    sizeChanged = false;
                });
            }
            //resize the overlay dialog
            if (overlay != null && !sizeChanged) {
                sizeChanged = true;
                Platform.runLater(() -> {
                    overlay.updateSize(width, height);
                    sizeChanged = false;
                });
            }
        }
    };

    //This is the only reliable way I've found to scroll the order to the bottom when adding an item
    ChangeListener<Number> orderHeightChangeListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            //only scroll to the bottom if the user didn't scroll up
            if (orderVScrollPos == 1.0) scrollPaneOrder.setVvalue(1.0);
        }
    };

    MainForm(Stage stage) {
        super(stage);
    }

//    private void cancelAddClicked(MouseEvent e) {
//        if (e.getPickResult().getIntersectedNode() == root.getChildren().get(1)) {
//            root.getChildren().remove(1);
//        }
//    }

    private void menuClicked(MenuItemController c, MouseEvent e) {
        //  MessageBox.show(controller.menuItem().name());
        try {
            FXMLLoader loader;
            boolean isImageOverlay = e.getPickResult().getIntersectedNode() == c.imageMagnify;
            if (isImageOverlay) {
                loader = new FXMLLoader(App.class.getResource("image-overlay.fxml"));
                overlay = new ImageOverlayController();
            } else {
                loader = new FXMLLoader(App.class.getResource("add-overlay.fxml"));
                overlay = new AddOverlayController();
            }
            loader.setController(overlay);
            loader.load();
            if (isImageOverlay) {
                overlay.init(c.menuItem());
            } else {
                overlay.init(new MenuItem(c.menuItem().owner(), c.menuItem()));
            }

            root.getChildren().add(overlay.root);
            FXUtils.fadeIn(overlay.root);
            overlay.updateSize(width, height);
            //remove the overlay if the user clicks outside the dialog
            overlay.root.setOnMouseClicked(event -> {
                if (overlay != null && root.getChildren().size() > 2) {//root.getChildren().remove(1);
                    if (event.getPickResult().getIntersectedNode() == root.getChildren().get(root.getChildren().indexOf(overlay.root))) {
                        closeOverlay();
                    }
                }
            });


            //remove the overlay if the user clicks the close button (image)
            overlay.imageClose.setOnMouseClicked(event -> {
                //if (root.getChildren().size() > 1) {
                closeOverlay();
                //  }
            });


            if (!isImageOverlay) {
                //   AddOverlayController ovl = ((AddOverlayController) overlay);
//                ovl.imageMinus.setOnMouseClicked(event -> {
//                    int val = Integer.parseInt(ovl.labelCount.getText());
//                    if (val > 1) {
//                        val--;
//                        ovl.labelCount.setText(String.valueOf(val));
//                        ovl.updateChoices();
//                    }
//                });
//                ovl.imagePlus.setOnMouseClicked(event -> {
//                    int val = Integer.parseInt(ovl.labelCount.getText());
//                    if (val < 9) {
//                        val++;
//                        ovl.labelCount.setText(String.valueOf(val));
//                        ovl.updateChoices();
//                    }
//                });
                //  fillOverlayForOption(ovl.menuItem().options().size() > 1 ? -1 : 0);
                fillOverlayForOption(overlay.menuItem().options().size() > 1 ? -1 : 0);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    void fillOverlayForOption(int optionIndex) {
        try {
            if (overlay == null) {
                throw new RuntimeException();
            }
            AddOverlayController ovl = ((AddOverlayController) overlay);
            MenuItem menuItem = ovl.menuItem();

            //    GridPane pane;
            FXMLLoader loader;
            for (int i = ovl.vboxOptionsRoot.getChildren().size() - 1; i > 0; i--) {
                // fadeOut(ovl.vboxOptionsRoot.getChildren().get(i),true);
                ovl.vboxOptionsRoot.getChildren().remove(i);
            }
            menuItem.setSelectedIndex(optionIndex);

            ovl.gridPaneAdd.setOnMouseClicked(e -> {
                if (!ovl.labelAdd.getText().equals("Add to Order")) {
                    //scroll to the first required choice
                    if (requiredChoiceHeaderControllers.size() > 0) {
                        double height = ovl.scrollPaneOptions.getContent().getBoundsInLocal().getHeight() - ovl.scrollPaneOptions.getHeight();
                        double y = requiredChoiceHeaderControllers.get(0).root.getBoundsInParent().getMinY();
                        y = Math.min(1, y / height);
                        if (ovl.scrollPaneOptions.getVvalue() != y) {
                            ovl.scrollPaneOptions.setVvalue(y);
                            FXUtils.pulsate(requiredChoiceHeaderControllers.get(0).root, 1);
                        }
                    }
                    return;

                }
                orderVScrollPos = scrollPaneOrder.getVvalue();
                Order.Item orderItem = order.new Item(order);
                order.items.add(orderItem);
                MenuItem item = ovl.menuItem();
                orderItem.name = item.name();
                MenuOption option = item.options().get(item.selectedIndex());
                orderItem.option = option.name();
                orderItem.count = Integer.parseInt(ovl.labelCount.getText());
                orderItem.price = option.price() * orderItem.count;
                for (MenuChoice choice : option.choices()) {
                    Order.Choice orderChoice = order.new Choice(orderItem);
                    orderItem.choices.add(orderChoice);
                    orderChoice.name = choice.name();
                    if (choice.selectedIndices().isEmpty()) {
                        orderChoice.items.add("No " + choice.name());
                    } else {
                        Collections.sort(choice.selectedIndices());
                        for (int i = 0; i < choice.selectedIndices().size(); i++) {
                            MenuListItem menuListItem = choice.menuList().items().get(choice.selectedIndices().get(i));
                            orderChoice.items.add(menuListItem.name());
                        }
                    }
                }
                //   MessageBox.show(ovl.labelCount.getText() + "x " + orderItem.name);

                try {
                    FXMLLoader loader2 = new FXMLLoader(App.class.getResource("order-item.fxml"));
                    OrderItemController orderItemController = new OrderItemController();
                    loader2.setController(orderItemController);
                    loader2.load();
                    vboxOrderRoot.getChildren().add(orderItemController.root);
                    FXUtils.pulsate(orderItemController.root, 1);
                    //    pane2.setStyle("-fx-background-color:" + (order.items.size() % 2 == 1 ? "#FFFFFF;" : "#CBCBCB;"));
                    orderItemController.init(orderItem);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                updateOrderPrice();
                closeOverlay();
            });

            if (menuItem.options().size() > 1) {
                loader = new FXMLLoader(App.class.getResource("option-header.fxml"));
                OptionHeaderController optionHeaderController = new OptionHeaderController();
                loader.setController(optionHeaderController);
                loader.load();
                ovl.vboxOptionsRoot.getChildren().add(optionHeaderController.root);
                FXUtils.fadeIn(optionHeaderController.root);
                optionHeaderController.init(menuItem);
                if (optionIndex > -1) {
                    optionHeaderController.labelRequired.setOpacity(0);
                }

                ToggleGroup optionToggleGroup = new ToggleGroup();

                for (int i = 0; i < menuItem.options().size(); i++) {
                    MenuOption option = menuItem.options().get(i);
                    loader = new FXMLLoader(App.class.getResource("option.fxml"));
                    OptionController optionController = new OptionController();
                    loader.setController(optionController);
                    loader.load();
                    optionController.radioButton.setToggleGroup(optionToggleGroup);
                    ovl.vboxOptionsRoot.getChildren().add(optionController.root);
                    FXUtils.fadeIn(optionController.root);
                    optionController.init(option);
                    if (i == optionIndex) optionController.radioButton.setSelected(true);
                    int index = i;

                    //option clicked
                    optionController.radioButton.setOnMouseClicked(event -> {
                        fillOverlayForOption(index);
                    });
                }
            }
            requiredChoiceHeaderControllers.clear();
            if (optionIndex > -1 && optionIndex < menuItem.options().size()) {
                MenuOption option = menuItem.options().get(optionIndex);
                for (int i = 0; i < option.choices().size(); i++) {
                    MenuChoice choice = option.choices().get(i);
                    if (choice.menuList() != null) {
                        loader = new FXMLLoader(App.class.getResource("option-header.fxml"));
                        ChoiceHeaderController choiceHeaderController = new ChoiceHeaderController();
                        loader.setController(choiceHeaderController);
                        loader.load();
                        ovl.vboxOptionsRoot.getChildren().add(choiceHeaderController.root);
                        FXUtils.fadeIn(choiceHeaderController.root);
                        choiceHeaderController.init(choice);
                        if (choiceHeaderController.choice().requiredCount() > 0) {
                            requiredChoiceHeaderControllers.add(choiceHeaderController);
                        }

                        ToggleGroup choiceToggleGroup = new ToggleGroup();
                        for (int j = 0; j < choice.menuList().items().size(); j++) {
                            if (choice.requiredCount() != 1) {
                                loader = new FXMLLoader(App.class.getResource("choice.fxml"));
                            } else {
                                loader = new FXMLLoader(App.class.getResource("option.fxml"));
                            }

                            ChoiceController choiceController = new ChoiceController();
                            loader.setController(choiceController);
                            loader.load();

                            if (choice.requiredCount() == 1) {
                                choiceController.radioButton.setToggleGroup(choiceToggleGroup);
                                choiceController.radioButton.setOnMouseClicked(event -> {
                                    choiceHeaderController.labelRequired.setOpacity(0);
                                    //remove the choice from the required headers
                                    requiredChoiceHeaderControllers.remove(choiceHeaderController);
                                    //set the selected index
                                    MenuChoice c = choiceController.choice();
                                    c.selectedIndices().clear();
                                    c.selectedIndices().add(c.menuList().indexOf(choiceController.radioButton.getText()));
                                    //update the UI
                                    ovl.updateChoices();
                                });
                            } else {
                                choiceController.checkBox.setOnMouseClicked(event -> {
                                    MenuChoice c = choiceController.choice();
                                    int index = c.menuList().indexOf(choiceController.checkBox.getText());
                                    int selIndex = c.selectedIndices().indexOf(index);
                                    if (choiceController.checkBox.isSelected()) {
                                        if (selIndex == -1) c.selectedIndices().add(index);
                                    } else {
                                        if (selIndex != -1) c.selectedIndices().remove(selIndex);
                                    }
                                    ovl.updateChoices();
                                });

                            }
                            ovl.vboxOptionsRoot.getChildren().add(choiceController.root);
                            FXUtils.fadeIn(choiceController.root);
                            choiceController.init(choice, choice.menuList().items().get(j), optionIndex);
                            choiceController.choice().selectedIndices().clear();
                        }
                    }
                }
            }
            overlay.updateSize(width, height);
            ovl.updateChoices();

            //     ((AddOverlayController) overlay).scrollPane.setContent(((AddOverlayController) overlay).vboxOptionsRoot);
        } catch (
                IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void closeOverlay() {
        if (overlay != null) {
            FXUtils.fadeOut(overlay.root, root);
            //    root.getChildren().remove(root.getChildren().indexOf(overlay.root));
            overlay = null;
        }
        requiredChoiceHeaderControllers.clear();
    }

    private void updateOrderPrice() {
        order.calculatePrice();
        labelSubTotal.setText(String.format("£%.2f", order.price()));
        double discount = order.price() == 0 ? 0 : order.price() / 100 * 10;
        labelDiscount.setText(String.format("£%.2f", discount));
        labelTotal.setText(String.format("£%.2f", order.price() - discount));
    }

    @Override
    public void init() {

        //used to make the order ScrollPane scroll to the bottom when an item is added to the order
        vboxOrderRoot.heightProperty().addListener(orderHeightChangeListener);
        //used to auto resize some gui elements
        stage.widthProperty().addListener(widthChangedListener);
        stage.heightProperty().addListener(heightChangedListener);
        //the contents of the categories ListView
        ObservableList<String> cat = FXCollections.observableArrayList();
        //linked list of menuItems
        firstMenuItem = null;
        MenuItemController controller = null;
        //set the size of the headers array to the now known number of the categories
        headers = new Label[App.menu.menuCategories.size()];
        for (int i = 0; i < App.menu.menuCategories.size(); i++) {
            //create the menu category label and keep track of it so we can scroll to it later
            MenuCategory c = App.menu.menuCategories.get(i);
            headers[i] = new Label();
            headers[i].setText(c.name());
            // header.setTextAlignment(TextAlignment.CENTER);
            //  header.setContentDisplay(ContentDisplay.TOP);
            headers[i].setMaxWidth(Double.MAX_VALUE);
            headers[i].setAlignment(Pos.CENTER);
            headers[i].setFont(Font.font(null, FontWeight.BOLD, 24));
            headers[i].setTextFill(Color.color(1, 1, 1));
            headers[i].setStyle("-fx-background-color:black");
            //add the header to the vbox container
            vBoxMenu.getChildren().add(headers[i]);
            //create a cetegory description label
            Label desc = new Label();
            desc.setText(c.description());
            desc.setMaxWidth(Double.MAX_VALUE);
            desc.setAlignment(Pos.CENTER);
            desc.setFont(Font.font(null, 16));
            //desc.setTextFill(Color.color(1, 1, 1));
            desc.setStyle("-fx-background-color:white");
            //add the description label to the vbox container
            vBoxMenu.getChildren().add(desc);
            //add the category to the ListView
            cat.add(c.name());
            //create the menu items for this category
            for (int j = 0; j < c.menuItems().size(); j++) {
                try {
                    FXMLLoader loader = new FXMLLoader(App.class.getResource("menu-item.fxml"));

                    controller = new MenuItemController(controller);
                    if (firstMenuItem == null) {
                        firstMenuItem = controller;
                    }

                    loader.setController(controller);
                    GridPane pane = loader.load();
//  pane.setMaxWidth(Double.MAX_VALUE);
//   pane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE); // Default width and height
//   pane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);


                    final MenuItemController finalController = controller;

                    pane.setOnMouseClicked(e -> menuClicked(finalController, e));
                    // pane.setStyle("-fx-background-color:" + (j % 2 == 1 ? "white" : "#CBCBCB;"));
                    pane.setStyle("-fx-background-color:white");
                    controller.init(c.menuItems().get(j));
                    vBoxMenu.getChildren().add(pane);

                    //       if (j == 0) controller.root.setPadding(new Insets(4, 0, 0, 0));
                    //even up the layout for last item of the category
                    if (j == c.menuItems().size() - 1) controller.root.setPadding(new Insets(0, 0, 8, 0));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //set the contents if the category ListView and select the first item
        listViewCategories.setItems(cat);
        if (!cat.isEmpty()) listViewCategories.getSelectionModel().select(0);

        //scroll to menu category header when the Listview category is selected
        listViewCategories.setOnMouseClicked(event ->

        {
            int index = listViewCategories.getSelectionModel().getSelectedIndex();
            if (index > -1) {
                double height = scrollPaneMenu.getContent().getBoundsInLocal().getHeight() - scrollPaneMenu.getHeight();
                double y = headers[index].getBoundsInParent().getMinY();
                scrollPaneMenu.setVvalue(y / height);
                FXUtils.pulsate(headers[index], 1);
            }
        });

        //automatically select the category from the current scroll position in the menu
        scrollPaneMenu.vvalueProperty().
                addListener(
                        (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->

                        {
                            if (newValue.doubleValue() > 0.999) {
                                return;
                            }
                            double height = scrollPaneMenu.getContent().getBoundsInLocal().getHeight() - scrollPaneMenu.getHeight();
                            double pos = newValue.doubleValue() * height;
                            for (int i = 0; i < headers.length - 1; i++) {
                                //the y position of this and the next header label
                                double y1 = headers[i].getBoundsInParent().getMinY();
                                double y2 = headers[i + 1].getBoundsInParent().getMinY();
                                if (pos >= y1 && pos < y2 - 0.0001) {
                                    //set the category selection
                                    listViewCategories.getSelectionModel().select(i);
                                    //scroll the item into view if not visible
                                    try {
                                        ListViewSkin<?> ts = (ListViewSkin<?>) listViewCategories.getSkin();
                                        VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
                                        int first = vf.getFirstVisibleCell().getIndex();
                                        int last = vf.getLastVisibleCell().getIndex();
                                        if (i <= first || i >= last) {
                                            listViewCategories.scrollTo(i);
                                        }
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                            }
                        });


        //category ListView cell formatting and change cursor when the mouse is over an item
        listViewCategories.setCellFactory(lv ->
        {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setPadding(new Insets(5, 5, 5, 5));
                        setText(item);
                        setFont(Font.font(null, FontWeight.BOLD, 18));
                    }
                }
            };
            cell.setOnMouseMoved(e -> {
                if (cell.isEmpty()) {
                    listViewCategories.setCursor(Cursor.DEFAULT);
                } else {
                    listViewCategories.setCursor(Cursor.HAND);
                }
            });
            return cell;
        });


        gridPaneCheckout.setOnMouseClicked(event -> {
            //    FXUtils.snapshot(vboxOrderRoot,"d:\\test.png");
            try {
                //Image image = SwingFXUtils.toFXImage(FXUtils.snapshot(vboxOrderRoot), null);

                FXMLLoader loader = new FXMLLoader(App.class.getResource("checkout-overlay.fxml"));
                CheckoutOverlayController ovl = new CheckoutOverlayController();
                overlay = ovl;
                loader.setController(ovl);
                loader.load();
                ovl.init(order);
                Image image = SwingFXUtils.toFXImage(FXUtils.snapshot(vboxOrderRoot), null);
                ovl.imageOrder.setImage(image);
                ovl.imageOrder.setFitWidth(image.getWidth());
                ovl.imageOrder.setFitHeight(image.getHeight());

                image = SwingFXUtils.toFXImage(FXUtils.snapshot(gridPaneOrderPrice), null);
                ovl.imagePrice.setImage(image);
                ovl.imagePrice.setFitWidth(image.getWidth());
                ovl.imagePrice.setFitHeight(image.getHeight());

                root.getChildren().add(ovl.root);
                overlay.updateSize(width, height);
                FXUtils.fadeIn(ovl.root);
                ovl.scrollPaneOrder.setPrefWidth(image.getWidth()+128);


                overlay.root.setOnMouseClicked(event2 -> {
                    if (overlay != null && root.getChildren().size() > 2) {
                        if (event2.getPickResult().getIntersectedNode() == root.getChildren().get(root.getChildren().indexOf(overlay.root))) {
                            closeOverlay();
                        }
                    }
                });

                //remove the overlay if the user clicks the close button (image)
                overlay.imageClose.setOnMouseClicked(event2 -> {
                    closeOverlay();
                });

                ovl.gridPanePrint.setOnMouseClicked(event2 -> {
                    String desktopPath = Registry.readString(
                            "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders",
                            "Desktop");
                    if (desktopPath == null) {
                        MessageBox.show(Alert.AlertType.ERROR, "Please collect your order using Order ID " + ovl.OrderID, "Error", "Unable to determine the Desktop folder location to save the receipt");
                    } else {
                        String filename = "Table5Order" + ovl.OrderID + ".png";
                        FXUtils.snapshot(ovl.vboxOrderRoot, desktopPath + "\\" + filename);
                   //     MessageBox.show(desktopPath + "\\"  + filename);
                        MessageBox.show("\"" + filename + "\" was saved to your desktop.");
                    }
                    closeOverlay();
                    introOverlay.startVideo();
                    introOverlay.updateSize(width, height);
                    FXUtils.fadeIn(introOverlay.root);
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("intro-overlay.fxml"));
            introOverlay = new IntroOverlayController();
            loader.setController(introOverlay);
            loader.load();
            introOverlay.init();
            root.getChildren().add(introOverlay.root);
            introOverlay.updateSize(width, height);

            introOverlay.labelStart.setOnMouseClicked(event -> {
                order.items.clear();
                vboxOrderRoot.getChildren().clear();
                updateOrderPrice();
                introOverlay.stopVideo();
                FXUtils.fadeOut(introOverlay.root, true);
            });

            introOverlay.imageAbout.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader2 = new FXMLLoader(App.class.getResource("about-overlay.fxml"));
                    overlay = new AboutOverlayController();
                    loader2.setController(overlay);
                    loader2.load();
                    overlay.init(null);
                    root.getChildren().add(overlay.root);
                    overlay.updateSize(width, height);
                    overlay.root.setOpacity(0);
                    FXUtils.fadeIn(overlay.root);
                    introOverlay.stopVideo();

                    overlay.imageClose.setOnMouseClicked(event2 -> {
                        closeOverlay();
                        introOverlay.startVideo();
                    });

                    overlay.root.setOnMouseClicked(event2 -> {
                        closeOverlay();
                        introOverlay.startVideo();
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}