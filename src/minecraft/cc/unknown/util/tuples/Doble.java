package cc.unknown.util.tuples;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Doble<A, B> {
    private final A first;
    private final B second;
}