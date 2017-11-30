package no.mernstsen.bst;

import java.util.*;

/**
 * A red-black tree is a binary tree with the following properties:
 *
 *   1. A node is red or black.
 *   2. The root node is black.
 *   3. Every leaf is black.
 *   4. If a node is red, both its children is black.
 *   5. For each node, all paths from the node to descendant leaves have the same number of black
 *      nodes.
 *
 * These properties ensure that no path from the root to a leave is more than twice as long as any
 * other such path, so that the tree remains approximately balanced.
 *
 * The implementation is based on the description in Introduction to Algorithms, third edition (Cormen,
 * Leiserson, Rivest, Stein).
 */
public class RedBlackTree<E> {

    private Node nil = new Node(null, false); // Sentinel leaf node
    private Node root = nil;
    private final Comparator<E> comparator;
    private int size = 0;

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public void add(E e) {
        Node z = new Node(e, true);
        z.left = nil;
        z.right = nil;

        Node y = nil;
        Node x = root;
        while (x != nil) {
            y = x;
            if (comparator.compare(z.key, x.key) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y == nil) {
            root = z;
        } else if (comparator.compare(z.key, y.key) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        size++;
        insertFixup(z);
    }

    private void insertFixup(Node z) {
        while (z.parent.red) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right;
                if (y.red) {
                    z.parent.red = false;
                    y.red = false;
                    z.parent.parent.red = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.red = false;
                    z.parent.parent.red = true;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node y = z.parent.parent.left;
                if (y.red) {
                    z.parent.red = false;
                    y.red = false;
                    z.parent.parent.red = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.red = false;
                    z.parent.parent.red = true;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.red = false;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node y) {
        Node x = y.left;
        y.left = x.right;
        if (x.right != nil) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == nil) {
            root = x;
        } else if (y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }
        x.right = y;
        y.parent = x;
    }

    public boolean contains(E e) {
        Node x = root;
        while (x != nil) {
            if (comparator.compare(e, x.key) == 0) {
                return true;
            }
            if (comparator.compare(e, x.key) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    private class Node {
        E key;
        boolean red;
        Node parent = nil;
        Node left = nil;
        Node right = nil;

        Node(E key, boolean red) {
            this.key = key;
            this.red = red;
        }
    }

    protected boolean isValidRedBlackTree() {
        Deque<Node> stack = new ArrayDeque<>();
        Map<Node, Integer> blackDescendantCounter = new HashMap<>();
        if (root == nil) {
            return true;
        } else {
            stack.push(root);
        }

        while (stack.size() > 0) {
            Node node = stack.pop();

            // BST property
            if (node.left != nil && comparator.compare(node.left.key, node.key) > 0) {
                return false;
            }
            if (node.right != nil && comparator.compare(node.right.key, node.key) < 0) {
                return false;
            }

            // Property 2
            if (node == root && node.red) {
                System.out.println("Tree invalid: root node is red.");
                return false;
            }
            // Property 3
            if (node == nil && node.red) {
                System.out.println("Tree invalid: leaf node is red.");
                return false;
            }
            // Property 4
            if (node.red && (node.left.red || node.right.red)) {
                System.out.println("Tree invalid: red node has one or two black children.");
                return false;
            }

            // Property 5
            // Check this property when node has a leaf child. Traverse up the lineage to the root and on the way, store
            // the number of black nodes below each node. If a node already has a count attached to it, verify that
            // this count is equal to the present count.
            if ((node.left == nil) || (node.right == nil)) {
                int blackCount = node.red ? 1 : 2;

                // Verify black count for leaf node
                blackDescendantCounter.putIfAbsent(node, blackCount);
                if (!blackDescendantCounter.get(node).equals(blackCount)) {
                    System.out.println("Tree invalid: descendant paths have different number of black nodes.");
                    return false;
                }

                if (node != root) {
                    // Verify black count for all ancestors to the leaf node
                    for (Node ancestor = node.parent; ancestor != nil; ancestor = ancestor.parent) {
                        if (!ancestor.red) {
                            blackCount++;
                        }

                        blackDescendantCounter.putIfAbsent(ancestor, blackCount);
                        if (!blackDescendantCounter.get(ancestor).equals(blackCount)) {
                            System.out.println("Tree invalid: descendant paths have different number of black nodes.");
                            return false;
                        }
                    }
                }
            }

            if (node.right != nil) {
                stack.push(node.right);
            }
            if (node.left != nil) {
                stack.push(node.left);
            }
        }
        return true;
    }
}
