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

    /**
     * Add key to the tree.
     * @param e the key to add
     */
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

    /* Restore red-black properties after adding a node. */
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

    /**
     * Test if the tree contains a key.
     *
     * @param e the key to test for
     * @return true if the key is present, false otherwise
     */
    public boolean contains(E e) {
        Node result = search(e);
        return result == null ? false : true;
    }

    private Node search(E e) {
        Node x = root;
        while (x != nil) {
            if (comparator.compare(e, x.key) == 0) {
                return x;
            }
            if (comparator.compare(e, x.key) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return null;
    }

    /**
     * Remove the smallest key from the tree and return it.
     *
     * @return the smallest key in the tree, or null if tree is empty
     */
    public E extractMin() {
        E minKey = min();
        if (minKey != null) {
            remove(minKey);
        }
        return minKey;
    }

    /**
     * Return the smallest key in the tree, or null if the key does not exist.
     *
     * @return the smallest key in the tree
     */
    public E min() {
        return size == 0 ? null : treeMinimum(root).key;
    }

    /* Return the smallest node from the subtree rooted at node. */
    private Node treeMinimum(Node node) {
        Node x = node;
        while (x.left != nil) {
            x = x.left;
        }
        return x;
    }


    /**
     * Remove key e from the tree.
     *
     * @param e the key to be removed
     * @return true if key was present in the tree, false otherwise
     */
    public boolean remove(E e) {
        Node z = search(e);
        if (z == null) {
            return false;
        }

        Node y = z;
        boolean yOriginalIsRed = y.red;
        Node x;
        if (z.left == nil) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == nil) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = treeMinimum(z.right);
            yOriginalIsRed = y.red;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.red = z.red;
        }
        if (!yOriginalIsRed) {
            deleteFixup(x);
        }

        size--;
        return true;
    }

    /* Restore red-black properties after deleting a node. */
    private void deleteFixup(Node node) {
        Node x = node;
        while (x != root && x.red == false) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.red) {
                    w.red = false;
                    x.parent.red = true;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (!w.left.red && !w.right.red) {
                    w.red = true;
                    x = x.parent;
                } else {
                    if (!w.right.red) {
                        w.left.red = false;
                        w.red = true;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.red = x.parent.red;
                    w.parent.red = false;
                    w.right.red = false;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.red) {
                    w.red = false;
                    x.parent.red = true;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (!w.left.red && !w.right.red) {
                    w.red = true;
                    x = x.parent;
                } else {
                    if (!w.left.red) {
                        w.right.red = false;
                        w.red = true;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.red = x.parent.red;
                    w.parent.red = false;
                    w.left.red = false;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.red = false;
    }

    /* Replace subtree rooted at u with subtree rooted at v. */
    private void transplant(Node u, Node v) {
        if (u.parent == nil) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
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
