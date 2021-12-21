package org.grain.algorithm.math.tree;


import lombok.Data;

import java.util.List;

@Data
public class Node {
    private int val;
    private List<Node> children;

    public Node() {
    }

    public Node(int val, List<Node> children) {
        this.val = val;
        this.children = children;
    }
}
