package com.gertoxq.wynnbuild.base.util;

public class BitVectorCursor {

    public BitVector bitVector;
    public int currentIndex;
    public int endIndex;

    public BitVectorCursor(BitVector bitVector, int index, int span) {
        if (span < 0 || index < 0) {
            throw new IllegalArgumentException("Index or span cannot be negative");
        }
        if (index + span > bitVector.length) {
            throw new IllegalArgumentException("Span exceeds the length of the BitVector");
        }
        this.bitVector = bitVector;
        this.currentIndex = index;
        this.endIndex = index + span;
    }

    public BitVectorCursor(BitVector bitVector, int index) {
        this(bitVector, index, bitVector.length - index);
    }

    public BitVectorCursor(BitVector bitVector) {
        this(bitVector, 0);
    }

    public BitVectorCursor spawn(int span, int index) {
        return new BitVectorCursor(bitVector, index, span);
    }

    public BitVectorCursor spawn(int span) {
        return spawn(span, currentIndex);
    }

    public boolean end() {
        return currentIndex == endIndex;
    }

    public long advance() {
        assert !end() : "Cannot advance beyond the end of the BitVector";
        int index = currentIndex;
        currentIndex++;
        return bitVector.readBit(index);
    }

    public int advanceBy(int amount) {
        if (currentIndex + amount > endIndex) {
            throw new IllegalArgumentException("Cannot advance beyond the end of the BitVector");
        }
        int index = currentIndex;
        currentIndex += amount;
        return (int) bitVector.slice(index, currentIndex);
    }

    public String advanceByChars(int amount) {
        int index = currentIndex;
        currentIndex += amount * 6;
        return bitVector.sliceB64(index, currentIndex);
    }

    public void skip(int amount) {
        assert currentIndex + amount <= endIndex : "Can't skip beyond end";
        currentIndex += amount;
    }

    public BitVector consume() {
        int index = currentIndex;
        currentIndex = endIndex;
        int len = endIndex - index;
        BitVector vec = new BitVector(0, 0);
        while (len > 32) {
            vec.append(bitVector.slice(index, index + 32), 32);
            index += 32;
            len -= 32;
        }
        vec.append(bitVector.slice(index, index + len), len);
        bitVector = null;
        return vec;
    }

}
