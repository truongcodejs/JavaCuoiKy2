package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileLogger {
    public static void log(String message) {
        try (FileWriter writer = new FileWriter("log.txt", true)) {
            writer.write(LocalDateTime.now() + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}