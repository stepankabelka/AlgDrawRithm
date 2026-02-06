package rasterizers;

import models.Line;
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
    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight();
    }

    @Override
    public void rasterize(Line line) {

        Color currentColor = line.getColor() != null ? line.getColor() : defaultColor;
        boolean isDotted = line.isDotted();

        if (line.getP2().getX() == line.getP1().getX()) {
            int x = line.getP1().getX();
            int yStart = Math.min(line.getP1().getY(), line.getP2().getY());
            int yEnd = Math.max(line.getP1().getY(), line.getP2().getY());

            int step = 0;
            for (int y = yStart; y <= yEnd; y++) {
                if (isInBounds(x, y) && shouldDrawPixel(step, isDotted)) {
                    raster.setPixel(x, y, currentColor.getRGB());
                }
                step++;
            }
            return;
        }
        double k = (line.getP2().getY() - line.getP1().getY())
                / (double) (line.getP2().getX() - line.getP1().getX());

        double q = line.getP1().getY() - k * line.getP1().getX();


        if (k < 1) {
            int x1 = Math.min(line.getP1().getX(), line.getP2().getX());
            int x2 = Math.max(line.getP1().getX(), line.getP2().getX());

            int step = 0;
            for (int x = x1; x <= x2; x++) {
                int y = (int) Math.round(k * x + q);
                if (isInBounds(x, y) && shouldDrawPixel(step, isDotted)) {
                    raster.setPixel(x, y, currentColor.getRGB());
                }
                step++;
            }
            }
         else {
        int y1 = Math.min(line.getP1().getY(), line.getP2().getY());
        int y2 = Math.max(line.getP1().getY(), line.getP2().getY());

        for (int y = y1; y <= y2; y++) {
            int x = (int) Math.round((y - q) / k);
            if (isInBounds(x, y)) {
                raster.setPixel(x, y, defaultColor.getRGB());
            }
        }

        }


    }
    private boolean shouldDrawPixel(int step, boolean isDotted) {
        if (!isDotted) {
            return true;
        }
        return (step % 10) < 5;
    }



}
