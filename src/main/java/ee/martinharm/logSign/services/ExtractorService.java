package ee.martinharm.logSign.services;

import ee.martinharm.logSign.models.HashNode;

public class ExtractorService {
    /**
     * Recursively extracts sub tree for given hash
     *
     * @param hashTree       Hash tree from where to extract
     * @param selectedNodeId Hash to extract
     * @param branch         Hash level to start from
     */

    public static void extractSubTree(HashNode[][] hashTree, int selectedNodeId, int branch) {

        int nodeCount = hashTree[branch].length;

        HashNode selectedNode = hashTree[branch][selectedNodeId];

        System.out.println(selectedNode.getHexValue());

        if (isEvenNode(selectedNodeId) && nodeCount > 1) {
            if (!isLastNode(selectedNodeId, nodeCount)) {
                System.out.println(hashTree[branch][selectedNodeId + 1].getHexValue());
            } else {
                HashNode sisterNode = hashTree[branch][selectedNodeId - 1];

                if (selectedNode.getParentId() == sisterNode.getParentId()) {
                    System.out.println(sisterNode.getHexValue());
                }
            }
        }

        if (isOddNode(selectedNodeId)) {
            System.out.println(hashTree[branch][selectedNodeId - 1].getHexValue());
        }

        if (branch == hashTree.length - 1) {
            return;
        }

        System.out.println();

        extractSubTree(hashTree, selectedNode.getParentId(), branch + 1);
    }

    private static boolean isEvenNode(int selectedNodeId) {
        return selectedNodeId % 2 == 0;
    }

    private static boolean isOddNode(int selectedNodeId) {
        return selectedNodeId % 2 != 0;
    }

    private static boolean isLastNode(int selectedNodeId, int nodeCount) {
        return selectedNodeId == nodeCount - 1;
    }


}
