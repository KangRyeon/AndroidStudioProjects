package com.example.mycloset.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ClothesDTO implements Serializable {
    private String id;
    private String dress_number;
    private String category;
    private String low_category;
    private String color;
    private String pattern;
    private String lengthC;

    public ClothesDTO(String id, String dress_number, String category, String low_category, String color, String pattern, String lengthC) {
        this.id = id;
        this.dress_number = dress_number;
        this.category = category;
        this.low_category = low_category;
        this.color = color;
        this.pattern = pattern;
        this.lengthC = lengthC;
    }

    public String getId() { return id; }
    public String getDress_number() { return dress_number; }
    public String getCategory() { return category; }
    public String getLow_category() { return low_category; }
    public String getColor() { return color; }
    public String getPattern() { return pattern; }
    public String getLength() { return lengthC; }

    public void setId(String id) { this.id = id; }
    public void setDress_number(String dress_number) { this.dress_number = dress_number; }
    public void setCategory(String category) { this.category = category; }
    public void setLow_category(String low_category) { this.low_category = low_category; }
    public void setColor(String color) { this.color = color; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public void setLength(String lengthC) { this.lengthC = lengthC; }
}
