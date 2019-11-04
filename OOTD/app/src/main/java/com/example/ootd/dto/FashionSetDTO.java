package com.example.ootd.dto;


import java.io.Serializable;

@SuppressWarnings("serial")
public class FashionSetDTO implements Serializable {
    private String id;
    private String set_name;
    private String outerC;
    private String upperC;
    private String lowerC;
    private String cap;
    private String bag;
    private String shoes;
    private String accessory1;
    private String accessory2;
    private String accessory3;

    public FashionSetDTO() {
    }

    public FashionSetDTO(String id, String set_name, String outerC, String upperC, String lowerC, String cap, String bag, String shoes, String accessory1, String accessory2, String accessory3) {
        this.id = id;
        this.set_name = set_name;
        this.outerC = outerC;
        this.upperC = upperC;
        this.lowerC = lowerC;
        this.cap = cap;
        this.bag = bag;
        this.shoes = shoes;
        this.accessory1 = accessory1;
        this.accessory2 = accessory2;
        this.accessory3 = accessory3;
    }

    public String getId() { return id; }
    public String getSet_name() { return set_name; }
    public String getOuter() { return outerC; }
    public String getBag() { return bag; }
    public String getShoes() { return shoes; }
    public String getCap() { return cap; }
    public String getUpper() { return upperC; }
    public String getLower() { return lowerC; }
    public String getAccessory1() { return accessory1; }
    public String getAccessory2() { return accessory2; }
    public String getAccessory3() { return accessory3; }

    public void setId(String id) { this.id = id; }
    public void setSet_name(String set_name) { this.set_name = set_name; }
    public void setOuter(String outer) { this.outerC = outer; }
    public void setBag(String bag) { this.bag = bag; }
    public void setShoes(String shoes) { this.shoes = shoes; }
    public void setCap(String cap) { this.cap = cap; }
    public void setUpper(String upper) { this.upperC = upper; }
    public void setLower(String lower) { this.lowerC = lower; }
    public void setAccessory1(String accessory1) { this.accessory1 = accessory1; }
    public void setAccessory2(String accessory2) { this.accessory2 = accessory2; }
    public void setAccessory3(String accessory3) { this.accessory3 = accessory3; }

}
