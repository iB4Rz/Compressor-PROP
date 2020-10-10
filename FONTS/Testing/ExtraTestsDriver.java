import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import ExtraTests.*;

public class ExtraTestsDriver {
        public static void main(String args[]) {
            System.out.printf("\n[III] INFO: THIS TEST LASTS FOR SEVERAL MINUTES AND CONSUMES A LOT OF SYSTEM ENTROPY, BE PATIENT [III]\n\n\n\n");
            JUnitCore core = new JUnitCore();
            CustomTestListener cl = new CustomTestListener();
            core.addListener(cl);
            core.run(IOTest.class, LZ78Test.class, LZSSTest.class, LZWTest.class, HuffmanTest.class);
            System.out.printf("\n\nTests Run: %d   Tests Failed: %d   Tests Ignored: %d\n", cl.Started, cl.Failed, cl.Ignored);
        }
}