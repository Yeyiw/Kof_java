package com.example.demo.model;


import javafx.scene.canvas.GraphicsContext;

public abstract class Character {
    private final CharacterData characterData;

    public Character(CharacterData characterData) {
        this.characterData = characterData;
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public abstract void update(double deltaTime);
    public abstract void draw(GraphicsContext gc);
}