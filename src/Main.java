import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Arquivos de entrada
        File file1 = new File("resources/DonQuixote-388208.txt");
        File file2 = new File("resources/Dracula-165307.txt");
        File file3 = new File("resources/MobyDick-217452.txt");

        Scanner scanner = new Scanner(System.in);

        // Menu para escolha do tipo de análise
        System.out.println("Escolha o tipo de análise:");
        System.out.println("1 - Buscar uma palavra específica");
        System.out.println("2 - Contar todas as palavras");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consumir a quebra de linha

        // Variáveis para configuração da busca
        boolean analyzeAllWords = false;
        String targetWord = "";

        if (choice == 1) {
            System.out.print("Digite a palavra a ser buscada: ");
            targetWord = scanner.nextLine().toLowerCase();
        } else if (choice == 2) {
            analyzeAllWords = true;
        } else {
            System.out.println("Escolha inválida. Saindo...");
            return;
        }

        // Resultados
        List<String[]> results = new ArrayList<>();
        Map<String, Map<String, Integer>> allWordsOccurrences = new HashMap<>();

        // Processar cada arquivo
        for (File file : Arrays.asList(file1, file2, file3)) {
            if (analyzeAllWords) {
                // Análise de todas as palavras
                Map<String, Integer> wordCounts = analyzeAllWordsInFile(file);
                allWordsOccurrences.put(file.getName(), wordCounts);
            } else {
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
        }

        if (analyzeAllWords) {
            // Salvar os resultados de todas as palavras
            String outputPath = "all_words_results.csv";
            writeAllWordsResultsToCSV(outputPath, allWordsOccurrences);
            System.out.println("Resultados de todas as palavras salvos em " + outputPath);
        } else {
            // Salvar os resultados da palavra específica
            String outputPath = "results.csv";
            writeResultsToCSV(outputPath, results);
            System.out.println("Resultados salvos em " + outputPath);
        }

        // Exibir gráficos se os arquivos CSV estiverem disponíveis
        if (new File("results.csv").exists()) {
            Map<String, Map<String, Long>> performanceData = GraficoDesempenho.loadDataFromCSV("results.csv");
            exibirGrafico(performanceData, "Desempenho de Algoritmos (Busca de Palavra Específica)");
        }

        if (new File("all_words_results.csv").exists()) {
            System.out.println("Gráfico para 'all_words_results.csv' pode ser gerado manualmente, caso necessário.");
        }
    }

    public static Map<String, Integer> analyzeAllWordsInFile(File file) throws IOException {
        Map<String, Integer> wordCounts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\W+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
        return wordCounts;
    }

    public static void writeResultsToCSV(String filePath, List<String[]> data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Amostra,Metodo,Ocorrencias,Tempo(ms)");
            for (String[] result : data) {
                writer.println(String.join(",", result));
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }
    }

    public static void writeAllWordsResultsToCSV(String filePath, Map<String, Map<String, Integer>> data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Arquivo,Palavra,Ocorrencias");
            for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
                String fileName = entry.getKey();
                for (Map.Entry<String, Integer> wordEntry : entry.getValue().entrySet()) {
                    writer.println(fileName + "," + wordEntry.getKey() + "," + wordEntry.getValue());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }
    }

    public static void exibirGrafico(Map<String, Map<String, Long>> data, String titulo) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new GraficoDesempenho(data, titulo));
        frame.setVisible(true);
    }
}
