package Domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
* @author Isaac Muñoz
*/

/**
* LZSS algorithm. Compress an InputStream to an OutputStream and decompress a valid InputStream to an OutputStream.
*/

public class LZSS extends Algorithm {
    /**
    * Number of bits to codify a position on the sliding window
    */
    private int m = 12;
    /**
    * Number of bits to codify the lenght of a coincidence on the sliding window
    */
    private int n = 4;

    /**
    * Size of the sliding window where we will look for matches
    */
    public static final int windowSize = (1 << 12) - 1;
    /**
    * Maximum size of a coincidence
    */
    private static final int maxLength = (1 << 4) - 1;
    /**
    * Minimal length that a coincidence should have
    */
    private static final int minLength = 2;

    /**
    * Writes a number of bits of a parameter to the output.
    * @param bos BitOuputStream where the bits will be written
    * @param n Parameter to read
    * @param bits Number of bits of the parameter that will be written
    * @throws IOException If writting to the BitOuputStream fails
    */
    void writeCode(BitOutputStream bos, int n, int bits) throws IOException {
        for (int i = 0; i < bits; ++i) {
            bos.write1Bit(n & 1);
            n = n / 2;
        }
    }

    /**
    * Reads a number of bits from BitInputStream
    * @param bis BitInputStream where the bits will be readed
    * @param bits Number of bits that will be readed
    * @return the bit sequence readed
    * @throws IOException If reading from BitInputStream fails
    */
    int readCode(BitInputStream bis, int bits) throws IOException {
        int n = 0;
        for (int i = 0; i < bits; ++i) {
            int next = bis.read1Bit();
            if (next < 0)
                return -1;
            n += next << i;
        }
        return n;
    }

    /**
    * Compresses the input stream into the output stream.
    * @param is InputStream with an amount of data to compress
    * @param os OutputStream will be written with the compressed InputStream
    * @throws IOException If reading/writting to the input and output streams fails
    */
    @Override
    public void compress(InputStream is, OutputStream os) throws IOException {
        ByteArray buffer = new ByteArray();
        BitOutputStream bos = new BitOutputStream(os);

        // size-parameters pass to the output. The decompress function will need them.
        bos.write8Bit(m);
        bos.write8Bit(n);

        ByteArray currentMatch = new ByteArray();
        int matchIndex = 0;
        int tempIndex = 0;
        int next;

        // encode all the stream
        while ((next = is.read()) >= 0) {
            tempIndex = buffer.indexOf(currentMatch.concatenate((byte) next));

            if (tempIndex != -1 && currentMatch.size() < maxLength) {
                currentMatch = currentMatch.concatenate((byte) next);
                matchIndex = tempIndex;
            }
            else {
                // encode current match as an offset/length pair
                if (currentMatch.size() >= minLength) {
                    bos.write1Bit(0); // O-flag to indicate that is an offset/length pair
                    writeCode(bos, matchIndex, m);
                    writeCode(bos, currentMatch.size(), n);
                    buffer = buffer.concatenate(currentMatch);
                    currentMatch = new ByteArray((byte) next);
                    matchIndex = 0;
                }
                // leave the bytes as a literal in the output (unencoded)
                else {
                    currentMatch = currentMatch.concatenate((byte) next);
                    matchIndex = -1;
                    while (currentMatch.size() > 0 && matchIndex == -1) {
                        bos.write1Bit(1); // 1-flag to indicate that is a literal
                        bos.write8Bit((byte) currentMatch.getBytePos(0));
                        buffer = buffer.concatenate(currentMatch.getBytePos(0));
                        currentMatch = currentMatch.subByteArray(1, currentMatch.size());
                        matchIndex = buffer.indexOf(currentMatch);
                    }
                }
                if (buffer.size() > windowSize)
                    buffer = buffer.delete(0, buffer.size() - windowSize);
            }
        }

        // check what left and encode it
        while (currentMatch.size() > 0) {
            // encode current match as an offset/length pair
            if (currentMatch.size() >= minLength) {
                bos.write1Bit(0); // O-flag to indicate that is an offset/length pair
                writeCode(bos, matchIndex, m);
                writeCode(bos, currentMatch.size(), n);
                buffer = buffer.concatenate(currentMatch);
                currentMatch = new ByteArray();
                matchIndex = 0;
            }
            // leave the bytes as a literal in the output (unencoded)
            else {
                matchIndex = -1;
                while (currentMatch.size() > 0 && matchIndex == -1) {
                    bos.write1Bit(1); // 1-flag to indicate that is a literal
                    bos.write8Bit((byte) currentMatch.getBytePos(0));
                    buffer = buffer.concatenate(currentMatch.getBytePos(0));
                    currentMatch = currentMatch.subByteArray(1, currentMatch.size());
                    matchIndex = buffer.indexOf(currentMatch);
                }
            }
            if (buffer.size() > windowSize)
                buffer = buffer.delete(0, buffer.size() - windowSize);
        }
        bos.eof();
    }

    /**
    * Decompresses the input stream into the output stream.
    * @param is InputStream with an amount of data to decompress
    * @param os OutputStream will be written with the decompressed InputStream
    * @throws IOException If reading/writting to the input and output streams fails
    */
    @Override
    public void decompress(InputStream is, OutputStream os) throws IOException {
        BitInputStream bis = new BitInputStream(is);
        int m = bis.read8Bit();
        int windowSize = (1 << m) - 1;
        int n = bis.read8Bit();
        ByteArray buffer = new ByteArray();
        int flag;
        while ((flag = bis.read1Bit()) >= 0) {
            // literal decompression
            if (flag == 1) {
                // leave the literal in the input the way it appears in the output (no traduction)
                int s = bis.read8Bit();
                buffer = buffer.concatenate((byte) s);
                os.write(s);
            }
            else {
                // traduce the offset/length pair and leave the traduced subarray in the output
                int offsetValue = readCode(bis, m);
                int lengthValue = readCode(bis, n);
                if (offsetValue < 0 || lengthValue < 0)
                    break;

                int start = offsetValue;
                int end = start + lengthValue;

                ByteArray temp = buffer.subByteArray(start, end);
                for (int i = 0; i < temp.size(); ++i) {
                    byte b = temp.getBytePos(i);
                    os.write(b);
                }
                buffer = buffer.concatenate(temp);
            }

            if (buffer.size() > windowSize)
                buffer = buffer.delete(0, buffer.size() - windowSize);
        }
    }
}
