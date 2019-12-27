package org.h2.index;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TempIndexStore {

    private static TempIndexStore tempIndexStore;
    private String indexName;
    private HashMap<Integer, KeyStore> keys;

    private TempIndexStore(String indexName) {
        this.indexName = indexName;
        this.keys = new HashMap<>();
    }

    public static TempIndexStore getStore(String indexName) {
        if (tempIndexStore == null || !tempIndexStore.indexName.equals(indexName)) {
            tempIndexStore = new TempIndexStore(indexName);
        }
        return tempIndexStore;
    }

    public void updateKeys(int pos, int key) {
        System.out.printf("%s | %d | %d\n", indexName, pos, key);
        if (this.keys.containsKey(pos)) {
            KeyStore s = this.keys.get(pos);
            s.updateKey(key);
            this.keys.remove(pos);
            this.keys.put(pos, s);
        }else {
            KeyStore s = new KeyStore();
            s.updateKey(key);
            this.keys.put(pos, s);
        }
    }

    public int getLastKey(int pos) {
        if (this.keys.containsKey(pos)) {
            return this.keys.get(pos).getKey();
        }
        return -1;
    }

    public void printKeys(){
        this.keys.values().forEach(KeyStore::printKeys);
    }

}

class FrequentKey {
    private int key;
    private int frequency;

    public FrequentKey(int key) {
        this.key = key;
        this.frequency = 1;
    }

    public int getKey() {
        return key;
    }

    public int getFrequency() {
        return frequency;
    }

    public void increaseFrequency() {
        this.frequency++;
    }

    @Override
    public String toString() {
        return "FrequentKey{" +
                "key=" + key +
                ", frequency=" + frequency +
                '}';
    }
}

class KeyStore {
    private List<FrequentKey> keys;

    public KeyStore() {
        this.keys = new ArrayList<>();
    }

    public int getKey() {
        if (this.keys.size() == 0) {
            return -1;
        }
        return keys.get(0).getKey();
    }

    public void rearrange() {
        this.keys = this.keys
                .stream()
                .sorted(Comparator.comparingInt(FrequentKey::getFrequency).reversed())
                .collect(Collectors.toList());
    }

    public void printKeys(){
        this.keys.forEach(System.out::println);
    }

    public void updateKey(int key) {
        for (FrequentKey k : keys) {
            if (k.getKey() == key) {
                k.increaseFrequency();
                rearrange();
                return;
            }
        }
        keys.add(new FrequentKey(key));
        if (keys.size() == 10) {
            keys.remove(8);
        }
    }
}

