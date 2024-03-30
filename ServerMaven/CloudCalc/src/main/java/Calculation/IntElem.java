package Calculation;

import java.util.ArrayList;
import java.util.List;

public class IntElem implements Elem {
    int value;

    public IntElem(int value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "int";
    }

    @Override
    public List<Integer> getValue() {
        List<Integer> res = new ArrayList<>();
        res.add(value);
        return res;
    }

    @Override
    public void setValue(List<Integer> value) {
        this.value = value.get(0);
    }
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
