package ee.martinharm.logSign.services;

import com.guardtime.ksi.hashing.HashAlgorithm;
import ee.martinharm.logSign.converters.ByteConverter;
import ee.martinharm.logSign.models.HashNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CalculatorServiceTest {
    private ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void shouldCalculateCorrectTreeTopHashForEmptyLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("emptyLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        byte[] treeTopHash = hashTree[hashTree.length-1][0].getValue();
        String hexTreeTopHash = ByteConverter.toHex(treeTopHash);
        assertThat(hexTreeTopHash, is("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855"));
    }

    @Test
    public void shouldCalculateCorrectTreeTopHashForLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("log.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        byte[] treeTopHash = hashTree[hashTree.length-1][0].getValue();
        String hexTreeTopHash = ByteConverter.toHex(treeTopHash);
        assertThat(hexTreeTopHash, is("488AD59EFEAFAE6AF0BE932803B65470B908C33377F4FF99AAD1FD8C68C4F463"));
    }

    @Test
    public void shouldCalculateCorrectTreeTopHashForSingleLineLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("singleLineLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        byte[] treeTopHash = hashTree[hashTree.length-1][0].getValue();
        String hexTreeTopHash = ByteConverter.toHex(treeTopHash);
        assertThat(hexTreeTopHash, is("6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B"));
    }

    @Test
    public void shouldCalculateCorrectTreeTopHashForTripleLineLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("tripleLineLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        byte[] treeTopHash = hashTree[hashTree.length-1][0].getValue();
        String hexTreeTopHash = ByteConverter.toHex(treeTopHash);
        assertThat(hexTreeTopHash, is("0932F1D2E98219F7D7452801E2B64EBD9E5C005539DB12D9B1DDABE7834D9044"));
    }

    @Test
    public void shouldThrowExceptionWhenInputFileNotFound() {
        boolean thrown = false;

        try {
            File inputFile = new File("");
            CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        } catch (IOException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }
}