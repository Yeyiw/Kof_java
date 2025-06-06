package com.example.demo.model;
import com.example.demo.util.FrameData;
import javafx.scene.image.Image;
import java.util.Map;


import com.example.demo.util.Animation;
import com.example.demo.util.ResourceLoader;

import java.util.HashMap;



import com.example.demo.util.FrameData;
import java.util.Map;

public class CharacterData {
    private final String name;
    private final String id;
    private final String color;
    private final String description;
    private final Map<String, FrameData> frameData;

    public CharacterData(String name, String id, String color,
                         String description, Map<String, FrameData> frameData) {
        this.name = name;
        this.id = id;
        this.color = color;
        this.description = description;
        this.frameData = frameData;
    }

    public CharacterData(CharacterData character) {
        this.name = character.getName();
        this.id = character.getId();
        this.color = character.getColor();
        this.description = character.getDescription();
        this.frameData = character.getFrameData();

    }


    // Getters
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, FrameData> getFrameData() {
        return frameData;
    }

}