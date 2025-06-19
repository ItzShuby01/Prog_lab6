package org.example.common.data;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private float x;
    private Float y; // Поле не может быть null
    private String name; // Длина строки не должна быть больше 530, Поле может быть null

    public Location(float x, Float y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public Location() {}

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String locationString = "Location \n";
        locationString += "\t x: " + x + "\n";
        locationString += "\t y: " + y + "\n";
        locationString += "\t name: " + name;
        return locationString;
    }
}

