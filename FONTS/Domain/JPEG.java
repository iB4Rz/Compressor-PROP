package Domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * @author Alexandre Perez
 */

/**
 * JPEG algorithm, compress from a valid ppm ASCII/BIN image in the form of an
 * InputStream to an OutputStream and decompress a valid compressed
 * image in the form of an InputStream to a valid binary ppm image in the form of an
 * OutputStream.
 */
public class JPEG extends Algorithm {
    /**
     * Table related to the amount of compression and quality on light intensity, initialized with a hand choosen table
     */
    int[][] LuminanceQuantizationTable = {  { 4,  3,  4,  7,  9,  11, 14, 17 },
                                            { 3,  3,  4,  7,  9,  12, 12, 12 },
                                            { 4,  4,  5,  9,  12, 12, 12, 12 },
                                            { 7,  7,  9,  12, 12, 12, 12, 12 },
                                            { 9,  9,  12, 12, 12, 12, 12, 12 },
                                            { 11, 12, 12, 12, 12, 12, 12, 12 },
                                            { 14, 12, 12, 12, 12, 12, 12, 12 },
                                            { 17, 12, 12, 12, 12, 12, 12, 12 }
                                         };
    /**
     * Table related to the amount of compression and quality on color, initialized with a hand choosen table
     */
    int[][] ChrominanceQuantizationTable = {    {  4,  6, 12, 22, 20, 20, 17, 17 },
                                                {  6,  8, 12, 14, 14, 12, 12, 12 },
                                                { 12, 12, 14, 14, 12, 12, 12, 12 },
                                                { 22, 14, 14, 12, 12, 12, 12, 12 },
                                                { 20, 14, 12, 12, 12, 12, 12, 12 },
                                                { 20, 12, 12, 12, 12, 12, 12, 12 },
                                                { 17, 12, 12, 12, 12, 12, 12, 12 },
                                                { 17, 12, 12, 12, 12, 12, 12, 12 }
                                            };
    /**
     * 2D matrix of 8x8 Matrices for channel 0
     */
    private int[][][][] M0;
    /**
     * 2D matrix of 8x8 Matrices for channel 0
     */
    private int[][][][] M1;
    /**
     * 2D matrix of 8x8 Matrices for channel 0
     */
    private int[][][][] M2;
    /**
     * image width
     */
    private int width = -1;
    /**
     * image height
     */
    private int height = -1;
    /**
     * number of horizontal 8x8 matrices
     */
    private int m_width = -1;
    /**
     * number of vertical 8x8 matrices
     */
    private int m_height = -1;
    /**
     * used to check if c and cT are initialized because matrix initialization order
     * must be enforced (c before cT)
     */
    private static boolean initDCTMatrices = false;
    /**
     * cosine matrix
     */
    static final double c[][] = new double[8][8];
    /**
     * transformed cosine matrix
     */
    static final double cT[][] = new double[8][8];
    /**
     * instance with operations compress(InputStream,OutputStream,int)
     * and decompress(InputStream,OutputStream,int)
     */
    private final Huffman matrixCompressor;

    /**
     * Default constructor
     * initializes c and cT matrices if they arent initialized yet in any instance
     * and sets the lossless compression algorithm
     * @param matrixCompressor Huffman class instance
     */
    public JPEG(Huffman matrixCompressor) {
        this.matrixCompressor = matrixCompressor;
        if (!initDCTMatrices)
            initDCTMatrices();
        initDCTMatrices = true;
    }

    /**
     * Initializes c and cT matrices with the cosine matrix and the transformed
     * cosine matrix. Must be executed before any call to DCT and inverseDCT
     */
    static void initDCTMatrices() {
        int i;
        int j;
        for (j = 0; j < 8; j++) {
            double nn = (double) (8);
            c[0][j] = 1.0 / Math.sqrt(nn);
            cT[j][0] = c[0][j];
        }
        for (i = 1; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                double jj = (double) j;
                double ii = (double) i;
                c[i][j] = Math.sqrt(2.0 / 8.0) * Math.cos(((2.0 * jj + 1.0) * ii * Math.PI) / (2.0 * 8.0));
                cT[j][i] = c[i][j];
            }
        }
    }

    /**
     * Sets the quantization tables used at compression time
     * @param LuminanceQT 8x8 Luminance quantization matrix
     * @param ChrominanceQT 8x8 Chrominance quantization matrix
     */
    public void setQuantizationTables(int[][] LuminanceQT, int[][] ChrominanceQT) {
        LuminanceQuantizationTable = LuminanceQT;
        ChrominanceQuantizationTable = ChrominanceQT;
    }

    /**
     * Compress a valid ppm image from an InputStream to an OutputStream
     * @param is InputStream that contains a stream of bytes with a valid ppm image codification
     * @param os OutputStream where the compressed image will be stored
     * @throws IOException if reading or writting to a stream fails
     */
    @Override
    public void compress(InputStream is, OutputStream os) throws IOException {
        PPMTranslator ppmfile = new PPMTranslator(is);
        width = ppmfile.getWidth();
        height = ppmfile.getHeight();
        
        // 8x8 Matrix subdivision (uses margin at the image right and down borders if
        // height or width arent multiple of 8)
        m_width = width / 8;
        if (width % 8 != 0)
            m_width = m_width + 1;
        m_height = height / 8;
        if (height % 8 != 0)
            m_height = m_height + 1;

        // Fills the 3 YCbCr components (one component to each matrix) from the input
        // image
        M0 = new int[m_height][m_width][8][8];
        M1 = new int[m_height][m_width][8][8];
        M2 = new int[m_height][m_width][8][8];
        Color color = new Color();
        for (int i = 0; i < m_height; ++i) {
            int vMax = 8;
            if (i + 1 == m_height)
                vMax = 8 - (m_height * 8 - height);
            for (int j = 0; j < m_width; ++j) {
                int hMax = 8;
                if (j + 1 == m_width)
                    hMax = 8 - (m_width * 8 - width);
                for (int k = 0; k < vMax; ++k)
                    for (int l = 0; l < hMax; ++l) {
                        if ((color = ppmfile.getNextColor()) == null)
                            throw new IOException();
                        M0[i][j][k][l] = color.Y;
                        M1[i][j][k][l] = color.Cb;
                        M2[i][j][k][l] = color.Cr;
                    }
            }
        }

        // Write quantization tables
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                os.write((byte)LuminanceQuantizationTable[i][j]);
                os.write((byte)ChrominanceQuantizationTable[i][j]);
            }

        // Write image dimensions
        os.write((byte) ((width & 0xFF000000) >> 24));
        os.write((byte) ((width & 0x00FF0000) >> 16));
        os.write((byte) ((width & 0x0000FF00) >> 8));
        os.write((byte) (width & 0x000000FF));
        os.write((byte) ((height & 0xFF000000) >> 24));
        os.write((byte) ((height & 0x00FF0000) >> 16));
        os.write((byte) ((height & 0x0000FF00) >> 8));
        os.write((byte) (height & 0x000000FF));

        // DCT and Quantization for each component matrix, then compress matrices with
        // huffman
        for (int i = 0; i < m_height; ++i)
            for (int j = 0; j < m_width; ++j) {
                DCT(M0[i][j]);
                Quantization(M0[i][j], M0[i][j], LuminanceQuantizationTable);
                LosslessEncode(M0[i][j], os);
                DCT(M1[i][j]);
                Quantization(M1[i][j], M1[i][j], ChrominanceQuantizationTable);
                LosslessEncode(M1[i][j], os);
                DCT(M2[i][j]);
                Quantization(M2[i][j], M2[i][j], ChrominanceQuantizationTable);
                LosslessEncode(M2[i][j], os);
            }

        os.flush();
    }


    /**
     * Decompress an image compressed with compress from an InputStream to an OutputStream
     * @param is InputStream an stream consisting in a image compressed with the compress method
     * @param os OutputStream where the compressed image will be stored with a valid ppm image codification
     * @throws IOException if reading or writting to a stream fails
     */
    @Override
    public void decompress(InputStream is, OutputStream os) throws IOException {
        // Read Quantization tables
        byte[] qMatrices = new byte[128]; // 8*8*2
        int tmp = 0;
        if (is.read(qMatrices) < 0)
            throw new IOException();
        LuminanceQuantizationTable = new int[8][8];
        ChrominanceQuantizationTable = new int[8][8];
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                LuminanceQuantizationTable[i][j] = qMatrices[tmp] & 0x00FF;
                ++tmp;
                ChrominanceQuantizationTable[i][j] = qMatrices[tmp] & 0x00FF;
                ++tmp;
            }

        // Read Image Size
        byte[] integer = new byte[8];
        if (is.read(integer) < 0)
            throw new IOException();
        width = 0;
        width |= ((int) integer[0]) << 24;
        width |= ((int) (integer[1]) << 16) & 0x00FF0000;
        width |= ((int) (integer[2]) << 8) & 0x0000FF00;
        width |= ((int) (integer[3])) & 0x000000FF;
        height = 0;
        height |= ((int) integer[4]) << 24;
        height |= ((int) (integer[5]) << 16) & 0x00FF0000;
        height |= ((int) (integer[6]) << 8) & 0x0000FF00;
        height |= ((int) (integer[7])) & 0x000000FF;
        
        // Initialize Matrices
        m_width = width / 8;
        if (width % 8 != 0)
            m_width = m_width + 1;
        m_height = height / 8;
        if (height % 8 != 0)
            m_height = m_height + 1;
        
        M0 = new int[m_height][m_width][8][8];
        M1 = new int[m_height][m_width][8][8];
        M2 = new int[m_height][m_width][8][8];

        // Decompress file, then Dequantizes and applies the DCT inverse for each
        // component matrix
        for (int i = 0; i < m_height; ++i)
            for (int j = 0; j < m_width; ++j) {
                M0[i][j] = LosslessDecode(is);
                Dequantization(M0[i][j], M0[i][j], LuminanceQuantizationTable);
                inverseDCT(M0[i][j]);
                M1[i][j] = LosslessDecode(is);
                Dequantization(M1[i][j], M1[i][j], ChrominanceQuantizationTable);
                inverseDCT(M1[i][j]);
                M2[i][j] = LosslessDecode(is);
                Dequantization(M2[i][j], M2[i][j], ChrominanceQuantizationTable);
                inverseDCT(M2[i][j]);
            }
        
        // writes the ppm file converting the component matrices into RGB output
        PPMTranslator ppmFile = new PPMTranslator(os, width, height);
        ppmFile.writeHeader();
        Color color = new Color();
        for (int i = 0; i < m_height; ++i) {
            int vMax = 8;
            if (i + 1 == m_height)
                vMax = 8 - (m_height * 8 - height);
            for (int j = 0; j < m_width; ++j) {
                int hMax = 8;
                if (j + 1 == m_width)
                    hMax = 8 - (m_width * 8 - width);
                for (int k = 0; k < vMax; ++k)
                    for (int l = 0; l < hMax; ++l) {
                        int Y = M0[i][j][k][l];
                        int Cb = M1[i][j][k][l];
                        int Cr = M2[i][j][k][l];
                        color.YCbCr(Y, Cb, Cr);
                        ppmFile.setNextComponent(Math.max(Math.min(255, color.r), 0));
                        ppmFile.setNextComponent(Math.max(Math.min(255, color.g), 0));
                        ppmFile.setNextComponent(Math.max(Math.min(255, color.b), 0));
                    }
            }
        }

        os.flush();
    }

    /**
     * Quantificates a matrix dividing it by a QuantificationTable and storing the integer part
     * @param input 8x8 matrix to be quantified
     * @param output quantified 8x8 matrix of input
     * @param QuantizationTable copy of the 8x8 quantization table used by the compression
     */
    void Quantization(int[][] input, int[][] output, int[][] QuantizationTable) {
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                output[i][j] = input[i][j] / QuantizationTable[i][j];
            }
    }

    /**
     * Dequantificates a matrix multiplying it by a QuantificationTable and storing the integer part
     * @param input 8x8 matrix to be dequantized
     * @param output dequantified 8x8 matrix of input
     * @param QuantizationTable copy of the 8x8 quantization table used by the decompression
     */
    void Dequantization(int[][] input, int[][] output, int[][] QuantizationTable) {
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                output[i][j] = input[i][j] * QuantizationTable[i][j];
            }
    }

    /**
     * Applies the DCT-II transformation to M matrix where M = input[y][x] - 128 (centers values around 0)
     * @param input 8x8 matrix to be transformed to the DCT-II transformation and stored in place
     */
    void DCT(int input[][]) {
        double temp[][] = new double[8][8];
        double temp1;
        int i;
        int j;
        int k;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                temp[i][j] = 0.0;
                for (k = 0; k < 8; k++) {
                    temp[i][j] += ((input[i][k] - 128) * cT[k][j]);
                }
            }
        }
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                temp1 = 0.0;
                for (k = 0; k < 8; k++) {
                    temp1 += (c[i][k] * temp[k][j]);
                }
                input[i][j] = (int) Math.round(temp1);
            }
        }
    }

    /**
     * Applies the DCT-III transformation to input matrix and adds 128 to each
     * to undo the 128 that was substracted in DCT-II
     * @param input 8x8 matrix to be transformed to the DCT-III transformation and stored in place
     */
    void inverseDCT(int input[][]) {
        double temp[][] = new double[8][8];
        double temp1;
        int i;
        int j;
        int k;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                temp[i][j] = 0.0;
                for (k = 0; k < 8; k++) {
                    temp[i][j] += input[i][k] * c[k][j];
                }
            }
        }
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                temp1 = 0.0;
                for (k = 0; k < 8; k++) {
                    temp1 += cT[i][k] * temp[k][j];
                }
                temp1 += 128.0;
                if (temp1 < 0) {
                    input[i][j] = 0;
                } else if (temp1 > 255) {
                    input[i][j] = 255;
                } else {
                    input[i][j] = (int) Math.round(temp1);
                }
            }
        }
    }

    /**
     * Reads from bottom to top doing the ZigZag pattern and stores values from
     * the first non 0 value to the top. The values are stored in a byte[] using 2 bytes per value
     * @param in 8x8 Matrix to be traversed
     * @return array with length from 0 to 128 elements with the 2 elements per value from
     * the in matrix traversed in zigzag from the first non 0 value.
     */
    byte[] ZigZag(int[][] in) {
        byte[] data = null;
        int data_index = 0;
        int i = 8;
        int j = 8;
        int count = 64;
        for (int element = 0; element < 64; element++) {
            if (data == null) {
                if (in[i - 1][j - 1] != 0) {
                    data = new byte[count * 2];
                    data_index = 2 * count - 1;
                    data[data_index] = (byte) (in[i - 1][j - 1] & 0x000000FF);
                    --data_index;
                    data[data_index] = (byte) ((in[i - 1][j - 1] >>> 8) & 0x000000FF);
                    --data_index;
                }
            } else {
                data[data_index] = (byte) (in[i - 1][j - 1] & 0x000000FF);
                --data_index;
                data[data_index] = (byte) ((in[i - 1][j - 1] >>> 8) & 0x000000FF);
                --data_index;
            }
            --count;
            if ((i + j) % 2 == 0) {
                // Even stripes
                if (j > 1)
                    j--;
                else
                    i -= 2;
                if (i < 8)
                    i++;
            } else {
                // Odd stripes
                if (i > 1)
                    i--;
                else
                    j -= 2;
                if (j < 8)
                    j++;
            }
        }
        if (data == null)
            data = new byte[0];
        return data;
    }

    /**
     * Reads from top to top doing the inverse ZigZag pattern and stores values encoded
     * with 2 bytes to the result 8x8 matrix, after all values from "in" are stored, fills the rest with 0s
     * @param in array with 0 to 128 elements representing 2 elements per value
     * @return 8x8 Matrix obtained from in doing the inverse zigzag and filling the elements after the last
     * value from with 0s.
     */
    int[][] inverseZigZag(byte[] in) {
        int[][] data = new int[8][8];
        int i = 1;
        int j = 1;
        int count = 0;
        for (int element = 0; element < 64; element++) {
            if (count < in.length) {
                int value = 0;
                value |= ((((int) in[count]) << 8) & 0xFFFFFF00);
                ++count;
                value |= (((int) in[count]) & 0x000000FF);
                ++count;
                data[i - 1][j - 1] = value;
            } else
                data[i - 1][j - 1] = 0;
            if ((i + j) % 2 == 0) {
                // Even stripes
                if (j < 8)
                    j++;
                else
                    i += 2;
                if (i > 1)
                    i--;
            } else {
                // Odd stripes
                if (i < 8)
                    i++;
                else
                    j += 2;
                if (j > 1)
                    j--;
            }
        }
        return data;
    }

    /**
     * Encodes the given matrix using zigzag and huffman and outputs the result to
     * the output stream
     * @param mat 8x8 matrix that will be encoded
     * @param os OutputStream where the encoded 8x8 mat matrix will be encoded
     * @throws IOException if fails to write to the OutputStream
     */
    void LosslessEncode(int[][] mat, OutputStream os) throws IOException {
        byte[] zz0 = ZigZag(mat);
        ByteArrayInputStream bai0 = new ByteArrayInputStream(zz0);
        matrixCompressor.setMaxSizeHint(128); // 8*8*2
        matrixCompressor.compress(bai0, os);
    }

    /**
     * Decodes the given InputStream using huffman and inverse zigzag and returns the result
     * a the 8x8 matrix
     * @param is Encoded InputStream representing a 8x8 matrix encoded with zigzag and huffman
     * @return 8x8 Matrix decoded from the encoded InputStream
     * @throws IOException if fails to read to the InputStream
     */
    int[][] LosslessDecode(InputStream is) throws IOException {
        ByteArrayOutputStream bao0 = new ByteArrayOutputStream();
        matrixCompressor.setMaxSizeHint(128); // 8*8*2
        matrixCompressor.decompress(is, bao0);
        bao0.flush();
        byte[] zz0 = bao0.toByteArray();
        bao0.close();
        return inverseZigZag(zz0);
    }

}
