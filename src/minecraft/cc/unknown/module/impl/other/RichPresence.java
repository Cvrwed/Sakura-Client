package cc.unknown.module.impl.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.util.ResourceLocation;;

@ModuleInfo(aliases = "Rich Presence", category = Category.OTHER, description = "Discord Status")
public class RichPresence extends Module {	
    private final String clientId = "1281488660360986635";
    private static final String DEFAULT_IMAGE = "sakura2";
    private Map<String, ServerData> serverDataMap = new HashMap<>();
    private boolean started;
    private String serverName;
    private String serveraddresses;
    
	@EventLink(value = Priority.EXTREMELY_HIGH)
	public final Listener<PreUpdateEvent> onTick = event -> onRPC();
	
    @Override
    public void onDisable() {
        DiscordRPC.discordShutdown();
        started = false;
    }
    
    private void onRPC() {
        String currentServerIP = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : null;

        if (!started || !isInGame() || (currentServerIP != null && !currentServerIP.endsWith(serveraddresses))) {
            if (started) {
                onDisable();
            }
            if (serverDataMap.isEmpty()) {
                try {
                    fetchServerMappings();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isInGame()) {
                if (findServerData(currentServerIP)) {
                    DiscordRPC.discordUpdatePresence(makeRPC("https://dc.zornhub.xyz/", ""));
                } else {
                    updatePrivateRPC();
                }
            }

            DiscordEventHandlers handlers = new DiscordEventHandlers();
            DiscordRPC.discordInitialize(clientId, handlers, true);
            new Thread(() -> {
                while (this.isEnabled()) {
                    DiscordRPC.discordRunCallbacks();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Discord RPC Callback").start();
            started = true;
        }
    }
    
    private void fetchServerMappings() throws IOException {
        try (InputStream inputStream = mc.getResourceManager().getResource(new ResourceLocation("sakura/mapping/servers.json")).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Gson gson = new Gson();

            List<ServerMapping> serverMappings = gson.fromJson(reader, new TypeToken<List<ServerMapping>>(){}.getType());

            for (ServerMapping mapping : serverMappings) {
                ServerData serverData = new ServerData();
                serverData.name = mapping.name;
                serverData.logo = mapping.images.logo;

                serverDataMap.put(mapping.primaryAddress.toLowerCase(), serverData);

                for (String address : mapping.addresses) {
                    serverDataMap.put(address.toLowerCase(), serverData);
                }
            }
        }
    }

    private String readFromConnection(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private boolean findServerData(String serverIP) {
        if (serverIP == null) return false;
        serverIP = serverIP.toLowerCase();
        serveraddresses = serverIP;
        ServerData serverData = serverDataMap.get(serverIP);

        if (serverData != null) {
            serverName = "Playing " + serverData.name;
            return true;
        }
        for (Map.Entry<String, ServerData> entry : serverDataMap.entrySet()) {
            String knownAddress = entry.getKey();
            if (serverIP.endsWith(knownAddress)) {
                serverData = entry.getValue();
                serverName = "Playing " + serverData.name;
                return true;
            }
        }

        return false;
    }


    private void updatePrivateRPC() {
        serverName = "Playing Private Server";
        DiscordRPC.discordUpdatePresence(makeRPC("", ""));
    }

    public DiscordRichPresence makeRPC(String bigText, String state) {
        if (bigText.startsWith("Playing")) bigText = bigText.substring(8);
        return new DiscordRichPresence.Builder(state)
                .setDetails(serverName)
                .setBigImage(DEFAULT_IMAGE, bigText)
                .setStartTimestamps(System.currentTimeMillis())
                .setParty(UUID.randomUUID().toString(), 1, 4)
                .setSecrets(UUID.randomUUID().toString(), "https://dc.zornhub.xyz/")
                .build();
    }
    
    class ServerData {
        String name;
        String logo;
        List<String> addresses;
    }

    class ServerMapping {
        String primaryAddress;
        String name;
        Images images;
        List<String> addresses;
        
        class Images {
            String logo;
        }
    }
}
