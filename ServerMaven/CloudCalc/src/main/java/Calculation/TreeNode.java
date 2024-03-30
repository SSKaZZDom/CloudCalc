package Calculation;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    String value;
    int depth;
    List<TreeNode> children;
    TreeNode parent;
    boolean flag;

    public TreeNode(String value, TreeNode parent, boolean flag, int depth) {
        this.depth = depth;
        this.flag = flag;
        this.parent = parent;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public String getValue() {
        return this.value;
    }

    public List<TreeNode> getList() {
        return this.children;
    }

    public void addChild(TreeNode node) {
        this.children.add(node);
    }

    public TreeNode getParent() {
        return this.parent;
    }
    public void setFlag(boolean value) {
        this.flag = value;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public int getDepth() {
        return this.depth;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void nullChildren() {
        this.children = null;
    }
}
