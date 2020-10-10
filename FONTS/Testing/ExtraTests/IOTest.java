package ExtraTests;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.* ;
import Domain.BitInputStream;
import Domain.BitOutputStream;
import java.util.Random;

// There's no need to test Byte + bit functions at the same time
// because Byte functions are implemented just by doing several
// calls to bit functions
public class IOTest {
    @Test
    public void IOPushBitsTest() {
        for (int i = 0; i <= 1025; ++i) {
            IOPushBits(i);
        }
    }

    @Test
    public void IOPushBytesTest() {
        for (int i = 0; i <= 1025; ++i) {
            IOPushBytes(i);
        }
    }


    // Both are tested at the same time because we dont care about the disk representation of the bits,
    // we just care about the recoverability of the data:
    // X0->WRITE->X->READ->X1 (X0 == X1)
    private void IOPushBits(int bits) { 
        try {
            byte[] IN = new byte[(bits/8)+1];
            Random random = new Random();
            random.nextBytes(IN);
            
            ByteArrayOutputStream os0 = new ByteArrayOutputStream();
            BitOutputStream bos = new BitOutputStream(os0);

            for (int i = 0; i < bits; ++i) {
                int mask = 1 << (i%8);
                int val = IN[i/8] & mask;
                bos.write1Bit(val); // 0: 0,  1: otherwise
            }
            bos.flush();
            byte[] Disk = os0.toByteArray();

            InputStream is0 = new ByteArrayInputStream(Disk);
            BitInputStream bis = new BitInputStream(is0);
            int next;
            String output;
            for (int i = 0; i < bits; ++i) {
                int value = (IN[i/8] & (1 << (i%8))) >>> (i%8);
                next = bis.read1Bit();
                output = String.format("\n[!!!] READ RETURNED -1 ON BIT %d OF %d\n", i+1, bits);
                assertTrue(output, next >= 0);
                output = String.format("\n[!!!] DIFFERENCE IN BIT %d (%d) OF %d (%d)\n", i+1, next, bits, value);
                assertEquals(output, value, next);
            }
            next = bis.read1Bit();
            output = String.format("\n[!!!] END OF STREAM NOT REACHED, READ DATA > WRITTEN DATA\n");
            assertEquals(output, -1, next);
        
        } catch (Exception e) {
            String output = String.format("\n[!!!] EXCEPTION REACHED: %s\n", e.toString());
            assertEquals(output, "no exception", "exception");
        }
    }

    // Both are tested at the same time because we dont care about the disk representation of the bits,
    // we just care about the recoverability of the data:
    // X0->WRITE->X->READ->X1 (X0 == X1)
    private void IOPushBytes(int Bytes) {
        try {
            byte[] IN = new byte[Bytes];
            Random random = new Random();
            random.nextBytes(IN);
            
            ByteArrayOutputStream os0 = new ByteArrayOutputStream();
            BitOutputStream bos = new BitOutputStream(os0);

            for (int i = 0; i < Bytes; ++i) {
                bos.write8Bit(IN[i]);
            }
            bos.flush();
            byte[] Disk = os0.toByteArray();

            InputStream is0 = new ByteArrayInputStream(Disk);
            BitInputStream bis = new BitInputStream(is0);
            int next;
            String output;
            for (int i = 0; i < Bytes; ++i) {
                next = bis.read8Bit();
                output = String.format("\n[!!!] READ RETURNED -1 ON BYTE %d OF %d\n", i+1, Bytes);
                assertTrue(output, next >= 0);
                output = String.format("\n[!!!] DIFFERENCE IN BYTE %d (%d) OF %d (%d)\n", i+1, next, Bytes, IN[i]);
                assertEquals(output, IN[i], (byte)next);
            }
            next = bis.read8Bit();
            output = String.format("\n[!!!] END OF STREAM NOT REACHED, READ DATA > WRITTEN DATA\n");
            assertEquals(output, -1, next);
        
        } catch (Exception e) {
            String output = String.format("\n[!!!] EXCEPTION REACHED: %s\n", e.toString());
            assertEquals(output, "no exception", "exception");
        }
    }
}
