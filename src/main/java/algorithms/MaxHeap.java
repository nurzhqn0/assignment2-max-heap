package algorithms;

import metrics.PerformanceTracker;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class MaxHeap {
    private int[] heap;
    private int size;
    private int capacity;
    private PerformanceTracker tracker;

    public MaxHeap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
        this.tracker = null;
    }

    public MaxHeap(int capacity, PerformanceTracker tracker) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
        this.tracker = tracker;
    }

    public MaxHeap(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        this.capacity = Math.max(array.length, 1); // Ensure at least capacity of 1
        this.size = array.length;
        this.heap = Arrays.copyOf(array, capacity);
        this.tracker = null;

        if (size > 0) {
            buildMaxHeap();
        }
    }

    public MaxHeap(int[] array, PerformanceTracker tracker) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        this.capacity = Math.max(array.length, 1);
        this.size = array.length;
        this.heap = Arrays.copyOf(array, capacity);
        this.tracker = tracker;

        if (size > 0) {
            buildMaxHeap();
        }
    }

    public void buildMaxHeap(){
        for(int i = (size / 2) - 1; i >= 0; i--){
           heapifyDown(i);
        }
    }

    private int getParent(int index){
        return (index - 1) / 2;
    }

    private int getLeftChild(int index){
        return 2 * index + 1;
    }

    private int getRightChild(int index){
        return 2 * index + 2;
    }

    private void swap(int i, int j){
        if(tracker != null){
            tracker.incrementSwaps();
            tracker.incrementArrayAccesses();
        }
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void heapifyUp(int index){
        while(index > 0){
            int parentIndex = getParent(index);

            if(tracker != null){
                tracker.incrementArrayAccesses();
                tracker.incrementComparisons();
            }

            if(heap[index] > heap[parentIndex]){
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int i) {
        while (true) {
            int largest = i;
            int left = getLeftChild(i);
            int right = getRightChild(i);

            if (left < size) {
                if (tracker != null) {
                    tracker.incrementComparisons();
                    tracker.incrementArrayAccesses(2);
                }
                if (heap[left] > heap[largest]) {
                    largest = left;
                }
            }

            if (right < size) {
                if (tracker != null) {
                    tracker.incrementComparisons();
                    tracker.incrementArrayAccesses(2);
                }
                if (heap[right] > heap[largest]) {
                    largest = right;
                }
            }

            if (largest != i) {
                swap(i, largest);
                i = largest;
            } else {
                break;
            }
        }
    }

    public void insert(int value) {
        if (size == capacity) {
            resize();
        }

        if (tracker != null) {
            tracker.incrementArrayAccesses();
        }

        heap[size] = value;
        heapifyUp(size);
        size++;
    }


    public int extractMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        if (tracker != null) {
            tracker.incrementArrayAccesses(2);
        }

        int max = heap[0];
        heap[0] = heap[size - 1];
        size--;

        if (size > 0) {
            heapifyDown(0);
        }

        return max;
    }

    public int getMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        if (tracker != null) {
            tracker.incrementArrayAccesses();
        }

        return heap[0];
    }

    public void increaseKey(int index, int newValue) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        if (tracker != null) {
            tracker.incrementComparisons();
            tracker.incrementArrayAccesses();
        }

        if (newValue < heap[index]) {
            throw new IllegalArgumentException("New value must be greater than current value");
        }

        if (tracker != null) {
            tracker.incrementArrayAccesses();
        }

        heap[index] = newValue;
        heapifyUp(index);
    }

    private void resize() {
        capacity *= 2;
        if (tracker != null) {
            tracker.incrementMemoryAllocations();
        }
        heap = Arrays.copyOf(heap, capacity);
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isValidMaxHeap() {
        return isValidMaxHeapRecursive(0);
    }

    public int[] toArray() {
        return Arrays.copyOf(heap, size);
    }

    /**
     * Recursive helper for heap validation.
     */
    private boolean isValidMaxHeapRecursive(int i) {
        if (i >= size) {
            return true;
        }

        int left = getLeftChild(i);
        int right = getRightChild(i);

        if (left < size && heap[i] < heap[left]) {
            return false;
        }

        if (right < size && heap[i] < heap[right]) {
            return false;
        }

        return isValidMaxHeapRecursive(left) && isValidMaxHeapRecursive(right);
    }
}
