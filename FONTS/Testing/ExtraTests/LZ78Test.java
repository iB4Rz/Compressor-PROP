package ExtraTests;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import Domain.LZ78;
import java.io.*;
import java.util.Random;

public class LZ78Test {
    @Test
    public void CompressDecompressSmall() {
        // Small size subset
        for (int Fsize = 0; Fsize <= 17; ++Fsize)
            for (int Ds = 0; Ds < 31; ++Ds)
                for (int attempt = 1; attempt <= 256; ++attempt)
                    CompressDecompress(Ds, Fsize);
    }

    @Test
    public void CompressDecompressMedium() {
        // Medium size subset
        for (int Fsize = 1023; Fsize <= 1032; ++Fsize)
            for (int Ds = 0; Ds < 31; ++Ds)
                for (int attempt = 1; attempt <= 256; ++attempt)
                    CompressDecompress(Ds, Fsize);
    }

    @Test
    public void CompressDecompressBig() {
        // Small size subset
        for (int Fsize = 262143; Fsize <= 262145; ++Fsize)
            for (int Ds = 0; Ds < 31; ++Ds)
                CompressDecompress(Ds, Fsize);
    }

    public void CompressDecompress(int DictSize, int Fsize) {
        try {
            byte[] IN = new byte[Fsize];
            new Random().nextBytes(IN);
            InputStream is0 = new ByteArrayInputStream(IN);
            ByteArrayOutputStream os0 = new ByteArrayOutputStream();
            LZ78 alg_0 = new LZ78();
            alg_0.setDictionarySize(DictSize);
            alg_0.compress(is0, os0);
            os0.close();
            byte[] Compressed = os0.toByteArray();

            InputStream is1 = new ByteArrayInputStream(Compressed);
            ByteArrayOutputStream os1 = new ByteArrayOutputStream();
            LZ78 alg_1 = new LZ78();
            alg_1.decompress(is1, os1);
            os1.close();
            byte[] Decompressed = os1.toByteArray();
            
            int size = IN.length;
            String output = String.format("\n[!!!] ORIGINAL IS: %dbytes AND DECOMPRESSED LENGTH IS: %dbytes\n", size, Decompressed.length);
            assertEquals(output, size, Decompressed.length);
            for (int i = 0; i < size; ++i) {
                output = String.format("\n[!!!] DIFFERENCE IN BYTE NUMBER %d. ORIGINAL HAS %dbytes DECOMPRESSED HAS %dbytes\n"
                +"DICTIONARY SIZE WAS %d (2^%d)\n", i, size, Decompressed.length, DictSize, DictSize);
                assertEquals(output, IN[i], Decompressed[i]);
            }
        
        } catch (Exception e) {
            String output = String.format("\n[!!!] EXCEPTION REACHED: %s\n", e.toString());
            assertEquals(output, "no exception", "exception");
        }
    }
}