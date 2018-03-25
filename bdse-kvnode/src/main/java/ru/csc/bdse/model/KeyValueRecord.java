package ru.csc.bdse.model;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class KeyValueRecord {
    @PrimaryKey
    private String key;
    private boolean isDeleted;
    private long timestamp;
    private byte[] data;

    private KeyValueRecord() {
        this.timestamp = System.currentTimeMillis();
    }

    public KeyValueRecord(String key, byte[] data) {
        this.key = key;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
