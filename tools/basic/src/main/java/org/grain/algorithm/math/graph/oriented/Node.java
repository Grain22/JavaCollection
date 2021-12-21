package org.grain.algorithm.math.graph.oriented;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author wulifu
 * 有向图基础节点
 */
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    String id;
    String description;
    String option;
    Set<String> next;
}
