public class Color {
    public static final Color BLACK = makeColor(0, 0, 0);
    public static final Color BLUE = makeColor(0, 0, 255);
    public static final Color MAGENTA = makeColor(255, 0, 255);

    private int red;
    private int green;
    private int blue;

    public Color(int r, int g, int b) {
        red = r;
        green = g;
        blue = b;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public boolean equals(Color otherColor) {
        return red == otherColor.getRed() && green == otherColor.getGreen() && blue == otherColor.getBlue();
    }

    public String toString() {
        return "color: (" + red + ", " + green + ", " + blue + ")";
    }

    // class method to create Color object:
    //     - used for constants above
    //     - call this factory class method with RGB color values
    //       a table for color name -> RGB int values is available at
    //           http://en.wikipedia.org/wiki/Web_colors
    public static Color makeColor(int r, int g, int b) {
        return new Color(r, g, b);
    }
}