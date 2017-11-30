package no.mernstsen.bst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class RedBlackTreeTest {
    private RedBlackTree<Integer> rbTree;

    @BeforeEach
    void init() {
        rbTree = new RedBlackTree<>(Comparator.comparingInt(Integer::intValue));
    }

    Integer[] makeSmallTree() {
        Integer[] numbers = {5, 50, -1, -5, 234, 17, 47, 48, 49, 15, 0};
        for (Integer n : numbers) {
            rbTree.add(n);
        }
        return numbers;
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

    @Test
    void validateTreeStructure() {
        makeSmallTree();
        assertTrue(rbTree.isValidRedBlackTree());
    }

    @Test
    void minReturnsSmallestElement() {
        Integer[] numbers = makeSmallTree();
        Arrays.sort(numbers);
        assertEquals(numbers[0], rbTree.min());
    }

    @Test
    void removeElementInTreeReturnsTrue() {
        Integer[] numbers = makeSmallTree();
        boolean result = rbTree.remove(numbers[0]);
        assertTrue(result);
    }

    @Test
    void removeElementNotInTreeReturnsFalse() {
        rbTree.add(4);
        rbTree.add(3);
        rbTree.add(2);
        boolean result = rbTree.remove(18);
        assertFalse(result);
    }

    @Test
    void removeElementInTreeDecreasesSizeByOne() {
        Integer[] numbers = makeSmallTree();
        assertEquals(numbers.length, rbTree.size());
        boolean result = rbTree.remove(numbers[0]);
        assertEquals(numbers.length - 1, rbTree.size());
    }

    @Test
    void removedElementIsNoLongerInTree() {
        Integer[] numbers = makeSmallTree();
        boolean result = rbTree.remove(numbers[0]);
        assertFalse(rbTree.contains(numbers[0]));
    }

    @Test
    void validateTreeStructureAfterRemoval() {
        Integer[] numbers = makeSmallTree();
        assertTrue(rbTree.isValidRedBlackTree());
        boolean result = rbTree.remove(numbers[1]);
        assertTrue(rbTree.isValidRedBlackTree());
    }
}