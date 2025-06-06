package com.example.demo.model;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import com.example.demo.controller.GameController;

public class GameLoop extends AnimationTimer {
    private final GameController gameController;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private long lastTime;

    public GameLoop(GameController gameController, Canvas canvas) {
        this.gameController = gameController;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void handle(long now) {
        // 计算时间差（秒）
        double deltaTime = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        // 限制最大deltaTime防止卡顿导致的异常
        deltaTime = Math.min(deltaTime, 0.1);

        // 更新游戏状态
        gameController.update(deltaTime);

        // 清空画布
        gc.setFill(javafx.scene.paint.Color.valueOf("#222222"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 绘制游戏
        gameController.render(gc);

        // 检查游戏结束
        if (!gameController.isRunning()) {
            stop();
            gameController.showGameOver(gc);
        }
    }

    @Override
    public void start() {
        lastTime = System.nanoTime();
        super.start();
    }
}
