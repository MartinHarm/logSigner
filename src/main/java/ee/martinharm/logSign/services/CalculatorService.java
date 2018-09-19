package ee.martinharm.logSign.services;

import com.guardtime.ksi.hashing.DataHasher;
import com.guardtime.ksi.hashing.HashAlgorithm;
import ee.martinharm.logSign.models.HashNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CalculatorService {

    private static DataHasher dataHasher;

    /**
     * Computes a hash checksum for each line of the input file.
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     *
     * @param inputFile a File to compute the tree hash for
     * @return a HashNode[][] containing the hashTree
     * @throws IOException Thrown if there's an issue reading the input file
     */
    public static HashNode[][] calculateHashTree(File inputFile, HashAlgorithm hashAlgorithm) throws IOException {

        dataHasher = new DataHasher(hashAlgorithm);

        HashNode[] lineHashNodes = getHashesForLines(inputFile);

        if (lineHashNodes.length == 1) {
            return buildSingleNodeTree(lineHashNodes);
        }

        return calculateHashTree(lineHashNodes);
    }

    /**
     * This method iteratively computes the hash tree
     * level by level. Each iteration takes two adjacent elements from the
     * previous level source array, computes the hash on their
     * concatenated value and places the result in the next level's destination
     * array. At the end of an iteration, the destination array becomes the
     * source array for the next level.
     * <p>
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     *
     * @param lineHashNodes An array of checksums
     * @return A HashNode[][] containing the hash tree for the input lines
     */
    private static HashNode[][] calculateHashTree(HashNode[] lineHashNodes) {

        HashNode[][] hashTree = buildInitialTree(lineHashNodes);

        int level = 1;
        int childNode;
        int parentNode = 0;

        HashNode[] prevLevelNodes = lineHashNodes;

        while (prevLevelNodes.length > 1) {

            int len = getLevelNodeCount(prevLevelNodes.length);

            hashTree[level] = new HashNode[len];

            HashNode[] currentLevelNodes = new HashNode[len];

            childNode = 0;

            for (int prevCounter = 0, currentCounter = 0; prevCounter < prevLevelNodes.length; prevCounter += 2, currentCounter++) {
                int prevLevelNodeCount = prevLevelNodes.length - prevCounter;

                if (prevLevelNodeCount > 1) {
                    dataHasher.reset();
                    byte[] prevLevelLeftHash = prevLevelNodes[prevCounter].getValue();
                    byte[] prevLevelRightHash = prevLevelNodes[prevCounter + 1].getValue();
                    dataHasher.addData(prevLevelLeftHash)
                            .addData(prevLevelRightHash);
                    byte[] currentLevelHash = dataHasher.getHash().getValue();
                    HashNode currentLevelHashNode = new HashNode(dataHasher.getHash().getValue());
                    currentLevelNodes[currentCounter] = currentLevelHashNode;

                    HashNode currentNode = new HashNode(currentLevelHash);

                    if (isEvenNode(childNode)) {
                        parentNode++;
                    }

                    currentNode.setParentId(parentNode);

                    hashTree[level][childNode] = currentNode;

                } else {
                    currentLevelNodes[currentCounter] = prevLevelNodes[prevCounter];
                    HashNode hashNode = new HashNode(currentLevelNodes[currentCounter].getValue());
                    hashNode.setParentId(parentNode);
                    hashTree[level][childNode] = hashNode;
                }
                childNode++;
            }
            level++;
            parentNode = 0;
            prevLevelNodes = currentLevelNodes;
        }

        return hashTree;

    }

    /**
     * Computes a hash for each line of the input file.
     * https://docs.aws.amazon.com/amazonglacier/latest/dev/checksum-calculations.html
     *
     * @param file A file to compute checksums on
     * @return a HashNode[] containing the checksums of each line
     * @throws IOException Thrown if there's an IOException when reading the file
     */
    private static HashNode[] getHashesForLines(File file) throws IOException {

        dataHasher.reset();

        long nrOfLines = Files.lines(Paths.get(file.getPath()), Charset.defaultCharset()).count();

        if (nrOfLines == 0) {
            //FIXME - what to do when file has no lines?
            return new HashNode[]{new HashNode(dataHasher.getHash().getValue())};
        }

        HashNode[] lineHashes = new HashNode[(int) nrOfLines];

        FileInputStream fileStream = null;

        try {
            fileStream = new FileInputStream(file);
            int idx = 0;

            BufferedReader b = Files.newBufferedReader(Paths.get(file.getPath()));

            String readLine;

            while ((readLine = b.readLine()) != null) {
                dataHasher.reset();
                dataHasher.addData(readLine.getBytes());
                HashNode hash = new HashNode(dataHasher.getHash().getValue());

                lineHashes[idx++] = hash;
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

    private static boolean isEvenNode(int childNode) {
        return childNode % 2 == 0 && childNode != 0;
    }

    private static HashNode[][] buildSingleNodeTree(HashNode[] lineHashNodes) {
        HashNode[][] hashTree = new HashNode[1][1];
        hashTree[0][0] = lineHashNodes[0];
        return hashTree;
    }

    private static HashNode[][] buildInitialTree(HashNode[] hashNodes) {

        int levelCount = getLevelCount(hashNodes.length, 1);

        HashNode[][] hashTree = new HashNode[levelCount][];

        hashTree[0] = new HashNode[hashNodes.length];

        int child = 0;
        int parent = 0;

        for (HashNode item : hashNodes) {
            item.setParentId(parent);
            hashTree[0][child] = item;
            child++;
            if (child % 2 == 0) {
                parent++;
            }
        }

        return hashTree;
    }

    private static int getLevelNodeCount(int prevLevelNodeCount) {
        int nodeCount = prevLevelNodeCount / 2;
        if (prevLevelNodeCount % 2 != 0) {
            nodeCount++;
        }
        return nodeCount;
    }

    private static int getLevelCount(int lineCount, int level) {

        if (lineCount == 1) {
            return level;
        }

        return getLevelCount((int) Math.ceil((double) lineCount / 2), level + 1);
    }

}
