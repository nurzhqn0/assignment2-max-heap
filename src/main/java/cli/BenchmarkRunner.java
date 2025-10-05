package cli;

import algorithms.MaxHeap;
import metrics.PerformanceTracker;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class BenchmarkRunner {

    private static final int[] DEFAULT_SIZES = {100, 1000, 10000, 100000};
    private static final String[] DISTRIBUTION_TYPES = {
            "random", "sorted", "reverse", "nearly-sorted", "duplicates"
    };

    private final PerformanceTracker tracker;
    private final Random random;

    public BenchmarkRunner() {
        this.tracker = new PerformanceTracker();
        this.random = new Random(42);
    }

    public static void main(String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();

        if (args.length > 0) {
            runner.runWithArgs(args);
        } else {
            runner.runInteractive();
        }
    }

    private void runWithArgs(String[] args) {
        try {
            if (args[0].equals("--help") || args[0].equals("-h")) {
                printHelp();
                return;
            }

            if (args[0].equals("--quick")) {
                runQuickBenchmark();
                return;
            }

            if (args[0].equals("--full")) {
                runFullBenchmark();
                return;
            }

            if (args[0].equals("--size")) {
                if (args.length < 2) {
                    System.err.println("Error: --size requires an argument");
                    printHelp();
                    return;
                }
                int size = Integer.parseInt(args[1]);
                String distribution = args.length > 2 ? args[2] : "random";
                runSingleBenchmark(size, distribution);
                return;
            }

            System.err.println("Unknown option: " + args[0]);
            printHelp();

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format");
            printHelp();
        }
    }

    private void printHelp() {
        System.out.println("MaxHeap Benchmark Runner");
        System.out.println("\nUsage:");
        System.out.println("  java -cp target/daa-assignment1-1.0-SNAPSHOT.jar cli.BenchmarkRunner [OPTIONS]");
        System.out.println("\nOptions:");
        System.out.println("  --help, -h              Show this help message");
        System.out.println("  --quick                 Run quick benchmark (small sizes)");
        System.out.println("  --full                  Run full benchmark (all sizes and distributions)");
        System.out.println("  --size <N> [TYPE]       Run benchmark for size N with distribution TYPE");
        System.out.println("\nDistribution types:");
        System.out.println("  random, sorted, reverse, nearly-sorted, duplicates");
        System.out.println("\nExamples:");
        System.out.println("  BenchmarkRunner --quick");
        System.out.println("  BenchmarkRunner --size 1000 random");
        System.out.println("  BenchmarkRunner --full");
    }

    private void runInteractive() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== MaxHeap Benchmark Runner ===\n");
        System.out.println("Select benchmark mode:");
        System.out.println("1. Quick benchmark (100, 1000, 10000)");
        System.out.println("2. Full benchmark (all sizes and distributions)");
        System.out.println("3. Custom benchmark");
        System.out.println("4. Exit");
        System.out.print("\nChoice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                runQuickBenchmark();
                break;
            case 2:
                runFullBenchmark();
                break;
            case 3:
                runCustomBenchmark(scanner);
                break;
            case 4:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid choice");
        }

        scanner.close();
    }

    private void runQuickBenchmark() {
        System.out.println("\n=== Quick Benchmark ===\n");
        int[] sizes = {100, 1000, 10000};
        String[] distributions = {"random", "sorted", "reverse"};

        for (int size : sizes) {
            for (String distribution : distributions) {
                benchmarkBuildHeap(size, distribution);
                benchmarkInsertOperations(size, distribution);
                benchmarkExtractMax(size, distribution);
            }
        }

        exportResults("quick_benchmark.csv");
    }

    private void runFullBenchmark() {
        System.out.println("\n=== Full Benchmark ===\n");
        System.out.println("This may take several minutes...\n");

        for (int size : DEFAULT_SIZES) {
            for (String distribution : DISTRIBUTION_TYPES) {
                System.out.printf("Testing size %,d with %s distribution...%n", size, distribution);

                benchmarkBuildHeap(size, distribution);
                benchmarkInsertOperations(size, distribution);
                benchmarkExtractMax(size, distribution);
                benchmarkIncreaseKey(size, distribution);
            }
        }

        exportResults("full_benchmark.csv");
        tracker.printAllSnapshots();
    }

    private void runSingleBenchmark(int size, String distribution) {
        System.out.printf("\n=== Benchmarking size %,d with %s distribution ===\n\n", size, distribution);

        benchmarkBuildHeap(size, distribution);
        benchmarkInsertOperations(size, distribution);
        benchmarkExtractMax(size, distribution);
        benchmarkIncreaseKey(size, distribution);

        tracker.printAllSnapshots();
        exportResults(String.format("benchmark_%d_%s.csv", size, distribution));
    }


    private void runCustomBenchmark(Scanner scanner) {
        System.out.print("\nEnter array size: ");
        int size = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nSelect distribution:");
        for (int i = 0; i < DISTRIBUTION_TYPES.length; i++) {
            System.out.printf("%d. %s%n", i + 1, DISTRIBUTION_TYPES[i]);
        }
        System.out.print("Choice: ");
        int distChoice = scanner.nextInt() - 1;

        if (distChoice < 0 || distChoice >= DISTRIBUTION_TYPES.length) {
            System.out.println("Invalid distribution choice");
            return;
        }

        String distribution = DISTRIBUTION_TYPES[distChoice];
        runSingleBenchmark(size, distribution);
    }

    private void benchmarkBuildHeap(int size, String distribution) {
        int[] array = generateArray(size, distribution);

        tracker.reset();
        tracker.startTiming();
        MaxHeap heap = new MaxHeap(array, tracker);
        tracker.stopTiming();

        tracker.recordSnapshot(size, "build-heap-" + distribution);

        System.out.printf("  Build-heap [%s, n=%,d]: %.3f ms, %,d comparisons%n",
                distribution, size, tracker.getExecutionTimeMillis(), tracker.getComparisons());
    }

    private void benchmarkInsertOperations(int size, String distribution) {
        int[] values = generateArray(size, distribution);
        MaxHeap heap = new MaxHeap(size * 2);

        tracker.reset();
        tracker.startTiming();

        for (int value : values) {
            heap.insert(value);
        }

        tracker.stopTiming();
        tracker.recordSnapshot(size, "insert-" + distribution);

        System.out.printf("  Insert ops [%s, n=%,d]: %.3f ms, %,d comparisons%n",
                distribution, size, tracker.getExecutionTimeMillis(), tracker.getComparisons());
    }

    private void benchmarkExtractMax(int size, String distribution) {
        int[] array = generateArray(size, distribution);
        MaxHeap heap = new MaxHeap(array, tracker);

        tracker.reset();
        tracker.startTiming();

        // Extract half the elements
        int extractCount = size / 2;
        for (int i = 0; i < extractCount; i++) {
            heap.extractMax();
        }

        tracker.stopTiming();
        tracker.recordSnapshot(size, "extract-max-" + distribution);

        System.out.printf("  Extract-max [%s, n=%,d]: %.3f ms, %,d comparisons%n",
                distribution, size, tracker.getExecutionTimeMillis(), tracker.getComparisons());
    }

    private void benchmarkIncreaseKey(int size, String distribution) {
        int[] array = generateArray(size, distribution);
        MaxHeap heap = new MaxHeap(array, tracker);

        tracker.reset();
        tracker.startTiming();

        int operationCount = Math.min(100, size / 10);
        for (int i = 0; i < operationCount; i++) {
            int index = random.nextInt(heap.getSize());
            int newValue = random.nextInt(1000000);
            try {
                heap.increaseKey(index, newValue);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        tracker.stopTiming();
        tracker.recordSnapshot(size, "increase-key-" + distribution);

        System.out.printf("  Increase-key [%s, n=%,d]: %.3f ms, %,d comparisons%n",
                distribution, size, tracker.getExecutionTimeMillis(), tracker.getComparisons());
    }

    private int[] generateArray(int size, String distribution) {
        int[] array = new int[size];

        switch (distribution.toLowerCase()) {
            case "random":
                for (int i = 0; i < size; i++) {
                    array[i] = random.nextInt(size * 10);
                }
                break;

            case "sorted":
                for (int i = 0; i < size; i++) {
                    array[i] = i;
                }
                break;

            case "reverse":
                for (int i = 0; i < size; i++) {
                    array[i] = size - i;
                }
                break;

            case "nearly-sorted":
                for (int i = 0; i < size; i++) {
                    array[i] = i;
                }
                int swaps = size / 20;
                for (int i = 0; i < swaps; i++) {
                    int idx1 = random.nextInt(size);
                    int idx2 = random.nextInt(size);
                    int temp = array[idx1];
                    array[idx1] = array[idx2];
                    array[idx2] = temp;
                }
                break;

            case "duplicates":
                int uniqueCount = Math.max(1, size / 10);
                for (int i = 0; i < size; i++) {
                    array[i] = random.nextInt(uniqueCount);
                }
                break;

            default:
                System.err.println("Unknown distribution: " + distribution);
                for (int i = 0; i < size; i++) {
                    array[i] = random.nextInt(size * 10);
                }
        }

        return array;
    }

    private void exportResults(String filename) {
        try {
            tracker.exportToCSV(filename);
        } catch (IOException e) {
            System.err.println("Error exporting results: " + e.getMessage());
        }
    }
}