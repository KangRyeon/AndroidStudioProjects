package com.example.mycloset.dto;


import java.io.Serializable;

@SuppressWarnings("serial")
public class FashionSetDTO implements Serializable {
    private String id;
    private String[] accessory;
    private String outer;
    private String bag;
    private String shoes;
    private String cap;
    private String upper;
    private String lower;

    public String getId() { return id; }
    public String[] getAccessory() { return accessory; }
    public String getOuter() { return outer; }
    public String getBag() { return bag; }
    public String getShoes() { return shoes; }
    public String getCap() { return cap; }
    public String getUpper() { return upper; }
    public String getLower() { return lower; }

    public void setId(String id) { this.id = id; }
    public void setAccessory(String[] accessory) { this.accessory = accessory; }
    public void setOuter(String outer) { this.outer = outer; }
    public void setBag(String bag) { this.bag = bag; }
    public void setShoes(String shoes) { this.shoes = shoes; }
    public void setCap(String cap) { this.cap = cap; }
    public void setUpper(String upper) { this.upper = upper; }
    public void setLower(String lower) { this.lower = lower; }
}
