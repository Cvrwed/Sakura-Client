package cc.unknown.util.file;

import java.io.File;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public class FileManager {

    public static final File DIRECTORY = new File(Accessor.mc.mcDataDir, Sakura.NAME);

    public void init() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
        }
    }
}