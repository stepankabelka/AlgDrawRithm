package models;

import java.awt.*;
import java.util.List;

public interface Shape {
    void draw(java.util.function.BiConsumer<Integer, Integer> setPixel);
    boolean contains(int x, int y);
    void move(int dx, int dy);
    void resize(Point p1, Point p2);
    Color getColor();
    void setColor(Color color);
    int getThickness();
    void setThickness(int thickness);
    LineStyle getStyle();
    void setStyle(LineStyle style);
    boolean isFilled();
    void setFilled(boolean filled);
    Color getFillColor();
    void setFillColor(Color fillColor);
    List<Point> getPoints();

    ResizeHandle getResizeHandle(int x, int y);
    void resizeByHandle(ResizeHandle handle, int dx, int dy);
    Rectangle getBounds();
}