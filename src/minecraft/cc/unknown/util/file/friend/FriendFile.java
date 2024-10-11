package cc.unknown.util.file.friend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.util.file.FileType;

public class FriendFile extends cc.unknown.util.file.File {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    public FriendFile(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        try {
            final FileReader fileReader = new FileReader(this.getFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final JsonObject jsonObject = getGSON().fromJson(bufferedReader, JsonObject.class);

            bufferedReader.close();
            fileReader.close();

            if (jsonObject == null) {
                return false;
            }
            
            FriendAndTargetComponent friendComponent = new FriendAndTargetComponent();

            friendComponent.getFriends().clear();

            JsonArray array = jsonObject.getAsJsonArray("friends");
            if (array != null) {
                for (int i = 0; i < array.size(); ++i) {
                    String friend = array.get(i).getAsString();
                    friendComponent.addFriend(friend);
                }
            }
        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }

	@Override
	public boolean write() {
        try {
            JsonObject jsonObject = new JsonObject();
            JsonArray array = new JsonArray();
            FriendAndTargetComponent friendComponent = new FriendAndTargetComponent();

            for (String friend : friendComponent.getFriends()) {
            	array.add(new JsonPrimitive(friend));
            }

            jsonObject.add("friends", array);

            final FileWriter fileWriter = new FileWriter(this.getFile());
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(getGSON().toJson(jsonObject));

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }

        return true;
	}

}
