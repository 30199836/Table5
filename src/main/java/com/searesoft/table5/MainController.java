package com.searesoft.table5;

import com.searesoft.table5.menu.*;
import com.searesoft.lib.*;
import com.searesoft.table5.menu.MenuItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controller that handles the main application logic
 */
public class MainController {
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
    GridPane gridPaneCancel;

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

    public Stage stage = null;

    private MenuItemController firstMenuItemController;
    private BaseMenuController overlay;
    private Label[] headers;
    private ArrayList<ChoiceHeaderController> requiredChoiceHeaderControllers = new ArrayList<>();
    private double width, height;
    private boolean sizeChanged = false;
    private final Order order = new Order();
    double orderVScrollPos = 1.0;
    IntroOverlayController introOverlay;

    private ArrayList<OrderItemController> orderItemControllers = new ArrayList<>();

    //keep track of the window width change, run later so both the width and height have been updated
    //update the menu text wrap and resize the overlay dialogs
    ChangeListener<Number> widthChangedListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            width = t1.doubleValue();
            updateSize();
        }
    };


    //keep track of the window height change, run later so both the width and height have been updated
    ChangeListener<Number> heightChangedListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            height = t1.doubleValue();
            updateSize();
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

    private void updateSize() {
        //update the menu text wrap
        //  Platform.runLater(() -> {
        MenuItemController controller = firstMenuItemController;
        while (controller != null) {
            controller.updateSize(width, height);
            controller = controller.next();
        }
        //  });

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

    /**
     * Constructor
     *
     * @param stage the stage this controller belongs to
     */
    MainController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Called when a menu item is clicked
     *
     * @param c the controller of the menu item clicked
     * @param e the triggered event
     */
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
            setOverlayCloseEvents();

            if (!isImageOverlay) {
                fillOverlayForOption(overlay.menuItem().options().size() > 1 ? -1 : 0);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Rebuild the add dialog with the choices for current option
     *
     * @param optionIndex the current option index
     */
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
                ovl.vboxOptionsRoot.getChildren().remove(i);
            }
            menuItem.setOptionIndex(optionIndex);

            ovl.gridPaneAdd.setOnMouseClicked(event -> addButtonClicked());

            if (menuItem.options().size() > 1) {
                loader = new FXMLLoader(App.class.getResource("option-header.fxml"));
                OptionHeaderController optionHeaderController = new OptionHeaderController();
                loader.setController(optionHeaderController);
                loader.load();
                ovl.vboxOptionsRoot.getChildren().add(optionHeaderController.root);
                // FXUtils.fadeIn(optionHeaderController.root);
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
                    //   FXUtils.fadeIn(optionController.root);
                    optionController.init(option);
                    if (i == optionIndex) optionController.radioButton.setSelected(true);
                    int index = i;

                    //option clicked
                    optionController.radioButton.setOnMouseClicked(event -> fillOverlayForOption(index));
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
                            if (choice.requiredCount() == 1 && choice.allowedCount() < 2) {
                                loader = new FXMLLoader(App.class.getResource("option.fxml"));
                            } else {
                                loader = new FXMLLoader(App.class.getResource("choice.fxml"));
                            }

                            ChoiceController choiceController = new ChoiceController();
                            loader.setController(choiceController);
                            loader.load();

                            if (choice.requiredCount() == 1 && choice.allowedCount() < 2) {
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
                                    choiceHeaderController.labelRequired.setOpacity(0);
                                    //remove the choice from the required headers
                                    requiredChoiceHeaderControllers.remove(choiceHeaderController);

                                    int index = c.menuList().indexOf(choiceController.checkBox.getText());
                                    if (c.allowedCount() > 0 && choiceController.checkBox.isSelected() && c.selectedIndices().size() >= c.allowedCount()) {
                                        //deselect the checkbox because too many are selected
                                        choiceController.checkBox.setSelected(false);
                                    } else {
                                        //add the selection
                                        int selIndex = c.selectedIndices().indexOf(index);
                                        if (choiceController.checkBox.isSelected()) {
                                            if (selIndex == -1) c.selectedIndices().add(index);
                                        } else {
                                            if (selIndex != -1) c.selectedIndices().remove(selIndex);
                                        }
                                    }
                                    //update the ui
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
        } catch (
                IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Called when the add to order button is clicked
     */
    private void addButtonClicked() {
        AddOverlayController ovl = ((AddOverlayController) overlay);

        if (!(ovl.labelAdd.getText().equals("Add to Order"))) {
            //scroll to the first required choice
            if (requiredChoiceHeaderControllers.size() > 0) {
                double height = ovl.scrollPaneOptions.getContent().getBoundsInLocal().getHeight() - ovl.scrollPaneOptions.getHeight();
                double y = requiredChoiceHeaderControllers.get(0).root.getBoundsInParent().getMinY();
                y = Math.min(1, y / height);
                if (ovl.scrollPaneOptions.getVvalue() != y) {
                    ovl.scrollPaneOptions.setVvalue(y);
                    FXUtils.pulsate(requiredChoiceHeaderControllers.get(0).root, 2, false);
                }
            }
            return;
        }

        //build the order item
        orderVScrollPos = scrollPaneOrder.getVvalue();
        Order.Item orderItem;
        orderItem = order.new Item(order);
        order.items.add(orderItem);
        MenuItem item = ovl.menuItem();
        orderItem.name = item.name();
        MenuOption option = item.options().get(item.optionIndex());
        orderItem.option = option.name();
        orderItem.count = Integer.parseInt(ovl.labelCount.getText());
        orderItem.price = option.price();
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
                    if (menuListItem.prices().size() > item.optionIndex()) {
                        orderItem.price += menuListItem.prices().get(item.optionIndex());
                        orderChoice.prices.add(menuListItem.prices().get(item.optionIndex()));
                    } else {
                        orderChoice.prices.add(0.0);
                    }
                }
            }
        }
        orderItem.price *= orderItem.count;

        //load and fill the gui order item
        try {
            OrderItemController orderItemController;
            FXMLLoader loader2 = new FXMLLoader(App.class.getResource("order-item.fxml"));
            orderItemController = new OrderItemController();
            loader2.setController(orderItemController);
            loader2.load();
            orderItemController.root.setOnMouseClicked(event -> orderItemClicked(orderItemController));
            vboxOrderRoot.getChildren().add(orderItemController.root);
            orderItemControllers.add(orderItemController);
            orderItemController.init(orderItem);
            FXUtils.pulsate(orderItemController.root, 2, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        updateOrderPrice();
        closeOverlay();
    }

    /**
     * Called when the add to order item is clicked
     */
    private void orderItemClicked(OrderItemController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("edit-overlay.fxml"));
            EditOverlayController ovl = new EditOverlayController();
            overlay = ovl;
            loader.setController(ovl);
            loader.load();
            ovl.init(controller);
            root.getChildren().add(ovl.root);
            FXUtils.fadeIn(ovl.root);
            //remove the overlay if the user clicks outside the dialog
            setOverlayCloseEvents();

            if (controller.orderItem().choices.size() > 0) {
                FXUtils.setOpacityTree(controller.vboxOrderRoot, 1);
                controller.hideDetails();
                Image image = FXUtils.snapshot(controller.vboxOrderRoot);
                controller.showDetails();
                ovl.imageOrder.setImage(image);
                ovl.imageOrder.setFitWidth(image.getWidth());
                ovl.imageOrder.setFitHeight(image.getHeight());
            } else {
                ovl.vBoxImage.setVisible(false);
            }

            ovl.gridPaneUpdate.setOnMouseClicked(event -> updateButtonClicked());

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Called when the update order button is clicked from EditOverlayController
     */
    private void updateButtonClicked() {
        EditOverlayController ovl = ((EditOverlayController) overlay);

        if (ovl.labelCount.getText().equals("0")) {
            order.items.remove(ovl.orderItem);
            vboxOrderRoot.getChildren().remove(ovl.orderItemController.root);
            if (orderItemControllers.indexOf(ovl.orderItemController) == 0 && orderItemControllers.size() > 1) {
                orderItemControllers.get(1).hideSeparator();
            }
            orderItemControllers.remove(ovl.orderItemController);

            FXUtils.pulsate(gridPaneOrderPrice, 2, false);
        } else {
            ovl.orderItem.price = ovl.orderItem.price / ovl.orderItem.count * Integer.parseInt(ovl.labelCount.getText());
            ovl.orderItem.count = Integer.parseInt(ovl.labelCount.getText());

            ovl.orderItemController.labelPrice.setText(String.format("£%.2f", ovl.orderItem.price));
            ovl.orderItemController.labelCount.setText(String.format("%dx", ovl.orderItem.count));

            FXUtils.pulsate(ovl.orderItemController.root, 2, false);
        }

        updateOrderPrice();
        closeOverlay();
    }

    /**
     * Close the current overlay
     */
    private void closeOverlay() {
        if (overlay != null) {
            FXUtils.fadeOut(overlay.root, root);
            //    root.getChildren().remove(root.getChildren().indexOf(overlay.root));
            overlay = null;
        }
        requiredChoiceHeaderControllers.clear();
    }

    /**
     * update and display the prices for the order
     */
    private void updateOrderPrice() {
        order.calculatePrice();
        labelSubTotal.setText(String.format("£%.2f", order.price()));
        double discount = order.price() == 0 ? 0 : order.price() / 100 * 10;
        labelDiscount.setText(String.format("£%.2f", discount));
        labelTotal.setText(String.format("£%.2f", order.price() - discount));
    }

    /**
     * Initialize the controller
     */
    public void init() {
        loadIntroOverlay();
        //used to make the order ScrollPane scroll to the bottom when an item is added to the order
        vboxOrderRoot.heightProperty().addListener(orderHeightChangeListener);
        //used to auto resize some gui elements
        stage.widthProperty().addListener(widthChangedListener);
        stage.heightProperty().addListener(heightChangedListener);

        LoadMenuThreaded();
    }

    /**
     * Load the menu in a thread so the intro can be used like a splash screen
     */
    private void LoadMenuThreaded() {
        FXUtils.fadeIn(introOverlay.progressBar);
        new Thread(() -> {
            //the contents of the categories ListView
            ObservableList<String> cat = FXCollections.observableArrayList();
            //linked list of menuItems

            int progress = 0;
            int count = 0;
            for (int i = 0; i < App.menu.menuCategories.size(); i++)
                count += App.menu.menuCategories.get(i).menuItems().size();

            firstMenuItemController = null;
            MenuItemController prevController = null;
            //set the size of the headers array to the now known number of the categories
            headers = new Label[App.menu.menuCategories.size()];
            for (int i = 0; i < App.menu.menuCategories.size(); i++) {
                //create the menu category label and keep track of it so we can scroll to it later
                MenuCategory c = App.menu.menuCategories.get(i);
                Label header = new Label();
                headers[i] = header;
                header.setText(c.name());
                header.setMaxWidth(Double.MAX_VALUE);
                header.setAlignment(Pos.CENTER);
                header.setFont(Font.font(null, FontWeight.BOLD, 24));
                header.setTextFill(Color.color(1, 1, 1));
                header.setStyle("-fx-background-color:black");
                Platform.runLater(() -> {
                    //add the header to the vbox container
                    vBoxMenu.getChildren().add(header);
                });
                //create a category description label
                Label desc = new Label();
                desc.setText(c.description());
                desc.setMaxWidth(Double.MAX_VALUE);
                desc.setAlignment(Pos.CENTER);
                desc.setFont(Font.font(null, 16));
                desc.setStyle("-fx-background-color:white");
                //add the description label to the vbox container
                Platform.runLater(() -> {
                    vBoxMenu.getChildren().add(desc);
                });
                //add the category to the ListView
                cat.add(c.name());

                //create the menu items for this category
                for (int j = 0; j < c.menuItems().size(); j++) {
                    progress++;
                    introOverlay.progressBar.setProgress(1.0 / count * progress);
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("menu-item.fxml"));
                        MenuItemController controller = new MenuItemController(prevController);
                        prevController = controller;
                        if (firstMenuItemController == null) {
                            firstMenuItemController = controller;
                        }
                        loader.setController(controller);
                        loader.load();

                        controller.root.setOnMouseClicked(event -> menuClicked(controller, event));
                        controller.root.setStyle("-fx-background-color:white");
                        controller.init(c.menuItems().get(j));
                        boolean isLastItem = (j == c.menuItems().size() - 1);
                        Platform.runLater(() -> {
                            vBoxMenu.getChildren().add(controller.root);
                            //even up the layout for last item of the category
                            if (isLastItem) controller.root.setPadding(new Insets(0, 0, 8, 0));
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            //set the contents if the category ListView and select the first item
            Platform.runLater(() -> {
                listViewCategories.setItems(cat);
                if (!cat.isEmpty()) listViewCategories.getSelectionModel().select(0);
                //scroll to menu category header when the Listview category is selected
                listViewCategories.setOnMouseClicked(event -> listViewCategoriesClicked());
            });

            //automatically select the category from the current scroll position in the menu
            scrollPaneMenu.vvalueProperty().addListener(
                    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
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

            //category ListView cell formatting and cursor change when the mouse is over an item
            listViewCategories.setCellFactory(lv -> {
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

            Platform.runLater(() -> {
                gridPaneCheckout.setOnMouseClicked(event -> checkoutButtonClicked());
                gridPaneCancel.setOnMouseClicked(event -> {
                    closeOverlay();
                    introOverlay.startVideos();
                    introOverlay.updateSize(width, height);
                    FXUtils.fadeIn(introOverlay.root);
                });

            });

            //show the start new order button once the menu is fully loaded
            Platform.runLater(() -> {
                FXUtils.fadeIn(introOverlay.labelStart);
                introOverlay.progressBar.setVisible(false);
                //FXUtils.fadeOut(introOverlay.progressBar);
            });


        }).start();
    }

    /**
     * Called when the checkout button is clicked
     */
    private void checkoutButtonClicked() {
        if (order.items.size() == 0) {
            FXUtils.pulsate(gridPaneOrderPrice, 2, false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("checkout-overlay.fxml"));
            CheckoutOverlayController ovl = new CheckoutOverlayController();
            overlay = ovl;
            loader.setController(ovl);
            loader.load();
            ovl.init(order);
            //if the user quickly clicked checkout after adding the order item it might still be pulsating
            FXUtils.setOpacityTree(vboxOrderRoot, 1);
            Image image = FXUtils.snapshot(vboxOrderRoot);
            ovl.imageOrder.setImage(image);
            ovl.imageOrder.setFitWidth(image.getWidth());
            ovl.imageOrder.setFitHeight(image.getHeight());

            image = FXUtils.snapshot(gridPaneOrderPrice);
            ovl.imagePrice.setImage(image);
            ovl.imagePrice.setFitWidth(image.getWidth());
            ovl.imagePrice.setFitHeight(image.getHeight());

            root.getChildren().add(ovl.root);
            overlay.updateSize(width, height);
            FXUtils.fadeIn(ovl.root);
            ovl.scrollPaneOrder.setPrefWidth(image.getWidth() + 64);
            //   ovl.scrollPaneOrder.setPrefHeight(Math.min(image.getHeight(),720));

            setOverlayCloseEvents();

            ovl.gridPaneConfirm.setOnMouseClicked(event -> confirmOrderButtonClicked());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the confirm button is clicked in the CheckoutOrderController
     */
    private void confirmOrderButtonClicked() {
        CheckoutOverlayController ovl = (CheckoutOverlayController) overlay;
        try {
            ovl.updateDetails();
            String desktopPath = Registry.readString(
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders",
                    "Desktop");

            if (desktopPath == null) {
                MessageBox.show(Alert.AlertType.ERROR, "Please collect your order using Order ID " + ovl.orderID()+"\nThank you for your custom!", "Error", "Unable to determine the Desktop folder location to save the receipt");
            } else {
                String filename = "Table5Order" + ovl.orderID() + ".png";
                ovl.showDetails();
                try {
                    FXUtils.snapshot(ovl.vBoxOrderRoot, desktopPath + "\\" + filename);
                } finally {
                    ovl.hideDetails();
                }
                //     MessageBox.show(desktopPath + "\\"  + filename);
                MessageBox.show("\"" + filename + "\" was saved to your desktop.");
            }
        } catch (Exception e) {
            MessageBox.show(Alert.AlertType.ERROR, "Please collect your order using Order ID " + ovl.orderID(), "Error", "An error occurred");
            throw new RuntimeException(e);
        } finally {
            closeOverlay();
            introOverlay.startVideos();
            introOverlay.updateSize(width, height);
            FXUtils.fadeIn(introOverlay.root);
        }
    }

    /**
     * Called when a category is clicked
     */
    private void listViewCategoriesClicked() {
        int index = listViewCategories.getSelectionModel().getSelectedIndex();
        if (index > -1) {
            double height = scrollPaneMenu.getContent().getBoundsInLocal().getHeight() - scrollPaneMenu.getHeight();
            double y = headers[index].getBoundsInParent().getMinY();
            scrollPaneMenu.setVvalue(y / height);
            FXUtils.pulsate(headers[index], 1);
        }
    }

    /**
     * Load and configure the intro overlay
     */
    private void loadIntroOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("intro-overlay.fxml"));
            introOverlay = new IntroOverlayController();
            loader.setController(introOverlay);
            loader.load();
            introOverlay.init();
            root.getChildren().add(introOverlay.root);
            introOverlay.updateSize(width, height);

            introOverlay.labelStart.setOnMouseClicked(event -> startButtonClicked());

            introOverlay.imageAbout.setOnMouseClicked(event -> aboutButtonClicked());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the about button is clicked in IntroOverlayController
     */
    private void aboutButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("about-overlay.fxml"));
            overlay = new AboutOverlayController();
            loader.setController(overlay);
            loader.load();
            overlay.init(null);
            root.getChildren().add(overlay.root);
            overlay.updateSize(width, height);
            overlay.root.setOpacity(0);
            FXUtils.fadeIn(overlay.root);
            introOverlay.stopVideos();

            overlay.imageClose.setOnMouseClicked(event -> {
                closeOverlay();
                introOverlay.startVideos();
            });

            overlay.root.setOnMouseClicked(event -> {
                closeOverlay();
                introOverlay.startVideos();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the start new order button is clicked in IntroOverlayController
     */
    private void startButtonClicked() {
        listViewCategories.getSelectionModel().select(0);
        scrollPaneMenu.setVvalue(0);
        updateSize();
        orderItemControllers.clear();
        order.items.clear();
        vboxOrderRoot.getChildren().clear();
        updateOrderPrice();
        introOverlay.stopVideos();
        FXUtils.fadeOut(introOverlay.root, true);
    }

    /**
     * Configure the events used to close the overlays
     */
    private void setOverlayCloseEvents() {
        //workaround for mouse up event being classed as a click
        //When you scroll the ScrollPane and release the mouse button outside the dialog, that's not a click!
        overlay.root.setOnMousePressed(event -> {
            if (overlay != null) overlay.mouseDownNode = event.getPickResult().getIntersectedNode();
        });

        //remove the overlay if the user clicks outside the dialog
        overlay.root.setOnMouseClicked(event -> {
            if (overlay != null && root.getChildren().size() > 2) {
                if ((event.getPickResult().getIntersectedNode() == overlay.mouseDownNode) &&
                        overlay.mouseDownNode == root.getChildren().get(root.getChildren().indexOf(overlay.root))) {
                    closeOverlay();
                }
            }
        });

        //remove the overlay if the user clicks the close button (image)
        overlay.imageClose.setOnMouseClicked(event2 -> closeOverlay());
    }


}