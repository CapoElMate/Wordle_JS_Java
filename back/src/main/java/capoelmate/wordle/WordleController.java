package capoelmate.wordle;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordleController {
    
    @GetMapping("/")
    public String home() {
        return "{\"message\":\"Hello World\"}";
    }
}
