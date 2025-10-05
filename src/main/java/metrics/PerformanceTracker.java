package metrics;

import java.io.BufferedWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;
    private long memoryAllocations;
    private long startTime;
    private long endTime;
    private final List<MetricSnapshot> snapshots;

    public static class MetricSnapshot {
        private final int inputSize;
        private final long comparisons;
        private final long swaps;
        private final long arrayAccesses;
        private final long executionTimeNanos;
        private final String inputType;

        public MetricSnapshot(int inputSize, long comparisons, long swaps,
                              long arrayAccesses, long executionTimeNanos, String inputType) {
            this.inputSize = inputSize;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.arrayAccesses = arrayAccesses;
            this.executionTimeNanos = executionTimeNanos;
            this.inputType = inputType;
        }

        public int getInputSize() { return inputSize; }
        public long getComparisons() { return comparisons; }
        public long getSwaps() { return swaps; }
        public long getArrayAccesses() { return arrayAccesses; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public double getExecutionTimeMillis() { return executionTimeNanos / 1_000_000.0; }
        public String getInputType() { return inputType; }
    }

    public PerformanceTracker() {
        this.snapshots = new ArrayList<>();
        reset();
    }

    public void reset() {
        this.comparisons = 0;
        this.swaps = 0;
        this.arrayAccesses = 0;
        this.memoryAllocations = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    public void startTiming() {
        this.startTime = System.nanoTime();
    }

    public void stopTiming() {
        this.endTime = System.nanoTime();
    }

    public void incrementComparisons() {
        this.comparisons++;
    }

    public void incrementComparisons(long count) {
        this.comparisons += count;
    }

    public void incrementSwaps() {
        this.swaps++;
    }

    public void incrementArrayAccesses() {
        this.arrayAccesses++;
    }

    public void incrementArrayAccesses(long count) {
        this.arrayAccesses += count;
    }

    public void incrementMemoryAllocations() {
        this.memoryAllocations++;
    }

    public void recordSnapshot(int inputSize, String inputType) {
        long executionTime = endTime - startTime;
        snapshots.add(new MetricSnapshot(inputSize, comparisons, swaps,
                arrayAccesses, executionTime, inputType));
    }

    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getArrayAccesses() { return arrayAccesses; }
    public long getMemoryAllocations() { return memoryAllocations; }
    public long getExecutionTimeNanos() { return endTime - startTime; }
    public double getExecutionTimeMillis() { return (endTime - startTime) / 1_000_000.0; }
    public List<MetricSnapshot> getSnapshots() { return new ArrayList<>(snapshots); }

    public void exportToCSV(String filename) throws IOException {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename must not be null or blank");
        }
        if (filename.contains("/") || filename.contains("\\") || filename.contains("\0")) {
            throw new IllegalArgumentException("filename must not contain path separators or null characters");
        }

        Path dir = Path.of("docs/performance-plots");
        Files.createDirectories(dir);

        Path target = dir.resolve(filename);
        boolean fileExists = Files.exists(target);

        try (BufferedWriter bw = Files.newBufferedWriter(
                target,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            try (PrintWriter writer = new PrintWriter(bw)) {
                if (!fileExists) {
                    writer.println("InputSize,InputType,Comparisons,Swaps,ArrayAccesses,ExecutionTimeMs");
                }

                for (MetricSnapshot snapshot : snapshots) {
                    String inputType = snapshot.getInputType() == null ? "" : snapshot.getInputType();

                    writer.printf(
                            "%d,%s,%d,%d,%d,%.3f%n",
                            snapshot.getInputSize(),
                            inputType,
                            snapshot.getComparisons(),
                            snapshot.getSwaps(),
                            snapshot.getArrayAccesses(),
                            snapshot.getExecutionTimeMillis()
                    );
                }

                writer.flush();
            }
        }

        System.out.println("✓ Data " + (fileExists ? "appended to" : "written to") + ": " + target);
    }

    public void printSummary() {
        System.out.println("=== Performance Metrics ===");
        System.out.printf("Comparisons: %,d%n", comparisons);
        System.out.printf("Swaps: %,d%n", swaps);
        System.out.printf("Array Accesses: %,d%n", arrayAccesses);
        System.out.printf("Memory Allocations: %,d%n", memoryAllocations);
        System.out.printf("Execution Time: %.3f ms%n", getExecutionTimeMillis());
        System.out.println("===========================");
    }

    public void printAllSnapshots() {
        System.out.println("\n=== All Performance Snapshots ===");
        System.out.printf("%-10s %-15s %-20s %-15s %-20s %-15s%n",
                "Size", "Type", "Comparisons", "Swaps", "Accesses", "Time (ms)");
        System.out.println("-".repeat(100));

        for (MetricSnapshot snapshot : snapshots) {
            System.out.printf("%-10d %-15s %,20d %,15d %,20d %15.3f%n",
                    snapshot.getInputSize(),
                    snapshot.getInputType(),
                    snapshot.getComparisons(),
                    snapshot.getSwaps(),
                    snapshot.getArrayAccesses(),
                    snapshot.getExecutionTimeMillis()
            );
        }
        System.out.println("=================================\n");
    }
}