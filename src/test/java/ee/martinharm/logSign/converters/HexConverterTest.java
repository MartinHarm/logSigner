package ee.martinharm.logSign.converters;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HexConverterTest {

    @Test
    public void shouldConvertBytesToHex() {
        byte[] bytes = {0,1,2,3,4};
        assertThat(HexConverter.convert(bytes), is("0001020304"));
    }
}