package com.searesoft.table5;

import com.searesoft.lib.ByRef;
import com.searesoft.lib.FXUtils;
import com.searesoft.lib.MessageBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;

import static com.searesoft.lib.ByRef.BooleanRef;

public class IntroOverlayController {
    @FXML
    MediaView mediaViewLogo;
    @FXML
    MediaView mediaViewDiscount;

    @FXML
    GridPane gridPaneLogo;
    @FXML
    GridPane gridPaneDiscount;

    @FXML
    VBox vboxContentRoot;
    @FXML
    GridPane root;
    @FXML
    Label labelStart;
    @FXML
    ImageView imageAbout;
    private boolean stopped = false;
    private boolean looping = false;

    public void init() {
        labelStart.setVisible(false);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                URL url = App.class.getResource("/videos/Table5-cropped.mp4");
                Media media = new Media(url.toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setAutoPlay(true);

                player.setOnEndOfMedia(() -> {
                    if (stopped || looping) return;
                    looping = true;
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                            loopVideo();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            looping = false;
                        }
                    }).start();
                });
                mediaViewLogo.setMediaPlayer(player);
                FXUtils.fadeIn(mediaViewLogo);
                player.play();
            });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                URL url = App.class.getResource("/videos/10% Off-cropped.mp4");
                Media media = new Media(url.toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setAutoPlay(true);
                player.setCycleCount(MediaPlayer.INDEFINITE);
                mediaViewDiscount.setMediaPlayer(player);
                FXUtils.fadeIn(mediaViewDiscount);
                player.play();
            });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                FXUtils.fadeIn(labelStart);
            });
        }).start();

    }

    public void stopVideo() {
        stopped = true;
        if (mediaViewLogo.getMediaPlayer() != null) mediaViewLogo.getMediaPlayer().stop();
        if (mediaViewDiscount.getMediaPlayer() != null) mediaViewDiscount.getMediaPlayer().stop();

    }

    public void startVideo() {
        stopped = false;
        loopVideo();
    }

    private void loopVideo() {
        if (mediaViewLogo.getMediaPlayer() != null) {
            MediaPlayer player = mediaViewLogo.getMediaPlayer();
            player.seek(Duration.ZERO);
            player.play();
        }
        if (mediaViewDiscount.getMediaPlayer() != null) mediaViewDiscount.getMediaPlayer().play();
    }

    public void updateSize(double width, double height) {
        double size = Math.min(Math.max(Math.round(Math.min(width, height) * 0.75), 64), 720);
        double hsize = size * 0.5;
        mediaViewLogo.setFitWidth(size);
        mediaViewLogo.setFitHeight(hsize);
        gridPaneLogo.setPrefWidth(size);
        gridPaneLogo.setPrefHeight(hsize);
        size *= 0.5;
        mediaViewDiscount.setFitWidth(size);
        mediaViewDiscount.setFitHeight(hsize);
        gridPaneDiscount.setPrefWidth(size);
        gridPaneDiscount.setPrefHeight(hsize);
    }
}
