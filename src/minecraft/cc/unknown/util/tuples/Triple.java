package cc.unknown.util.tuples;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;
}