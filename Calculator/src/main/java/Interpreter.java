import java.util.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Interpreter {
    private final Map<String, Function> functions = new HashMap<>();

    public Interpreter() {
        /*Варианты функций для double
        functions.put("+", args -> args.stream().mapToDouble(Double::doubleValue).sum());
        functions.put("-", args -> args.stream().reduce((a, b) -> a - b).orElse(0.0));
        functions.put("*", args -> args.stream().reduce(1.0, (a, b) -> a * b));
        functions.put("/", args -> args.size() == 1 ? args.get(0) : args.stream().reduce((a, b) -> a / b).orElse(0.0));
        functions.put("**", args -> args.size() == 1 ? args.get(0) : Math.pow(args.get(0), args.get(1)));*/

        functions.put("+", args -> args.stream().mapToInt(Integer::intValue).sum());
        functions.put("-", args -> args.stream().reduce((a, b) -> a - b).orElse(0));
        functions.put("*", args -> args.stream().reduce(1, (a, b) -> a * b));
        functions.put("/", args -> args.size() == 1 ? args.get(0) : args.stream().reduce((a, b) -> a / b).orElse(0));
        functions.put("**", args -> args.size() == 1 ? args.get(0) : (int) Math.pow(args.get(0), args.get(1)));
    }

    /***
     * Функция добавляющая пользовательские функции в список функций
     * @param name - имя функции
     * @param parameters - список параметров в буковах
     * @param body - выражение определяющее функцию
     */
    private void defineFunction(String name, List<String> parameters, String body) {
        functions.put(name, args -> {
            Map<String, Integer> environment = new HashMap<>();
            for (int i = 0; i < parameters.size(); i++) {
                environment.put(parameters.get(i), args.get(i));
            }
            return evaluateFunction(body, environment);
        });
    }

    /***
     * функция для рассчёта пользовательских функций
     * @param body
     * @param environment
     * @return
     */
    private int evaluateFunction(String body, Map<String, Integer> environment) {
        TreeNode tree = stringParser(body);
        tree = envChange(tree, environment);
        return evaluateTree(tree);
    }

    /***
     * Функция заменяющая все параметры в буковах на аргументы, которые пользователь передал в функции main
     * @param tree
     * @param environment
     * @return
     */
    private TreeNode envChange(TreeNode tree, Map<String, Integer> environment) {
        String value;
        TreeNode res = new TreeNode(tree.getValue(), null, false, 1);
        for (TreeNode child : tree.getList()) {
            value = child.getValue();
            if (environment.containsKey(value)) {
                res.addChild(new TreeNode(Integer.toString(environment.get(value)),res, false, res.getDepth() + 1));
            } else {
                res.addChild(envChange(child, environment));
            }
        }
        return res;
    }

    /***
     * Функция, парсящая выражение пользовательской функции, и добавляющая её в список
     * @param str
     */

    public void addFunction(String str) {
        String[] strs = str.split(" ", 2);
        String[] strs1 = strs[1].split("\\) \\(", 2);
        strs1[1] = "(" + strs1[1];
        String[] argsA = strs1[0].substring(1).split(" ");
        List<String> args = new ArrayList<>(Arrays.asList(argsA));
        defineFunction(strs[0], args, strs1[1]);
    }

    /***
     * Функция вычисляющая выражение представленное в виде дерева вычислений
     * @param tree
     * @return
     */
    public int evaluate (TreeNode tree) {
        List<Integer> operands = new ArrayList<>();
        for (TreeNode child : tree.getList()) {
            if (functions.containsKey(child.getValue())) {
                operands.add(evaluate(child));
            } else {
                operands.add(Integer.parseInt(child.getValue()));
            }
        }
        System.out.println(tree.getValue());
        return functions.get(tree.getValue()).apply(operands);
    }
}

interface Function {
    int apply(List<Integer> args);
}
