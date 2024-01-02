package com.searesoft.table5;

import com.searesoft.lib.FXUtils;
import com.searesoft.lib.MessageBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;

/**
 * Controller for the intro overlay
 */
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
    VBox vBoxDialogRoot;
    @FXML
    GridPane root;
    @FXML
    Label labelStart;
    @FXML
    ImageView imageAbout;
    @FXML
    ProgressBar progressBar;
    private boolean stopped = false;
    private boolean looping = false;
    private boolean firstPlay = true;

    /**
     * Initialize the controller
     */
    public void init() {
        labelStart.setVisible(false);
        loadLogoVideo();
        loadDiscountVideo();
    }

    /**
     * Load the logo video after an aesthetic delay
     */
    private void loadLogoVideo() {
        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            loadVideo(mediaViewLogo, "/videos/Table5-cropped.mp4", 0);
        }).start();
    }

    /**
     * Load the discount video after an aesthetic delay
     */
    private void loadDiscountVideo() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            loadVideo(mediaViewDiscount, "/videos/10% Off-cropped.mp4", MediaPlayer.INDEFINITE);
        }).start();
    }

    /**
     * Load the intro videos and work around the fact that the video just doesn't load sometimes
     *
     * @param mediaView  the MediaView to display the video in
     * @param filename   the filename of the video
     * @param cycleCount the loop count
     */
    private void loadVideo(MediaView mediaView, String filename, int cycleCount) {
        new Thread(() -> {
            try {
                //if the video failed to load
                if (mediaView.getMediaPlayer() != null && mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.UNKNOWN) {
                    Thread.sleep(100);
                } else {
                    Platform.runLater(() -> {
                        FXUtils.fadeIn(mediaView);
                    });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //if the video hasn't been loaded or failed to load last time
            if (mediaView.getMediaPlayer() == null || mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.UNKNOWN) {
                Platform.runLater(() -> {
                    URL url = App.class.getResource(filename);
                    Media media = new Media(url.toString());
                    MediaPlayer player = new MediaPlayer(media);
                    player.setAutoPlay(true);
                    player.setCycleCount(cycleCount);

                    //single shot video gets looped manually after 20 seconds
                    if (cycleCount == 0) {
                        player.setOnEndOfMedia(() -> {
                            if (stopped || looping) return;
                            //end of media can be called multiple times
                            //only restart the playback once
                            looping = true;
                            //pulsate the start order button for extra effect
                            if (!firstPlay) FXUtils.pulsate(labelStart, 3);
                            firstPlay = false;
                            new Thread(() -> {
                                try {
                                    //use 1 second intervals so the app closes quicker
                                    for (int i = 0; i < 20; i++) {
                                        //quite if the app is closed
                                        if (App.terminated) return;
                                        Thread.sleep(1000);
                                    }
                                    loopVideo(mediaView);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    looping = false;
                                }
                            }).start();
                        });
                    }

                    mediaView.setMediaPlayer(player);
                    FXUtils.fadeIn(mediaView);
                    //call loadVideo again to check if the video actually loaded
                    loadVideo(mediaView, filename, cycleCount);
                });
            }
        }).start();
    }

    /**
     * Stop playing the videos
     */
    public void stopVideos() {
        stopped = true;
        if (mediaViewLogo.getMediaPlayer() != null) mediaViewLogo.getMediaPlayer().stop();
        if (mediaViewDiscount.getMediaPlayer() != null) mediaViewDiscount.getMediaPlayer().stop();
    }

    /**
     * Start playing the videos
     */
    public void startVideos() {
        stopped = false;
        firstPlay = true;
        if (mediaViewLogo.getMediaPlayer() != null) loopVideo(mediaViewLogo);
        if (mediaViewDiscount.getMediaPlayer() != null) loopVideo(mediaViewDiscount);
    }

    /**
     * Rewind and start playing the video loop
     */
    private void loopVideo(MediaView mediaView) {
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().seek(Duration.ZERO);
            mediaView.getMediaPlayer().play();
        }
    }

    /**
     * Update the dialog size
     *
     * @param width  new width of the window
     * @param height new height of the window
     */
    public void updateSize(double width, double height) {
        double size = Math.min(Math.max(Math.round(Math.min(width, height) * 0.75), 64), 720);
        double hsize = size * 0.5;

        mediaViewLogo.setFitWidth(size);
        mediaViewLogo.setFitHeight(hsize);

        gridPaneLogo.setPrefWidth(size);
        gridPaneLogo.setPrefHeight(hsize);

        size = hsize;
        hsize = size * 0.5;

        mediaViewDiscount.setFitWidth(size);
        mediaViewDiscount.setFitHeight(hsize);
        gridPaneDiscount.setPrefWidth(size);
        gridPaneDiscount.setPrefHeight(hsize);
    }
}
