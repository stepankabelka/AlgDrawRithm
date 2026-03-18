package rasterizers;

import models.Line;
import models.LineStyle;
import rasters.Raster;

import java.awt.*;

public class TrivialRasterizer implements Rasterizer {

    private Color defaultColor;
    private Raster raster;

    public TrivialRasterizer(Raster raster, Color defaultColor) {
        this.raster = raster;
        this.defaultColor = defaultColor;
    }

    @Override
    public void setColor(Color color) {
        defaultColor = color;
    }

    @Override
    public void rasterize(Line line) {
        int x1 = line.getP1().getX();
        int y1 = line.getP1().getY();
        int x2 = line.getP2().getX();
        int y2 = line.getP2().getY();

        Color lineColor = line.getColor() != null ? line.getColor() : defaultColor;
        int thickness = line.getThickness();
        LineStyle style = line.getStyle();

        // Handle vertical line (x1 == x2)
        if (x1 == x2) {
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);
            int pixelCount = 0;
            for (int y = startY; y <= endY; y++) {
                if (shouldDrawPixel(style, pixelCount++)) {
                    drawThickPixel(x1, y, thickness, lineColor.getRGB());
                }
            }
            return;
        }

        // Handle horizontal line (y1 == y2)
        if (y1 == y2) {
            int startX = Math.min(x1, x2);
            int endX = Math.max(x1, x2);
            int pixelCount = 0;
            for (int x = startX; x <= endX; x++) {
                if (shouldDrawPixel(style, pixelCount++)) {
                    drawThickPixel(x, y1, thickness, lineColor.getRGB());
                }
            }
            return;
        }

        double k = (y2 - y1) / (double) (x2 - x1);
        double q = y1 - k * x1;

        int pixelCount = 0;
        if (Math.abs(k) < 1) {
            // Iterate over x (more horizontal line)
            int startX = Math.min(x1, x2);
            int endX = Math.max(x1, x2);

            for (int x = startX; x <= endX; x++) {
                int y = (int) Math.round(k * x + q);
                if (shouldDrawPixel(style, pixelCount++)) {
                    drawThickPixel(x, y, thickness, lineColor.getRGB());
                }
            }
        } else {
            // Iterate over y (more vertical line)
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);

            for (int y = startY; y <= endY; y++) {
                int x = (int) Math.round((y - q) / k);
                if (shouldDrawPixel(style, pixelCount++)) {
                    drawThickPixel(x, y, thickness, lineColor.getRGB());
                }
            }
        }
    }

    private boolean shouldDrawPixel(LineStyle style, int pixelCount) {
        switch (style) {
            case SOLID:
                return true;
            case DASHED:
                return (pixelCount / 10) % 2 == 0;
            case DOTTED:
                return pixelCount % 3 == 0;
            default:
                return true;
        }
    }

    private void drawThickPixel(int x, int y, int thickness, int color) {
        int halfThickness = thickness / 2;
        for (int dy = -halfThickness; dy <= halfThickness; dy++) {
            for (int dx = -halfThickness; dx <= halfThickness; dx++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, color);
                }
            }
        }
    }
}