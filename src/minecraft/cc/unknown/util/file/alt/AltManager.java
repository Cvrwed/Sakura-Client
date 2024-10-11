package cc.unknown.util.file.alt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.util.account.Account;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.FileType;

public class AltManager {
    public static final File ALT_DIRECTORY = new File(FileManager.DIRECTORY, "alts");
    private final List<Account> accounts = new ArrayList<>();

    public void init() {
        if (!ALT_DIRECTORY.exists()) {
            ALT_DIRECTORY.mkdir();
        }
    }

    public AltFile getAltFile() {
        return new AltFile(getFile(), FileType.ACCOUNT);
    }

    public boolean load() {
        return getAltFile().read();
    }

    public boolean update() {
        return getAltFile().write();
    }

    private File getFile() {
        return new File(ALT_DIRECTORY, "alts.json");
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}