package org.example.common.data;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer x; // Максимальное значение поля: 629, Поле не может быть null
    private double y;

    public Coordinates(Integer x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {}

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        String coordinateString = "Coordinates \n";
        coordinateString += "\t x: " + x + "\n";
        coordinateString += "\t y: " + y;
        return coordinateString;
    }
}
