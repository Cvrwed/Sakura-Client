package cc.unknown.util.account.impl;

import java.util.UUID;

import cc.unknown.util.account.Account;
import cc.unknown.util.account.AccountType;

public class CrackedAccount extends Account {
    public CrackedAccount(String name) {
        super(AccountType.CRACKED, name, getUUID(name), "accessToken");
    }

    /**
     * Converts cracked name to UUID.
     *
     * @param name Cracked name.
     * @return UUID.
     */
    private static String getUUID(String name) {
        String s = "OfflinePlayer:" + name;
        return UUID.nameUUIDFromBytes(s.getBytes()).toString().replace("-", "");
    }
}
