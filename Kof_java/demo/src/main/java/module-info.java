module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.logging;
    // 开放控制器包给javafx.fxml
    opens com.example.demo.controller to javafx.fxml;
    // 如果需要FXMLLoader能实例化控制器类，还需导出包
    exports com.example.demo.controller;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}