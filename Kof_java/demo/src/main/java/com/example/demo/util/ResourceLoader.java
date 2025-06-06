package com.example.demo.util;


import javafx.scene.image.Image;


import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ResourceLoader {
    private static final Logger LOGGER = Logger.getLogger(ResourceLoader.class.getName());

    // 私有构造器防止实例化
    private ResourceLoader() {}

    /**
     * 安全加载图片资源
     * @param path 资源路径（以"/"开头，例如"/assets/character.png"）
     * @return 加载成功的Image对象，失败时返回null
     */

    public static Image loadImage(String path) {
        try {
            return new Image(Objects.requireNonNull(
                    ResourceLoader.class.getResourceAsStream(path),
                    "Resource not found: " + path
            ));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load image: " + path, e);
            return null;
        }
    }

    /**
     * 带默认值的图片加载
     * @param path 资源路径
     * @param defaultImage 加载失败时返回的默认图片
     */
    public static Image loadImage(String path, Image defaultImage) {
        Image img = loadImage(path);
        return img != null ? img : defaultImage;
    }
    public static InputStream loadResourceAsStream(String path) {
        return Objects.requireNonNull(
                ResourceLoader.class.getResourceAsStream(path),
                "Resource not found: " + path
        );}
}