import java.util.List;

public class ListElem implements Elem{
    List<Integer> value;
    public ListElem(List<Integer> value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "list";
    }
    @Override

    public List<Integer> getValue() {
        return value;
    }
    @Override

    public void setValue(List<Integer> value) {
        this.value = value;
    }
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("[");
        for (int i = 0; i < value.size() - 1; i++) {
            res.append(value.get(i)).append(",");
        }
        res.append((value.get(value.size() - 1)));
        res.append("]");
        return res.toString();
    }
}
