package ee.martinharm.logSign.calculators;

import com.guardtime.ksi.hashing.DataHasher;
import com.guardtime.ksi.hashing.HashAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SHA256TreeHashCalculator {

    /**
     * Computes a SHA256 checksum for each line of the input file.
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     *
     * @param inputFile a File to compute the SHA-256 tree hash for
     * @return a byte[] containing the SHA-256 tree hash
     * @throws IOException Thrown if there's an issue reading the input file
     */
    public static byte[] calculateSHA256TreeHash(File inputFile) throws IOException {
        byte[][] lineHashes = getSHA256HashesForLines(inputFile);
        return calculateSHA256TreeHash(lineHashes);
    }

    /**
     * Computes the SHA-256 tree hash for the passed array of line
     * checksums. This method uses a pair of arrays to iteratively compute the tree hash
     * level by level. Each iteration takes two adjacent elements from the
     * previous level source array, computes the SHA-256 hash on their
     * concatenated value and places the result in the next level's destination
     * array. At the end of an iteration, the destination array becomes the
     * source array for the next level.
     * <p>
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     *
     * @param lineHashes An array of SHA-256 checksums
     * @return A byte[] containing the SHA-256 tree hash for the input chunks
     */
    private static byte[] calculateSHA256TreeHash(byte[][] lineHashes) {

        DataHasher dataHasher = new DataHasher(HashAlgorithm.SHA2_256);

        byte[][] prevLvlHashes = lineHashes;

        while (prevLvlHashes.length > 1) {

            int len = prevLvlHashes.length / 2;
            if (prevLvlHashes.length % 2 != 0) {
                len++;
            }

            byte[][] currLvlHashes = new byte[len][];

            int j = 0;

            for (int i = 0; i < prevLvlHashes.length; i = i + 2, j++) {

                if (prevLvlHashes.length - i > 1) {
                    dataHasher.reset();
                    dataHasher.addData(prevLvlHashes[i])
                            .addData(prevLvlHashes[i + 1]);
                    currLvlHashes[j] = dataHasher.getHash().getValue();
                } else {
                    currLvlHashes[j] = prevLvlHashes[i];
                }
            }
            prevLvlHashes = currLvlHashes;
        }
        return prevLvlHashes[0];
    }

    /**
     * Computes a SHA256 checksum for each line of the input file.
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     *
     * @param file A file to compute checksums on
     * @return a byte[][] containing the checksums of each 1 MB chunk
     * @throws IOException Thrown if there's an IOException when reading the file
     */
    private static byte[][] getSHA256HashesForLines(File file) throws IOException {

        DataHasher dataHasher = new DataHasher(HashAlgorithm.SHA2_256);

        long nrOfLines = Files.lines(Paths.get(file.getPath()), Charset.defaultCharset()).count();

        if (nrOfLines == 0) {
            return new byte[][]{dataHasher.getHash().getValue()};
        }

        byte[][] lineHashes = new byte[(int) nrOfLines][];

        FileInputStream fileStream = null;

        try {
            fileStream = new FileInputStream(file);
            int idx = 0;

            BufferedReader b = Files.newBufferedReader(Paths.get(file.getPath()));

            String readLine;

            while ((readLine = b.readLine()) != null) {
                dataHasher.reset();
                dataHasher.addData(readLine.getBytes());
                lineHashes[idx++] = dataHasher.getHash().getValue();
            }

            return lineHashes;

        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException ioe) {
                    System.err.printf("Exception while closing %s.\n %s", file.getName(),
                            ioe.getMessage());
                }
            }
        }
    }

}
