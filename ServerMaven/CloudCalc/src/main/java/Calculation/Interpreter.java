package Calculation;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Interpreter {
    private List<String> funs;
    private final Map<String, Function> functions = new HashMap<>();

    public Interpreter(List<String> funs) {
        this.funs = new ArrayList<>(funs);
        for (String function : funs) {
            addFunction(function);
        }
        functions.put("+", args -> {
            if (!args.isEmpty()) {
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
            } else if (!args.isEmpty()) {
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
            if (!args.isEmpty()) {
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
            } else if (!args.isEmpty()) {
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
            } else if (!args.isEmpty()) {
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

    private void addFunction(String str) {
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
     * @param expression
     * @return
     */

    public Elem evaluate(String expression) throws IOException, ArgumentsException {
        TreeNode tree = null;
        tree = stringParser(expression);
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/servers.txt"), StandardCharsets.UTF_8);
        List<URL> urls = new ArrayList<>();
        List<URL> urlsFun = new ArrayList<>();
        for (String line : lines) {
            try {
                URL url = new URL("http://" + line + ":8081/test");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    urls.add(new URL("http://" + line + ":8081"));
                    urlsFun.add(new URL("http://" + line + "8081/function"));
                }
                connection.disconnect();
            } catch (ConnectException e) {
                System.out.println("Ошибка при подключении");
            }
        }

        int size = urls.size();
        if(size > 0) {
            TreeNodeIterator iterator = new TreeNodeIterator(tree);
            int cnt = 1;
            int cntFun;
            int depth = 1;
            while (cnt <= size || iterator.hasNext()) {
                if (iterator.hasNext()) {
                    TreeNode node = iterator.next();
                    if (node.getDepth() - depth >= 2) {
                        break;
                    }
                    List<TreeNode> children = node.getList();
                    cntFun = 0;
                    if (children != null) {
                        for (TreeNode child : children) {
                            if (child.getList() != null) {
                                cntFun++;
                            }
                        }
                        if (cntFun <= size - cnt + 1) {
                            node.setFlag(false);
                            cnt += cntFun - 1;
                            depth = children.get(1).getDepth();

                        }
                    }
                }
            }
            cntFun = 0;
            List<TreeNode> parts = new ArrayList<>();
            iterator = new TreeNodeIterator(tree);
            while (cntFun < cnt) {
                if (iterator.hasNext()) {
                    TreeNode node = iterator.next();
                    if (node.getFlag() && !node.getParent().getFlag()) {
                        cntFun++;
                        parts.add(node);
                    }
                }
            }
            String json;
            URL url;
            Set<String> funcs;
            for (int i = 0; i < cnt; i++) {
                funcs = search(parts.get(i));
                url = urlsFun.get(i);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                for (String fun : funcs) {
                    byte[] postData = fun.getBytes(StandardCharsets.UTF_8);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(postData, 0, postData.length);
                    }
                    connection.disconnect();
                }
                json = new Gson().toJson(parts.get(i));
                url = urls.get(i);
                byte[] postData = json.getBytes(StandardCharsets.UTF_8);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData, 0, postData.length);
                }
                connection.disconnect();
            }
            List<CompletableFuture<String>> results = new ArrayList<>();
            for (int i = 0; i < cnt; i++) {
                URL finalUrl = urls.get(i);
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) finalUrl.openConnection();
                        connection.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String result = in.readLine();
                        in.close();
                        return result;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    return null;
                });
                results.add(future);
            }
            CompletableFuture<Void> allOf = CompletableFuture.allOf(results.toArray(new CompletableFuture[0]));
            try {
                allOf.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            String value;
            for (int i = 0; i < cnt; i++) {
                try {
                    value = results.get(i).get();
                    parts.get(i).setValue(value);
                    parts.get(i).nullChildren();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return evaluateTree(tree);
    }

    /***
     * Функция парсящая Строку в дерево вычислений
     * @param str
     * @return
     */
    private TreeNode stringParser (String str) throws ArgumentsException {
        TreeNode res;
        String[] subs = str.split(" ");
        List<String> tokens = new ArrayList<>();
        boolean flag = false;
        String list = "";
        for (String element : subs) {
            if (element.startsWith("(") && element.length() > 1) {
                while (element.startsWith("(")) {
                    tokens.add("(");
                    element = element.substring(1);
                }
                if(element.startsWith("[") && flag) {
                    throw new ArgumentsException("There should be no nested lists in your expression");
                } else if (element.startsWith("[")) {
                    flag = true;
                    list += element;
                    if (list.endsWith("]")) {
                        flag = false;
                        tokens.add(list);
                        list = "";
                    }
                } else {
                    tokens.add(element);
                }
            }
            else if (element.endsWith(")")) {
                int cnt = 0;
                while (element.endsWith(")")) {
                    element = element.substring(0, element.length() - 1);
                    cnt++;
                }
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
                } else if (!element.isEmpty()){
                    tokens.add(element);
                }
                for (int i = 0; i < cnt; i++) {
                    tokens.add(")");
                }
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
        List<String> sub;
        List<String> ends;
        int mapCnt = -1;
        int size;
        while(tokens.contains("map")) {
            int st = tokens.indexOf("map") - 1;
            int end = 0;
            sub = new ArrayList<>();
            for (int i = st; i < tokens.size(); i++){
                if (tokens.get(i).equals("(")) {
                    mapCnt++;
                } else if (tokens.get(i).equals(")")) {
                    mapCnt--;
                    if (mapCnt == -1) {
                        sub.add(tokens.get(i));
                        end = i;
                        break;
                    }
                }
                sub.add(tokens.get(i));
            }
            size = tokens.size();
            sub = mapParser(sub);
            ends = tokens.subList(end + 1, size);
            tokens = tokens.subList(0, st);
            sub.addAll(ends);
            tokens.addAll(sub);

        }
        System.out.println(tokens);
        res = createTree(tokens);
        return res; 
    }

    private List<String> mapParser(List<String> tokens) {
        List<String> res = new ArrayList<>();
        int mapCnt = 0;
        String fun = tokens.get(2);
        for (int i = 3; i < tokens.size() - 1; i++) {
            if (tokens.get(i).equals("(")){
                mapCnt++;
                res.add(tokens.get(i));
                if (mapCnt == 1) {
                    res.add(fun);
                }
            } else if(tokens.get(i).equals(")")){
                res.add(tokens.get(i));
                mapCnt--;
            } else {
                res.add(tokens.get(i));
            }

        }
        return res;
    }

    /***
     * Функция создающая Дерево из списка токенов
     * @param list
     * @return
     */
    private TreeNode createTree (List<String> list) {
        TreeNode res = new TreeNode(list.get(1), null, true, 1);
        int cnt = 0;
        String element;
        List<String> sub = null;
        for (int i = 2; i < list.size() - 1; i++) {
            element = list.get(i);
            if (cnt > 0) {
                sub.add(element);
                if (element.equals("(")) {
                    cnt++;
                }
                if (element.equals(")")) {
                    cnt--;
                    if (cnt == 0) {
                        res.addChild(createTree(sub));
                    }
                }
            } else {
                if (element.equals("(")) {
                    sub = new ArrayList<>();
                    sub.add(element);
                    cnt++;
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
            System.out.println(tree.getValue());
            System.out.println(operands);
            return functions.get(tree.getValue()).apply(operands);
        } catch (ArgumentsException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> search(TreeNode tree) {
        Set<String> res = new HashSet<>();
        List<String> names = new ArrayList<>();
        List<String> bodies = new ArrayList<>();
        for (String fun : funs) {
            String[] words = fun.split(" ", 2);
            names.add(words[0]);
            bodies.add(words[1]);
        }
        searchRec(tree, res, names);
        return res;
    }

    private void searchRec(TreeNode tree, Set<String> set, List<String> names) {
        if (names.contains(tree.getValue())) {
            if (!set.contains(funs.get(names.indexOf(tree.getValue())))) {
                set.add(funs.get(names.indexOf(tree.getValue())));
                searchFun(funs.get(names.indexOf(tree.getValue())), set, names);
            }
        }
        for (TreeNode child : tree.getList()) {
            searchRec(child, set, names);
        }
    }

    private void searchFun(String fun, Set<String> set, List<String> names) {
        String[] words = fun.split(" ", 2);
        String body = words[1];
        for (String name : names) {
            if (body.contains(name) && !set.contains(funs.get(names.indexOf(name)))) {
                set.add(funs.get(names.indexOf(name)));
                searchFun(funs.get(names.indexOf(name)), set, names);
            }
        }
    }
}

interface Function {
    Elem apply(List<Elem> args) throws ArgumentsException;
}
