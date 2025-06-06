package com.example.demo.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Background {
    private final Image image;
    private final int frameWidth = 800;
    private final int frameHeight = 428;
    private double currentFrame = 0;
    private final int totalFrames = 11;
    private final double animationSpeed = 0.1;

    public Background() {
        this.image = new Image(getClass().getResourceAsStream(
                "/com/example/demo/assets/backgrounds/battle_spritesheet.png"));
    }

    public void update(double deltaTime) {
        currentFrame += animationSpeed * deltaTime * 60;
        if (currentFrame >= totalFrames) {
            currentFrame = 0;
        }
    }

    public void draw(GraphicsContext gc) {
        int frameX = (int)currentFrame * frameWidth;
        gc.drawImage(image,
                frameX, 0, frameWidth, frameHeight,
                0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}