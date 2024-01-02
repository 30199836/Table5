package com.searesoft.lib;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * JavaFX node based stuff
 */
public class FXUtils {
    public static Image snapshot(Node node) {
        SnapshotParameters param = new SnapshotParameters();
        param.setDepthBuffer(true);
        return node.snapshot(param, null);
    }

    public static boolean snapshot(Node node, String filename) throws IOException {
        String formatName = StrUtils.extractFileExtension(filename);
        if (formatName == null) return false;
        Image fxImg = snapshot(node);
        //remove the alpha channel so we can support jpeg, it's not needed anyway.
        BufferedImage img = new BufferedImage((int) fxImg.getWidth(), (int) fxImg.getHeight(), BufferedImage.TYPE_INT_RGB);
        SwingFXUtils.fromFXImage(fxImg, img);
        return ImageIO.write(img, formatName, new File(filename));
    }

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        node.setVisible(true);
        new Thread(() -> {
            fadeInBlocking(node,1);
        }).start();
    }

    public static void fadeIn(Node node ,double opacity) {
        node.setOpacity(0);
        node.setVisible(true);
        new Thread(() -> {
            fadeInBlocking(node,opacity);
        }).start();
    }

    public static void fadeOut(Node node, boolean hide) {
        fadeOut(node, hide, null);
    }

    public static void fadeOut(Node node, Pane parent) {
        fadeOut(node, false, parent);
    }

    public static void fadeOut(Node node) {
        fadeOut(node, false, null);
    }

    public static void fadeOut(Node node, boolean hide, Pane parent) {
        new Thread(() -> {
            fadeOutBlocking(node, hide, parent);
        }).start();
    }

    public static void pulsate(Node node, int count) {
        pulsate(node, count, false);
    }

    public static void pulsate(Node node, int count, boolean fadeIn) {
        if (fadeIn) node.setOpacity(0);
        node.setVisible(true);
        new Thread(() -> {
            if (fadeIn) fadeInBlocking(node,1);
            for (int i = 0; i < count; i++) {
                fadeOutBlocking(node, false, null);
                fadeInBlocking(node,1);
            }
        }).start();
    }

    private static void fadeInBlocking(Node node,double opacity) {
        while (node.getOpacity() < opacity) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    node.setOpacity(opacity);
                });
                return;
            }
            Semaphore semaphore = new Semaphore(0);
            Platform.runLater(() -> {
                node.setOpacity(node.getOpacity() + 0.05);
                semaphore.release();
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                semaphore.release();
                Platform.runLater(() -> {
                    node.setOpacity(opacity);
                });
                return;
            }
        }
        Platform.runLater(() -> {
            node.setOpacity(1);
        });
    }

    private static void fadeOutBlocking(Node node, boolean hide, Pane parent) {
        while (node.getOpacity() > 0) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    node.setOpacity(0);
                });
                return;
            }
            Semaphore semaphore = new Semaphore(0);
            Platform.runLater(() -> {
                node.setOpacity(node.getOpacity() - 0.02);
                semaphore.release();
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    node.setOpacity(0);
                });
                return;
            }
        }
        Platform.runLater(() -> {
            node.setOpacity(0);
            if (hide) node.setVisible(false);
            if (parent != null) parent.getChildren().remove(parent.getChildren().indexOf(node));
        });
    }

    public static void setOpacityTree(Parent parent, double opacity) {
        parent.setOpacity(opacity);
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Parent) {
                setOpacityTree((Parent) child, opacity);
            } else {
                child.setOpacity(opacity);
            }
        }
    }
}
