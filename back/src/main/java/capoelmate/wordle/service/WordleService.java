package capoelmate.wordle.service;

import org.springframework.boot.web.server.Cookie;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import capoelmate.wordle.repository.WordsRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;

@Service
public class WordleService {
    
    private WordsRepository wordsRepository;
    private String palabraDelDia;


    public WordleService(WordsRepository wordsRepository) {
        this.wordsRepository = wordsRepository;
        
        palabraDelDia = getPalabraDelDia();
        System.out.println("WordleService creado");
        System.out.println(" ");
        System.out.println("palabra del dia: " + palabraDelDia);
        System.out.println(" ");
    }


    public void updatePalabraDelDia() {
        palabraDelDia = getPalabraDelDia();
    }

    //este metodo se usaria mas para actualizar la palabra del dia cada dia
    private String getPalabraDelDia() {
        String palabraElegida = "";
        String palabraDelDia = "";
        
        //si llegaran a haber mas de 365 palabras ya usadas, habria que resetear las palabras usadas
        wordsRepository.resetearPalabrasUsadasSiNecesario(365);

        //si ya hay una palabra del dia, se devuelve esa palabra
        palabraDelDia=wordsRepository.getPalabraDelDia();

        if(palabraDelDia != null && palabraDelDia != "") {
            return palabraDelDia;
        }

        //si no hay una palabra del dia, se elige una palabra aleatoria
        
        int size = 500;
        //solo quiero las primeras 500 palabras, asi que recorto el size a 500
        if (wordsRepository.getSize() < 500) {
            size = wordsRepository.getSize() ;
        }

        do{
            //obtiene una al azar hasta que no se haya usado
            double nroRandomFloat = Math.random() * size;
            System.out.println("palabra elegida: " + nroRandomFloat);

            int nroRandom = (int)(nroRandomFloat);
            System.out.println("palabra elegida: " + nroRandom);

            palabraElegida = wordsRepository.getWord(nroRandom);
            System.out.println("palabra elegida: " + palabraElegida);
        }while(wordsRepository.getPalabrasUsadas().get(palabraElegida) != null);

        //cuando se elige una palabra, se escribe en la base de datos sqlite, para no volver a elegirla en el futuro
        wordsRepository.escribirPalabraUsada(palabraElegida, LocalDate.now());
        
        return palabraElegida;
    }



    public boolean palabraValida(String word) {
        return wordsRepository.palabraExiste(word);
    }

    public String[] guess(String guess) {
        //retorna un array de strings, indicando con ok, casi, o mal cada letra.
        
        //primero ve cuales esta ok y tacha los que estan ok
        String resultado[] = new String[5];
        boolean letraUsada[] = new boolean[5];

        for(int i=0; i<5; i++) {
            if(guess.charAt(i) == palabraDelDia.charAt(i)) {
                resultado[i] = "ok";
                letraUsada[i] = true;
            }
            else{
                resultado[i] = "mal";
                letraUsada[i] = false;
            }
        }
        //luego ve cuales estan casi, pero esta vez repito el proceso por cada letra del palabraDelDia

        for(int i=0; i<5; i++) {
            if(resultado[i] == "mal") {
                for(int j=0; j<5; j++) {
                    if(!letraUsada[j] && guess.charAt(i) == palabraDelDia.charAt(j)) {
                        resultado[i] = "casi";
                        letraUsada[j] = true;
                        break;
                    }
                }
            }
        }


        return resultado;  
    }

}
