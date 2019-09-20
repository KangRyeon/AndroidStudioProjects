package com.example.mycloset.dto;


import java.io.Serializable;

@SuppressWarnings("serial")
public class FashionSetDTO implements Serializable {
    private String id;
    private String set_name;
    private String accessory1;
    private String accessory2;
    private String accessory3;
    private String outer;
    private String bag;
    private String shoes;
    private String cap;
    private String upper;
    private String lower;

    public String getId() { return id; }
    public String getSet_name() { return set_name; }
    public String getAccessory1() { return accessory1; }
    public String getAccessory2() { return accessory2; }
    public String getAccessory3() { return accessory3; }
    public String getOuter() { return outer; }
    public String getBag() { return bag; }
    public String getShoes() { return shoes; }
    public String getCap() { return cap; }
    public String getUpper() { return upper; }
    public String getLower() { return lower; }

    public void setId(String id) { this.id = id; }
    public void setSet_name(String set_name) { this.set_name = set_name; }
    public void setAccessory1(String accessory1) { this.accessory1 = accessory1; }
    public void setAccessory2(String accessory2) { this.accessory2 = accessory2; }
    public void setAccessory3(String accessory3) { this.accessory3 = accessory3; }
    public void setOuter(String outer) { this.outer = outer; }
    public void setBag(String bag) { this.bag = bag; }
    public void setShoes(String shoes) { this.shoes = shoes; }
    public void setCap(String cap) { this.cap = cap; }
    public void setUpper(String upper) { this.upper = upper; }
    public void setLower(String lower) { this.lower = lower; }

}
