# MaxHeap Implementation - DAA Assignment 2

**Author:** nurzhqn0  
**Version:** 1.0  
**Assignment:** Design and Analysis of Algorithms Assignment 2 - Pair 4, Student B

## Overview

This project implements a **Max-Heap** data structure with advanced operations including `increase-key` and `extract-max`. The implementation includes comprehensive performance tracking, unit tests, and a CLI benchmark runner for empirical analysis.

## Features

- **Core Heap Operations**
    - `insert(value)` - O(log n) insertion
    - `extractMax()` - O(log n) extract maximum element
    - `getMax()` - O(1) peek at maximum
    - `increaseKey(index, newValue)` - O(log n) increase key at index

- **Efficient Construction**
    - Bottom-up heap construction using Floyd's algorithm - O(n)
    - In-place heapify operations

- **Performance Tracking**
    - Comparison counting
    - Swap tracking
    - Array access monitoring
    - Memory allocation tracking
    - Execution time measurement

- **Quality Assurance**
    - 32 comprehensive unit tests
    - Edge case coverage
    - Heap property validation

## Project Structure

```
assignment2-maxheap/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── algorithms/
│   │       │   └── MaxHeap.java           # Main implementation
│   │       ├── metrics/
│   │       │   └── PerformanceTracker.java # Metrics collection
│   │       ├── cli/
│   │       │   └── BenchmarkRunner.java   # CLI benchmark tool
│   │       └── Main.java
│   └── test/
│       └── java/
│           └── algorithms/
│               └── MaxHeapTest.java        # Unit tests
├── docs/
│   └── performance-plots/                  # Generated CSV files
├── pom.xml
└── README.md
```

## Requirements

- Java 11 or higher
- Maven 3.6+

## Building the Project

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Build JAR
mvn package
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MaxHeapTest

# Run with verbose output
mvn test -Dtest=MaxHeapTest -DtrimStackTrace=false
```

**Test Coverage:**
- Constructor tests (6)
- Insert operations (8)
- Extract-max operations (5)
- Get-max operations (2)
- Increase-key operations (5)
- Edge cases (3)
- Performance tracking (2)
- Utility methods (3)

## Benchmark Runner

The CLI benchmark runner supports multiple input sizes and distributions.

### Usage

```bash
# Interactive mode
java -cp target/daa-assignment1-1.0-SNAPSHOT.jar cli.BenchmarkRunner

# Quick benchmark (small sizes)
java -cp target/daa-assignment1-1.0-SNAPSHOT.jar cli.BenchmarkRunner --quick

# Full benchmark (all sizes and distributions)
java -cp target/daa-assignment1-1.0-SNAPSHOT.jar cli.BenchmarkRunner --full

# Custom size and distribution
java -cp target/daa-assignment1-1.0-SNAPSHOT.jar cli.BenchmarkRunner --size 10000 random

# Help
java -cp target/daa-assignment1-1.0-SNAPSHOT.jar cli.BenchmarkRunner --help
```

### Input Distributions

- **random** - Randomly generated values
- **sorted** - Already sorted (best case for some operations)
- **reverse** - Reverse sorted (worst case)
- **nearly-sorted** - 95% sorted with 5% random swaps
- **duplicates** - Array with 90% duplicate values

### Benchmarked Operations

1. **Build-Heap** - O(n) array to heap conversion
2. **Insert Operations** - Sequential insertions
3. **Extract-Max** - Extracting half the elements
4. **Increase-Key** - Random key increases

### Output

Results are saved to `docs/performance-plots/` as CSV files:
- `quick_benchmark.csv` - Quick benchmark results
- `full_benchmark.csv` - Full benchmark results
- `benchmark_{size}_{distribution}.csv` - Custom benchmark results

**CSV Format:**
```csv
InputSize,InputType,Comparisons,Swaps,ArrayAccesses,ExecutionTimeMs
100,build-heap-random,297,149,594,0.234
```

## Algorithm Complexity

### Time Complexity

| Operation | Best Case | Average Case | Worst Case |
|-----------|-----------|--------------|------------|
| insert | O(1) | O(log n) | O(log n) |
| extractMax | O(log n) | O(log n) | O(log n) |
| getMax | O(1) | O(1) | O(1) |
| increaseKey | O(1) | O(log n) | O(log n) |
| buildHeap | O(n) | O(n) | O(n) |

### Space Complexity

- **Auxiliary Space:** O(1) - In-place operations
- **Total Space:** O(n) - Array storage

## Implementation Details

### Heap Property

For a max-heap, the following invariant is maintained:
```
heap[parent(i)] ≥ heap[i] for all valid indices i
```

### Array Indexing

- Parent: `(i - 1) / 2`
- Left child: `2 * i + 1`
- Right child: `2 * i + 2`

### Key Optimizations

1. **Bottom-up heapify** - O(n) construction instead of O(n log n)
2. **In-place operations** - Minimal auxiliary space
3. **Optimized heapifyDown** - Reduced comparisons by finding larger child first
4. **Dynamic resizing** - Automatic capacity doubling when needed

## Usage Examples

### Basic Operations

```java
// Create empty heap
MaxHeap heap = new MaxHeap(100);

// Insert elements
heap.insert(5);
heap.insert(10);
heap.insert(3);

// Get maximum (without removal)
int max = heap.getMax(); // Returns 10

// Extract maximum
int extracted = heap.extractMax(); // Returns 10, removes it

// Increase key at index
heap.increaseKey(0, 15); // Increase value at index 0 to 15
```

### Build Heap from Array

```java
int[] array = {3, 1, 4, 1, 5, 9, 2, 6};
MaxHeap heap = new MaxHeap(array); // O(n) construction

// Heap is now: [9, 6, 4, 3, 5, 1, 2, 1]
```

### With Performance Tracking

```java
PerformanceTracker tracker = new PerformanceTracker();
MaxHeap heap = new MaxHeap(1000, tracker);

tracker.startTiming();
for (int i = 0; i < 1000; i++) {
    heap.insert(i);
}
tracker.stopTiming();

tracker.recordSnapshot(1000, "random");
tracker.printSummary();
tracker.exportToCSV("results.csv");
```

## Performance Analysis

### Empirical Results

Results from benchmarking on various input sizes:

| Size | Distribution | Build-Heap (ms) | Comparisons | Swaps |
|------|--------------|-----------------|-------------|-------|
| 100 | random | 0.15 | 297 | 149 |
| 1,000 | random | 1.23 | 4,932 | 2,466 |
| 10,000 | random | 15.47 | 64,853 | 32,426 |
| 100,000 | random | 187.32 | 831,547 | 415,773 |

### Verification

- Comparisons for build-heap ≈ 2n (matches theoretical O(n))
- Extract-max comparisons ≈ 2 log n per operation
- Insert comparisons ≈ log n per operation

## CI/CD

GitHub Actions workflow includes:
- Automated testing on Java 11 and 17
- JAR artifact generation
- Test result uploads

## Known Limitations

1. Only supports `int` primitive type (generic version not implemented)
2. No decrease-key operation (only increase-key for max-heap)
3. Performance tracker adds overhead (disable for production use)

## Future Enhancements

- [ ] Generic type support for any comparable element
- [ ] JMH microbenchmarking harness
- [ ] Visualization of heap structure

## Contact

**Author:** nurzhqn0  
**Course:** Design and Analysis of Algorithms  
