package com.gertoxq.quickbuild;

import java.util.ArrayList;
import java.util.List;

import com.gertoxq.quickbuild.Base64;
import org.w3c.dom.ranges.RangeException;

public class BitVector {

    private int[] bits;
    private int length;

    /**
     * Constructs an arbitrary-length bit vector.
     *
     * @param data   The data to append.
     * @param length A set length for the data. Ignored if data is a string.
     *               The structure of the array should be [[last, ..., first], ..., [last, ..., first], [empty space, last, ..., first]]
     */
    public BitVector(Object data, int length) {
        ArrayList<Integer> bitVector = new ArrayList<>();

        if (data instanceof String) {
            int intVal = 0;
            int bvIndex = 0;
            String strData = (String) data;
            length = strData.length() * 6;

            for (int i = 0; i < strData.length(); i++) {
                char character = strData.charAt(i);
                int charValue = Base64.toInt(String.valueOf(character));
                int prePos = bvIndex % 32;
                intVal |= (charValue << bvIndex);
                bvIndex += 6;
                int postPos = bvIndex % 32;
                if (postPos < prePos) { // We have to have filled up the integer
                    bitVector.add(intVal);
                    intVal = (charValue >>> (6 - postPos));
                }

                if (i == strData.length() - 1 && postPos != 0) {
                    bitVector.add(intVal);
                }
            }
        } else if (data instanceof Integer) {
            if (length < 0) {
                throw new IllegalArgumentException("BitVector must have nonnegative length.");
            }

            int intData = (int) data;

            // Range of numbers that won't fit in a uint32
            if (intData < -((int) Math.pow(2, 31))) {
                throw new IllegalArgumentException("Numerical data has to fit within a 32-bit integer range to instantiate a BitVector.");
            }
            bitVector.add(intData);
        } else {
            throw new IllegalArgumentException("BitVector must be instantiated with a Number or a B64 String");
        }

        this.length = length;
        this.bits = new int[bitVector.size()];
        for (int i = 0; i < bitVector.size(); i++) {
            this.bits[i] = bitVector.get(i);
        }
    }

    /**
     * Return value of bit at index idx.
     *
     * @param idx The index to read
     * @return The bit value at position idx
     */
    public int readBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot read bit outside the range of the BitVector. (" + idx + " > " + this.length + ")");
        }
        return ((this.bits[idx / 32] & (1 << idx)) == 0 ? 0 : 1);
    }

    /**
     * Returns an integer value (if possible) made from the range of bits [start, end).
     * Undefined behavior if the range to read is too big.
     *
     * @param start The index to start slicing from. Inclusive.
     * @param end   The index to end slicing at. Exclusive.
     * @return An integer representation of the sliced bits.
     */
    public int slice(int start, int end) {
        // TO NOTE: Java shifting is ALWAYS in mod 32. a << b will do a << (b mod 32) implicitly.

        if (end < start) {
            throw new IllegalArgumentException("Cannot slice a range where the end is before the start.");
        } else if (end == start) {
            return 0;
        } else if (end - start > 32) {
            // Requesting a slice of longer than 32 bits (safe integer "length")
            throw new IllegalArgumentException("Cannot slice a range of longer than 32 bits (unsafe to store in an integer).");
        }

        int res;
        if ((end - 1) / 32 == start / 32) {
            // The range is within 1 uint32 section - do some relatively fast bit twiddling
            res = (this.bits[start / 32] & ~((((~0) << (end - 1)) << 1) | ~((~0) << start))) >>> (start % 32);
        } else {
            // The number of bits in the uint32s
            int start_pos = (start % 32);
            int int_idx = start / 32;
            res = (this.bits[int_idx] & ((~0) << start)) >>> (start_pos);
            res |= (this.bits[int_idx + 1] & ~((~0) << end)) << (32 - start_pos);
        }

        return res;

        // General code - slow
        // for (let i = start; i < end; i++) {
        //     res |= (get_bit(i) << (i - start));
        // }
    }

    /**
     * Assign bit at index idx to 1.
     *
     * @param idx The index to set.
     */
    public void setBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot set bit outside the range of the BitVector.");
        }
        this.bits[idx / 32] |= (1 << idx % 32);
    }

    /**
     * Assign bit at index idx to 0.
     *
     * @param idx The index to clear.
     */
    public void clearBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot clear bit outside the range of the BitVector.");
        }
        this.bits[idx / 32] &= ~(1 << idx % 32);
    }

    /**
     * Creates a string version of the bit vector in B64. Does not keep the order of elements a sensible human-readable format.
     *
     * @return A b64 string representation of the BitVector.
     */
    public String toB64() {
        if (this.length == 0) {
            return "";
        }
        StringBuilder b64Str = new StringBuilder();
        int i = 0;
        while (i < this.length) {
            b64Str.append(Base64.fromIntN(slice(i, i + 6), 1));
            i += 6;
        }

        return b64Str.toString();
    }

    /**
     * Returns a BitVector in bitstring format. Probably only useful for dev debugging.
     *
     * @return A bit string representation of the BitVector. Goes from higher-indexed bits to lower-indexed bits. (n ... 0)
     */
    public String toString() {
        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            retStr.insert(0, (readBit(i) == 0 ? "0" : "1"));
        }
        return retStr.toString();
    }

    /**
     * Returns a BitVector in bitstring format. Probably only useful for dev debugging.
     *
     * @return A bit string representation of the BitVector. Goes from lower-indexed bits to higher-indexed bits. (0 ... n)
     */
    public String toStringR() {
        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            retStr.append(readBit(i) == 0 ? "0" : "1");
        }
        return retStr.toString();
    }

    /**
     * Appends data to the BitVector.
     *
     * @param data   The data to append.
     * @param length The length, in bits, of the new data. This is ignored if data is a string.
     */
    public void append(Object data, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("BitVector length must increase by a nonnegative number.");
        }

        ArrayList<Integer> bitVector = new ArrayList<>();
        for (int uint : this.bits) {
            bitVector.add(uint);
        }
        if (data instanceof String) {
            int intVal = bitVector.get(bitVector.size() - 1);
            int bvIndex = this.length;
            String strData = (String) data;
            length = strData.length() * 6;
            boolean updatedCurr = false;
            for (int i = 0; i < strData.length(); i++) {
                int charValue = Base64.toInt(String.valueOf(strData.charAt(i)));
                int prePos = bvIndex % 32;
                intVal |= (charValue << bvIndex);
                bvIndex += 6;
                int postPos = bvIndex % 32;
                if (postPos < prePos) { // we have to have filled up the integer
                    if (bitVector.size() == this.bits.length && !updatedCurr) {
                        bitVector.set(bitVector.size() - 1, intVal);
                        updatedCurr = true;
                    } else {
                        bitVector.add(intVal);
                    }
                    intVal = (charValue >>> (6 - postPos));
                }

                if (i == strData.length() - 1) {
                    if (bitVector.size() == this.bits.length && !updatedCurr) {
                        bitVector.set(bitVector.size() - 1, intVal);
                    } else if (postPos != 0) {
                        bitVector.add(intVal);
                    }
                }
            }
        } else if (data instanceof Integer) {
            int intData = (int) data;

            // Range of numbers that "could" fit in a uint32 -> [0, 2^32) U [-2^31, 2^31)
            if (intData > Math.pow(2, 32) - 1 || intData < -((int) Math.pow(2, 31))) {
                throw new IllegalArgumentException("Numerical data has to fit within a 32-bit integer range to instantiate a BitVector.");
            }
            // Could be split between multiple new ints
            // Reminder that shifts implicitly mod 32
            bitVector.set(bitVector.size() - 1, bitVector.get(bitVector.size() - 1) | ((intData & ~((~0) << length)) << (this.length)));
            if (((this.length - 1) % 32 + 1) + length > 32) {
                bitVector.add(intData >>> (32 - this.length));
            }
        } else {
            throw new IllegalArgumentException("BitVector must be appended with a Number or a B64 String");
        }

        this.bits = new int[bitVector.size()];
        for (int i = 0; i < bitVector.size(); i++) {
            this.bits[i] = bitVector.get(i);
        }
        this.length += length;
    }
}