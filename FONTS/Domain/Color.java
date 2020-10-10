package Domain;
/**
 * Utility class to store color and transform RGB/YCbCr
 */
public class Color {
    public int r, g, b; // yes, this can get messy, but its to avoid getters
    public int Y, Cb, Cr;

    /**
     * Stores the intruduced rgb and generates the YCbCr equivalent
     * @param r R component from RGB
     * @param g G component from RGB
     * @param b B component from RGB
     */
    public void RGB(int r, int g, int b) { // uses float instead of int to reduce aproximation imprecision
        this.r = r;
        this.g = g;
        this.b = b;
        Y = (int) (float) ((0.299 * r) + (0.587 * g) + (0.114 * b));
        Cb = (int) (float) (128.0 - (0.168736 * r) - (0.331264 * g) + (0.5 * b));
        Cr = (int) (float) (128.0 + (0.5 * r) - (0.418688 * g) - (0.081312 * b));
    }

    /**
     * Stores the intruduced YCbCr and generates the rgb equivalent
     * @param Y Y component from YCbCr
     * @param Cb Cb component from YCbCr
     * @param Cr Cr component from YCbCr
     */
    public void YCbCr(int Y, int Cb, int Cr) {
        this.Y = Y;
        this.Cb = Cb;
        this.Cr = Cr;
        r = (int) (float) (Y + 1.402 * (Cr - 128.0));
        g = (int) (float) (Y - 0.344136 * (Cb - 128.0) - 0.714136 * (Cr - 128.0));
        b = (int) (float) (Y + 1.772 * (Cb - 128.0));
    }
}