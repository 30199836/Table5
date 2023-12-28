package com.searesoft.table5;

import com.searesoft.table5.menu.Menu;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    public static final int width = 1280;
    public static final int height = 720;

    public static Scene scene = null;

    public static Menu menu = new Menu();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("main-form.fxml"));
        MainForm main = new MainForm(stage);
        loader.setController(main);
        scene = new Scene(loader.load(), width, height);
        stage.setTitle("Table5 - Savour the moment, taste the tradition!");
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("/icons/icon32.png")));
    //    stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        main.init();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}