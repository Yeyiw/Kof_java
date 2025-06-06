package com.example.demo.controller;



import com.example.demo.model.Fighter;

public class AIController {
    private final Fighter aiPlayer;
    private final Fighter opponent;
    private double actionTimer = 0;
    private final double actionInterval = 0.1;
    private final double aggressiveness = 0.7;

    public AIController(Fighter aiPlayer, Fighter opponent) {
        this.aiPlayer = aiPlayer;
        this.opponent = opponent;
    }

    public void update(double deltaTime) {
        actionTimer += deltaTime;

        if (actionTimer >= actionInterval) {
            actionTimer = 0;
            makeDecision();
        }
    }

    private void makeDecision() {
        double distance = Math.abs(aiPlayer.getX() - opponent.getX());

        if (distance < 150) { // 近距离
            if (Math.random() < aggressiveness) {
                attackDecision();
            } else {
                defensiveDecision();
            }
        } else if (distance < 300) { // 中距离
            if (Math.random() < 0.9) {
                moveTowardOpponent();
            } else {
                defensiveDecision();
            }
        } else { // 远距离
            moveTowardOpponent();
        }
    }

    private void attackDecision() {
        double random = Math.random();
        if (random < 0.5) {
            aiPlayer.attack("punch");
        } else if (random < 0.8) {
            aiPlayer.attack("kick");
        } else {
            aiPlayer.attack("special");
        }
    }

    private void defensiveDecision() {
        if (Math.random() < 0.7) {
            aiPlayer.block();
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            aiPlayer.stopBlocking();
                        }
                    },
                    500
            );
        } else {
            // 后跳躲避
            if (!aiPlayer.isJumping()) {
                aiPlayer.jumpBack();
            }
        }
    }

    private void moveTowardOpponent() {
        if (aiPlayer.isJumping() || aiPlayer.isAttacking()) return;

        if (aiPlayer.getX() < opponent.getX() - 50) {
            aiPlayer.moveRight();
        } else if (aiPlayer.getX() > opponent.getX() + 50) {
            aiPlayer.moveLeft();
        } else {
            aiPlayer.stopMoving();
        }

        // 随机冲刺
        if (Math.random() < 0.1 && aiPlayer.canDash()) {
            aiPlayer.dash();
        }
    }
}
