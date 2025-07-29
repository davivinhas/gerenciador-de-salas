package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String FILE_PATH = "logActions.txt";

    public static void log(String message) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) { //cria o arquivo .txt
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); //horario atual

            writer.write("[" + timestamp + "] " + message + "\n");
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    public static void logIfSuccess(boolean success, String mensagem) {
        if (success) {
            log(mensagem);
        }
    }
}
