package capoelmate.wordle.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import capoelmate.wordle.service.WordleService;

import java.time.LocalDate;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WordleController {
    
    private WordleService wordleService;

    public WordleController(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @GetMapping("/")
    public String home() {
        return "<h1>Welcome to Wordle!</h1>";
    }


    @GetMapping("/guess")
    public String guess(@RequestParam String guess) {

        guess = guess.toLowerCase();

        String[] word = wordleService.guess(guess);
        
        
        return "{\"result\": [\"" + String.join("\", \"", word) + "\"]}";
        
    }


    @GetMapping("/esPalabraValida")
    public boolean esPalabraValida(@RequestParam String palabra) {

        palabra = palabra.toLowerCase();

        return wordleService.palabraValida(palabra);
        
    }

}
