package cc.unknown.module.impl.visual;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@ModuleInfo(aliases = "Packet Debug", description = "Display packets client/server side", category = Category.VISUALS)
public class PacketDebug extends Module {
	
    ArrayList<Group<Class<?>, String, Long>> list = new ArrayList<>();
    
	@EventLink
	public final Listener<WorldChangeEvent> onWord = event -> {
		list.clear();
	};
	
	@Override
	public void onEnable() {
		list.clear();
	}
    
    @Override
    public void onDisable() {
    	list.clear();
    }

	@EventLink
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isSend()) {
	        Packet<? extends INetHandler> packet = event.getPacket();
	        Optional<Group<Class<?>, String, Long>> optional = list.stream().filter(p -> p.a.equals(packet.getClass())).findFirst();
	        if (optional.isPresent()) {
	            Group<Class<?>, String, Long> group = optional.get();
	            group.c++;
	            group.b = group.a.getSimpleName() + ": " + group.c + " - " + packetToString(packet);
	        } else
	            list.add(new Group<>(packet.getClass(), packet.getClass().getSimpleName() + ": " + 1 + " - " + packetToString(packet), 1L));
		}
	};
	
	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		if (isClickGui()) return;
		
		for (int i = 0; i < list.size(); i++) {
			Group<Class<?>, String, Long> group = list.get(i);
			Fonts.MAIN.get(12, Weight.LIGHT).drawWithShadow(group.b, 10, 20 + i * Fonts.MAIN.get(12, Weight.LIGHT).height(), -1);
		}
	};
	
    private String packetToString(Packet<? extends INetHandler> packet) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        Field[] fields = packet.getClass().getDeclaredFields();
        int validFieldsCount = 0;

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(packet);

                if (field.getType().isPrimitive() || value instanceof String) {
                    if (validFieldsCount > 0) {
                        sb.append(", ");
                    }
                    sb.append(field.getName()).append(": ").append(value);
                    validFieldsCount++;
                }
            } catch (IllegalAccessException e) {
                ChatUtil.display("Error accessing field: " + field.getName() + " - " + e.getMessage());
            }
        }

        sb.append("}");
        return sb.toString();
    }

    static class Group<A, B, C> {
        A a;
        B b;
        C c;

        public Group(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
}