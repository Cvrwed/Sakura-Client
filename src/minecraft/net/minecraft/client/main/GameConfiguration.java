package net.minecraft.client.main;

import java.io.File;
import java.net.Proxy;

import com.mojang.authlib.properties.PropertyMap;

import lombok.AllArgsConstructor;
import net.minecraft.util.Session;

@AllArgsConstructor
public class GameConfiguration {
    public final GameConfiguration.UserInformation userInfo;
    public final GameConfiguration.DisplayInformation displayInfo;
    public final GameConfiguration.FolderInformation folderInfo;
    public final GameConfiguration.GameInformation gameInfo;
    public final GameConfiguration.ServerInformation serverInfo;

    @AllArgsConstructor
    public static class DisplayInformation {
        public final int width;
        public final int height;
        public final boolean fullscreen;
        public final boolean checkGlErrors;
    }

    @AllArgsConstructor
    public static class FolderInformation {
        public final File mcDataDir;
        public final File resourcePacksDir;
        public final File assetsDir;
        public final String assetIndex;
    }

    @AllArgsConstructor
    public static class GameInformation {
        public final String version;
    }

    @AllArgsConstructor
    public static class ServerInformation {
        public final String serverName;
        public final int serverPort;
    }

    @AllArgsConstructor
    public static class UserInformation {
        public final Session session;
        public final PropertyMap userProperties;
        public final PropertyMap field_181172_c;
        public final Proxy proxy;

    }
}
