package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.util.FrameData;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuController {
    @FXML private VBox characterSelect;
    @FXML private HBox gameContainer;
    @FXML private Button startButton;
    @FXML private Button modePvP;
    @FXML private Button modePvC;
    @FXML private VBox player1Selection;
    @FXML private VBox player2Selection;


    private GameMode gameMode = GameMode.PVP;
    private final List<CharacterData> availableCharacters = new ArrayList<>();
    private CharacterData player1Selected;
    private CharacterData player2Selected;

    public void initialize() {
        initializeCharacters();
        setupModeSelection();
        setupCharacterSelection();
        setupStartButton();
    }

    private void initializeCharacters() {
        // 八神庵
        Map<String, FrameData> ioriFrames = new HashMap<>();
        ioriFrames.put("idle", new FrameData(9, 8));
        ioriFrames.put("walk", new FrameData(10, 4));
        ioriFrames.put("walkBack", new FrameData(9, 8));
        ioriFrames.put("dash", new FrameData(8, 4));
        ioriFrames.put("dashBack", new FrameData(12, 4));
        ioriFrames.put("punch", new FrameData(8, 4));
        ioriFrames.put("kick", new FrameData(10, 4));
        ioriFrames.put("jump", new FrameData(13, 2));
        ioriFrames.put("block", new FrameData(1, 8));
        ioriFrames.put("hit", new FrameData(4, 6));


        availableCharacters.add(new CharacterData(
                "八神庵", "iori", "purple",
                "八神庵是《拳皇》系列中的经典角色...",
                ioriFrames
        ));

        Map<String, FrameData> andyFrames = new HashMap<>();
        andyFrames.put("idle", new FrameData(11, 8));
        andyFrames.put("walk", new FrameData(5, 4));
        andyFrames.put("walkBack", new FrameData(5, 8));
        andyFrames.put("dash", new FrameData(6, 4));
        andyFrames.put("dashBack", new FrameData(6, 4));
        andyFrames.put("punch", new FrameData(6, 4));
        andyFrames.put("kick", new FrameData(3, 8));
        andyFrames.put("jump", new FrameData(13, 2));
        andyFrames.put("block", new FrameData(1, 8));
        andyFrames.put("hit", new FrameData(4, 6));

        availableCharacters.add(new CharacterData(
                "安迪", "andy", "red",
                "安迪是一位勇敢的战士，擅长使用各种武器进行战斗。",
                andyFrames
        ));

        Map<String, FrameData> billyFrames = new HashMap<>();
        billyFrames.put("idle", new FrameData(6, 8));
        billyFrames.put("walk", new FrameData(6, 4));
        billyFrames.put("walkBack", new FrameData(6, 8));
        billyFrames.put("dash", new FrameData(6, 4));
        billyFrames.put("dashBack", new FrameData(5, 6));
        billyFrames.put("punch", new FrameData(8, 4));
        billyFrames.put("kick", new FrameData(6, 4));
        billyFrames.put("jump", new FrameData(12, 2));
        billyFrames.put("block", new FrameData(1, 8));
        billyFrames.put("hit", new FrameData(4, 6));

        availableCharacters.add(new CharacterData(
                "比利", "billy", "pink",
                "吉斯·霍华德的忠实部下，擅长使用三节棍的狠辣打手。",
                billyFrames
        ));

        Map<String, FrameData> yuriFrames = new HashMap<>();
        yuriFrames.put("idle", new FrameData(6, 8));
        yuriFrames.put("walk", new FrameData(4, 4));
        yuriFrames.put("walkBack", new FrameData(4, 8));
        yuriFrames.put("dash", new FrameData(6, 4));
        yuriFrames.put("dashBack", new FrameData(12, 4));
        yuriFrames.put("punch", new FrameData(4, 6));
        yuriFrames.put("kick", new FrameData(4, 8));
        yuriFrames.put("jump", new FrameData(14, 1.5f));
        yuriFrames.put("block", new FrameData(1, 8));
        yuriFrames.put("hit", new FrameData(3, 6));

        availableCharacters.add(new CharacterData(
                "坂崎尤莉", "yuri", "blue",
                "坂崎尤莉是一位活泼开朗的少女，擅长使用空手道进行战斗。",
                yuriFrames
        ));
    }

    private void setupModeSelection() {
        if (modePvP == null) {
            throw new IllegalStateException("FXML未成功注入modePvP按钮！");
        }
        modePvP.setOnAction(e -> {
            gameMode = GameMode.PVP;
            modePvP.getStyleClass().add("active");
            modePvC.getStyleClass().remove("active");
            player2Selection.setDisable(false);
        });

        modePvC.setOnAction(e -> {
            gameMode = GameMode.PVC;
            modePvC.getStyleClass().add("active");
            modePvP.getStyleClass().remove("active");
            player2Selection.setDisable(true);
            // 自动为玩家2选择随机角色
            player2Selected = availableCharacters.get(
                    (int)(Math.random() * availableCharacters.size()));
        });
    }

    private void setupCharacterSelection() {
        availableCharacters.forEach(character -> {
            player1Selection.getChildren().add(createCharacterOption(character, true));
            player2Selection.getChildren().add(createCharacterOption(character, false));
        });
    }

    private VBox createCharacterOption(CharacterData character, boolean isPlayer1) {
        VBox option = new VBox(5);
        option.getStyleClass().add("character-option");
        ImageView imageView;
        try {
            // 使用前斜杠和完整路径
            String path = "/com/example/demo/assets/characters/" +
                    character.getId() + "/" + character.getId() + "_preview.png";

            // 使用类加载器获取资源URL
            URL imgUrl = getClass().getResource(path);
            if (imgUrl == null) {
                throw new FileNotFoundException("找不到角色图片: " + path);
            }

            imageView = new ImageView(new Image(imgUrl.toExternalForm()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(150);
        } catch (Exception e) {
            System.err.println("创建角色选项失败: " + e.getMessage());
            return new VBox(new Label("加载失败"));
        }


        Label nameLabel = new Label(character.getName());
        Label descLabel = new Label(character.getDescription());
        descLabel.getStyleClass().add("tooltip");
        descLabel.setVisible(false);

        option.getChildren().addAll(imageView, nameLabel);

        option.setOnMouseEntered(e -> descLabel.setVisible(true));
        option.setOnMouseExited(e -> descLabel.setVisible(false));

        option.setOnMouseClicked(e -> {
            if (isPlayer1) {
                player1Selected = character;
            } else {
                player2Selected = character;
            }
            // 更新选中状态
            clearSelections(isPlayer1);
            option.getStyleClass().add("selected");
        });

        return option;
    }

    private void clearSelections(boolean isPlayer1) {
        VBox selectionBox = isPlayer1 ? player1Selection : player2Selection;
        selectionBox.getChildren().forEach(node ->
                node.getStyleClass().remove("selected"));
    }

    private void setupStartButton() {
        startButton.setOnAction(e -> {
            // 验证角色选择
            if (player1Selected == null) {
                showAlert("请选择玩家1的角色！");
                return;
            }

            if (gameMode == GameMode.PVP && player2Selected == null) {
                showAlert("请选择玩家2的角色！");
                return;
            }

            if (gameMode == GameMode.PVP &&
                    player1Selected.getId().equals(player2Selected.getId())) {
                showAlert("两位玩家不能选择相同的角色！");
                return;
            }

            // 获取当前舞台
            Stage primaryStage = (Stage) startButton.getScene().getWindow();

            // 启动游戏
            GameController.startGame(
                    gameMode,
                    player1Selected,
                    gameMode == GameMode.PVP ? player2Selected : null,
                    primaryStage
            );
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}