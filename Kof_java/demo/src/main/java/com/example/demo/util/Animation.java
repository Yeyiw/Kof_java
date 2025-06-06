package com.example.demo.util;


import javafx.scene.image.Image;

public class Animation {
    private Image[] frames;
    private double frameDuration;
    private double currentTime;
    private int currentFrameIndex;

    public Animation(Image[] frames, double frameDuration) {
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.currentTime = 0;
        this.currentFrameIndex = 0;
    }

    public void update(double deltaTime) {
        currentTime += deltaTime;
        if (currentTime >= frameDuration) {
            currentTime -= frameDuration;
            currentFrameIndex = (currentFrameIndex + 1) % frames.length;
        }
    }

    public Image getCurrentFrame() {
        return frames[currentFrameIndex];
    }

    public void reset() {
        currentTime = 0;
        currentFrameIndex = 0;
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    public Image[] getFrames() {
        return frames;
    }
}