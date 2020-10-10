package Domain;

import org.junit.Test;
import static org.junit.Assert.*;

import UnitTest_JPEG.*;
import Domain.JPEG;
import java.io.*;

public class UnitTest {

    // One pixel image in Binary ppm format
    static final byte[] Image1Pixel = { 0x50, 0x36, 0x0a, 0x31, 0x20, 0x31, 0x0a, 0x32, 0x35, 0x35, 0x0a, (byte) 0xff,
        (byte) 0xff, (byte) 0xff };

    // One pixel image in Binary ppm decompressed
    static final byte[] Image1PixelDecompressed = {0x50, 0x36, 0x0a, 0x31, 0x20, 0x31, 0x0a, 0x32, 0x35, 0x35, 0x0a,
        (byte)0x90, (byte)0xff, 0x7b};

    // One pixel image in Binary ppm compressed
    static final byte[] Image1PixelCompressed = { 0x04, 0x04, 0x03, 0x06, 0x04, 0x0C, 0x07, 0x16, 0x09, 0x14, 0x0B, 0x14, 
        0x0E, 0x11, 0x11, 0x11, 0x03, 0x06, 0x03, 0x08, 0x04, 0x0C, 0x07, 0x0E, 0x09, 0x0E, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C,  
        0x04, 0x0C, 0x04, 0x0C, 0x05, 0x0E, 0x09, 0x0E, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x07, 0x16, 0x07, 0x0E, 0x09, 0x0E, 0x0C, 0x0C,  
        0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x09, 0x14, 0x09, 0x0E, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C,  
        0x0B, 0x14, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0E, 0x11, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C,  
        0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x11, 0x11, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C,  
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x27, 0x02, 0x05, 0x21, 0x27, 0x7F, 0x02, 0x20, 0x24, 0x02, 0x40, 0x62, 0x08, 0x45, 0x10, 0x3C,  
        0x1D, 0x00, 0x06, 0x38, 0x5F, 0x3E, 0x15, 0x66, 0x14, 0x17, 0x6F, 0x42, 0x6B, 0x58, 0x57, 0x36, 0x4C, 0x3C, 0x10, 0x47, 0x4D, 0x26, 0x6B, 0x2D,  
        0x1A, 0x73, 0x1B, 0x2D, 0x19, 0x75, 0x29, 0x4C, 0x6A, 0x5D, 0x26, 0x35, 0x7A, 0x55, 0x3E, (byte) 0xAA, 0x3D, 0x04, 0x06, 0x40, 0x48, 0x13, 0x7F, 0x40,  
        0x30, 0x01, 0x66, 0x44, 0x2C, 0x2F, 0x14, 0x5F, 0x36, 0x7E, 0x6D, 0x5F, 0x5B, 0x36, 0x7E, 0x6D, 0x5B, 0x3F, 0x6D, 0x5F, 0x7D, (byte) 0xF5, 0x3D, 0x04,  
        0x06, 0x40, 0x48, 0x13, 0x7F, 0x40, 0x30, 0x01, 0x66, 0x44, 0x2C, 0x2F, 0x14, 0x5F, 0x36, 0x7E, 0x6D, 0x5F, 0x5B, 0x36, 0x7E, 0x6D, 0x5B, 0x3F,  
        0x6D, 0x5F, 0x7D, (byte)0xF5 };
    
    static final int[][] Luminance09 = { { 4,  3,  4,  7,  9,  11, 14, 17 },
                                         { 3,  3,  4,  7,  9,  12, 12, 12 },
                                         { 4,  4,  5,  9,  12, 12, 12, 12 },
                                         { 7,  7,  9,  12, 12, 12, 12, 12 },
                                         { 9,  9,  12, 12, 12, 12, 12, 12 },
                                         { 11, 12, 12, 12, 12, 12, 12, 12 },
                                         { 14, 12, 12, 12, 12, 12, 12, 12 },
                                         { 17, 12, 12, 12, 12, 12, 12, 12 } };
                             
    static final int[][] Chrominance09 = { {  4,  6, 12, 22, 20, 20, 17, 17 },
                                           {  6,  8, 12, 14, 14, 12, 12, 12 },
                                           { 12, 12, 14, 14, 12, 12, 12, 12 },
                                           { 22, 14, 14, 12, 12, 12, 12, 12 },
                                           { 20, 14, 12, 12, 12, 12, 12, 12 },
                                           { 20, 12, 12, 12, 12, 12, 12, 12 },
                                           { 17, 12, 12, 12, 12, 12, 12, 12 },
                                           { 17, 12, 12, 12, 12, 12, 12, 12 } };

    // Matrix 8x8 example for unit test
    final int[][] Mat8Input = {  { 23, 54, 72, 69, 28, 73, 74, 25 },
                                 { 77, 10, 15, 85, 70, 17, 35, 55 },
                                 { 46, 64, 44, 50,  8, 66, 18, 36 }, 
                                 { 63, 42,  4,  7, 88, 83, 14, 86 },
                                 { 43,  3, 11, 58, 75, 53, 27, 48 },
                                 { 16, 22, 47, 45, 21, 82, 20, 52 },
                                 {  5, 59, 39,  2, 51, 78,  1, 56 },
                                 { 62, 80, 29, 32, 38, 81, 67, 65 } };

    // Expected resulting matrix from Quantization method for the unit test
    static final int[][] Mat8ResQ = { {  5, 18, 18,  9,  3,  6,  5,  1 },
                                    { 25,  3,  3, 12,  7,  1,  2,  4 },
                                    { 11, 16,  8,  5,  0,  5,  1,  3 },
                                    {  9,  6,  0,  0,  7,  6,  1,  7 },
                                    {  4,  0,  0,  4,  6,  4,  2,  4 },
                                    {  1,  1,  3,  3,  1,  6,  1,  4 },
                                    {  0,  4,  3,  0,  4,  6,  0,  4 },
                                    {  3,  6,  2,  2,  3,  6,  5,  5 } };
    
    // Expected resulting matrix from Dequantization method for the unit test
    static final int[][] Mat8ResD = { { 92, 162, 288, 483, 252, 803, 1036, 425 },
                                    { 231,  30,  60, 595, 630, 204, 420, 660 },
                                    { 184, 256, 220, 450,  96, 792, 216, 432 },
                                    { 441, 294, 36, 84, 1056, 996, 168, 1032 },
                                    { 387,  27, 132, 696, 900, 636, 324, 576 },
                                    { 176, 264, 564, 540, 252, 984, 240, 624 },
                                    {  70, 708, 468,  24, 612, 936,  12, 672 },
                                    {1054, 960, 348, 384, 456, 972, 804, 780 } };

    //DCT c inicialization                            
    static final double[][] DCTInic = { { 0.35355339059327373, 0.35355339059327373, 0.35355339059327373, 0.35355339059327373, 0.35355339059327373, 0.35355339059327373, 0.35355339059327373, 0.35355339059327373 },
                                 { 0.4903926402016152, 0.4157348061512726, 0.27778511650980114, 0.09754516100806417, -0.0975451610080641, -0.277785116509801, -0.4157348061512727, -0.4903926402016152 },
                                 { 0.46193976625564337, 0.19134171618254492, -0.19134171618254486, -0.46193976625564337, -0.4619397662556434, -0.19134171618254517, 0.191341716182545, 0.46193976625564326 },
                                 { 0.4157348061512726, -0.0975451610080641, -0.4903926402016152, -0.2777851165098011, 0.2777851165098009, 0.4903926402016152, 0.09754516100806439, -0.41573480615127256 },
                                 { 0.3535533905932738, -0.35355339059327373, -0.35355339059327384, 0.3535533905932737, 0.35355339059327384, -0.35355339059327334, -0.35355339059327356, 0.3535533905932733 },
                                 { 0.27778511650980114, -0.4903926402016152, 0.09754516100806415, 0.4157348061512728, -0.41573480615127256, -0.09754516100806401, 0.4903926402016153, -0.27778511650980076 },
                                 { 0.19134171618254492, -0.4619397662556434, 0.46193976625564326, -0.19134171618254495, -0.19134171618254528, 0.46193976625564337, -0.4619397662556432, 0.19134171618254478 },
                                 { 0.09754516100806417, -0.2777851165098011, 0.4157348061512728, -0.4903926402016153, 0.4903926402016152, -0.4157348061512725, 0.27778511650980076, -0.09754516100806429 } };

    //DCT cT inicialization    
    static final double[][] DCTInicT = { { 0.35355339059327373, 0.4903926402016152, 0.46193976625564337, 0.4157348061512726, 0.3535533905932738, 0.27778511650980114, 0.19134171618254492, 0.09754516100806417 },
                                   { 0.35355339059327373, 0.4157348061512726, 0.19134171618254492, -0.0975451610080641, -0.35355339059327373, -0.4903926402016152, -0.4619397662556434, -0.2777851165098011 },
                                   { 0.35355339059327373, 0.27778511650980114, -0.19134171618254486, -0.4903926402016152, -0.35355339059327384, 0.09754516100806415, 0.46193976625564326, 0.4157348061512728 },
                                   { 0.35355339059327373, 0.09754516100806417, -0.46193976625564337, -0.2777851165098011, 0.3535533905932737, 0.4157348061512728, -0.19134171618254495, -0.4903926402016153 },
                                   { 0.35355339059327373, -0.0975451610080641, -0.4619397662556434, 0.2777851165098009, 0.35355339059327384, -0.41573480615127256, -0.19134171618254528, 0.4903926402016152 },
                                   { 0.35355339059327373, -0.277785116509801, -0.19134171618254517, 0.4903926402016152, -0.35355339059327334, -0.09754516100806401, 0.46193976625564337, -0.4157348061512725 },
                                   { 0.35355339059327373, -0.4157348061512727, 0.191341716182545, 0.09754516100806439, -0.35355339059327356, 0.4903926402016153, -0.4619397662556432, 0.27778511650980076 },
                                   { 0.35355339059327373, -0.4903926402016152, 0.46193976625564326, -0.41573480615127256, 0.3535533905932733, -0.27778511650980076, 0.19134171618254478, -0.09754516100806429 } };
                                       
    // Matrix 8x8 DCT-II transformation
    static final int[][] DCTMat = { { -665, -32, -9, 35, 13, -36, 35, -45 },
                                    { 10, 23, -19, -13,  26,  52, -18,  6 },
                                    { 28, 15,  6, -24, -52,  28, -26,  -3 },
                                    { -19, -21, -48, -23, -14, 25, 12,  7 },
                                    { 36, -19,  9,  29,  -2,  0, -18,  14 },
                                    { -5,  -4,  5,  -1, -59, -59, 25,  -6 },
                                    {  8,  13,  5, -21, -67, 30,  11, -34 },
                                    { -16, 17, -19, -14, -26,  8, -7,  -8 }
    };

    // Matrix 8x8 DCT-II inverse transformation
    static final int[][] DCTMatInv = { { 255, 13, 182, 138, 189, 109, 194, 110 },
                                    { 73, 178,  99, 106, 133, 194,  96, 145 },
                                    { 212, 113, 150, 100, 94, 133, 122, 111 },
                                    { 91, 112,  70, 100, 122, 165, 125, 147 },
                                    { 191, 84, 161, 146, 154, 134, 111, 141 },
                                    { 120, 121, 132, 140, 98,  72, 127, 126 },
                                    { 173, 122, 161, 110, 70, 139, 142, 103 },
                                    { 141, 140, 118, 112, 68, 135, 121, 106 } };
                                 
    // Expected result applied the zigzag function of the Mat8Input                   
    static final byte[] ZigZagData = { 0,23,0,54,0,77,0,46,0,10,0,72,0,69,0,15,0,
                                 64,0,63,0,43,0,42,0,44,0,85,0,28,0,73,0,70,0,
                                 50,0,4,0,3,0,16,0,5,0,22,0,11,0,7,0,8,0,17,0,
                                 74,0,25,0,35,0,66,0,88,0,58,0,47,0,59,0,62,0,
                                 80,0,39,0,45,0,75,0,83,0,18,0,55,0,36,0,14,0,
                                 53,0,21,0,2,0,29,0,32,0,51,0,82,0,27,0,86,0,48,
                                 0,20,0,78,0,38,0,81,0,1,0,52,0,56,0,67,0,65 };

    static final byte[] ZigZagCompressed = { 0x27, 0x02, 0x05, 0x21, 0x27, 0x7F, 0x02, 0x20,
                                0x24, 0x02, 0x40, 0x62, 0x08, 0x45, 0x10, 0x3C,
                                0x1D, 0x00, 0x06, 0x38, 0x5F, 0x3E, 0x15, 0x66,
                                0x14, 0x17, 0x6F, 0x42, 0x6B, 0x58, 0x57, 0x36,
                                0x4C, 0x3C, 0x10, 0x47, 0x4D, 0x26, 0x6B, 0x2D,
                                0x1A, 0x73, 0x1B, 0x2D, 0x19, 0x75, 0x29, 0x4C,
                                0x6A, 0x5D, 0x26, 0x35, 0x7A, 0x55, 0x3E, (byte) 0xAA };
                                       
    static final int[][] Mat8LossE = { { -248, 14, 10, 5, 3, 2, 1, 0 },
                                 { 14, 20, 14, 7,  4, 2,  2, 1 },
                                 { 10, 14, 10,  5, 3,  2, 1, 0 },
                                 { 5, 7,  5,  3,  3,  2,  1, 0 },
                                 { 3,  4,  3,  3,  2, 2,  1, 0 },
                                 { 2,  2,  2, 2,  2,  1,  1, 0 },
                                 { 1,  2,  1, 1,  1,  1,  0, 0 },
                                 { 0,  1,  0, 0,  0,  0,  0, 0 } };

    static final int[][] Mat8LossD = { {-248, 14, 10, 5, 3, 2, 1, 0 },
                                 { 14, 20, 14,  7, 4, 2, 2, 1 },
                                 { 10, 14, 10,  5, 3, 2, 1, 0 },
                                 { 5,  7, 5,  3, 3,  2, 1, 0 },
                                 { 3,  4, 3,  3, 2,  2, 1, 0 },
                                 { 2,  2, 2,  2, 2,  1, 1, 0 },
                                 { 1,  2, 1,  1, 1,  1, 0, 0 },
                                 { 0,  1, 0,  0, 0,  0, 0, 0 } };
                   
    @Test
    public void initDCTMatrices() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            jpeg.initDCTMatrices();
            assertEquals(8, jpeg.c.length);
            assertEquals(8, jpeg.c[0].length);
            assertEquals(8, jpeg.cT.length);
            assertEquals(8, jpeg.cT[0].length);
            for (int i = 0; i < 8; ++i)
                for (int j = 0; j < 8; ++j) { // 10 similar decimal places is more than enough for this test
                    String c = String.format("%10f", jpeg.c[i][j]);
                    String c_Test = String.format("%10f", DCTInic[i][j]);
                    assertEquals(c_Test, c);
                    String cT = String.format("%10f", jpeg.cT[i][j]);
                    String cT_Test = String.format("%10f", DCTInicT[i][j]);
                    assertEquals(cT_Test, cT);
                }

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void setQuantizationTables() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            jpeg.setQuantizationTables(Luminance09, Chrominance09);
            
            assertArrayEquals(jpeg.LuminanceQuantizationTable, Luminance09);
            assertArrayEquals(jpeg.ChrominanceQuantizationTable, Chrominance09);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void compress() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(Image1Pixel); 
            OutputStreamStub os = new OutputStreamStub();
            huffmanStub.setCount(0);
                                   
            JPEG jpeg = new JPEG(huffmanStub);
            jpeg.compress(is, os);
                                               
            byte[] out = os.StubGetWrittenDiskData();
            assertArrayEquals(Image1PixelCompressed, out);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void decompress() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(Image1PixelCompressed); 
            OutputStreamStub os = new OutputStreamStub();
            huffmanStub.setCount(0);
                                   
            JPEG jpeg = new JPEG(huffmanStub);;
            jpeg.decompress(is, os);
                                               
            byte[] out = os.StubGetWrittenDiskData();
            assertArrayEquals(Image1PixelDecompressed, out);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void Quantization() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            int Mat8 [][] = new int[8][8];
            jpeg.Quantization(Mat8Input, Mat8, Luminance09);
            assertArrayEquals(Mat8ResQ, Mat8);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void Dequantization() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            int Mat8 [][] = new int[8][8];
            jpeg.Dequantization(Mat8Input, Mat8, Luminance09);
            assertArrayEquals(Mat8ResD, Mat8);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test 
    public void DCT() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            jpeg.DCT(Mat8Input);
            assertArrayEquals(DCTMat, Mat8Input);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void inverseDCT() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            jpeg.inverseDCT(Mat8Input);
            assertArrayEquals(DCTMatInv, Mat8Input);
            
        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void ZigZag() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            byte data[] = jpeg.ZigZag(Mat8Input);
            assertArrayEquals(ZigZagData, data);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void inverseZigZag() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            int [][] Mat8 = jpeg.inverseZigZag(ZigZagData);
            assertArrayEquals(Mat8Input, Mat8);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }

    @Test
    public void LosslessEncode() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            OutputStreamStub os = new OutputStreamStub();
            huffmanStub.setCount(0);
            
            jpeg.LosslessEncode(Mat8LossE, os);
            byte[] out = os.StubGetWrittenDiskData();
            assertArrayEquals(ZigZagCompressed, out);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }
    
    @Test
    public void LosslessDecode() {
        try {
            HuffmanStub huffmanStub = new HuffmanStub();
            JPEG jpeg = new JPEG(huffmanStub);
            InputStreamStub is =  new InputStreamStub();
            is.StubInitializeDiskData(ZigZagCompressed);
            huffmanStub.setCount(0);

            int [][] Mat8 = jpeg.LosslessDecode(is);
            assertArrayEquals(Mat8LossD, Mat8);

        } catch (Exception e) {
            assertEquals("no exception", e.toString());
        }
    }
}