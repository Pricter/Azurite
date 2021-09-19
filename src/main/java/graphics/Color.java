package graphics;

import org.joml.Vector3f;
import org.joml.Vector4f;
import util.Utils;

import java.util.Random;

public class Color {

    public static final int TYPE_RGBA = 0;
    public static final int TYPE_HSLA = 1;

    public static Random random = new Random();

    /*
     * Color class used throughout the engine to represent red, green, blue and alpha
     * Contains static predefined colors that can easily be used during prototyping.
     */
    public static Color WHITE = new Color(255, 255, 255, 255);
    public static Color BLACK = new Color(0, 0, 0, 255);
    public static Color RED = new Color(255, 0, 0, 255);
    public static Color DARK_RED = new Color(127, 0, 0, 255);
    public static Color GREEN = new Color(0, 255, 0, 255);
    public static Color BLUE = new Color(0, 0, 255, 255);
    public static Color DARK_BLUE = new Color(0, 0, 127, 255);
    public static Color DIRTY_BLUE = new Color(0, 127, 127, 255);
    public static Color PINK = new Color(255, 0, 255, 255);
    public static Color CYAN = new Color(0, 255, 255, 255);
    public static Color YELLOW = new Color(255, 255, 0, 255);
    public static Color PURPLE = new Color(127, 0, 127, 255);
    public static Color SILVER = new Color(192, 192, 192, 255);
    public static Color GRAY = new Color(128, 128, 128, 255);
    public static Color MAROON = new Color(128, 0, 0, 255);
    public static Color OLIVE = new Color(120, 128, 0, 255);
    public static Color DARK_GREEN = new Color(0, 128, 0, 255);
    public static Color TEAL = new Color(0, 128, 128, 255);
    public static Color NAVY_BLUE = new Color(0, 0, 128, 255);
    public static Color BROWN = new Color(165, 42, 42, 255);
    public static Color FIREBRICK = new Color(178, 34, 34, 255);
    public static Color CRIMSON = new Color(220, 20, 60, 255);
    /**
     * Red component for this color. Range: 0-255
     */
    public float r;
    /**
     * Green component for this color. Range: 0-255
     */
    public float g;
    /**
     * Blue component for this color. Range: 0-255
     */
    public float b;
    /**
     * Alpha component for this color. Range: 0-255
     */
    public float a;

    /**
     * Creates new color with specified rgba values
     *
     * @param pr initial red value
     * @param pg initial green value
     * @param pb initial blue value
     * @param pa initial alpha value
     */
    public Color(float pr, float pg, float pb, float pa) {
        r = pr;
        g = pg;
        b = pb;
        a = pa;
    }

    /**
     * Creates new color with specified rgb values
     *
     * @param pr initial red value
     * @param pg initial green value
     * @param pb initial blue value
     */
    public Color(float pr, float pg, float pb) {
        this(pr, pg, pb, 255);
    }

    /**
     * Creates new color with specified value for rgb
     *
     * @param c value for red, green and blue
     */
    public Color(float c) {
        this(c, c, c, 255);
    }

    /**
     * @return random Color
     */
    public static Color randomColor() {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 255);
    }

    /**
     * Create a new color.
     *
     * @param x         the first component
     * @param y         the second component
     * @param z         the third component
     * @param a         the alpha value
     * @param inputType whether the input values are RGBA or HSLA
     * @return a RGBA {@link Color} representing the input
     */
    public static Color getColor(float x, float y, float z, float a, int inputType) {
        switch (inputType) {
            case TYPE_HSLA:
                return new HSLColor(x, y, z, a).toRGBColor();
            case TYPE_RGBA:
            default:
                return new Color(x, y, z, a);
        }
    }

    /**
     * Decode a rgb color code into a {@link Color}
     *
     * @param colorCode the color code
     * @return a valid rgb color
     * @see Integer#decode(String)
     */
    public static Color decode(String colorCode) {
        int i = Integer.decode(colorCode);
        return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }

    /**
     * Returns a Vector4f with rgba as xyzw
     */
    public Vector4f toVec4f() {
        return new Vector4f(r, g, b, a);
    }

    /**
     * Utility function to Map a color value from range 0-255 to range 0-1
     */
    private float m(float p) {
        return Utils.map(p, 0, 255, 0, 1);
    }

    /**
     * Get the Normalized Vector4f for this color. Used mostly in OpenGL
     */
    public Vector4f toNormalizedVec4f() {
        return new Vector4f(m(r), m(g), m(b), m(a));
    }

    /**
     * Get the Normalized Vector3f for this color. Used mostly in OpenGL
     */
    public Vector3f toNormalizedVec3f() {
        return new Vector3f(m(r), m(g), m(b));
    }

    /**
     * Turn a Color with normalised values to a color with values from 0-255
     */
    public Color fromNormalized() {
        return new Color(Utils.map(r, 0, 1, 0, 255), Utils.map(g, 0, 1, 0, 255), Utils.map(b, 0, 1, 0, 255),
                Utils.map(a, 0, 1, 0, 255));
    }

    /**
     * Check if colors are equal
     */
    @Override
    public boolean equals(Object c) {
        if (c == null) {
            return false;
        }
        if (!(c instanceof Color)) {
            return false;
        }
        Color otherColor = (Color) c;
        return otherColor.r == this.r && otherColor.g == this.g && otherColor.b == this.b && otherColor.a == this.a;
    }

    /**
     * Set Alpha for this color
     */
    public void setAlpha(float value) {
        a = value;
    }

    /**
     * Convert this RGB {@link Color} to a {@link HSLColor} using {@link HSLColor#toHSLA(float[])}
     *
     * @return a {@link HSLColor} representing the same color
     */
    public HSLColor toHSLColor() {
        float[] floats = HSLColor.toHSLA(new float[]{m(r), m(g), m(b), m(a)});
        return new HSLColor(floats[0], floats[1], floats[2], floats[3]);
    }

    /**
     * Set value for only one of r, g, b, a
     *
     * @param type  can be one of r, g, b, a
     * @param value value to set the component to
     */
    public void setColor(char type, float value) {
        switch (type) {
            case 'r':
                r = value;
                break;
            case 'g':
                g = value;
                break;
            case 'b':
                b = value;
                break;
            case 'a':
                a = value;
                break;
        }
    }

    /**
     * Set Color based on float
     *
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public void setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        clamp();
    }

    /**
     * Lerps the color based on the percentage
     *
     * @param colorB
     * @param percentage
     */
    public void lerp(Color colorB, float percentage) {
        r += (colorB.r - r) * percentage;
        g += (colorB.g - g) * percentage;
        b += (colorB.b - b) * percentage;
        a += (colorB.a - a) * percentage;
    }

    /**
     * Clamps the color from 0 to 1
     */
    private void clamp() {
        if (r > 1) r = 1; else if (r < 0) r = 0;
        if (g > 1) g = 1; else if (g < 0) g = 0;
        if (b > 1) b = 1; else if (b < 0) b = 0;
        if (a > 1) a = 1; else if (a < 0) a = 0;
    }
}

