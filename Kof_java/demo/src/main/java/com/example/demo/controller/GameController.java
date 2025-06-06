package com.example.demo.controller;

import com.example.demo.model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameController {
    // 游戏状态
    private final List<Fighter> players = new ArrayList<>();
    private Background background;
    private boolean isRunning = false;
    private GameMode gameMode;

    // 输入控制
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    // 游戏循环
    private GameLoop gameLoop;
    private Canvas canvas;
    private GraphicsContext gc;

    // 单例模式
    private static GameController instance;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    private GameController() {}

    // 初始化游戏
    public void initializeGame(GameMode mode, CharacterData player1Data, CharacterData player2Data, Canvas canvas) {
        this.gameMode = mode;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.isRunning = true;

        // 初始化背景
        this.background = new Background();

        // 创建玩家1
        Map<String, String> p1Controls = createPlayer1Controls();
        Fighter player1 = new Fighter(100, 400, 50, 100, player1Data, p1Controls);
        players.add(player1);

        // 创建玩家2（根据模式）
        if (mode == GameMode.PVP) {
            Map<String, String> p2Controls = createPlayer2Controls();
            Fighter player2 = new Fighter(650, 400, 50, 100, player2Data, p2Controls);
            players.add(player2);
        } else {
            // PVC模式创建AI对手
            Fighter player2 = new Fighter(650, 400, 50, 100, player2Data, Collections.emptyMap());
            players.add(player2);
            new AIController(player2, player1);
        }

        setupInputHandlers();
        startGameLoop();
    }

    private Map<String, String> createPlayer1Controls() {
        return Map.of(
                "left", "A", "right", "D", "jump", "W",
                "punch", "J", "kick", "K", "special", "U",
                "block", "H", "dash", "S"
        );
    }

    private Map<String, String> createPlayer2Controls() {
        return Map.of(
                "left", "LEFT", "right", "RIGHT", "jump", "UP",
                "punch", "NUMPAD1", "kick", "NUMPAD2",
                "special", "NUMPAD4", "block", "NUMPAD3",
                "dash", "DOWN"
        );
    }

    private void setupInputHandlers() {
        canvas.setFocusTraversable(true);

        canvas.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            handleInput();
        });

        canvas.setOnKeyReleased(e -> {
            pressedKeys.remove(e.getCode());
            handleInput();
        });
    }

    private void handleInput() {
        players.forEach(player -> {
            if (player.isDashing() || player.isBlocking() || player.isAttacking()) {
                return;
            }

            // 移动控制
            handleMovementInput(player);

            // 跳跃
            if (isKeyPressed(player, "jump") && player.canJump()) {
                player.jump();
            }

            // 攻击控制
            handleAttackInput(player);

            // 防御控制
            handleBlockInput(player);
        });
    }

    private void handleMovementInput(Fighter player) {
        boolean leftPressed = isKeyPressed(player, "left");
        boolean rightPressed = isKeyPressed(player, "right");

        if (leftPressed && !rightPressed) {
            player.moveLeft();
        } else if (rightPressed && !leftPressed) {
            player.moveRight();
        } else {
            player.stopMoving();
        }

        // 冲刺检测
        if (isKeyPressed(player, "dash") && player.canDash()) {
            player.dash();
        }
    }

    private void handleAttackInput(Fighter player) {
        if (isKeyPressed(player, "punch")) {
            player.attack("punch");
        } else if (isKeyPressed(player, "kick")) {
            player.attack("kick");
        } else if (isKeyPressed(player, "special")) {
            player.attack("special");
        }
    }

    private void handleBlockInput(Fighter player) {
        if (isKeyPressed(player, "block")) {
            player.block();
        } else if (player.isBlocking()) {
            player.stopBlocking();
        }
    }

    private boolean isKeyPressed(Fighter player, String action) {
        String key = player.getControl(action);
        return key != null && pressedKeys.contains(KeyCode.valueOf(key));
    }

    private void startGameLoop() {
        gameLoop = new GameLoop();
        gameLoop.start();
    }

    public void pauseGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public void resumeGame() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void endGame() {
        isRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public void update(double deltaTime) {
        background.update(deltaTime);
        players.forEach(player -> player.update(deltaTime));
        checkCollisions();
    }

    private void checkCollisions() {
        if (players.size() < 2) return;

        Fighter p1 = players.get(0);
        Fighter p2 = players.get(1);

        if (p1.isAttacking()) checkAttackHit(p1, p2);
        if (p2.isAttacking()) checkAttackHit(p2, p1);
    }

    private void checkAttackHit(Fighter attacker, Fighter defender) {
        double distance = Math.abs(attacker.getX() - defender.getX());
        if (distance < attacker.getCurrentAttackRange() && !defender.isBlocking()) {
            defender.takeHit(attacker.getCurrentAttackDamage(), attacker);
        }
    }

    public void render(GraphicsContext gc) {
        // 清空画布
        gc.setFill(Color.valueOf("#222222"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 绘制背景
        background.draw(gc);

        // 绘制地面
        gc.setFill(Color.valueOf("#555555"));
        gc.fillRect(0, 550, canvas.getWidth(), 50);

        // 绘制角色
        players.forEach(player -> player.draw(gc));

        // 绘制UI
        drawUI(gc);
    }

    private void drawUI(GraphicsContext gc) {
        if (players.isEmpty()) return;

        // 玩家1血条
        drawHealthBar(players.get(0), 50, 20, gc);

        // 玩家2血条（如果存在）
        if (players.size() > 1) {
            drawHealthBar(players.get(1), canvas.getWidth() - 250, 20, gc);
        }
    }

    private void drawHealthBar(Fighter player, double x, double y, GraphicsContext gc) {
        double width = 200;
        double height = 20;
        double healthPercent = player.getHealth() / 100.0;

        // 背景（红色）
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);

        // 血量（绿色）
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y, width * healthPercent, height);

        // 边框
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);

        // 文字
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(14));
        gc.fillText(String.format("%s: %d%%",
                        player.getCharacterData().getName(),
                        player.getHealth()),
                x + 5, y + 15);
    }

    public void showGameOver(GraphicsContext gc) {
        Fighter winner = players.stream()
                .filter(p -> p.getHealth() > 0)
                .findFirst()
                .orElse(null);

        if (winner != null) {
            this.gc.setFill(Color.WHITE);
            this.gc.setFont(new Font(48));
            this.gc.setTextAlign(TextAlignment.CENTER);
            this.gc.fillText("游戏结束!", canvas.getWidth()/2, canvas.getHeight()/2);

            this.gc.setFont(new Font(36));
            this.gc.fillText(winner.getCharacterData().getName() + "获胜!",
                    canvas.getWidth()/2, canvas.getHeight()/2 + 50);
        }
    }

    private class GameLoop extends AnimationTimer {
        private long lastTime = 0;

        @Override
        public void handle(long now) {
            if (lastTime == 0) {
                lastTime = now;
                return;
            }

            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            // 限制最大deltaTime防止卡顿
            deltaTime = Math.min(deltaTime, 0.1);

            update(deltaTime);
            render(gc);

            if (!isRunning) {
                stop();
                showGameOver(gc);
            }
        }
    }

    // Getters
    public boolean isRunning() { return isRunning; }
    public List<Fighter> getPlayers() { return players; }
    public static void startGame(GameMode mode,
                                 CharacterData player1Data,
                                 CharacterData player2Data,
                                 Stage primaryStage) {
        try {
            // 创建游戏画布和根布局
            Canvas gameCanvas = new Canvas(800, 600);
            Pane gameRoot = new Pane(gameCanvas);

            // 创建游戏场景
            Scene gameScene = new Scene(gameRoot);
            gameScene.getStylesheets().add(
                    GameController.class.getResource("/com/example/demo/css/style.css").toExternalForm()
            );

            // 初始化游戏控制器
            GameController controller = GameController.getInstance();
            controller.initializeGame(mode, player1Data, player2Data, gameCanvas);

            // 设置返回菜单的事件处理
            gameScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    returnToMenu(primaryStage);
                }
            });

            // 切换到游戏场景
            primaryStage.setScene(gameScene);
            gameCanvas.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static void returnToMenu(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GameController.class.getResource("/fxml/menu.fxml")
            );
            Parent root = loader.load();

            Scene menuScene = new Scene(root, 800, 600);
            menuScene.getStylesheets().add(
                    GameController.class.getResource("/css/style.css").toExternalForm()
            );

            primaryStage.setScene(menuScene);
            primaryStage.setTitle("简易拳皇 - 角色选择");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}