package cc.unknown.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EvictingList<T> extends LinkedList<T> implements Serializable {
    private int maxSize;

    public EvictingList(final Collection<? extends T> c, final int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(final T t) {
        if (size() >= getMaxSize()) removeFirst();
        return super.add(t);
    }

    public boolean isFull() {
        return size() >= getMaxSize();
    }

    public EvictingList<T> reverse() {
        EvictingList<T> list = new EvictingList<>(maxSize);
        for (int i = size() - 1; i >= 0; i--) {
            list.add(get(i));
        }
        return list;
    }
}