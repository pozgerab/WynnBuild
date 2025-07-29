package com.gertoxq.wynnbuild.base.util;

import java.util.ArrayList;
import java.util.List;

public class BitVector {

    public Uint32Array bits;
    public int length;
    public int tailIdx;

    public BitVector(String base64) {
        List<Integer> bitVec = new ArrayList<>();
        int acc = 0;
        int bvIdx = 0;
        this.length = base64.length() * 6;

        for (int i = 0; i < base64.length(); i++) {
            int chr = (int) Base64.toInt(String.valueOf(base64.charAt(i)));
            int prePos = bvIdx % 32;
            acc |= (chr << prePos);
            bvIdx += 6;
            int postPos = bvIdx % 32;

            if (postPos < prePos) {
                bitVec.add(acc);
                acc = chr >>> (6 - postPos);
            }

            if (i == base64.length() - 1 && postPos != 0) {
                bitVec.add(acc);
            }
        }

        this.tailIdx = bitVec.isEmpty() ? 1 : bitVec.size();
        this.bits = new Uint32Array(this.tailIdx);

        long[] raw = new long[this.tailIdx];
        for (int i = 0; i < bitVec.size(); i++) {
            raw[i] = bitVec.get(i);
        }
        this.bits.set(raw, 0);
    }

    public BitVector(long data, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("BitVector must have a nonnegative length.");
        }
        List<Long> bitVec = new ArrayList<>();
        bitVec.add(data);

        this.length = length;
        this.tailIdx = bitVec.size();
        this.bits = new Uint32Array(this.tailIdx);
        this.bits.set(bitVec, 0);
    }

    public int readBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot read bit outside the range of the BitVector. (" + idx + " > " + this.length + ")");
        }
        return ((this.bits.get(idx / 32) & (1L << (idx % 32))) == 0 ? 0 : 1);
    }

    public long slice(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("Cannot slice a range where the end is before the start.");
        } else if (end == start) {
            return 0;
        } else if (end - start > 32) {
            throw new IllegalArgumentException("Cannot slice a range of longer than 32 bits (unsafe to store in an integer).");
        }

        long res;
        if ((end - 1) / 32 == start / 32) {
            res = (this.bits.get(start / 32) >>> (start % 32)) & ((1L << (end - start)) - 1);
        } else {
            int startPos = start % 32;
            int intPos = start / 32;
            res = (this.bits.get(intPos) >>> startPos) | (this.bits.get(intPos + 1) << (32 - startPos));
            res &= (1L << (end - start)) - 1;
        }
        return res;
    }

    public String sliceB64(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("Cannot slice a range where the end is before the start.");
        } else if (end > this.length) {
            throw new IllegalArgumentException("Cannot slice past the end of the vector.");
        } else if (end == start) {
            return "";
        }
        StringBuilder b64String = new StringBuilder();
        for (int i = start; i < end; i += 6) {
            b64String.append(Base64.fromIntN(this.slice(i, Math.min(i + 6, end)), 1));
        }
        return b64String.toString();
    }

    public void setBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot set bit outside the range of the BitVector.");
        }
        long val = this.bits.get(idx / 32);
        this.bits.set(idx / 32, val | (1L << (idx % 32)));
    }

    public void clearBit(int idx) {
        if (idx < 0 || idx >= this.length) {
            throw new IndexOutOfBoundsException("Cannot clear bit outside the range of the BitVector.");
        }
        long val = this.bits.get(idx / 32);
        this.bits.set(idx / 32, val & ~(1L << (idx % 32)));
    }

    public String toB64() {
        if (this.length == 0) {
            return "";
        }
        StringBuilder b64String = new StringBuilder();
        for (int i = 0; i < this.length; i += 6) {
            b64String.append(Base64.fromIntN(this.slice(i, Math.min(i + 6, this.length)), 1));
        }
        return b64String.toString();
    }

    @Override
    public String toString() {
        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            retStr.insert(0, (this.readBit(i) == 0 ? "0" : "1"));
        }
        return retStr.toString();
    }

    public String toStringR() {
        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            retStr.append(this.readBit(i) == 0 ? "0" : "1");
        }
        return retStr.toString();
    }

    private void updateTailInt(long v, int vLen) {
        int prePos = this.length % 32;
        int postPos = prePos + vLen;

        long currentVal = this.bits.get(this.tailIdx - 1);
        this.bits.set(this.tailIdx - 1, currentVal | (v << prePos));

        if (postPos >= 32) {
            this.tailIdx += 1;
            long partial = v >>> (32 - prePos);
            long nextVal = (this.tailIdx - 1 < this.bits.length()) ? this.bits.get(this.tailIdx - 1) : 0;
            this.bits.set(this.tailIdx - 1, nextVal | partial);
        }
        this.length += vLen;
    }

    private void checkResize(int length) {
        int resizeLen = this.bits.length();
        int needed = (this.length + length - 1) / 32 + 1;
        if (needed >= resizeLen) {
            resizeLen = Math.max(needed, resizeLen * 2);
            Uint32Array newBits = new Uint32Array(resizeLen);
            newBits.set(this.bits, 0);
            this.bits = newBits;
        }
    }

    public void appendB64(String data) {
        if (data == null || data.isEmpty()) return;
        int length = data.length() * 6;
        checkResize(length);

        for (char c : data.toCharArray()) {
            long v = Base64.toInt(String.valueOf(c));
            updateTailInt(v, 6);
        }
    }

    public void append(long data, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("BitVector length must increase by a nonnegative number.");
        }
        checkResize(length);

        long int_ = data & 0xFFFFFFFFL;

        if (length != 32 && (int_ & ((1L << length) - 1)) != int_) {
            throw new IllegalArgumentException(data + " doesn't fit in " + length + " bits!");
        }

        updateTailInt(int_, length);
    }

    public void merge(List<BitVector> bitVecs) {
        for (BitVector bitVec : bitVecs) {
            int bitVecLen = bitVec.length;
            for (int i = 0; i < bitVec.tailIdx; ++i) {
                if (i == bitVec.tailIdx - 1) {
                    this.append(bitVec.bits.get(i), bitVecLen);
                } else {
                    this.append(bitVec.bits.get(i), 32);
                    bitVecLen -= 32;
                }
            }
        }
    }
}
