package de.sfn_kassel.robihei.nbodysim;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by robin on 14.05.15.
 */
public class SObject {
    private double x = 0, y = 0;
    private double oldX = 0, oldY = 0;
    private double mass = 0;
    private double aX = 0, aY = 0;
    private Color c = null;
    private double radius = 0;

    public SObject(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.oldX = x;
        this.oldY = y;
        this.radius = radius;
        this.mass = radius * radius * radius * 0.1;
        this.c = color;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Color getC() {
        return c;
    }

    public void setC(Color c) {
        this.c = c;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        this.mass = radius * radius * radius * 0.1;
    }

    public boolean contains(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        double r = Math.sqrt(dx * dx + dy * dy);

        return r < this.radius;
    }

    public double getOldX() {
        return oldX;
    }

    public void setOldX(double oldX) {
        this.oldX = oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public void setOldY(double oldY) {
        this.oldY = oldY;
    }

    public void addOldX(double oldX) {
        this.oldX += oldX;
    }

    public void addOldY(double oldY) {
        this.oldY += oldY;
    }

    public double getaX() {
        return aX;
    }

    public void setaX(double aX) {
        this.aX = aX;
    }

    public double getaY() {
        return aY;
    }

    public void setaY(double aY) {
        this.aY = aY;
    }

    public void addaX(double aX) {
        this.aX += aX;
    }

    public void addaY(double aY) {
        this.aY += aY;
    }

    public double getMass() {
        return mass;
    }

    public void addMass(double mass) {
        this.mass += mass;
        this.radius = Math.pow(10 * this.mass, 0.33333333333);
    }
}
