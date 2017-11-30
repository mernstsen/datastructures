package no.mernstsen.bst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class RedBlackTreeTest {
    private RedBlackTree<Integer> rbTree;

    @BeforeEach
    void init() {
        rbTree = new RedBlackTree<>(Comparator.comparingInt(Integer::intValue));
    }

    @Test
    void treeWithOneElementContainsElement() {
        rbTree.add(3);
        assertTrue(rbTree.contains(3));
    }

    @Test
    void treeWithOneElementDoesNotContainElement() {
        rbTree.add(0);
        assertFalse(rbTree.contains(1));
    }

    @Test
    void treeWithMultipleElementsContainElement() {
        rbTree.add(8);
        rbTree.add(1);
        rbTree.add(43);
        rbTree.add(3);
        rbTree.add(18);
        assertTrue(rbTree.contains(18));
    }

    @Test
    void emptyTreeDoesNotContainAnElement() {
        assertFalse(rbTree.contains(3));
    }

    @Test
    void addingElementIncreasesSizeByOne() {
        assertEquals(0, rbTree.size());
        rbTree.add(77);
        assertEquals(1, rbTree.size());
    }
}