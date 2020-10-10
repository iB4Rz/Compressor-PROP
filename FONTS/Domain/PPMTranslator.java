package Domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class PPMTranslator {
    /**
     * Defines which model (ASCII/BIN) its being used
     */
    PPMType encoding;
    /**
     * Current input stream, null if writting
     */
    InputStream is = null;
    /**
     * Current output stream, null if reading
     */
    OutputStream os = null;
    /**
     * height from a ppm image
     */
    int height;
    /**
     * width from a ppm image
     */
    int width;
    /**
     * maximum value of a color component
     */
    int maxValue;

    /**
     * Constructor for parsing
     * @param is InputStream with a valid ppm representation
     * @throws IOException if fails reading
     */
    public PPMTranslator(InputStream is) throws IOException {
        this.is = is;
        extractHeader();
    }

    /**
     * Constructor for writting
     * @param os OutputStream that will be written
     * @param width Width that will be stored in the header
     * @param height Height that will be stored in the header
     * @throws IOException if fails writting
     */
    public PPMTranslator(OutputStream os, int width, int height) throws IOException {
        this.os = os;
        this.width = width;
        this.height = height;
        maxValue = 255;
    }

    /**
     * Getter for width
     * @return width atribute
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for Height
     * @return height atribute
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter to return the next color component in the input stream
     * @return Component R or G or B
     * @throws IOException if fails reading
     */
    public int getNextComponent() throws IOException {
        assert (is != null);
        int num = 0;
        int next;
        if (encoding == PPMType.ASCII) {
            char c;
            while ((next = is.read()) >= 0) {
                c = (char) next;
                if (c == '\n')
                    return num;
                num = num * 10 + Character.getNumericValue(c);
            }
        } else if ((next = is.read()) >= 0)
            return next;
        return -1;
    }

    /**
     * Getter to return the next color in the input stream
     * @return Color instance with the 3 color components initialized
     * @throws IOException if fails reading
     */
    public Color getNextColor() throws IOException {
        int r, g, b;
        if ((r = getNextComponent()) < 0)
            return null;
        if ((g = getNextComponent()) < 0)
            throw new IOException("Wrong ppm codification! - Incomplete RGB color");
        if ((b = getNextComponent()) < 0)
            throw new IOException("Wrong ppm codification! - Incomplete RGB color");
        float pixelDepthFactor = (float) (255.0 / (float) maxValue);
        r = (int)Math.min(r * pixelDepthFactor, 255);
        g = (int)Math.min(g * pixelDepthFactor, 255);
        b = (int)Math.min(b * pixelDepthFactor, 255);
        Color c = new Color();
        c.RGB(r, g, b);
        return c;
    }

    /**
     * Setter for the next color component that will be written into the output stream
     * @param num component value
     * @throws IOException if fails writting
     */
    public void setNextComponent(int num) throws IOException {
        if (encoding == PPMType.ASCII) {
            String str = String.valueOf(num) + '\n';
            byte[] b = str.getBytes();
            os.write(b);
        } else
            os.write(num);
    }

    /**
     * Setter for the next color that will be written into the output stream
     * @param color valid Color instance
     * @throws IOException if fails writting
     */
    public void setNextColor(Color color) throws IOException {
        setNextComponent(color.r);
        setNextComponent(color.g);
        setNextComponent(color.b);
    }

    /**
     * Initializes this class atributes from reading a valid ppm from the input stream
     * @throws IOException if fails reading or ppm is invalid
     */
    private void extractHeader() throws IOException {
        int next;
        if ((next = is.read()) < 0)
            throw new IOException("Wrong ppm codification! - Empty file"); // 'P'
        if ((next = is.read()) < 0)
            throw new IOException("Wrong ppm codification! - File too short"); // '3' or '6'
        char c = (char) next;
        if (c == '3')
            encoding = PPMType.ASCII;
        else if (c == '6')
            encoding = PPMType.Raw;
        else
            throw new IOException("Wrong ppm codification! - Failed reading ascii/binary flag");

        if ((width = readHeaderNum()) < 0)
            throw new IOException("Wrong ppm codification! - Failed reading width");
        if ((height = readHeaderNum()) < 0)
            throw new IOException("Wrong ppm codification! - Failed reading height");
        if ((maxValue = readHeaderNum()) < 0)
            throw new IOException("Wrong ppm codification! - Failed reading max value");
    }

    /**
     * Reads and returns the next integer from the header, avoids comments
     * @return next integer value codified in ASCII in the input stream
     * @throws IOException if fails reading
     */
    int readHeaderNum() throws IOException {
        int next;
        int num = 0;
        boolean numberRead = false;
        while ((next = is.read()) >= 0) {
            char c = (char) next;
            if ((c == '\n' || c == ' ') && numberRead)
                return num; // reached end of number
            if (c == '\n' || c == ' ')
                continue; // avoid excesive '\n'
            if (c == '#') { // avoid comments
                if (numberRead)
                    return num; // number contains comment w/o space, return current num
                while ((next = is.read()) >= 0) {
                    c = (char) next;
                    if (c == '\n')
                        break; // comment finish
                }
                if (c != '\n')
                    return -1; // no number, just EOF
                continue;
            }
            num = num * 10;
            num = num + Character.getNumericValue(c);
            numberRead = true;
        }
        if (numberRead)
            return num; // reached end of number at end of file
        return -1; // no number, just EOF
    }

    /**
     * Writtes the current header atributes into the output stream
     * @throws IOException if fails writting
     */
    public void writeHeader() throws IOException {
        this.encoding = PPMType.Raw;
        String type;
        if (encoding == PPMType.ASCII)
            type = "P3";
        else
            type = "P6";
        String str = type + '\n' + String.valueOf(width) + ' ' + String.valueOf(height) + '\n' + String.valueOf(255)
                + '\n';
        byte[] header = str.getBytes();
        os.write(header);
    }

}
/**
 * The different types of ppm encodings
 */
enum PPMType {
    ASCII, Raw
}