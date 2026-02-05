package capoelmate.wordle.repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;

import jakarta.annotation.PostConstruct;


@Repository
public class WordsRepository {
    
    private HashSet<String> words;

    @PostConstruct
    public void init() {
        //en el init se lee el csv solo una vez y se guarda en una lista de strings, para no tener que leer el csv cada vez que se llama a un metodo
        words = new HashSet<String>();
        
        //leer el csv
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader("src/main/resources/static/words.csv"));) 
        {            
            System.out.println("leyendo csv...");
            Map<String, String> values;
            while ((values = reader.readMap()) != null) {
                for (String word : values.values()) {
                    // System.out.println("palabra: " + word);
                    words.add(word);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //tambien en el init se inicializa la base de datos sqlite
        inicializarBdd();

    }

    public boolean palabraExiste(String word) {
        //devuelve true si la palabra existe, false si no
        return words.contains(word);
        
    }

    public String getWord(int index) {
        //devuelve la palabra en la posicion index
        return (String) words.toArray()[index];
    }

    public int getSize() {
        return words.size();
    }


    public void inicializarBdd(){
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:src/main/resources/static/palabras_usadas.sqlite");
            java.sql.Statement stmt = conn.createStatement()) {
            
            String sql = "CREATE TABLE IF NOT EXISTS palabras_usadas (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "word TEXT NOT NULL," +
                         "date TEXT NOT NULL" +
                         ");";
            stmt.execute(sql);

            System.out.println("Base de datos inicializada" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escribirPalabraUsada(String word, LocalDate dates){
        //escribe las palabra usada en un archivo .sqlite

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:src/main/resources/static/palabras_usadas.sqlite");
            java.sql.PreparedStatement pstmt = conn.prepareStatement("INSERT INTO palabras_usadas(word, date) VALUES(?, ?)")) {
            
            pstmt.setString(1, word);
            pstmt.setString(2, dates.toString());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public Map<String,LocalDate> getPalabrasUsadas(){
        //devuelve la lista de palabras usadas. La clave es la palabra, el valor es la fecha en que se uso.

        Map<String,LocalDate> palabrasUsadas = new HashMap<>();

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:src/main/resources/static/palabras_usadas.sqlite");
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM palabras_usadas")) {
            
            while (rs.next()) {
               String word = rs.getString("word");
               LocalDate date = LocalDate.parse(rs.getString("date"));
               palabrasUsadas.put(word, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return palabrasUsadas;
    }

    
    public String getPalabraDelDia() {
        //devuelve la palabra del dia, que es la ultima palabra escrita en la base de datos
        String palabraDelDia = "";
        LocalDate today = LocalDate.now();

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:src/main/resources/static/palabras_usadas.sqlite");
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM palabras_usadas ORDER BY id DESC LIMIT 1")) {
            
            if (rs.next()) {
               palabraDelDia = rs.getString("word");
               LocalDate date = LocalDate.parse(rs.getString("date"));
               
               //si la fecha de la palabra del dia no es hoy, se elige una nueva palabra
               if (!date.equals(today)) {
                   palabraDelDia = "";
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return palabraDelDia;
    }

    public void resetearPalabrasUsadasSiNecesario(int limite) {
        //si el numero de palabras usadas es mayor que el limite, se borran todas las palabras usadas

        int numeroPalabrasUsadas = 0;

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:src/main/resources/static/palabras_usadas.sqlite");
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM palabras_usadas")) {
            
            if (rs.next()) {
               numeroPalabrasUsadas = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (numeroPalabrasUsadas >= limite) {
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:src/main/resources/static/palabras_usadas.sqlite");
                java.sql.Statement stmt = conn.createStatement()) {
                
                String sql = "DELETE FROM palabras_usadas;";
                stmt.execute(sql);

                System.out.println("Palabras usadas reseteadas" );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}


