package cc.unknown.util.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cc.unknown.util.Accessor;

/**
 * @author Patrick
 * @since 10/19/2021
 */

public abstract class File implements Accessor {

    private final java.io.File file;
    private final FileType fileType;

    public abstract boolean read();

    public abstract boolean write();

    public File(java.io.File file, FileType fileType) {
		this.file = file;
		this.fileType = fileType;
	}

	public java.io.File getFile() {
		return file;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void forceWrite(String name, ArrayList<String> lines) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(name, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        for (String line : lines) {
            writer.println(line);
        }
        writer.close();
    }

    public ArrayList<String> getString() {
        ArrayList<String> lines = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(getFile());
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			} catch (Exception e) {
				
			}

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }
}