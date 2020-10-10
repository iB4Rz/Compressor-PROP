package Domain;
import org.junit.Test;
import static org.junit.Assert.*;

import UnitTest_JPEG.*;
import Domain.JPEG;
import Domain.Color;
import java.io.*;

public class UtilsTest {

    // --------------- Color TEST ---------------

    public int r = 237;
    public int g = 135;
    public int b = 154;

    public int Y = 167;
    public int Cb = 120;
    public int Cr = 177;   

    // Different values because decimals are lost along the way
    public int R = 235;
    public int G = 134;
    public int B = 152;

    @Test
    public void RGB() {
        try {
        Color color = new Color();
        color.RGB(r, g, b);
        assertTrue(color.Y == this.Y && color.Cb == this.Cb && color.Cr == this.Cr);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void YCbCr() {
        try {
        Color color = new Color();
        color.YCbCr(Y, Cb, Cr);
        assertTrue(color.r == R && color.g == G && color.b == B);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    // --------------- PPMTranslator TEST ---------------

    // One pixel image in Binary ppm format
    static final byte[] Image1Pixel = { 0x50, 0x36, 0x0a, 0x31, 0x20, 0x31, 0x0a, 0x32, 0x35, 0x35, 0x0a, (byte) 0xff,
        (byte) 0xff, (byte) 0xff };

    static final int width = 20;
    static final int height = 40;

    static final byte[] val = {-19, -121, -102};
    static final byte[] val1 = {-19};
    static final byte[] valH = {80, 54, 10, 50, 48, 32, 52, 48, 10, 50, 53, 53, 10};

    @Test
    public void PPMTranslator() {
        try {
            OutputStreamStub os = new OutputStreamStub();
            PPMTranslator trans = new PPMTranslator(os, width, height);
            assertTrue(trans.width == width);
            assertTrue(trans.height == height);
            assertTrue(trans.maxValue == 255);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void getWidth() {
        try {
            OutputStreamStub os = new OutputStreamStub();
            PPMTranslator trans = new PPMTranslator(os, width, height);
            trans.getWidth();
            assertTrue(trans.width == width);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test 
    public void getHeight() {
        try {
            OutputStreamStub os = new OutputStreamStub();
            PPMTranslator trans = new PPMTranslator(os, width, height);
            trans.getHeight();
            assertTrue(trans.height == height);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test 
    public void getNextComponent() {
        try {
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(Image1Pixel);
            PPMTranslator trans = new PPMTranslator(is);
            int result = trans.getNextComponent();
            assertTrue(255 == result);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test 
    public void getNextColor() {
        try {
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(Image1Pixel);
            PPMTranslator trans = new PPMTranslator(is);
            Color color = new Color();
            color = trans.getNextColor();
            assertTrue(color.r == 255 && color.g == 255 && color.b == 255);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void setNextComponent() {
        try {
            OutputStreamStub os = new OutputStreamStub();
            PPMTranslator trans = new PPMTranslator(os, width, height);
            Color color = new Color();
            color.RGB(r, g, b);
            trans.setNextComponent(r);
            byte[] out = os.StubGetWrittenDiskData();
            assertArrayEquals(val1, out);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test 
    public void setNextColor() {
        try {
            OutputStreamStub os = new OutputStreamStub();
            PPMTranslator trans = new PPMTranslator(os, width, height);
            Color color = new Color();
            color.RGB(r, g, b);
            trans.setNextColor(color);
            byte[] out = os.StubGetWrittenDiskData();
            assertArrayEquals(val, out);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test 
    public void extractHeader() {
        try {
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(Image1Pixel);
            PPMTranslator trans = new PPMTranslator(is);
            assertTrue(trans.height == 1);
            assertTrue(trans.width == 1);
            assertTrue(trans.maxValue == 255);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void readHeaderNum() {
        try {
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(Image1Pixel);
            PPMTranslator trans = new PPMTranslator(is);
            int result = trans.readHeaderNum();
            assertTrue(-111 == result);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void writeHeader() {
        try {
            OutputStreamStub os = new OutputStreamStub();
            PPMTranslator trans = new PPMTranslator(os, width, height);
            trans.writeHeader();
            byte[] out = os.StubGetWrittenDiskData();
            assertArrayEquals(valH, out);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }
}
