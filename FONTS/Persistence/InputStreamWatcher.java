package Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class InputStreamWatcher extends FilterInputStream { // Decorator
    /**
     * byte counter
     */
    long readBytes = 0;
    
    /**
     * Constructor
     * @param is Input stream that will be decorated
     */
    public InputStreamWatcher(InputStream is) {
        super(is);
    }

    /**
     * Input Stream read function that also counts read bytes
     * @return byte read or -1 if eof
     * @throws IOException if error reading
     */
    @Override
    public int read() throws IOException {
        int result = super.read();
        if (result != -1)
            ++readBytes;
        return result;
    }

    /**
     * Input Stream read function that also counts read bytes
     * @param b byte array where the read bytes will be stored
     * @return read bytes
     * @throws IOException if error reading
     */
    @Override
    public int read(byte[] b) throws IOException {
        int result = super.read(b);
        if (result != -1)
            readBytes += b.length;
        return result;
    }

    /**
     * read bytes getter
     * @return bytes read
     */
    public long getReadBytes() {
        return readBytes;
    }
}