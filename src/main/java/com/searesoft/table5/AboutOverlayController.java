package com.searesoft.table5;

import com.searesoft.table5.menu.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Controller for the product image overlay dialog
 */
public class AboutOverlayController extends BaseMenuController {
    @FXML
    Hyperlink link;
    @FXML
    Label labelVersion;

    public void init(MenuItem menuItem) {
        link.setBorder(Border.EMPTY);
        try {
            String ver = new String(App.class.getResourceAsStream("/version.txt").readAllBytes(), StandardCharsets.UTF_8);
            if (ver.length() > 0) {
                labelVersion.setText("Version " + ver);
            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the dialog size
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        //  double size = Math.round(Math.min(width, height) * 0.75);
        //  image.setFitWidth(size);
        // image.setFitHeight(size);
    }
}
