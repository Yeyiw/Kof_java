package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class GameApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/com/example/demo/fxml/menu.fxml");
        System.out.println("FXML 文件路径: " + fxmlUrl); // 应输出非 null
        Parent root = FXMLLoader.load(fxmlUrl);
        // 加载菜单界面
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/demo/fxml/menu.fxml")
        );


        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/demo/css/style.css").toExternalForm()
        );

        primaryStage.setTitle("简易拳皇 - 角色选择");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}