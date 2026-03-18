package models;

import java.awt.*;

public class Line {

    private Point p1, p2;
    private Color color;
    private int thickness = 1;
    private LineStyle style = LineStyle.SOLID;

    public Line(Point p1, Point p2, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }

    public Line(Point p1, Point p2, Color color, int thickness, LineStyle style) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.thickness = thickness;
        this.style = style;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public LineStyle getStyle() {
        return style;
    }

    public void setStyle(LineStyle style) {
        this.style = style;
    }
}