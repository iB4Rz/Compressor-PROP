import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import java.util.HashMap;

public class CustomTestListener extends RunListener {
    int Started = 0;
    int Failed = 0;
    int Ignored = 0;
    HashMap<Integer, Boolean> map = new HashMap<Integer,Boolean>(); // TestHASH - SUCCEEDED
    @Override
    public void testRunStarted(Description description) throws Exception {
        
        super.testRunStarted(description);
    }
    @Override
    public void testStarted(Description description) throws Exception {
        ++Started;
        map.put(description.hashCode(), true);
        System.out.printf("TEST %s::%s STARTED\n", description.getClassName(), description.getMethodName());
        super.testStarted(description);
    }
    @Override
    public void testFinished(Description description) throws Exception {
        assert(map.containsKey(description.hashCode()));
        Boolean succeeded = map.get(description.hashCode());
        if (succeeded)
            System.out.printf("TEST %s::%s SUCCEEDED!\n\n", description.getClassName(), description.getMethodName());
        super.testFinished(description);
    }
    @Override
    public void testIgnored(Description description) throws Exception {
        ++Ignored;
        System.out.printf("TEST %s::%s IGNORED\n\n", description.getClassName(), description.getMethodName());
        super.testIgnored(description);
    }
    @Override
    public void testFailure(Failure failure) throws Exception {
        ++Failed;
        Description d = failure.getDescription();
        map.put(d.hashCode(), false);
        System.out.printf("TEST %s::%s FAILED!\n", d.getClassName(), d.getMethodName());
        System.out.printf("%s\n\n", failure.getTrace());
        super.testFailure(failure);
    }
}