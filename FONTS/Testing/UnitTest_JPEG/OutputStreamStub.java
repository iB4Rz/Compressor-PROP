package UnitTest_JPEG;
import java.io.*;

public class OutputStreamStub extends java.io.OutputStream {
    private byte[] DiskData = new byte[0];
    final boolean verbose = true;

    public byte[] StubGetWrittenDiskData() {
        return DiskData;
    }

    @Override
    public void write(int arg0) throws IOException {
        if (verbose)
            System.out.printf("Calls write(int arg0) from OutputStream STUB [arg0: %d]\n", arg0&0x00FF);
        byte b = (byte)(arg0);
        byte[] aux = new byte[DiskData.length + 1];
        System.arraycopy(DiskData, 0, aux, 0, DiskData.length);
        DiskData = aux;
        DiskData[DiskData.length - 1] = b;
    }

    @Override
    public void flush() throws IOException {
        // Do nothing
    }

    @Override
    public void close() throws IOException {
        // Do nothing
    }
}