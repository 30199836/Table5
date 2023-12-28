package com.searesoft.table5;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AboutDialog extends BaseDialog {
    @FXML
    Hyperlink link;
    @FXML
    Label labelVersion;

    AboutDialog(Stage stage) {
        super(stage);
    }

    public void init() {
        link.setBorder(Border.EMPTY);

        try {
            String ver = new String(App.class.getResourceAsStream("/version.txt").readAllBytes(), StandardCharsets.UTF_8);

            //just a test to see how to iterate lines in a text file
            for (String line : (Iterable<String>) () -> ver.lines().iterator()) {
                labelVersion.setText("Version " + line);
                break;
            }
//            if (ver.length() > 0) {
//                labelVersion.setText("Version " + ver);
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
