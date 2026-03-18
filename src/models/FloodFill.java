package models;

import models.FilledArea;
import models.Point;
import rasters.Raster;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {

    public static FilledArea fill(Raster raster, int x, int y, Color fillColor) {
        if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
            return null;
        }

        int targetColor = raster.getPixel(x, y);
        int replacementColor = fillColor.getRGB();

        if (targetColor == replacementColor) {
            return null;
        }

        FilledArea filledArea = new FilledArea(new Point(x, y), fillColor);

        Queue<PixelPoint> queue = new LinkedList<>();
        queue.add(new PixelPoint(x, y));

        while (!queue.isEmpty()) {
            PixelPoint p = queue.poll();
            int px = p.x;
            int py = p.y;

            if (px < 0 || px >= raster.getWidth() || py < 0 || py >= raster.getHeight()) {
                continue;
            }

            if (raster.getPixel(px, py) != targetColor) {
                continue;
            }

            raster.setPixel(px, py, replacementColor);
            filledArea.addPixel(px, py);

            queue.add(new PixelPoint(px + 1, py));
            queue.add(new PixelPoint(px - 1, py));
            queue.add(new PixelPoint(px, py + 1));
            queue.add(new PixelPoint(px, py - 1));
        }

        return filledArea;
    }

    private static class PixelPoint {
        int x, y;

        PixelPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}