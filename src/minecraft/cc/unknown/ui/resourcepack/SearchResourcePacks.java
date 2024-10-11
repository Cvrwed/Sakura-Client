package cc.unknown.ui.resourcepack;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cc.unknown.util.Accessor;
import cc.unknown.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

public class SearchResourcePacks implements Accessor {

    private GuiScreenResourcePacks parent;
    
    public SearchResourcePacks(GuiScreenResourcePacks parent) {
        this.parent = parent;
    }

    public void initGui(List<GuiButton> buttonList) {
        buttonList.forEach(b -> {
            b.setWidth(200);
            if (b.id == 2) {
                b.xPosition = parent.width / 2 - 204;
            }
        });
    }

    public void drawScreen(GuiResourcePackAvailable availableList, GuiResourcePackSelected selectedList,
                           int mouseX, int mouseY, float partialTicks, FontRenderer fontRendererObj, int width) {
        parent.drawBackground(0);
        availableList.drawScreen(mouseX, mouseY, partialTicks);
        selectedList.drawScreen(mouseX, mouseY, partialTicks);
        parent.drawCenteredString(fontRendererObj, I18n.format("resourcePack.title"), width / 2, 16, 16777215);
    }

    public GuiResourcePackAvailable updateList(GuiTextField search, GuiResourcePackAvailable clone, List<ResourcePackListEntry> available, Minecraft mc, int width, int height) {
        GuiResourcePackAvailable availableList;
        if (search == null || search.getText().isEmpty()) {
        	availableList = new GuiResourcePackAvailable(mc, 200, height, available);
        	availableList.setSlotXBoundsFromLeft(width / 2 - 4 - 200);
            clone.registerScrollButtons(7, 8);
        } else {
        	availableList = new GuiResourcePackAvailable(mc, 200, height, Arrays.asList(clone.getList().stream()
                .filter(listEntry -> {
                try {
                    Method method = ResourcePackListEntry.class.getDeclaredMethod("func_148312_b");
                    method.setAccessible(true);
                    String name = ChatColor.stripColor(((String) method.invoke(listEntry)).
                        replaceAll("[^A-Za-z0-9 ]", "").trim().toLowerCase());
                    String text = search.getText().toLowerCase();

                    if (name.endsWith("zip")) {
                        name = name.subSequence(0, name.length() - 3).toString();
                    }

                    for (String s : text.split(" ")) {
                        if (!name.contains(s.toLowerCase())) {
                            return false;
                        }
                    }

                    return name.startsWith(text) || name.contains(text) || name.equalsIgnoreCase(text);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            }).toArray(ResourcePackListEntry[]::new)));

        	availableList.setSlotXBoundsFromLeft(width / 2 - 4 - 200);
        	availableList.registerScrollButtons(7, 8);
        }

        return availableList;
    }
    
    enum ChatColor {
        BLACK('0'),
        DARK_BLUE('1'),
        DARK_GREEN('2'),
        DARK_AQUA('3'),
        DARK_RED('4'),
        DARK_PURPLE('5'),
        GOLD('6'),
        GRAY('7'),
        DARK_GRAY('8'),
        BLUE('9'),
        GREEN('a'),
        AQUA('b'),
        RED('c'),
        LIGHT_PURPLE('d'),
        YELLOW('e'),
        WHITE('f'),
        MAGIC('k', true),
        BOLD('l', true),
        STRIKETHROUGH('m', true),
        UNDERLINE('n', true),
        ITALIC('o', true),
        RESET('r');

        public static final char COLOR_CHAR = '\u00A7';
        private final char code;
        private final boolean isFormat;
        private final String toString;

        ChatColor(char code) {
            this(code, false);
        }

        ChatColor(char code, boolean isFormat) {
            this.code = code;
            this.isFormat = isFormat;
            toString = new String(new char[]{COLOR_CHAR, code});
        }

        public static String stripColor(final String input) {
            return input == null ? null : Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]").matcher(input).replaceAll("");
        }

        public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
            char[] b = textToTranslate.toCharArray();
            int bound = b.length - 1;
            for (int i = 0; i < bound; i++) {
                if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                    b[i] = ChatColor.COLOR_CHAR;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }

            return new String(b);
        }

        public char getChar() {
            return code;
        }

        @Override
        public String toString() {
            return toString;
        }

        public boolean isFormat() {
            return isFormat;
        }

        public boolean isColor() {
            return !isFormat && this != RESET;
        }
    }
}