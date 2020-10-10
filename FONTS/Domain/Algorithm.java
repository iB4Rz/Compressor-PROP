package Domain;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Algorithm {
    /**
     * compresses an input stream to an output stream
     * @param is Input stream of data
     * @param os Output stream of data
     * @throws Exception if compression cant be done due to i/o errors or format issues
     */
    public abstract void compress(InputStream is, OutputStream os) throws Exception;

    /**
     * decompresses an input stream to an output stream
     * @param is Input stream of data
     * @param os Output stream of data
     * @throws Exception if decompression cant be done due to i/o errors or format issues
     */
    public abstract void decompress(InputStream is, OutputStream os) throws Exception;
}