package ee.martinharm.logSign;

import org.junit.Test;
import java.util.Objects;
import static org.junit.Assert.assertTrue;

public class LogSignApplicationTest {
    private ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void shouldThrowExceptionWhenProvidedFileNotFound() {
        LogSignApplication.main(new String[]{Objects.requireNonNull(classLoader.getResource("log.txt")).getFile()});
        assertTrue(true);
    }
}