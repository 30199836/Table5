package com.searesoft.lib;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * JavaFX node based stuff
 */
public class FXUtils {
    public static BufferedImage snapshot(Node node) {
        SnapshotParameters param = new SnapshotParameters();
        param.setDepthBuffer(true);
        WritableImage snapshot = node.snapshot(param, null);
        BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);
        BufferedImage img = null;
        byte[] imageInByte;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(tempImg, "png", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
            InputStream in = new ByteArrayInputStream(imageInByte);
            img = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public static void snapshot(Node node,String filename) {
        SnapshotParameters param = new SnapshotParameters();
        param.setDepthBuffer(true);
        WritableImage snapshot = node.snapshot(param, null);
        BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);
        try {
            File outputFile = new File(filename);
            ImageIO.write(tempImg, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        node.setVisible(true);
        new Thread(() -> {
            fadeInBlocking(node);
        }).start();
    }

    public static void fadeOut(Node node, boolean hide) {
        new Thread(() -> {
            fadeOutBlocking(node, hide, null);
        }).start();
    }

    public static void fadeOut(Node node, Pane parent) {
        new Thread(() -> {
            fadeOutBlocking(node, false, parent);
        }).start();
    }

    public static void fadeOut(Node node) {
        new Thread(() -> {
            fadeOutBlocking(node, false, null);
        }).start();
    }

    public static void fadeOut(Node node, boolean hide, Pane parent) {
        new Thread(() -> {
            fadeOutBlocking(node, hide, parent);
        }).start();
    }

    public static void pulsate(Node node, int count) {
        node.setVisible(true);
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                fadeOutBlocking(node, false, null);
                fadeInBlocking(node);
            }
        }).start();
    }

    private static void fadeInBlocking(Node node) {
        while (node.getOpacity() < 1) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    node.setOpacity(1);
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
                    node.setOpacity(1);
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


}
