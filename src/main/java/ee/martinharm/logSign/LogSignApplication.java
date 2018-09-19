package ee.martinharm.logSign;

import com.guardtime.ksi.hashing.DataHasher;
import com.guardtime.ksi.hashing.HashAlgorithm;
import ee.martinharm.logSign.converters.ByteConverter;
import ee.martinharm.logSign.models.HashNode;
import ee.martinharm.logSign.services.CalculatorService;
import ee.martinharm.logSign.services.ExtractorService;
import ee.martinharm.logSign.services.SigningService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class LogSignApplication {

    public static void main(String[] args) {

        validateInputParams(args);

        File inputFile = new File(args[0]);

        try {
            HashNode[][] hashTree = CalculatorService.calculateHashTree(inputFile, HashAlgorithm.SHA2_256);
            byte[] treeTopHash = hashTree[hashTree.length - 1][0].getValue();

            SigningService.signHash(treeTopHash);

            if (hasLineArgument(args)) {
                int hashId = getSelectedHashId(hashTree[0], args[1].getBytes());
                ExtractorService.extractSubTree(hashTree, hashId, 0);
            } else {
                System.out.println(ByteConverter.toHex(treeTopHash));
            }

        } catch (IOException ioe) {
            System.err.format("Exception reading from file: %s",
                    inputFile.getName());
            System.exit(-1);
        } catch (Exception e) {
            System.err.println("Exception accourd in Tree calculation!");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static int getSelectedHashId(HashNode[] hashNodes, byte[] selectedLine) throws Exception {
        byte[] selectedLineHash = getLineHash(selectedLine);

        for (int i = 0; i < hashNodes.length; i++) {
            if (Arrays.equals(
                    hashNodes[i].getValue(),
                    selectedLineHash)) {
                return i;
            }
        }

        throw new Exception("Specified line was not found from hash tree!");
    }

    private static boolean hasLineArgument(String[] args) {
        return args != null && args.length == 2;
    }

    private static byte[] getLineHash(byte[] selectedLine) {
        return new DataHasher().addData(selectedLine).getHash().getValue();
    }

    private static void printUsageMessage() {
        System.err.println("Usage: java -jar logSigner.jar {PATH TO LOG FILE} [OPTIONALLY SPECIFIED LINE]");
    }

    private static void validateInputParams(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("No arguments provided.");
            printUsageMessage();
            System.exit(-1);
        } else if (args.length > 2) {
            System.err.println("Too many arguments provided");
            printUsageMessage();
            System.exit(-1);
        }
    }

}