package net.minecraft.server.management;

import java.io.File;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
    public UserListBans(final File bansFile) {
        super(bansFile);
    }

    protected UserListEntry<GameProfile> createEntry(final JsonObject entryData) {
        return new UserListBansEntry(entryData);
    }

    public boolean isBanned(final GameProfile profile) {
        return this.hasEntry(profile);
    }

    public String[] getKeys() {
        final String[] astring = new String[this.getValues().size()];
        int i = 0;

        for (final UserListBansEntry userlistbansentry : this.getValues().values()) {
            astring[i++] = userlistbansentry.getValue().getName();
        }

        return astring;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(final GameProfile obj) {
        return obj.getId().toString();
    }

    public GameProfile isUsernameBanned(final String username) {
        for (final UserListBansEntry userlistbansentry : this.getValues().values()) {
            if (username.equalsIgnoreCase(userlistbansentry.getValue().getName())) {
                return userlistbansentry.getValue();
            }
        }

        return null;
    }
}
