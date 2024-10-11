package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class RenderTextEvent implements Event {
    @Getter @Setter private String string;
    private Type type;

	public enum Type {
    	Pre, Post;
    }
}