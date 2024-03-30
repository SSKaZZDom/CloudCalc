package Server;
import Calculation.ArgumentsException;
import Calculation.Elem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Calculation.Interpreter;

@RestController
public class Controller {
    Map<Integer, String> result = new HashMap<>();
    int id = 1;
    @PostMapping("/evaluate")
    public void evaluateExpression(@RequestBody String exp) throws IOException, ArgumentsException {
        System.out.println(exp);
        String mainExp = null;
        String[] arr = exp.split("\n");
        List<String> funs = new ArrayList<>();
        for (String str : arr) {
            str = str.trim();
            if (str.startsWith("main")) {
                String[] subs = str.split(" ", 2);
                mainExp = subs[1];
            } else {
                funs.add(str);
                //interpreter.addFunction(str);
            }
        }
        Interpreter interpreter = new Interpreter(funs);
        result.put(id, interpreter.evaluate(mainExp).toString());
        id++;
    }

    @GetMapping
    public ResponseEntity<String> getMain() {

        return ResponseEntity.ok("Готов принимать ваши запросы");
    }

    @GetMapping("/results")
    public ResponseEntity<Map<Integer,String>> getResult() {

        return ResponseEntity.ok(result);
    }
}
