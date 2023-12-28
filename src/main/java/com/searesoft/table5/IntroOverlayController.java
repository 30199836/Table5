package com.searesoft.table5;

import com.searesoft.lib.ByRef;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;

import static com.searesoft.lib.ByRef.BooleanRef;

public class IntroOverlayController {
    @FXML
    MediaView video;
    @FXML
    GridPane root;
    @FXML
    Label labelStart;
    @FXML
    ImageView imageAbout;
    private boolean stopped = false;
    private boolean looping = false;

    public void init() {
        Media media = null;
        URL url = App.class.getResource("/videos/Table5.mp4");
        if (url != null) media = new Media(url.toExternalForm());
        if (media != null) {
            MediaPlayer player = new MediaPlayer(media);
            video.setMediaPlayer(player);
            player.setAutoPlay(true);
            //  player.setCycleCount(0);

            player.setOnEndOfMedia(() -> {
                if (stopped || looping) return;
                looping = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        loopVideo();
                    } catch (Exception e) {
                    } finally {
                        looping = false;
                    }
                }).start();
            });

//            Platform.runLater(() -> {
//                startVideo();
//            });
        }
    }

    public void stopVideo() {
        stopped = true;
    //    video.getMediaPlayer().stop();
    }

    public void startVideo() {
        stopped = false;
        loopVideo();
    }

    private void loopVideo() {
        MediaPlayer player = video.getMediaPlayer();
        player.seek(Duration.ZERO);
        player.play();
    }

    public void updateSize(double width, double height) {
        double size = Math.round(Math.min(width, height) * 0.75);
        video.setFitWidth(size);
        video.setFitHeight(size);
    }
}
