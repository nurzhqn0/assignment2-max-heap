package algorithms;

import static org.junit.jupiter.api.Assertions.*;
import metrics.PerformanceTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.NoSuchElementException;

/**
 * Unit tests for MaxHeap implementation.
 * Tests correctness, edge cases, and heap property maintenance.
 *
 * @author nurzhqn0
 * @version 1.0
 */
class MaxHeapTest {

    private MaxHeap heap;
    private PerformanceTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new PerformanceTracker();
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Constructor creates empty heap with valid capacity")
    void testConstructorWithCapacity() {
        heap = new MaxHeap(10);
        assertEquals(0, heap.getSize());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Constructor throws exception for invalid capacity")
    void testConstructorWithInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new MaxHeap(0));
        assertThrows(IllegalArgumentException.class, () -> new MaxHeap(-5));
    }

    @Test
    @DisplayName("Constructor with null array throws exception")
    void testConstructorWithNullArray() {
        assertThrows(IllegalArgumentException.class, () -> new MaxHeap(null));
    }

    @Test
    @DisplayName("Constructor builds valid heap from array")
    void testConstructorWithArray() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6};
        heap = new MaxHeap(array);

        assertEquals(array.length, heap.getSize());
        assertTrue(heap.isValidMaxHeap());
        assertEquals(9, heap.getMax());
    }

    @Test
    @DisplayName("Constructor handles single element array")
    void testConstructorWithSingleElement() {
        int[] array = {42};
        heap = new MaxHeap(array);

        assertEquals(1, heap.getSize());
        assertEquals(42, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Constructor handles empty array")
    void testConstructorWithEmptyArray() {
        int[] array = {};
        heap = new MaxHeap(array);

        assertEquals(0, heap.getSize());
        assertTrue(heap.isEmpty());
    }

    // ========== Insert Tests ==========

    @Test
    @DisplayName("Insert single element")
    void testInsertSingleElement() {
        heap = new MaxHeap(10);
        heap.insert(5);

        assertEquals(1, heap.getSize());
        assertEquals(5, heap.getMax());
        assertFalse(heap.isEmpty());
    }

    @Test
    @DisplayName("Insert multiple elements maintains heap property")
    void testInsertMultipleElements() {
        heap = new MaxHeap(10);
        int[] values = {3, 1, 4, 1, 5, 9, 2, 6};

        for (int val : values) {
            heap.insert(val);
        }

        assertEquals(values.length, heap.getSize());
        assertEquals(9, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Insert in ascending order")
    void testInsertAscending() {
        heap = new MaxHeap(10);
        for (int i = 1; i <= 10; i++) {
            heap.insert(i);
            assertEquals(i, heap.getMax());
            assertTrue(heap.isValidMaxHeap());
        }
    }

    @Test
    @DisplayName("Insert in descending order")
    void testInsertDescending() {
        heap = new MaxHeap(10);
        for (int i = 10; i >= 1; i--) {
            heap.insert(i);
            assertEquals(10, heap.getMax());
            assertTrue(heap.isValidMaxHeap());
        }
    }

    @Test
    @DisplayName("Insert with automatic resize")
    void testInsertWithResize() {
        heap = new MaxHeap(2, tracker);

        for (int i = 1; i <= 10; i++) {
            heap.insert(i);
        }

        assertEquals(10, heap.getSize());
        assertEquals(10, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
        assertTrue(tracker.getMemoryAllocations() > 0);
    }

    @Test
    @DisplayName("Insert duplicate values")
    void testInsertDuplicates() {
        heap = new MaxHeap(10);
        heap.insert(5);
        heap.insert(5);
        heap.insert(5);

        assertEquals(3, heap.getSize());
        assertEquals(5, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Insert negative values")
    void testInsertNegativeValues() {
        heap = new MaxHeap(10);
        heap.insert(-5);
        heap.insert(-1);
        heap.insert(-10);

        assertEquals(3, heap.getSize());
        assertEquals(-1, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    // ========== Extract-Max Tests ==========

    @Test
    @DisplayName("Extract-max from empty heap throws exception")
    void testExtractMaxFromEmpty() {
        heap = new MaxHeap(10);
        assertThrows(NoSuchElementException.class, () -> heap.extractMax());
    }

    @Test
    @DisplayName("Extract-max single element")
    void testExtractMaxSingleElement() {
        heap = new MaxHeap(10);
        heap.insert(42);

        assertEquals(42, heap.extractMax());
        assertEquals(0, heap.getSize());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Extract-max returns maximum and maintains heap property")
    void testExtractMaxMultiple() {
        heap = new MaxHeap(10);
        int[] values = {3, 1, 4, 1, 5, 9, 2, 6};
        for (int val : values) {
            heap.insert(val);
        }

        assertEquals(9, heap.extractMax());
        assertEquals(6, heap.extractMax());
        assertEquals(5, heap.extractMax());

        assertEquals(5, heap.getSize());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Extract-max all elements returns sorted order")
    void testExtractMaxAllElements() {
        heap = new MaxHeap(10);
        int[] values = {3, 1, 4, 1, 5, 9, 2, 6};
        for (int val : values) {
            heap.insert(val);
        }

        int[] expected = {9, 6, 5, 4, 3, 2, 1, 1};
        for (int expectedVal : expected) {
            assertEquals(expectedVal, heap.extractMax());
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Extract-max with duplicates")
    void testExtractMaxWithDuplicates() {
        heap = new MaxHeap(10);
        heap.insert(5);
        heap.insert(5);
        heap.insert(5);

        assertEquals(5, heap.extractMax());
        assertEquals(5, heap.extractMax());
        assertEquals(5, heap.extractMax());
        assertTrue(heap.isEmpty());
    }

    // ========== Get-Max Tests ==========

    @Test
    @DisplayName("Get-max from empty heap throws exception")
    void testGetMaxFromEmpty() {
        heap = new MaxHeap(10);
        assertThrows(NoSuchElementException.class, () -> heap.getMax());
    }

    @Test
    @DisplayName("Get-max does not remove element")
    void testGetMaxDoesNotRemove() {
        heap = new MaxHeap(10);
        heap.insert(5);
        heap.insert(10);
        heap.insert(3);

        assertEquals(10, heap.getMax());
        assertEquals(3, heap.getSize());
        assertEquals(10, heap.getMax());
    }

    // ========== Increase-Key Tests ==========

    @Test
    @DisplayName("Increase-key with invalid index throws exception")
    void testIncreaseKeyInvalidIndex() {
        heap = new MaxHeap(10);
        heap.insert(5);

        assertThrows(IndexOutOfBoundsException.class, () -> heap.increaseKey(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> heap.increaseKey(5, 10));
    }

    @Test
    @DisplayName("Increase-key with smaller value throws exception")
    void testIncreaseKeyWithSmallerValue() {
        heap = new MaxHeap(10);
        heap.insert(10);

        assertThrows(IllegalArgumentException.class, () -> heap.increaseKey(0, 5));
    }

    @Test
    @DisplayName("Increase-key maintains heap property")
    void testIncreaseKeyMaintainsHeap() {
        int[] array = {10, 8, 9, 4, 7, 6, 5, 1, 2, 3};
        heap = new MaxHeap(array);

        assertTrue(heap.isValidMaxHeap());

        // Increase key at index 9 (value 3) to 15
        heap.increaseKey(9, 15);

        assertEquals(15, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Increase-key to same value")
    void testIncreaseKeyToSameValue() {
        heap = new MaxHeap(10);
        heap.insert(5);

        heap.increaseKey(0, 5);
        assertEquals(5, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Increase-key multiple times")
    void testIncreaseKeyMultipleTimes() {
        int[] array = {10, 8, 9, 4, 7};
        heap = new MaxHeap(array);

        heap.increaseKey(3, 12);  // 4 -> 12
        assertEquals(12, heap.getMax());

        heap.increaseKey(4, 15);  // 7 -> 15
        assertEquals(15, heap.getMax());

        assertTrue(heap.isValidMaxHeap());
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Large heap maintains property")
    void testLargeHeap() {
        heap = new MaxHeap(1000);

        for (int i = 0; i < 1000; i++) {
            heap.insert(i);
        }

        assertEquals(1000, heap.getSize());
        assertEquals(999, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Heap with all same values")
    void testAllSameValues() {
        heap = new MaxHeap(10);
        for (int i = 0; i < 10; i++) {
            heap.insert(5);
        }

        assertEquals(10, heap.getSize());
        assertEquals(5, heap.getMax());
        assertTrue(heap.isValidMaxHeap());
    }

    @Test
    @DisplayName("Mixed insert and extract operations")
    void testMixedOperations() {
        heap = new MaxHeap(10);

        heap.insert(5);
        heap.insert(10);
        assertEquals(10, heap.extractMax());

        heap.insert(7);
        heap.insert(3);
        assertEquals(7, heap.getMax());

        heap.insert(15);
        assertEquals(15, heap.extractMax());

        assertTrue(heap.isValidMaxHeap());
    }

    // ========== Performance Tracking Tests ==========

    @Test
    @DisplayName("Performance tracking records metrics")
    void testPerformanceTracking() {
        heap = new MaxHeap(10, tracker);

        tracker.startTiming();
        for (int i = 1; i <= 5; i++) {
            heap.insert(i);
        }
        tracker.stopTiming();

        assertTrue(tracker.getComparisons() > 0);
        assertTrue(tracker.getSwaps() >= 0);
        assertTrue(tracker.getArrayAccesses() > 0);
    }

    @Test
    @DisplayName("Extract-max tracks operations")
    void testExtractMaxTracking() {
        int[] array = {10, 8, 9, 4, 7, 6, 5};
        heap = new MaxHeap(array, tracker);

        tracker.reset();
        tracker.startTiming();
        heap.extractMax();
        tracker.stopTiming();

        assertTrue(tracker.getComparisons() > 0);
        assertTrue(tracker.getSwaps() > 0);
    }

    // ========== Array Conversion Tests ==========

    @Test
    @DisplayName("toArray returns correct array")
    void testToArray() {
        heap = new MaxHeap(10);
        heap.insert(5);
        heap.insert(10);
        heap.insert(3);

        int[] array = heap.toArray();
        assertEquals(3, array.length);
        assertEquals(10, array[0]); // Max should be at root
    }

    @Test
    @DisplayName("toArray returns copy not reference")
    void testToArrayReturnsCopy() {
        heap = new MaxHeap(10);
        heap.insert(5);

        int[] array1 = heap.toArray();
        int[] array2 = heap.toArray();

        assertNotSame(array1, array2);
    }
}