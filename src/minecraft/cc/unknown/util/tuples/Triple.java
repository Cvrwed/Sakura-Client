package cc.unknown.util.tuples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;
}