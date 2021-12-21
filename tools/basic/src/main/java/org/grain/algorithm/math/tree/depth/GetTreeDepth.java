package org.grain.algorithm.math.tree.depth;

import org.grain.algorithm.math.tree.Node;

/**
 * @author laowu
 */
@SuppressWarnings("unused")
public class GetTreeDepth {
    public int maxDepth(Node root) {
        if (root == null) {
            return 0;
        } else {
            int max = 0;
            for (int i = 0; i < root.getChildren().size(); i++) {
                /*遍历子结点，找出子结点中的最大深度*/
                max = Math.max(max, maxDepth(root.getChildren().get(i)));
            }
            return 1 + max;
        }
    }

}


