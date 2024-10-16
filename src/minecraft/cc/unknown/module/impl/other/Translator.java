package cc.unknown.module.impl.other;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

@ModuleInfo(aliases = "Translator", description = "Translates your chat, might not work with some VPNs", category = Category.OTHER)
public final class Translator extends Module {

    private Executor translatorThread = Executors.newFixedThreadPool(1);

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Delay"))
            .add(new SubMode("Resend"))
            .setDefault("Delay");

    @EventLink
    public final Listener<PacketEvent> onPacketReceive = event -> {
    	if (!event.isReceive()) return;
        if (mc.world == null || mc.player == null) return;

        Packet<?> packet = event.getPacket();

        if (packet instanceof S02PacketChat) {
            S02PacketChat wrapper = (S02PacketChat) packet;

            IChatComponent component = wrapper.getChatComponent();
            String text = StringUtils.stripControlCodes(component.getFormattedText());

            if (text.contains("\n")) {
                return;
            }

            switch (this.mode.getValue().getName()) {
                case "Delay": {
                    event.setCancelled();

                    this.sendTranslatedMessage(text);
                    break;
                }

                case "Resend": {
                    this.sendTranslatedMessage(text);
                    break;
                }
            }
        }
    };

    public void sendTranslatedMessage(String text) {
        translatorThread.execute(() -> {
            try {
                String encodedText = URLEncoder.encode(text, "UTF-8");
                String response = requestLine("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + encodedText, "GET");

                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();

                JsonElement jsonElement = jsonParser.parse(response);
                JsonArray array = jsonElement.getAsJsonArray();


                String translated = array.get(0).getAsJsonArray().get(0).getAsJsonArray().get(0).getAsString();
                String language = new Locale(array.get(2).getAsString()).getDisplayLanguage(Locale.ENGLISH);

                ChatComponentText translatedComponent = new ChatComponentText(translated);

                if (!translated.equals(text)) {
                    translatedComponent.appendText(" ");

                    ChatComponentText hoverComponent = new ChatComponentText(getTheme().getChatAccentColor() + "[T]");
                    ChatStyle style = new ChatStyle();
                    style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Translated from " + language + "\n" + text)));
                    hoverComponent.setChatStyle(style);

                    translatedComponent.appendSibling(hoverComponent);
                }

                mc.player.addChatMessage(translatedComponent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private String requestLine(String url, String requestMethod) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(requestMethod);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}