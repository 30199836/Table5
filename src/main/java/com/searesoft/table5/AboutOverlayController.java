package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static java.awt.Desktop.getDesktop;


/**
 * Controller for the product image overlay dialog
 */
public class AboutOverlayController extends BaseMenuController {
    @FXML
    Hyperlink link;
    @FXML
    Label labelVersion;
    @FXML
    VBox dialogRoot;

    public void init(MenuItem menuItem) {
        try {
            String ver = new String(App.class.getResourceAsStream("/version.txt").readAllBytes(), StandardCharsets.UTF_8);
            if (ver.length() > 0) {
                labelVersion.setText("Version " + ver);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        link.setBorder(Border.EMPTY);
        link.setOnMouseClicked(event -> {
            try {
                getDesktop().mail(new URI("mailto:" + link.getText()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * Update the dialog size
     *
     * @param width  new width of the window
     * @param height new height of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
     //   dialogRoot.setPrefWidth(width );
     //   dialogRoot.setPrefHeight(height );
    }
}
