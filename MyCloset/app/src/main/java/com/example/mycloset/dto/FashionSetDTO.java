package com.example.mycloset.dto;


public class FashionSetDTO {
    private String[] accessory;
    private String outer;
    private String bag;
    private String shose;
    private String cap;
    private String upper;
    private String lower;

    public String[] getAccessory() { return accessory; }
    public String getOuter() { return outer; }
    public String getBag() { return bag; }
    public String getShose() { return shose; }
    public String getCap() { return cap; }
    public String getUpper() { return upper; }
    public String getLower() { return lower; }

    public void setAccessory(String[] accessory) { this.accessory = accessory; }
    public void setOuter(String outer) { this.outer = outer; }
    public void setBag(String bag) { this.bag = bag; }
    public void setShose(String shose) { this.shose = shose; }
    public void setCap(String cap) { this.cap = cap; }
    public void setUpper(String upper) { this.upper = upper; }
    public void setLower(String lower) { this.lower = lower; }
}
