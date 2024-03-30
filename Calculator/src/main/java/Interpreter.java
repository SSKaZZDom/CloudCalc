import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {
    private final Map<String, Function> functions = new HashMap<>();

    public Interpreter() {
        functions.put("+", args -> {
            if (args.get(0) != null) {
                List<Integer> sumL = new ArrayList<>();
                int sum = 0;
                int size = 0;
                for (Elem arg : args) {
                    List<Integer> argValue = arg.getValue();
                    if (arg.getType().equals("int")) {
                        sum += argValue.get(0);
                    } else {
                        size = sumL.size();
                        if (size > argValue.size()) {
                            for (int i = 0; i < argValue.size(); i++){
                                sumL.set(i, sumL.get(i) + argValue.get(i));
                            }
                        } else {
                            for (int i = 0; i < size; i++){
                                sumL.set(i, sumL.get(i) + argValue.get(i));
                            }
                            for (int i = size; i <argValue.size(); i ++) {
                                sumL.add(argValue.get(i));
                            }
                        }
                    }
                }
                if (sumL.isEmpty()) {
                    return new IntElem(sum);
                } else {
                    int finalSum = sum;
                    sumL = sumL.stream()
                            .map(elem -> elem + finalSum)
                            .collect(Collectors.toList());
                    return new ListElem(sumL);
                }
            }
            throw new ArgumentsException("There are no arguments");
        });
        functions.put("-", args -> {
            if (args.size() > 2) {
                throw new ArgumentsException("Too many arguments");
            } else if (args.get(0) != null) {
                if (args.get(0).getType().equals("int") && args.get(1).getType().equals("int")) {
                    return new IntElem(args.get(0).getValue().get(0) - args.get(1).getValue().get(0));
                }
                if (args.get(0).getType().equals("list") && args.get(1).getType().equals("int")) {
                    List<Integer> res = args.get(0).getValue();
                    int arg = args.get(1).getValue().get(0);
                    res = res.stream()
                            .map(elem -> elem - arg)
                            .collect(Collectors.toList());
                    return new ListElem(res);
                }
                if (args.get(0).getType().equals("int") && args.get(1).getType().equals("list")) {
                    List<Integer> res = args.get(1).getValue();
                    int arg = args.get(0).getValue().get(0);
                    res = res.stream()
                            .map(elem -> elem - arg)
                            .collect(Collectors.toList());
                    return new ListElem(res);
                } else {
                    throw new ArgumentsException("Two lists cannot be put in this function");
                }
            }
            throw new ArgumentsException("There are no arguments");
        });
        functions.put("*", args -> {
            if (args.get(0) != null) {
                List<Integer> multL = new ArrayList<>();
                int mult = 0;
                int size;
                for (Elem arg : args) {
                    List<Integer> argValue = arg.getValue();
                    if (arg.getType().equals("int")) {
                        mult *= argValue.get(0);
                    } else {
                        size = multL.size();
                        if (size > argValue.size()) {
                            for (int i = 0; i < argValue.size(); i++){
                                multL.set(i, multL.get(i) * argValue.get(i));
                            }
                        } else {
                            for (int i = 0; i < size; i++){
                                multL.set(i, multL.get(i) * argValue.get(i));
                            }
                            for (int i = size; i <argValue.size(); i ++) {
                                multL.add(argValue.get(i));
                            }
                        }
                    }
                }
                if (multL.isEmpty()) {
                    return new IntElem(mult);
                } else {
                    int finalmult = mult;
                    multL = multL.stream()
                            .map(elem -> elem * finalmult)
                            .collect(Collectors.toList());
                    return new ListElem(multL);
                }
            }
            throw new ArgumentsException("There are no arguments");
        });
        functions.put("/", args -> {
            if (args.size() > 2) {
                throw new ArgumentsException("Too many arguments");
            } else if (args.get(0) != null) {
                if (args.get(0).getType().equals("int") && args.get(1).getType().equals("int")) {
                    return new IntElem(args.get(0).getValue().get(0) / args.get(1).getValue().get(0));
                }
                if (args.get(0).getType().equals("list") && args.get(1).getType().equals("int")) {
                    List<Integer> res = args.get(0).getValue();
                    int arg = args.get(1).getValue().get(0);
                    res = res.stream()
                            .map(elem -> elem / arg)
                            .collect(Collectors.toList());
                    return new ListElem(res);
                }
                if (args.get(0).getType().equals("int") && args.get(1).getType().equals("list")) {
                    List<Integer> res = args.get(1).getValue();
                    int arg = args.get(0).getValue().get(0);
                    res = res.stream()
                            .map(elem -> elem / arg)
                            .collect(Collectors.toList());
                    return new ListElem(res);
                } else {
                    throw new ArgumentsException("Two lists cannot be put in this function");
                }
            }
            throw new ArgumentsException("There are no arguments");
        });
        functions.put("**", args -> {
            if (args.size() > 2) {
                throw new ArgumentsException("Too many arguments");
            } else if (args.get(0) != null) {
                System.out.println(args.get(0).getType());
                System.out.println(args.get(1).getType());
                if (args.get(0).getType().equals("int") && args.get(1).getType().equals("int")) {
                    return new IntElem((int) Math.pow(args.get(0).getValue().get(0), args.get(1).getValue().get(0)));
                }else if (args.get(0).getType().equals("list") && args.get(1).getType().equals("int")) {
                    List<Integer> res = args.get(0).getValue();
                    int deg = args.get(1).getValue().get(0);
                    res = res.stream()
                            .map(elem -> (int) Math.pow(elem, deg))
                            .collect(Collectors.toList());
                    return new ListElem(res);
                } else if (args.get(0).getType().equals("int") && args.get(1).getType().equals("list")) {
                    List<Integer> res = args.get(1).getValue();
                    int deg = args.get(0).getValue().get(0);
                    res = res.stream()
                            .map(elem -> (int) Math.pow(elem, deg))
                            .collect(Collectors.toList());
                    return new ListElem(res);
                } else {
                    throw new ArgumentsException("Two lists cannot be put in this function");
                }
            }
            throw new ArgumentsException("There are no arguments");
        });
    }

    /***
     * Функция, парсящая выражение пользовательской функции, и добавляющая её в список
     * @param str
     */

    void addFunction(String str) {
        String[] strs = str.split(" ", 2);
        String[] strs1 = strs[1].split("\\) \\(", 2);
        strs1[1] = "(" + strs1[1];
        String[] argsA = strs1[0].substring(1).split(" ");
        List<String> args = new ArrayList<>(Arrays.asList(argsA));
        defineFunction(strs[0], args, strs1[1]);
    }

    /***
     * Функция добавляющая пользовательские функции в список функций
     * @param name - имя функции
     * @param parameters - список параметров в буковах
     * @param body - выражение определяющее функцию
     */
    private void defineFunction(String name, List<String> parameters, String body) {
        functions.put(name, args -> {
            if (!args.isEmpty()) {
                Map<String, Elem> environment = new HashMap<>();
                for (int i = 0; i < parameters.size(); i++) {
                    environment.put(parameters.get(i), args.get(i));
                }
                return evaluateFunction(body, environment);
            }
            throw new ArgumentsException("There are no arguments");
        });
    }

    /***
     * функция для рассчёта пользовательских функций
     * @param body
     * @param environment
     * @return
     */
    private Elem evaluateFunction(String body, Map<String, Elem> environment) throws ArgumentsException {
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
    private TreeNode envChange(TreeNode tree, Map<String, Elem> environment) {
        String value;
        TreeNode res = new TreeNode(tree.getValue(), null, true, 1);
        for (TreeNode child : tree.getList()) {
            value = child.getValue();
            if (environment.containsKey(value)) {
                res.addChild(new TreeNode(environment.get(value).getValue().toString(), res, true, res.getDepth() + 1));
            } else {
                res.addChild(envChange(child, environment));
            }
        }
        return res;
    }

    /***
     * Функция запускающая процесс вычисления функции main
     * @return
     */

    public Elem evaluate(TreeNode tree) throws IOException, ArgumentsException {
        List<Elem> operands = new ArrayList<>();
        for (TreeNode child : tree.getList()) {
            if (functions.containsKey(child.getValue())) {
                operands.add(evaluateTree(child));
            } else {
                if(child.getValue().contains("[")) {
                    String[] elems = child.getValue().split(",");
                    int size = elems.length;
                    List<Integer> res = new ArrayList<>();
                    res.add(Integer.parseInt(elems[0].substring(1)));
                    for (int i = 1; i < size - 1; i ++) {
                        res.add(Integer.parseInt(elems[i]));
                    }
                    res.add(Integer.parseInt(String.valueOf(elems[size-1].substring(0, elems[size-1].length() - 1))));
                    operands.add(new ListElem(res));
                } else {
                    operands.add(new IntElem(Integer.parseInt(child.getValue())));
                }
            }
        }
        try {
            return functions.get(tree.getValue()).apply(operands);
        } catch (ArgumentsException e) {
            throw new RuntimeException(e);
        }
    }

    private TreeNode stringParser (String str) throws ArgumentsException {
        TreeNode res;
        String[] subs = str.split(" ");
        List<String> tokens = new ArrayList<>();
        boolean flag = false;
        String list = "";
        for (String element : subs) {
            if (element.startsWith("(") && element.length() > 1) {
                tokens.add("(");
                element = element.substring(1);
                if (!functions.containsKey(element)) {
                    throw new ArgumentsException("Arguments order is wrong");
                }
                if(element.startsWith("[") && flag) {
                    throw new ArgumentsException("There should be no nested lists in your expression");
                } else if (element.startsWith("[")) {
                    flag = true;
                    list += element;
                } else {
                    tokens.add(element);
                }
            }
            else if (element.endsWith(")") && element.length() > 1) {
                element = element.substring(0, element.length() - 1);
                if(element.startsWith("[") && flag) {
                    throw new ArgumentsException("There should be no nested lists in your expression");
                } else if (element.startsWith("[")) {
                    flag = true;
                }
                if(flag) {
                    list += element;
                    if (element.endsWith("]")) {
                        flag = false;
                        tokens.add(list);
                        list = "";
                    }
                } else {
                    tokens.add(element);
                }
                tokens.add(")");
            }
            else {
                if(element.startsWith("[") && flag) {
                    throw new ArgumentsException("There should be no nested lists in your expression");
                } else if (element.startsWith("[")) {
                    flag = true;
                }
                if(flag) {
                    list += element;
                    if (element.endsWith("]")) {
                        flag = false;
                        tokens.add(list);
                        list = "";
                    }
                } else {
                    tokens.add(element);
                }
            }
        }
        res = createTree(tokens);
        return res;
    }


    /***
     * Функция создающая Дерево из списка токенов
     * @param list
     * @return
     */
    private TreeNode createTree (List<String> list) {
        TreeNode res = new TreeNode(list.get(1), null, true, 1);
        boolean flag = false;
        String element;
        List<String> sub = null;
        for (int i = 2; i < list.size() - 1; i++) {
            element = list.get(i);
            if (flag) {
                sub.add(element);
                if (element.equals(")")) {
                    flag = false;
                    res.addChild(createTree(sub));
                }
            } else {
                if (element.equals("(")) {
                    sub = new ArrayList<>();
                    sub.add(element);
                    flag = true;
                } else {
                    res.addChild(new TreeNode(element, res, true, res.getDepth() + 1));
                }
            }
        }
        return res;
    }


    /***
     * Функция вычисляющая выражение представленное в виде дерева вычислений
     * @param tree
     * @return
     */
    private Elem evaluateTree (TreeNode tree) {
        List<Elem> operands = new ArrayList<>();
        for (TreeNode child : tree.getList()) {
            if (functions.containsKey(child.getValue())) {
                operands.add(evaluateTree(child));
            } else {
                if(child.getValue().contains("[")) {
                    String[] elems = child.getValue().split(",");
                    int size = elems.length;
                    List<Integer> res = new ArrayList<>();
                    res.add(Integer.parseInt(elems[0].substring(1)));
                    for (int i = 1; i < size - 1; i ++) {
                        res.add(Integer.parseInt(elems[i]));
                    }
                    res.add(Integer.parseInt(String.valueOf(elems[size-1].substring(0, elems[size-1].length() - 1))));
                    operands.add(new ListElem(res));
                } else {
                    operands.add(new IntElem(Integer.parseInt(child.getValue())));
                }
            }
        }
        try {
            return functions.get(tree.getValue()).apply(operands);
        } catch (ArgumentsException e) {
            throw new RuntimeException(e);
        }
    }
}

interface Function {
    Elem apply(List<Elem> args) throws ArgumentsException;
}
