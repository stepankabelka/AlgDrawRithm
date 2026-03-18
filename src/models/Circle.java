package models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Circle implements Shape {
    private Point center;
    private int radius;
    private Color color;
    private int thickness;
    private LineStyle style;
    private boolean filled;
    private Color fillColor;

    public Circle(Point center, int radius, Color color, int thickness, LineStyle style) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
        this.style = style;
        this.filled = false;
        this.fillColor = color;
    }

    @Override
    public void draw(java.util.function.BiConsumer<Integer, Integer> setPixel) {
        // Bresenham's circle algorithm
        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        if (filled) {
            drawFilledCirclePoints(x, y, setPixel);
        } else {
            drawCirclePoints(x, y, setPixel);
        }

        while (y >= x) {
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
            if (filled) {
                drawFilledCirclePoints(x, y, setPixel);
            } else {
                drawCirclePoints(x, y, setPixel);
            }
        }
    }

    private void drawCirclePoints(int x, int y, java.util.function.BiConsumer<Integer, Integer> setPixel) {
        drawThickPoint(center.getX() + x, center.getY() + y, setPixel);
        drawThickPoint(center.getX() - x, center.getY() + y, setPixel);
        drawThickPoint(center.getX() + x, center.getY() - y, setPixel);
        drawThickPoint(center.getX() - x, center.getY() - y, setPixel);
        drawThickPoint(center.getX() + y, center.getY() + x, setPixel);
        drawThickPoint(center.getX() - y, center.getY() + x, setPixel);
        drawThickPoint(center.getX() + y, center.getY() - x, setPixel);
        drawThickPoint(center.getX() - y, center.getY() - x, setPixel);
    }

    private void drawFilledCirclePoints(int x, int y, java.util.function.BiConsumer<Integer, Integer> setPixel) {
        for (int i = -x; i <= x; i++) {
            setPixel.accept(center.getX() + i, center.getY() + y);
            setPixel.accept(center.getX() + i, center.getY() - y);
        }
        for (int i = -y; i <= y; i++) {
            setPixel.accept(center.getX() + i, center.getY() + x);
            setPixel.accept(center.getX() + i, center.getY() - x);
        }
    }

    private void drawThickPoint(int x, int y, java.util.function.BiConsumer<Integer, Integer> setPixel) {
        int half = thickness / 2;
        for (int dy = -half; dy <= half; dy++) {
            for (int dx = -half; dx <= half; dx++) {
                setPixel.accept(x + dx, y + dy);
            }
        }
    }

    @Override
    public boolean contains(int x, int y) {
        int dx = x - center.getX();
        int dy = y - center.getY();
        return dx * dx + dy * dy <= (radius + 10) * (radius + 10);
    }

    @Override
    public void move(int dx, int dy) {
        center = new Point(center.getX() + dx, center.getY() + dy);
    }

    @Override
    public void resize(Point p1, Point p2) {
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();
        radius = (int) Math.sqrt(dx * dx + dy * dy);
    }
    @Override
    public ResizeHandle getResizeHandle(int x, int y) {
        int handleSize = 8;

        int rightX = center.getX() + radius;
        if (Math.abs(x - rightX) <= handleSize && Math.abs(y - center.getY()) <= handleSize) {
            return ResizeHandle.RIGHT;
        }

        return ResizeHandle.NONE;
    }

    @Override
    public Color getColor() { return color; }

    @Override
    public void setColor(Color color) { this.color = color; }

    @Override
    public int getThickness() { return thickness; }

    @Override
    public void setThickness(int thickness) { this.thickness = thickness; }

    @Override
    public LineStyle getStyle() { return style; }

    @Override
    public void setStyle(LineStyle style) { this.style = style; }

    @Override
    public boolean isFilled() { return filled; }

    @Override
    public void setFilled(boolean filled) { this.filled = filled; }

    @Override
    public Color getFillColor() { return fillColor; }

    @Override
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; }

    @Override
    public List<Point> getPoints() {
        List<Point> points = new ArrayList<>();
        points.add(center);
        return points;
    }


    @Override
    public void resizeByHandle(ResizeHandle handle, int dx, int dy) {

    }

    @Override
    public models.Rectangle getBounds() {
        Point topLeft = new Point(center.getX() - radius, center.getY() - radius);
        Point bottomRight = new Point(center.getX() + radius, center.getY() + radius);
        return new models.Rectangle(topLeft, bottomRight, color, thickness, style);
    }
}