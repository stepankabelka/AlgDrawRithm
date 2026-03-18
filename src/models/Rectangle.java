package models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Rectangle implements Shape {
    private Point p1, p2;
    private Color color;
    private int thickness;
    private LineStyle style;
    private boolean filled;
    private Color fillColor;

    public Rectangle(Point p1, Point p2, Color color, int thickness, LineStyle style) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.thickness = thickness;
        this.style = style;
        this.filled = false;
        this.fillColor = color;
    }

    @Override
    public void draw(java.util.function.BiConsumer<Integer, Integer> setPixel) {
        int x1 = Math.min(p1.getX(), p2.getX());
        int y1 = Math.min(p1.getY(), p2.getY());
        int x2 = Math.max(p1.getX(), p2.getX());
        int y2 = Math.max(p1.getY(), p2.getY());

        if (filled) {
            for (int y = y1; y <= y2; y++) {
                for (int x = x1; x <= x2; x++) {
                    setPixel.accept(x, y);
                }
            }
        } else {

            for (int x = x1; x <= x2; x++) {
                drawThickPoint(x, y1, setPixel);
                drawThickPoint(x, y2, setPixel);
            }

            for (int y = y1; y <= y2; y++) {
                drawThickPoint(x1, y, setPixel);
                drawThickPoint(x2, y, setPixel);
            }
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
        int x1 = Math.min(p1.getX(), p2.getX()) - 10;
        int y1 = Math.min(p1.getY(), p2.getY()) - 10;
        int x2 = Math.max(p1.getX(), p2.getX()) + 10;
        int y2 = Math.max(p1.getY(), p2.getY()) + 10;
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    @Override
    public void move(int dx, int dy) {
        p1 = new Point(p1.getX() + dx, p1.getY() + dy);
        p2 = new Point(p2.getX() + dx, p2.getY() + dy);
    }

    @Override
    public void resize(Point newP1, Point newP2) {
        this.p1 = newP1;
        this.p2 = newP2;
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
        points.add(p1);
        points.add(p2);
        return points;
    }

    @Override
    public ResizeHandle getResizeHandle(int x, int y) {
        int handleSize = 20;
        int y1 = Math.min(p1.getY(), p2.getY());
        int x2 = Math.max(p1.getX(), p2.getX());

        if (Math.abs(x - x2) <= handleSize && Math.abs(y - y1) <= handleSize) {
            return ResizeHandle.TOP_RIGHT;
        }

        return ResizeHandle.NONE;
    }

    @Override
    public void resizeByHandle(ResizeHandle handle, int dx, int dy) {
        if (handle == ResizeHandle.TOP_RIGHT) {
            int x1 = Math.min(p1.getX(), p2.getX());
            int y1 = Math.min(p1.getY(), p2.getY());
            int x2 = Math.max(p1.getX(), p2.getX());
            int y2 = Math.max(p1.getY(), p2.getY());

            x2 += dx;
            y1 += dy;

            p1 = new Point(x1, y1);
            p2 = new Point(x2, y2);
        }
    }

    @Override
    public models.Rectangle getBounds() {
        return this;
    }
}