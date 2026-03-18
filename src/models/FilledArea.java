package models;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilledArea implements Shape {
    private Set<PixelPoint> pixels;
    private Color color;
    private Point seedPoint;

    public FilledArea(Point seedPoint, Color color) {
        this.seedPoint = seedPoint;
        this.color = color;
        this.pixels = new HashSet<>();
    }

    public void addPixel(int x, int y) {
        pixels.add(new PixelPoint(x, y));
    }

    @Override
    public void draw(java.util.function.BiConsumer<Integer, Integer> setPixel) {
        for (PixelPoint p : pixels) {
            setPixel.accept(p.x, p.y);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        return pixels.contains(new PixelPoint(x, y));
    }

    @Override
    public void move(int dx, int dy) {
        Set<PixelPoint> newPixels = new HashSet<>();
        for (PixelPoint p : pixels) {
            newPixels.add(new PixelPoint(p.x + dx, p.y + dy));
        }
        pixels = newPixels;
        seedPoint = new Point(seedPoint.getX() + dx, seedPoint.getY() + dy);
    }

    @Override
    public void resize(Point p1, Point p2) {
        // Not applicable for filled area
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getThickness() {
        return 1;
    }

    @Override
    public void setThickness(int thickness) {
        // Not applicable
    }

    @Override
    public LineStyle getStyle() {
        return LineStyle.SOLID;
    }

    @Override
    public void setStyle(LineStyle style) {
        // Not applicable
    }

    @Override
    public boolean isFilled() {
        return true;
    }

    @Override
    public void setFilled(boolean filled) {
        // Always filled
    }

    @Override
    public Color getFillColor() {
        return color;
    }

    @Override
    public void setFillColor(Color fillColor) {
        this.color = fillColor;
    }

    @Override
    public List<Point> getPoints() {
        List<Point> points = new ArrayList<>();
        points.add(seedPoint);
        return points;
    }

    @Override
    public ResizeHandle getResizeHandle(int x, int y) {
        return null;
    }

    @Override
    public void resizeByHandle(ResizeHandle handle, int dx, int dy) {

    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    private static class PixelPoint {
        int x, y;

        PixelPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PixelPoint that = (PixelPoint) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
}