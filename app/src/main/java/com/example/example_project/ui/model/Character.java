package com.example.example_project.ui.model;

import java.io.Serializable;

public class Character implements Serializable {

    private String name;
    private String level;
    private int icon;
    private String strength;
    private String agility;
    private String intellect;
    private String will;
    private String email;
    private String id;

    // keep this constructor empty for Firebase
    public Character() {
    }

    public Character(String name, String level, int icon, String strength, String agility, String intellect, String will, String email) {
        this.name = name;
        this.level = level;
        this.icon = icon;
        this.strength = strength;
        this.agility = agility;
        this.intellect = intellect;
        this.will = will;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public int getIcon() {
        return icon;
    }


    public String getStrength() {
        return strength;
    }

    public String getAgility() {
        return agility;
    }

    public String getIntellect() {
        return intellect;
    }

    public String getWill() {
        return will;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
