package UnitTest_JPEG;
import java.io.*;

public class InputStreamStub extends java.io.InputStream {
    private byte[] DiskData;
    private int currentIndex = 0;
    final boolean verbose = true;

    public void StubInitializeDiskData(byte[] DiskData) {
        this.DiskData = DiskData;
    }

    @Override
    public int read() throws IOException {
        
        if (currentIndex >= DiskData.length) {
            if (verbose)
                System.out.print("Calls read() from InputStream STUB [returns: -1]\n");
            return -1;
        }
        int ret = DiskData[currentIndex] & 0x000000FF;
        ++currentIndex;
        if (verbose)
            System.out.printf("Calls read() from InputStream STUB [returns: %d]\n", ret);
        return ret;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if ((currentIndex+b.length-1) >= DiskData.length) {
            if (verbose)
                System.out.printf("Calls read(byte[] b) from InputStream STUB [arg0: %d byte array; returns: -1]\n", b.length);
            return -1;
        } 
        for (int i = 0; i < b.length; ++i) {
            b[i] = DiskData[currentIndex];
            ++currentIndex;
        }
        if (verbose)
            System.out.printf("Calls read(byte[] b) from InputStream STUB [arg0: %d byte array; returns: 0]\n", b.length);
        return 0;
    }

    @Override
    public void close() throws IOException {
        // Do nothing
    }
}