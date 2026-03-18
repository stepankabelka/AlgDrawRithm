# 📐 Kreslicí aplikace - Dokumentace

## Přehled
Desktopová kreslicí aplikace v Java Swing s podporou geometrických tvarů, čar a vyplňování ploch.

## Struktura projektu
```
src/
├── App.java                    # Hlavní třída
├── models/                     
│   ├── Shape.java             # Interface pro tvary
│   ├── Circle.java            # Kružnice (Bresenham)
│   ├── Rectangle.java         # Obdélník
│   ├── Polygon.java           # Polygon (scanline fill)
│   ├── FilledArea.java        # Flood fill oblast
│   ├── Line.java              # Čára
│   ├── FloodFill.java         # BFS flood fill algoritmus
│   └── DrawingCanvas.java     # Správa objektů
├── rasterizers/               
│   └── TrivialRasterizer.java # Vykreslování čar
└── rasters/                   
    └── RasterBufferedImage.java # Pixel management
```

## Funkce

### Nástroje
- **Čára**: 2x kliknutí (start/end)
- **Kružnice**: Drag od středu
- **Obdélník**: Drag úhlopříčně
- **Polygon**: Klikání bodů + **Enter** pro dokončení
- **Výběr**: Přesun objektů, resize tažením červených úchytů
- **Guma**: Mazání objektů
- **Výplň**: Vyplnění objektu nebo plochy (flood fill)

### Vlastnosti
- Barva obrysu + výplně (color picker)
- Tloušťka: 1-20px
- Styl čáry: Plná / Přerušovaná / Tečkovaná
- Checkbox pro zapnutí výplně

### Klávesy
- **Enter**: Dokončit polygon
- **Delete**: Smazat objekt / vymazat vše

## Klíčové třídy

**App.java** - Hlavní třída
- Mouse adaptery (pressed, released, dragged, moved)
- `redraw()` - překreslení plátna
- `drawResizeHandles()` - červené úchyty pro resize

**Shape interface** - Společné metody pro všechny tvary
```java
void draw(BiConsumer<Integer, Integer> setPixel);
boolean contains(int x, int y);
void move(int dx, int dy);
void resizeByHandle(ResizeHandle handle, int dx, int dy);
ResizeHandle getResizeHandle(int x, int y);
Rectangle getBounds();
```

**Circle.java** - Bresenhamův algoritmus, 1 úchyt vpravo, rovnoměrné zvětšování

**Rectangle.java** - Výplň + ohraničení, 1 úchyt vpravo nahoře

**Polygon.java** - Scanline fill, pohyb všech bodů, resize nepodporován

**FilledArea.java** - Set pixelů z flood fill, pohyb pixelů, resize nepodporován

**FloodFill.java** - BFS algoritmus pro vyplňování uzavřených oblastí

**TrivialRasterizer.java** - Vykreslování čar všemi směry, podpora stylů a tloušťky
