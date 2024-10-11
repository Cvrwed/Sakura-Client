package cc.unknown.font;

import java.util.HashMap;
import java.util.function.Supplier;

import cc.unknown.util.font.Font;
import cc.unknown.util.font.impl.rise.FontRenderer;
import cc.unknown.util.font.impl.rise.FontUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

@Getter
public enum Fonts {
    MAIN("SF-Pro-Rounded-%s", getExt()),
    MINECRAFT("Minecraft", () -> Minecraft.getMinecraft().fontRendererObj);
	
    Supplier<Font> get;
    Font font;
    String name;
    final String extention;
    private final HashMap<Integer, FontRenderer> sizes = new HashMap<>();
	
    Fonts(String name, String extension) {
        this.name = name;
        this.extention = extension;
    }
    
    Fonts(String name, Supplier<Font> get) {
        this.name = name;
        this.extention = "";
        this.font = get.get();
        this.get = get;
    }

    public Font get(int size) {
        return get(size, Weight.NONE);
    }

    public Font get() {
        return get(0, Weight.NONE);
    }

    public Font get(int size, Weight weight) {
        if (get != null) {
            if (font == null) font = get.get();
            return font;
        }
        
        int key = Integer.parseInt(size + "" + weight.getNum());

        if (!sizes.containsKey(key)) {
            java.awt.Font font = null;
            String location = "unknown";

            for (String alias : weight.getAliases()) {
            	location = "sakura/font/" + String.format(name, alias) + "." + extention;
            	font = FontUtil.getResource(location, size);
            	
            	if (font != null) break;
            }
            

            if (font != null) {
                sizes.put(key, new FontRenderer(font, true, true, false));
            }
        }

        return sizes.get(key);
    }
    
    private static String getExt() {
        String vendor = System.getProperty("java.vendor");
        return vendor != null && vendor.contains("Oracle") ? "ttf" : "otf";
    }
}
