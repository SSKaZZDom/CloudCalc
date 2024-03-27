package Calculation;

import java.util.*;

public class TreeNodeIterator implements Iterator<TreeNode> {
    private Queue<TreeNode> queue;

    public TreeNodeIterator(TreeNode root) {
        queue = new LinkedList<>();
        if (root != null) {
            queue.add(root);
        }
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public TreeNode next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more elements in the tree.");
        }
        TreeNode current = queue.poll();
        assert current != null;
        if (current.getList() != null) {
            queue.addAll(current.getList());
        }
        return current;
    }
}