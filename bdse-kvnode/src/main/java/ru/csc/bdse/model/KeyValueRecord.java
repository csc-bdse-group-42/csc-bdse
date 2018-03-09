package ru.csc.bdse.model;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class KeyValueRecord {
    @PrimaryKey
    private String key;
    private byte[] data;

    private KeyValueRecord() { }

    public KeyValueRecord(String key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
