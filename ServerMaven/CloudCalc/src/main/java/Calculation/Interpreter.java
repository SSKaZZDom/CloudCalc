package Calculation;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Interpreter {
    private List<String> funs;
    private final Map<String, Function> functions = new HashMap<>();

    public Interpreter(List<String> funs) {
        /*Варианты функций для double
        functions.put("+", args -> args.stream().mapToDouble(Double::doubleValue).sum());
        functions.put("-", args -> args.stream().reduce((a, b) -> a - b).orElse(0.0));
        functions.put("*", args -> args.stream().reduce(1.0, (a, b) -> a * b));
        functions.put("/", args -> args.size() == 1 ? args.get(0) : args.stream().reduce((a, b) -> a / b).orElse(0.0));
        functions.put("**", args -> args.size() == 1 ? args.get(0) : Math.pow(args.get(0), args.get(1)));*/
        this.funs = new ArrayList<>(funs);
        for (String function : funs) {
            addFunction(function);
        }
        functions.put("+", args -> args.stream().mapToInt(Integer::intValue).sum());
        functions.put("-", args -> args.stream().reduce((a, b) -> a - b).orElse(0));
        functions.put("*", args -> args.stream().reduce(1, (a, b) -> a * b));
        functions.put("/", args -> args.size() == 1 ? args.get(0) : args.stream().reduce((a, b) -> a / b).orElse(0));
        functions.put("**", args -> args.size() == 1 ? args.get(0) : (int) Math.pow(args.get(0), args.get(1)));
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
        TreeNode res = new TreeNode(tree.getValue(), null, true, 1);
        for (TreeNode child : tree.getList()) {
            value = child.getValue();
            if (environment.containsKey(value)) {
                res.addChild(new TreeNode(Integer.toString(environment.get(value)), res, true, res.getDepth() + 1));
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

    public int evaluate(String expression) throws IOException {
        TreeNode tree = stringParser(expression);
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
                results.add(new CompletableFuture<>());
                url = urls.get(i);
                URL finalUrl = url;
                results.get(i) = CompletableFuture.supplyAsync(() -> {
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) finalUrl.openConnection();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        connection.setRequestMethod("GET");
                    } catch (ProtocolException e) {
                        throw new RuntimeException(e);
                    }

                    BufferedReader in;
                    try {
                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        String result = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return result;
                })

            }
        }
        return evaluateTree(tree);
    }

    /***
     * Функция парсящая Строку в дерево вычислений
     * @param str
     * @return
     */
    private TreeNode stringParser (String str) {
        TreeNode res;
        String[] subs = str.split(" ");
        List<String> tokens = new ArrayList<>();
        for (String element : subs) {
            if (element.startsWith("(") && element.length() > 1) {
                tokens.add("(");
                element = element.substring(1);
                tokens.add(element);
            }
            else if (element.endsWith(")") && element.length() > 1) {
                element = element.substring(0, element.length() - 1);
                tokens.add(element);
                tokens.add(")");
            }
            else {
                tokens.add(element);
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
    private int evaluateTree (TreeNode tree) {
        List<Integer> operands = new ArrayList<>();
        for (TreeNode child : tree.getList()) {
            if (functions.containsKey(child.getValue())) {
                operands.add(evaluateTree(child));
            } else {
                operands.add(Integer.parseInt(child.getValue()));
            }
        }
        return functions.get(tree.getValue()).apply(operands);
    }

    private Set<String> search(TreeNode tree) {
        Set<String> res = new HashSet<>();
        List<String> names = new ArrayList<>();
        for (String fun : funs) {
            String[] words = fun.split(" ", 2);
            names.add(words[0]);
        }
        searchRec(tree, res, names);
        return res;
    }

    private void searchRec(TreeNode tree, Set<String> set, List<String> names) {
        if (names.contains(tree.getValue())) {
            set.add(funs.get(names.indexOf(tree.getValue())));
        }
        for (TreeNode child : tree.getList()) {
            searchRec(child, set, names);
        }
    }
}

interface Function {
    int apply(List<Integer> args);
}
