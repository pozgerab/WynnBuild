package com.gertoxq.wynnbuild.base.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * A Java implementation of JavaScript's Uint32Array, representing an array of 32-bit unsigned integers.
 * This class uses a direct ByteBuffer for memory management, similar to an ArrayBuffer.
 * Values are handled as longs in the public API to accommodate the full unsigned 32-bit range (0 to 2^32 - 1),
 * as Java's int is signed.
 */
public class Uint32Array {

    public static final int BYTES_PER_ELEMENT = 4;
    private final ByteBuffer buffer;
    private final int byteOffset;
    private final int length;

    // Main constructor for creating views
    private Uint32Array(ByteBuffer buffer, int byteOffset, int length) {
        this.buffer = buffer;
        this.byteOffset = byteOffset;
        this.length = length;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN); // As per JS TypedArray spec on most systems
    }

    /**
     * Allocates a new Uint32Array of the specified length, initialized to zero.
     *
     * @param length The number of elements.
     */
    public Uint32Array(int length) {
        this(ByteBuffer.allocate(length * BYTES_PER_ELEMENT), 0, length);
    }

    /**
     * Creates a new Uint32Array from an array of longs.
     * Values are truncated to 32-bit unsigned integers.
     *
     * @param array The source array.
     */
    public Uint32Array(long[] array) {
        this(array.length);
        this.set(array, 0);
    }

    /**
     * Creates a new Uint32Array from a byte array.
     * The byte array's length must be a multiple of 4.
     *
     * @param array The source byte array.
     */
    public Uint32Array(byte[] array) {
        if (array.length % BYTES_PER_ELEMENT != 0) {
            throw new IllegalArgumentException("Byte array length must be a multiple of 4.");
        }
        this.length = array.length / BYTES_PER_ELEMENT;
        this.byteOffset = 0;
        this.buffer = ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Copy constructor. Creates a new Uint32Array with a copy of the elements from the source array.
     *
     * @param array The array to copy.
     */
    public Uint32Array(Uint32Array array) {
        this(array.length);
        this.set(array, 0);
    }

    /**
     * Returns the number of elements in the array.
     *
     * @return The length of the array.
     */
    public int length() {
        return this.length;
    }

    /**
     * Returns the underlying ByteBuffer.
     *
     * @return The ByteBuffer.
     */
    public ByteBuffer buffer() {
        return this.buffer;
    }

    /**
     * Returns the byte offset of this view from the start of its underlying ByteBuffer.
     *
     * @return The byte offset.
     */
    public int byteOffset() {
        return this.byteOffset;
    }

    /**
     * Returns the total byte length of this view.
     *
     * @return The byte length.
     */
    public int byteLength() {
        return this.length * BYTES_PER_ELEMENT;
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index The index of the element to retrieve.
     * @return The unsigned 32-bit integer value as a long.
     */
    public long get(int index) {
        checkIndex(index);
        return Integer.toUnsignedLong(this.buffer.getInt(this.byteOffset + index * BYTES_PER_ELEMENT));
    }

    /**
     * Sets the element at the specified index.
     * The long value is truncated to a 32-bit unsigned integer.
     *
     * @param index The index of the element to set.
     * @param value The value to set.
     */
    public void set(int index, long value) {
        checkIndex(index);
        this.buffer.putInt(this.byteOffset + index * BYTES_PER_ELEMENT, (int) value);
    }

    /**
     * Copies values from a source array into this array.
     *
     * @param source The source array of longs.
     * @param offset The offset into this array at which to start writing.
     */
    public void set(long[] source, int offset) {
        checkOffset(offset, source.length);
        for (int i = 0; i < source.length; i++) {
            this.set(offset + i, source[i]);
        }
    }

    /**
     * Copies values from a source List of longs into this array.
     *
     * @param source The source List of longs.
     * @param offset The offset into this array at which to start writing.
     */
    public void set(List<Long> source, int offset) {
        checkOffset(offset, source.size());
        for (int i = 0; i < source.size(); i++) {
            this.set(offset + i, source.get(i));
        }
    }

    /**
     * Copies values from another Uint32Array into this array.
     *
     * @param source The source Uint32Array.
     * @param offset The offset into this array at which to start writing.
     */
    public void set(Uint32Array source, int offset) {
        checkOffset(offset, source.length());

        // Per JS spec, if source and this are views of the same buffer,
        // the source data is copied to a temporary buffer before setting.
        // We can simulate this by creating a slice (copy) of the source.
        Uint32Array src = source;
        if (this.buffer == src.buffer()) {
            src = src.slice(0, src.length());
        }

        for (int i = 0; i < src.length(); i++) {
            this.set(offset + i, src.get(i));
        }
    }

    /**
     * Creates a new Uint32Array view over the same underlying buffer.
     * This is the equivalent of JavaScript's `subarray`.
     *
     * @param begin The beginning index, inclusive.
     * @param end   The ending index, exclusive.
     * @return A new Uint32Array representing the specified portion of the original array.
     */
    public Uint32Array subarray(int begin, int end) {
        if (begin < 0 || end < 0 || begin > end || end > this.length) {
            throw new IndexOutOfBoundsException("Invalid subarray range: begin=" + begin + ", end=" + end + ", length=" + this.length);
        }
        int newLength = end - begin;
        int newByteOffset = this.byteOffset + begin * BYTES_PER_ELEMENT;
        return new Uint32Array(this.buffer, newByteOffset, newLength);
    }

    /**
     * Creates a new Uint32Array with a shallow copy of a portion of this array.
     * This is the equivalent of JavaScript's `slice`.
     *
     * @param begin The beginning index, inclusive.
     * @param end   The ending index, exclusive.
     * @return A new Uint32Array containing the copied elements.
     */
    public Uint32Array slice(int begin, int end) {
        if (begin < 0 || end < 0 || begin > end || end > this.length) {
            throw new IndexOutOfBoundsException("Invalid slice range: begin=" + begin + ", end=" + end + ", length=" + this.length);
        }
        int newLength = end - begin;
        Uint32Array newArray = new Uint32Array(newLength);

        ByteBuffer newBuf = newArray.buffer();
        ByteBuffer oldBuf = this.buffer.duplicate();

        oldBuf.position(this.byteOffset + begin * BYTES_PER_ELEMENT);
        oldBuf.limit(this.byteOffset + end * BYTES_PER_ELEMENT);

        newBuf.put(oldBuf);
        newBuf.flip();

        return newArray;
    }

    /**
     * Fills all the elements of the array from a start index to an end index with a static value.
     *
     * @param value The value to fill the array with.
     * @param start The start index, inclusive.
     * @param end   The end index, exclusive.
     */
    public void fill(long value, int start, int end) {
        if (start < 0 || end < 0 || start > end || end > this.length) {
            throw new IndexOutOfBoundsException("Invalid fill range: start=" + start + ", end=" + end + ", length=" + this.length);
        }
        int intValue = (int) value;
        for (int i = start; i < end; i++) {
            this.buffer.putInt(this.byteOffset + i * BYTES_PER_ELEMENT, intValue);
        }
    }

    /**
     * Fills the entire array with a static value.
     *
     * @param value The value to fill with.
     */
    public void fill(long value) {
        fill(value, 0, this.length);
    }

    /**
     * Sorts the elements of the array in place.
     * The elements are sorted in ascending order as unsigned 32-bit integers.
     */
    public void sort() {
        long[] temp = new long[this.length];
        for (int i = 0; i < this.length; i++) {
            temp[i] = this.get(i);
        }
        Arrays.sort(temp);
        this.set(temp, 0);
    }

    /**
     * Reverses the order of the elements in the array in place.
     */
    public void reverse() {
        for (int i = 0; i < this.length / 2; i++) {
            int j = this.length - 1 - i;
            long temp = get(i);
            set(i, get(j));
            set(j, temp);
        }
    }

    @Override
    public String toString() {
        return LongStream.range(0, length)
                .mapToObj(i -> String.valueOf(get((int) i)))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + this.length);
        }
    }

    private void checkOffset(int offset, int sourceLength) {
        if (offset < 0 || offset > this.length) {
            throw new IndexOutOfBoundsException("Offset " + offset + " is out of bounds for length " + this.length);
        }
        if (offset + sourceLength > this.length) {
            throw new IllegalArgumentException("Source array is too large to fit at offset " + offset);
        }
    }
}
