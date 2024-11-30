import java.io.*;

public class SerialCPU {
    public static int countWordOccurrences(File file, String targetWord) throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Padronizar comparação
                String[] words = line.toLowerCase().split("\\W+");
                for (String word : words) {
                    if (word.equals(targetWord.toLowerCase())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
