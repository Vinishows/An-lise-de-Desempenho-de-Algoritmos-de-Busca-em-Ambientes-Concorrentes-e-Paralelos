import java.io.*;
import java.util.concurrent.*;

public class ParallelCPU {
    public static int countWordOccurrences(File file, String targetWord, int numThreads) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int totalOccurrences = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            BlockingQueue<Future<Integer>> futures = new LinkedBlockingQueue<>();

            while ((line = reader.readLine()) != null) {
                String currentLine = line.toLowerCase();
                Future<Integer> future = executor.submit(() -> {
                    int count = 0;
                    String[] words = currentLine.split("\\W+");
                    for (String word : words) {
                        if (word.equals(targetWord.toLowerCase())) {
                            count++;
                        }
                    }
                    return count;
                });
                futures.add(future);
            }

            for (Future<Integer> future : futures) {
                totalOccurrences += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return totalOccurrences;
    }
}
