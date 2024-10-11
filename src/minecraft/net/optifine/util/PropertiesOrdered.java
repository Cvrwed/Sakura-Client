package net.optifine.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class PropertiesOrdered extends Properties {
    private final Set<Object> keysOrdered = new LinkedHashSet();

    public synchronized Object put(final Object key, final Object value) {
        this.keysOrdered.add(key);
        return super.put(key, value);
    }

    public Set<Object> keySet() {
        final Set<Object> set = super.keySet();
        this.keysOrdered.retainAll(set);
        return Collections.unmodifiableSet(this.keysOrdered);
    }

    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(this.keySet());
    }
}
