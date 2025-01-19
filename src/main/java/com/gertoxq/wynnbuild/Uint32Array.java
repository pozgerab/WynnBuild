package com.gertoxq.wynnbuild;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Uint32Array {
    private final int BYTES_PER_ELEMENT = 4;
    private final int[] array;
    private final ByteBuffer buffer;

    public Uint32Array(int length) {
        array = new int[length];
        buffer = ByteBuffer.allocate(length * BYTES_PER_ELEMENT);
        buffer.order(ByteOrder.nativeOrder());
    }

    public Uint32Array(int[] array) {
        this.array = array;
        buffer = ByteBuffer.allocate(array.length * BYTES_PER_ELEMENT);
        buffer.order(ByteOrder.nativeOrder());
        for (int value : array) {
            buffer.putInt(value);
        }
        buffer.rewind();
    }

    public Uint32Array(Uint32Array array) {
        this.array = Arrays.copyOf(array.array, array.array.length);
        this.buffer = ByteBuffer.wrap(array.buffer.array());
        this.buffer.order(ByteOrder.nativeOrder());
    }

    public int[] getArray() {
        return array;
    }

    public int get(int index) {
        return array[index];
    }

    public void set(int index, int value) {
        array[index] = value;
        buffer.putInt(index * BYTES_PER_ELEMENT, value);
    }

    public int length() {
        return array.length;
    }

    public Uint32Array subarray(int begin, int end) {
        int[] subArray = Arrays.copyOfRange(array, begin, end);
        return new Uint32Array(subArray);
    }

    public byte[] toByteArray() {
        return buffer.array();
    }
}

