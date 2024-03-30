import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {
    TreeNode result ;
    Interpreter interpreter = new Interpreter();
    String str;

    @PostMapping
    public void evaluate(@RequestBody String exp) {
       str = exp;
    }

    @PostMapping("/test")
    public void test(){
        System.out.println("Связь протестирована");
    }
    @PostMapping("/function")
    public void addFunction(@RequestBody String fun) {
        interpreter.addFunction(fun);
    }
    @GetMapping
    public CompletableFuture<String> handleReqDefResult() {
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            Gson gson = new Gson();
            TreeNode tree = gson.fromJson(str, TreeNode.class);
            try {
                return interpreter.evaluate(tree).toString();
            } catch (IOException | ArgumentsException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }
}
