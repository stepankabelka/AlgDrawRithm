package models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon implements Shape {
    private List<Point> points;
    private Color color;
    private int thickness;
    private LineStyle style;
    private boolean filled;
    private Color fillColor;

    public Polygon(Color color, int thickness, LineStyle style) {
        this.points = new ArrayList<>();
        this.color = color;
        this.thickness = thickness;
        this.style = style;
        this.filled = false;
        this.fillColor = color;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    @Override
    public void draw(java.util.function.BiConsumer<Integer, Integer> setPixel) {
        if (points.size() < 2) return;

        if (filled && points.size() >= 3) {
            fillPolygon(setPixel);
        }

        // Draw edges
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            drawLine(p1, p2, setPixel);
        }
    }

    private void fillPolygon(java.util.function.BiConsumer<Integer, Integer> setPixel) {
        if (points.isEmpty()) return;

        int minY = points.get(0).getY();
        int maxY = points.get(0).getY();
        for (Point p : points) {
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }

        for (int y = minY; y <= maxY; y++) {
            List<Integer> intersections = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                Point p2 = points.get((i + 1) % points.size());

                if ((p1.getY() <= y && p2.getY() > y) || (p2.getY() <= y && p1.getY() > y)) {
                    int x = p1.getX() + (y - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
                    intersections.add(x);
                }
            }

            intersections.sort(Integer::compareTo);
            for (int i = 0; i < intersections.size() - 1; i += 2) {
                for (int x = intersections.get(i); x <= intersections.get(i + 1); x++) {
                    setPixel.accept(x, y);
                }
            }
        }
    }

    private void drawLine(Point p1, Point p2, java.util.function.BiConsumer<Integer, Integer> setPixel) {
        int x1 = p1.getX(), y1 = p1.getY();
        int x2 = p2.getX(), y2 = p2.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            drawThickPoint(x1, y1, setPixel);

            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
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
        if (points.isEmpty()) return false;
        for (Point p : points) {
            int dx = x - p.getX();
            int dy = y - p.getY();
            if (dx * dx + dy * dy <= 100) return true;
        }
        return false;
    }

    @Override
    public void move(int dx, int dy) {
        List<Point> newPoints = new ArrayList<>();
        for (Point p : points) {
            newPoints.add(new Point(p.getX() + dx, p.getY() + dy));
        }
        points = newPoints;
    }

    @Override
    public void resize(Point p1, Point p2) {
        // Not applicable for polygon
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
    public List<Point> getPoints() { return new ArrayList<>(points); }

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
}