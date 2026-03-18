import models.*;
import models.Point;
import models.Polygon;
import models.Rectangle;
import models.Shape;
import rasterizers.TrivialRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.List;


public class App {

    private final JPanel panel;
    private final Raster raster;
    private TrivialRasterizer rasterizer;
    private MouseAdapter mouseAdapter;
    private Point startPoint;
    private DrawingCanvas canvas;


    private ToolType currentTool = ToolType.LINE;
    private Color currentColor = Color.WHITE;
    private Color currentFillColor = Color.RED;
    private int currentThickness = 1;
    private LineStyle currentStyle = LineStyle.SOLID;
    private boolean fillEnabled = false;


    private Point lineStartPoint = null;
    private Polygon currentPolygon = null;


    private Shape selectedShape = null;
    private Point dragStart = null;
    private boolean isDragging = false;
    private ResizeHandle currentResizeHandle = ResizeHandle.NONE;

    public static void main() {
        SwingUtilities.invokeLater(() -> new App(1000, 700).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xFFFFFF);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Kreslicí aplikace");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        JPanel toolPanel = createToolPanel();
        JPanel controlPanel = createControlPanel();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        rasterizer = new TrivialRasterizer(raster, currentColor);

        createAdapters();
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);

        canvas = new DrawingCanvas();
    }

    private void redrawWithLinePreview(Point currentPoint) {
        redraw();

        Line previewLine = new Line(lineStartPoint, currentPoint, currentColor, currentThickness, currentStyle);
        rasterizer.setColor(currentColor);
        rasterizer.rasterize(previewLine);

        panel.repaint();
    }

    private JPanel createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBorder(BorderFactory.createTitledBorder("Nástroje"));

        String[] tools = {"Čára", "Kružnice", "Obdélník", "Polygon", "Výběr", "Guma", "Výplň"};
        ToolType[] toolTypes = {ToolType.LINE, ToolType.CIRCLE, ToolType.RECTANGLE,
                ToolType.POLYGON, ToolType.SELECT, ToolType.ERASER, ToolType.FILL};

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < tools.length; i++) {
            JToggleButton btn = new JToggleButton(tools[i]);
            final ToolType tool = toolTypes[i];
            btn.addActionListener(e -> {
                currentTool = tool;
                if (tool != ToolType.POLYGON) {
                    currentPolygon = null;
                }
                panel.requestFocus();
            });
            group.add(btn);
            toolPanel.add(btn);
            if (i == 0) btn.setSelected(true);
        }

        return toolPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Vlastnosti"));


        JLabel colorLabel = new JLabel("Barva:");
        controlPanel.add(colorLabel);

        JButton colorButton = getJButton();
        controlPanel.add(colorButton);


        JLabel thicknessLabel = new JLabel("  Tloušťka:");
        controlPanel.add(thicknessLabel);

        SpinnerModel thicknessModel = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner thicknessSpinner = new JSpinner(thicknessModel);
        thicknessSpinner.setPreferredSize(new Dimension(60, 25));
        thicknessSpinner.addChangeListener(e -> {
            currentThickness = (Integer) thicknessSpinner.getValue();
        });
        controlPanel.add(thicknessSpinner);


        JLabel styleLabel = new JLabel("  Styl:");
        controlPanel.add(styleLabel);

        JComboBox<String> styleCombo = new JComboBox<>(new String[]{
                "Plná", "Přerušovaná", "Tečkovaná"
        });
        styleCombo.addActionListener(e-> {
            int index = styleCombo.getSelectedIndex();
            switch (index) {
                case 0: currentStyle = LineStyle.SOLID; break;
                case 1: currentStyle = LineStyle.DASHED; break;
                case 2: currentStyle = LineStyle.DOTTED; break;
            }
        });
        controlPanel.add(styleCombo);


        JCheckBox fillCheck = new JCheckBox("Výplň");
        fillCheck.addActionListener(e -> {
            fillEnabled = fillCheck.isSelected();
        });
        controlPanel.add(fillCheck);


        JButton fillColorButton = new JButton("  ");
        fillColorButton.setBackground(currentFillColor);
        fillColorButton.setPreferredSize(new Dimension(50, 25));
        fillColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(panel, "Vyberte barvu výplně", currentFillColor);
            if (newColor != null) {
                currentFillColor = newColor;
                fillColorButton.setBackground(currentFillColor);
            }
        });
        controlPanel.add(fillColorButton);


        JButton clearButton = new JButton("Vymazat vše");
        clearButton.addActionListener(e -> {
            canvas.clear();
            currentPolygon = null;
            redraw();
        });
        controlPanel.add(clearButton);

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        panel.getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedShape != null) {
                    canvas.removeShape(selectedShape);
                    selectedShape = null;
                } else {
                    canvas.clear();
                    currentPolygon = null;
                }
                redraw();
            }
        });

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finishPolygon");
        panel.getActionMap().put("finishPolygon", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPolygon != null && currentPolygon.getPoints().size() >= 3) {
                    canvas.addShape(currentPolygon);
                    currentPolygon = null;
                    redraw();
                }
            }
        });

        return controlPanel;
    }

    private JButton getJButton() {
        JButton colorButton = new JButton(" idk ");
        colorButton.setBackground(currentColor);
        colorButton.setPreferredSize(new Dimension(50, 25));
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(panel, "Vyberte barvu obrysu", currentColor);
            if (newColor != null) {
                currentColor = newColor;
                colorButton.setBackground(currentColor);
                rasterizer.setColor(currentColor);
            }
        });
        return colorButton;
    }

    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = new Point(e.getX(), e.getY());
                panel.requestFocus();

                if (currentTool == ToolType.LINE) {
                    if (lineStartPoint == null) {
                        lineStartPoint = startPoint;
                    } else {
                        Line line = new Line(lineStartPoint, startPoint, currentColor, currentThickness, currentStyle);
                        canvas.addLine(line);
                        lineStartPoint = null;
                        redraw();
                    }
                } else if (currentTool == ToolType.POLYGON) {
                    if (currentPolygon == null) {
                        currentPolygon = new Polygon(currentColor, currentThickness, currentStyle);
                        currentPolygon.setFilled(fillEnabled);
                        currentPolygon.setFillColor(currentFillColor);
                    }
                    currentPolygon.addPoint(startPoint);
                    redraw();
                } else if (currentTool == ToolType.SELECT) {
                    if (selectedShape != null) {
                        currentResizeHandle = selectedShape.getResizeHandle(e.getX(), e.getY());
                        if (currentResizeHandle != ResizeHandle.NONE) {
                            dragStart = startPoint;
                            return;
                        }
                    }

                    selectedShape = canvas.findShapeAt(e.getX(), e.getY());
                    if (selectedShape != null) {
                        currentResizeHandle = selectedShape.getResizeHandle(e.getX(), e.getY());
                        if (currentResizeHandle == ResizeHandle.NONE) {
                            dragStart = startPoint;
                            isDragging = true;
                        } else {
                            dragStart = startPoint;
                        }
                    } else {
                        currentResizeHandle = ResizeHandle.NONE;
                        isDragging = false;
                    }
                    redraw();
                } else if (currentTool == ToolType.ERASER) {
                    Shape shape = canvas.findShapeAt(e.getX(), e.getY());
                    if (shape != null) {
                        canvas.removeShape(shape);
                        redraw();
                    }
                } else if (currentTool == ToolType.FILL) {
                    Shape shape = canvas.findShapeAt(e.getX(), e.getY());
                    if (shape != null) {
                        shape.setFilled(true);
                        shape.setFillColor(currentFillColor);
                        redraw();
                    } else {
                        FilledArea filledArea = FloodFill.fill(raster, e.getX(), e.getY(), currentFillColor);
                        if (filledArea != null) {
                            canvas.addShape(filledArea);
                        }
                        panel.repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool == ToolType.POLYGON || currentTool == ToolType.LINE) {
                    return;
                }

                Point endPoint = new Point(e.getX(), e.getY());

                if (isDragging || currentResizeHandle != ResizeHandle.NONE) {
                    isDragging = false;
                    currentResizeHandle = ResizeHandle.NONE;
                    dragStart = null;
                    redraw();
                    return;
                }

                Shape shape = null;
                switch (currentTool) {
                    case CIRCLE:
                        int dx = endPoint.getX() - startPoint.getX();
                        int dy = endPoint.getY() - startPoint.getY();
                        int radius = (int) Math.sqrt(dx * dx + dy * dy);
                        shape = new Circle(startPoint, radius, currentColor, currentThickness, currentStyle);
                        break;
                    case RECTANGLE:
                        shape = new models.Rectangle(startPoint, endPoint, currentColor, currentThickness, currentStyle);
                        break;
                }

                if (shape != null) {
                    shape.setFilled(fillEnabled);
                    shape.setFillColor(currentFillColor);
                    canvas.addShape(shape);
                }

                redraw();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentTool == ToolType.LINE && lineStartPoint != null) {

                    Point currentPoint = new Point(e.getX(), e.getY());
                    redrawWithLinePreview(currentPoint);
                } else if (currentTool == ToolType.POLYGON && currentPolygon != null && !currentPolygon.getPoints().isEmpty()) {

                    Point currentPoint = new Point(e.getX(), e.getY());
                    redrawWithPolygonPreview(currentPoint);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool == ToolType.ERASER) {
                    Shape shape = canvas.findShapeAt(e.getX(), e.getY());
                    if (shape != null) {
                        canvas.removeShape(shape);
                        redraw();
                    }
                    return;
                }

                if (selectedShape != null && currentResizeHandle != ResizeHandle.NONE) {
                    Point current = new Point(e.getX(), e.getY());
                    int dx = current.getX() - dragStart.getX();
                    int dy = current.getY() - dragStart.getY();
                    selectedShape.resizeByHandle(currentResizeHandle, dx, dy);
                    dragStart = current;
                    redraw();
                    return;
                }

                if (isDragging && selectedShape != null) {
                    Point current = new Point(e.getX(), e.getY());
                    int dx = current.getX() - dragStart.getX();
                    int dy = current.getY() - dragStart.getY();
                    selectedShape.move(dx, dy);
                    dragStart = current;
                    redraw();
                    return;
                }

                Point endPoint = new Point(e.getX(), e.getY());
                redrawWithPreview(endPoint);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentTool == ToolType.POLYGON && e.getClickCount() == 2) {
                    if (currentPolygon != null) {
                        canvas.addShape(currentPolygon);
                        currentPolygon = null;
                        redraw();
                    }
                }
            }
        };
    }

    private void redraw() {
        clear(0xFFFFFF);


        for (Shape shape : canvas.getShapes()) {
            drawShape(shape, shape.isFilled() ? shape.getFillColor() : shape.getColor());
        }


        for (Line line : canvas.getLines()) {
            rasterizer.setColor(line.getColor());
            rasterizer.rasterize(line);
        }


        if (currentPolygon != null && !currentPolygon.getPoints().isEmpty()) {
            drawShape(currentPolygon, currentPolygon.getColor());
        }

        if (selectedShape != null && currentTool == ToolType.SELECT) {
            drawResizeHandles(selectedShape);
        }

        panel.repaint();
    }

    private void redrawWithPolygonPreview(Point currentPoint) {
        redraw();


        if (currentPolygon != null && !currentPolygon.getPoints().isEmpty()) {
            List<Point> points = currentPolygon.getPoints();
            Point lastPoint = points.getLast();
            Point FirstPoint = points.getFirst();
            Line previewLine = new Line(lastPoint, currentPoint, currentColor, currentThickness, currentStyle);
            Line previewLine2 = new Line(FirstPoint, currentPoint, currentColor, currentThickness, currentStyle);
            rasterizer.setColor(currentColor);
            rasterizer.rasterize(previewLine);
            rasterizer.rasterize(previewLine2);
        }

        panel.repaint();
    }

    private void drawResizeHandles(Shape shape) {
        models.Rectangle bounds = shape.getBounds();
        if (bounds == null) return;

        List<Point> points = bounds.getPoints();
        if (points.size() < 2) return;

        int x1 = Math.min(points.get(0).getX(), points.get(1).getX());
        int y1 = Math.min(points.get(0).getY(), points.get(1).getY());
        int x2 = Math.max(points.get(0).getX(), points.get(1).getX());
        int y2 = Math.max(points.get(0).getY(), points.get(1).getY());
        int midY = (y1 + y2) / 2;

        int handleSize = 20;
        Color handleColor = Color.RED;

        if (shape instanceof Circle) {
            drawHandle(x2, midY, handleSize, handleColor);
        }
        else if (shape instanceof models.Rectangle) {
            drawHandle(x2, y1, handleSize, handleColor);
        }
        else {
            drawHandle(x2, y1, handleSize, handleColor);
            drawHandle(x2, midY, handleSize, handleColor);
        }
    }

    private void drawHandle(int x, int y, int size, Color color) {
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, color.getRGB());
                }
            }
        }

        for (int dy = -size-1; dy <= size+1; dy++) {
            for (int dx = -size-1; dx <= size+1; dx++) {
                if (Math.abs(dx) == size+1 || Math.abs(dy) == size+1) {
                    int px = x + dx;
                    int py = y + dy;
                    if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                        raster.setPixel(px, py, Color.WHITE.getRGB());
                    }
                }
            }
        }
    }

    private void redrawWithPreview(Point endPoint) {
        redraw();


        switch (currentTool) {
            case LINE:
                Line previewLine = new Line(startPoint, endPoint, currentColor, currentThickness, currentStyle);
                rasterizer.setColor(currentColor);
                rasterizer.rasterize(previewLine);
                break;
            case CIRCLE:
                int dx = endPoint.getX() - startPoint.getX();
                int dy = endPoint.getY() - startPoint.getY();
                int radius = (int) Math.sqrt(dx * dx + dy * dy);
                Circle previewCircle = new Circle(startPoint, radius, currentColor, currentThickness, currentStyle);
                previewCircle.setFilled(fillEnabled);
                previewCircle.setFillColor(currentFillColor);
                drawShape(previewCircle, fillEnabled ? currentFillColor : currentColor);
                break;
            case RECTANGLE:
                Rectangle previewRect = new Rectangle(startPoint, endPoint, currentColor, currentThickness, currentStyle);
                previewRect.setFilled(fillEnabled);
                previewRect.setFillColor(currentFillColor);
                drawShape(previewRect, fillEnabled ? currentFillColor : currentColor);
                break;
        }

        panel.repaint();
    }

    private void drawShape(Shape shape, Color color) {
        shape.draw((x, y) -> {
            if (x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight()) {
                raster.setPixel(x, y, color.getRGB());
            }
        });
    }
}