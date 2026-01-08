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

    @Override
    public void rasterize(Line line) {
        double k = (line.getP2().getY() - line.getP1().getY())
                / (double) (line.getP2().getX() - line.getP1().getX());

        double q = line.getP1().getY() - k * line.getP1().getX();

        // TODO vyřešit hranice okna
        // TODO vyřešit svislou úsečku

        if (k < 1) {
            // TODO prohodit body pokud je potřeba

            for (int x = line.getP1().getX(); x <= line.getP2().getX() ; x++) {
                int y = (int) Math.round(k * x + q);

                raster.setPixel(x, y, defaultColor.getRGB());
            }
        } else {
            // TODO prohodit body pokud je potřeba

            for (int y = line.getP1().getY(); y <= line.getP2().getY() ; y++) {
                int x = (int) Math.round((y - q) / k);

                raster.setPixel(x, y, defaultColor.getRGB());
            }
        }
    }

}
