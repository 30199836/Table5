package com.searesoft.table5;

import com.searesoft.table5.menu.Menu;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App entry point.
 */
public class App extends Application {
    public static final int width = 1280;
    public static final int height = 720;

    public static Scene scene = null;

    public static Menu menu = new Menu();
    public static boolean terminated = false;

    /**
     * Called by JavaFX to set up the default stage
     *
     * @param stage the default stage
     * @throws IOException if the resource fails to load
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        MainController main = new MainController(stage);
        loader.setController(main);
        scene = new Scene(loader.load(), width, height);
        stage.setTitle("Table5 - Savour the moment, taste the tradition!");
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("/icons/icon32.png")));
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setOnCloseRequest(event -> {
            terminated = true;
        });
        main.init();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}