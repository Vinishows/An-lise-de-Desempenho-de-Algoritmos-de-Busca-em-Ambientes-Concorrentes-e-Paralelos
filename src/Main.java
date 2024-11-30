import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Arquivos de entrada
        File file1 = new File("resources/DonQuixote-388208.txt");
        File file2 = new File("resources/Dracula-165307.txt");
        File file3 = new File("resources/MobyDick-217452.txt");

        String targetWord = "word";

        // Resultados
        List<String[]> results = new ArrayList<>();

        // Processar cada arquivo
        for (File file : Arrays.asList(file1, file2, file3)) {
            // SerialCPU
            long startTime = System.currentTimeMillis();
            int countSerial = SerialCPU.countWordOccurrences(file, targetWord);
            long endTime = System.currentTimeMillis();
            results.add(new String[]{"SerialCPU", file.getName(), String.valueOf(countSerial), String.valueOf(endTime - startTime)});

            // ParallelCPU
            startTime = System.currentTimeMillis();
            int countParallelCPU = ParallelCPU.countWordOccurrences(file, targetWord, 4); // 4 threads
            endTime = System.currentTimeMillis();
            results.add(new String[]{"ParallelCPU", file.getName(), String.valueOf(countParallelCPU), String.valueOf(endTime - startTime)});

            // ParallelGPU
            startTime = System.currentTimeMillis();
            int countParallelGPU = ParallelGPU.countWordOccurrences(file.getPath(), targetWord);
            endTime = System.currentTimeMillis();
            results.add(new String[]{"ParallelGPU", file.getName(), String.valueOf(countParallelGPU), String.valueOf(endTime - startTime)});
        }

        // Gerar CSV diretamente
        String outputPath = "results.csv";
        writeResultsToCSV(outputPath, results);
        System.out.println("Resultados salvos em " + outputPath);
    }

    /**
     * Método para escrever os resultados no arquivo CSV.
     *
     * @param filePath Caminho do arquivo CSV de saída.
     * @param data     Lista de resultados a serem salvos.
     */
    public static void writeResultsToCSV(String filePath, List<String[]> data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Cabeçalho
            writer.println("Amostra,Metodo,Ocorrencias,Tempo(ms)");

            // Dados
            for (String[] result : data) {
                writer.println(String.join(",", result));
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }
    }
}
