package models;

import java.util.ArrayList;
import java.util.List;

public class DrawingCanvas {
    private List<Shape> shapes;
    private List<Line> lines;

    public DrawingCanvas() {
        shapes = new ArrayList<>();
        lines = new ArrayList<>();
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    public void removeLine(Line line) {
        lines.remove(line);
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void clear() {
        shapes.clear();
        lines.clear();
    }

    public Shape findShapeAt(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                return shapes.get(i);
            }
        }
        return null;
    }
}