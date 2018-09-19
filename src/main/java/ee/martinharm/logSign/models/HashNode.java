package ee.martinharm.logSign.models;

import ee.martinharm.logSign.converters.ByteConverter;

public class HashNode {
    private byte[] value;
    private int parentId;

    public HashNode(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getHexValue() {
        return ByteConverter.toHex(this.value);
    }

}