package cc.unknown.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdaptiveMap<K, V> extends HashMap<K, V> implements Serializable {

    private final ArrayList<V> arrayList = new ArrayList<>();

    public void put(V type) {
        arrayList.add(type);
    }

    @Override
    public ArrayList<V> values() {
        ArrayList<V> collection = new ArrayList<>(super.values());
        collection.addAll(arrayList);
        return collection;
    }

    // Removes all values from both the hashmap and the list
    public void removeValue(V value) {
        for (Map.Entry<K, V> entry : this.entrySet()) {
            if (entry.getValue().equals(value)) {
                this.remove(entry.getKey());
                break;
            }
        }

        arrayList.remove(value);
    }
}