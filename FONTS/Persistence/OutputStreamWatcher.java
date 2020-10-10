package Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class OutputStreamWatcher extends FilterOutputStream { // Decorator
    /**
     * written bytes counter
     */
    long writtenBytes = 0;
    
    /**
     * Constructor
     * @param os Output Stream that will be decorated
     */
    public OutputStreamWatcher(OutputStream os) {
        super(os);
    }

    /**
     * write byte into stream
     * @param b byte to be written
     * @throws IOException if error writting
     */
    @Override
    public void write(int b) throws IOException {
        super.write(b);
        ++writtenBytes;
    }

    /**
     * written bytes getter
     * @return bytes written
     */
    public long getWrittenBytes() {
        return writtenBytes;
    }
}