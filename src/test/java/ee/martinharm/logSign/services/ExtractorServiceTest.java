package ee.martinharm.logSign.services;

import com.guardtime.ksi.hashing.HashAlgorithm;
import ee.martinharm.logSign.models.HashNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import static org.junit.Assert.assertEquals;

public class ExtractorServiceTest {
    private ClassLoader classLoader = getClass().getClassLoader();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    @Test
    public void shouldCalculateCorrectSubTreeHashForSingleLineLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("singleLineLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        ExtractorService.extractSubTree(hashTree, 0, 0);
        assertEquals("6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B\r\n" , outContent.toString());
    }

    @Test
    public void shouldCalculateCorrectSubTreeHashForDoubleLineLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("log.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        ExtractorService.extractSubTree(hashTree, 0, 0);
        assertEquals("6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B\r\n" +
                "6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B\r\n" +
                        "\r\n" +
                        "488AD59EFEAFAE6AF0BE932803B65470B908C33377F4FF99AAD1FD8C68C4F463\r\n" , outContent.toString());
    }

    @Test
    public void shouldCalculateCorrectSubTreeHashForTripleLineLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("tripleLineLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        ExtractorService.extractSubTree(hashTree, 0, 0);
        assertEquals("6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B\r\n" +
                "D4735E3A265E16EEE03F59718B9B5D03019C07D8B6C51F90DA3A666EEC13AB35\r\n" +
                "\r\n" +
                "4295F72EEB1E3507B8461E240E3B8D18C1E7BD2F1122B11FC9EC40A65894031A\r\n" +
                "4E07408562BEDB8B60CE05C1DECFE3AD16B72230967DE01F640B7E4729B49FCE\r\n" +
                "\r\n" +
                "0932F1D2E98219F7D7452801E2B64EBD9E5C005539DB12D9B1DDABE7834D9044\r\n" , outContent.toString());
    }

    @Test
    public void shouldCalculateCorrectSubTreeHashForLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("fourLineLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        ExtractorService.extractSubTree(hashTree, 2, 0);
        assertEquals("5FECEB66FFC86F38D952786C6D696C79C2DBC239DD4E91B46729D73A27FB57E9\r\n" +
                "6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B\r\n" +
                "\r\n" +
                "B9B10A1BC77D2A241D120324DB7F3B81B2EDB67EB8E9CF02AF9C95D30329AEF5\r\n" +
                "488AD59EFEAFAE6AF0BE932803B65470B908C33377F4FF99AAD1FD8C68C4F463\r\n" +
                "\r\n" +
                "D1F74BCF489002A8FCA70E11C0A6A1D93278F3A6BA122140967EC90F04D245BD\r\n", outContent.toString());
    }

    @Test
    public void shouldCalculateCorrectSubTreeHashForSevenLineLogFile() throws IOException {
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("sevenLineLog.txt")).getFile());
        HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
        ExtractorService.extractSubTree(hashTree, 6, 0);
        assertEquals("5FECEB66FFC86F38D952786C6D696C79C2DBC239DD4E91B46729D73A27FB57E9\r\n" +
                "\r\n" +
                "5FECEB66FFC86F38D952786C6D696C79C2DBC239DD4E91B46729D73A27FB57E9\r\n" +
                "488AD59EFEAFAE6AF0BE932803B65470B908C33377F4FF99AAD1FD8C68C4F463\r\n" +
                "\r\n" +
                "3F2457814B200D8D3051EA43DC04ADA96FE35CF3880E627126C5742D2E6989FE\r\n" +
                "06852DE4B6B79A48F7C7BF7F71FEDC1E73D5E79723AEB43BF813858BA72174B8\r\n" +
                "\r\n" +
                "103EB725DE17FF254D66AEFA29D49E45B0A605296C78D1A919B0EE97DDDDE741\r\n", outContent.toString());
    }
}