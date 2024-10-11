package cc.unknown.util.file.alt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cc.unknown.Sakura;
import cc.unknown.util.account.Account;
import cc.unknown.util.account.AccountType;
import cc.unknown.util.account.impl.CrackedAccount;
import cc.unknown.util.account.impl.MicrosoftAccount;
import cc.unknown.util.file.FileType;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public class AltFile extends cc.unknown.util.file.File {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    public AltFile(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        try {
            // reads file to a json object
            final FileReader fileReader = new FileReader(this.getFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final JsonObject jsonObject = getGSON().fromJson(bufferedReader, JsonObject.class);

            // closes both readers
            bufferedReader.close();
            fileReader.close();

            // checks if there was data read
            if (jsonObject == null) {
                return false;
            }

            // get account list ref and clear first
            List<Account> accounts = Sakura.instance.getAltManager().getAccounts();
            accounts.clear();

            // load all accounts
            JsonArray array = jsonObject.getAsJsonArray("data");
            if (array != null) {
                for (int i = 0; i < array.size(); ++i) {
                    JsonObject object = array.get(i).getAsJsonObject();
                    Account account = new Account(AccountType.CRACKED, "", "", "");
                    account.parseJson(object);

                    switch (account.getType()) {
                        case CRACKED: {
                            account = new CrackedAccount("");
                            account.parseJson(object);
                            break;
                        }
                        case MICROSOFT: {
                            account = new MicrosoftAccount("", "", "", "");
                            account.parseJson(object);
                            break;
                        }
                    }
                    accounts.add(account);
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
            // creates the file
            if (!this.getFile().exists()) {
                this.getFile().createNewFile();
            }

            // creates a new json object where all data is stored in
            List<Account> accounts = Sakura.instance.getAltManager().getAccounts();
            if (accounts.isEmpty()) {
                return true;
            }

            // Add some extra information to the config
            final JsonObject jsonObject = new JsonObject();
            final JsonObject metadataJsonObject = new JsonObject();
            metadataJsonObject.addProperty("version", Sakura.VERSION_FULL);
            metadataJsonObject.addProperty("creationDate", DATE_FORMATTER.format(new Date()));
            jsonObject.add("Metadata", metadataJsonObject);

            // converts accounts to json objects and puts inside array
            JsonArray array = new JsonArray();
            for (Account account : accounts) {
                array.add(account.toJson());
                System.out.println("writing account: " + account.getName());
            }

            jsonObject.add("data", array);

            // writes json object data to a file
            final FileWriter fileWriter = new FileWriter(getFile());
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            getGSON().toJson(jsonObject, bufferedWriter);

            // closes the writer
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }
}