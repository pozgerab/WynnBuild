package com.gertoxq.quickbuild;

import java.util.ArrayList;

public class BitVector {

    public Uint32Array bits;
    public int length;

    public BitVector(Object data, int length) {
        ArrayList<Integer> bitVector = new ArrayList<>();
        int readLength = length;
        if (data instanceof String strData) {
            int intVal = 0;
            int bvIdx = 0;
            readLength = strData.length() * 6;

            for (int i = 0; i < strData.length(); i++) {
                int charVal = Base64.toInt(String.valueOf(strData.charAt(i)));
                int prePos = bvIdx % 32;
                intVal |= (charVal << bvIdx);
                bvIdx += 6;
                int postPos = bvIdx % 32;

                if (postPos < prePos) {
                    bitVector.add(intVal);
                    intVal = charVal >>> (6 - postPos);
                }

                if (i == strData.length() - 1 && postPos != 0) {
                    bitVector.add(intVal);
                }
            }
        } else if (data instanceof Integer) {
            if (readLength < 0) {
                throw new IllegalArgumentException("BitVector must have nonnegative length.");
            }

            int intData = (int) data;

            if (intData < -((int) Math.pow(2, 31))) {
                throw new IllegalArgumentException("Numerical data has to fit within a 32-bit integer range to instantiate a BitVector.");
            }
            bitVector.add(intData);
        } else {
            throw new IllegalArgumentException("BitVector must be instantiated with a Number or a B64 String");
        }

        this.length = readLength;
        this.bits = new Uint32Array(bitVector.stream().mapToInt(i -> i).toArray());
    }

    public int readBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            //throw new IndexOutOfBoundsException("Cannot read bit outside the range of the BitVector. (" + idx + " > " + this.length + ")");
            System.out.println("Cannot read bit outside the range of the BitVector. (" + idx + " > " + this.length + ")");
            return 0;
        }
        return ((this.bits.get(idx / 32) & (1 << idx)) == 0 ? 0 : 1);
    }

    public int slice(int start, int end) {

        if (end < start) {
            throw new IllegalArgumentException("Cannot slice a range where the end is before the start.");
        } else if (end == start) {
            return 0;
        } else if (end - start > 32) {
            throw new IllegalArgumentException("Cannot slice a range of longer than 32 bits (unsafe to store in an integer).");
        }

        int res;
        if ((end - 1) / 32 == start / 32) {
            res = (this.bits.get(start / 32) & ~((((~0) << (end - 1)) << 1) | ~((~0) << start))) >>> (start % 32);
        } else {
            int start_pos = (start % 32);
            int int_idx = start / 32;
            res = (this.bits.get(int_idx) & ((~0) << start)) >>> (start_pos);
            res |= (this.bits.get(int_idx + 1) & ~((~0) << end)) << (32 - start_pos);
        }

        return res;
    }

    public void setBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot set bit outside the range of the BitVector.");
        }
        int n = this.bits.get(idx / 32);
        n |= (1 << idx % 32);
        this.bits.set(idx / 32, n);
    }

    public void clearBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot clear bit outside the range of the BitVector.");
        }
        int n = bits.get(idx / 32);
        n &= ~(1 << idx % 32);
        this.bits.set(idx / 32, n);
    }

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

    public String toString() {
        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            retStr.insert(0, (readBit(i) == 0 ? "0" : "1"));
        }
        return retStr.toString();
    }

    public String toStringR() {
        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            retStr.append(readBit(i) == 0 ? "0" : "1");
        }
        return retStr.toString();
    }

    public void append(Object data, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("BitVector length must increase by a nonnegative number.");
        }

        ArrayList<Integer> bitVector = new ArrayList<>();
        for (int uint : this.bits.getArray()) {
            bitVector.add(uint);
        }
        if (data instanceof String strData) {
            int intVal = bitVector.get(bitVector.size() - 1);
            int bvIndex = this.length;
            length = strData.length() * 6;
            boolean updatedCurr = false;
            for (int i = 0; i < strData.length(); i++) {
                int charValue = Base64.toInt(String.valueOf(strData.charAt(i)));
                int prePos = bvIndex % 32;
                intVal |= (charValue << bvIndex);
                bvIndex += 6;
                int postPos = bvIndex % 32;
                if (postPos < prePos) {
                    if (bitVector.size() == this.bits.length() && !updatedCurr) {
                        bitVector.set(bitVector.size() - 1, intVal);
                        updatedCurr = true;
                    } else {
                        bitVector.add(intVal);
                    }
                    intVal = (charValue >>> (6 - postPos));
                }

                if (i == strData.length() - 1) {
                    if (bitVector.size() == this.bits.length() && !updatedCurr) {
                        bitVector.set(bitVector.size() - 1, intVal);
                    } else if (postPos != 0) {
                        bitVector.add(intVal);
                    }
                }
            }
        } else if (data instanceof Integer) {
            int intData = (int) data;

            if (intData < -((int) Math.pow(2, 31))) {
                throw new IllegalArgumentException("Numerical data has to fit within a 32-bit integer range to instantiate a BitVector.");
            }
            bitVector.set(bitVector.size() - 1, bitVector.get(bitVector.size() - 1) | ((intData & ~((~0) << length)) << (this.length)));
            if (((this.length - 1) % 32 + 1) + length > 32) {
                bitVector.add(intData >>> (32 - this.length));
            }
        } else {
            throw new IllegalArgumentException("BitVector must be appended with a Number or a B64 String");
        }

        this.bits = new Uint32Array(bitVector.stream().mapToInt(i -> i).toArray());
        this.length += length;
    }
}