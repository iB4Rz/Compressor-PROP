package Domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Albert Ibars
 */

public class LZW extends Algorithm {

    Dictionary dict;
    // The number of bits to be written for each code
    int nBits = 12;

    // The previous byte array that we should remember
    ByteArray emptyBA = new ByteArray();
    ByteArray ba = emptyBA;

    /**
     * Encodes the next byte
     * @param n the byte to encode
     * @return the code generated, if not returns -1
     */
    private int encodeByte (int n) {
        byte b = (byte)n;
        ByteArray aux = ba.concatenate(b);
        int code = dict.getNumStr(aux);
        // if it exists then we continue searching for a longer byte array
        if (code != -1) {
            ba = aux;
            return -1;
        }
        else {
            dict.add(aux);
            aux = ba;
            ba = new ByteArray(b);
            return dict.getNumStr(aux);
        }
    }

    /**
     * Encode de last byte of the sequence if there is something left
     * @return the code left
     */
    private int encodeLastByte() {
        ByteArray aux = ba;
        ba = emptyBA;
        return dict.getNumStr(aux);
    }

    /**
     * Write the code in bits into output stream with the help of the BitOutputStream
     * @param bos BitOuputStream where the bits will be written
     * @param code the code to write
     * @throws IOException if writting to the output stream fails
     */
    private void writeCode (BitOutputStream bos, int code) throws IOException {
        for (int i = 0; i < nBits; ++i) {
            bos.write1Bit(code&1);
            code /= 2;  
        }
    }

    /**
     * Sets the dictionary size that will be used at compression time
     * @param DictBitSize maximum size in bytes of the input stream (use 2^31-1 if unknown or default), this value its needed for decompress
     */
    public void setDictionarySize(int DictBitSize) {
        if (DictBitSize > 31 || DictBitSize < 8) throw new IllegalArgumentException("Dict size must be between 2^8 and 2^31 !");
        nBits = DictBitSize;
    }

    /**
     * Compresses the input stream into the output stream given the max size of the input stream
     * @param is InputStream with an amount of data between 0 and DictBitSize
     * @param os OutputStream will be written with the compressed InputStream
     * @throws Exception If reading or writting to a stream fails
     */
    @Override
    public void compress (InputStream is, OutputStream os) throws Exception {
        BitOutputStream bos = new BitOutputStream(os);
        bos.write8Bit(nBits); // Write DictBitSize to the compressed stream

        dict = new Dictionary(1<<nBits);
        // Create a new dictionary with maximum of 2^bits entries
        for (int i = 0; i < 256; ++i)
            dict.add(new ByteArray((byte)i));

        int code;   // next input byte
        int next;   // next code generated
        while ((next = is.read()) >= 0){
            code = encodeByte(next);
            if (code >= 0) writeCode(bos, code);
        }
        code = encodeLastByte();
        if (code >= 0) writeCode(bos, code);
        bos.eof();
    }

    /**
     * Read the code from the given bit input stream, and returns it as an int
     * @param bis BitInputStream where the bits will be readed
     * @return an int with the code of nBits gereneted from the input stream
     * @throws IOException If reading from the input stream fails
     */
    private int readCode (BitInputStream bis) throws IOException {
        int n = 0;
        for (int i = 0; i < nBits; ++i) {
            int next = bis.read1Bit();
            if (next < 0) return -1;
            n += next << i;
        }
        return n;
    }

    /**
     * Decodes the next code
     * @param code the code to decode
     * @return a ByteArray with the code decoded
     */
    private ByteArray disarray (int code) {
        ByteArray s = dict.getStrNum(code);
        if (s == null) {
            s = ba.concatenate(ba.getBytePos(0));
            dict.add(s);
        }
        else 
            if (!ba.isEmpty()) 
                dict.add(ba.concatenate(s.getBytePos(0)));
            ba = s;
        return ba;
    }

    /**
     * Decompresses the input stream into the output stream given the max size of the input stream
     * @param is InputStream with an amount of data between 0 and DictBitSize
     * @param os OutputStream will be written with the compressed InputStream
     * @throws Exception If reading/writting to the input and output streams fails
     */
    @Override
    public void decompress (InputStream is, OutputStream os) throws Exception {
        BitInputStream bis = new BitInputStream(is);
        nBits = bis.read8Bit();  // Dict size
        if (nBits > 31 || nBits < 8) throw new IllegalArgumentException("Dict size must be between 2^8 and 2^31 !");
        // Create a new dictionary with maximum of 2^bits entries
        dict = new Dictionary(1<<nBits);
        // Add all ascii characters to the dictionary
        for (int i = 0; i < 256; ++i)
            dict.add(new ByteArray((byte)i));

        ByteArray s;    // Next entry
        int code;       // Next code to be read
        while ((code = readCode(bis)) >= 0) {
            s = disarray(code);
            os.write(s.getBytes());
        }
    }
}