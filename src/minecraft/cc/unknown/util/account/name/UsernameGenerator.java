package cc.unknown.util.account.name;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.util.Accessor;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class UsernameGenerator implements Accessor {

	// this shit is better
    public static String[] retrieve() {
        try {
            InputStream stream = mc.getResourceManager().getResource(new ResourceLocation("sakura/altmanager/usernames.txt")).getInputStream();
            if (stream == null) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            for (String s; (s = reader.readLine()) != null; builder.append(s).append(System.lineSeparator())) ;
            reader.close();

            return builder.toString().split(System.lineSeparator());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    public static String generate() {
        return generate(1)[0];
    }

    public static String[] generate(int amount) {
        String[] usernames = retrieve();
        if (usernames == null) {
            return null;
        }

        List<String> acceptableUsernames = Arrays.stream(usernames)
                .filter(username -> username.length() >= 3 && username.length() <= 6)
                .collect(Collectors.toList());

        if (acceptableUsernames.isEmpty()) {
            return null;
        }

        String[] generated = new String[amount];
        int size = acceptableUsernames.size();

        for (int i = 0; i < amount; ++i) {
            String prefix = acceptableUsernames.get(ThreadLocalRandom.current().nextInt(size));
            String suffix = acceptableUsernames.get(ThreadLocalRandom.current().nextInt(size));
            String username = applyPattern(prefix, suffix);
            generated[i] = applyPattern(username);
        }

        return generated;
    }

    private static String applyPattern(String prefix, String suffix) {
        int pattern = (int) (Math.random() * 4);
        switch (pattern) {
            case 0: {
                return prefix + "_" + suffix;
            }
            case 1: {
                return prefix + suffix.substring(0, 2) + (int) (Math.random() * 100);
            }
            case 2: {
                int index = (int) (Math.random() * Math.min(prefix.length(), suffix.length()));
                return prefix.substring(0, index) + "_" + suffix.substring(index);
            }
            case 3: {
                StringBuilder merge = new StringBuilder(prefix).append(suffix);
                int uIndex = (int) (Math.random() * merge.length());
                int nIndex = (int) (Math.random() * merge.length());
                merge.insert(uIndex, "_");
                merge.insert(nIndex, (int) (Math.random() * 100));
                return merge.toString();
            }
            default: {
                return prefix + suffix;
            }
        }
    }

    private static String applyPattern(String username) {
        double numberChance = 0.125;
        double upperChance = 0.25;

        char[] chars = username.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if ((i == 0 || (chars[i - 1] == '_' || Character.isDigit(chars[i - 1])) && Character.isLetter(c))) {
                if (Math.random() < upperChance) {
                    chars[i] = Character.toUpperCase(c);
                    continue;
                }
            }

            char lower = Character.toLowerCase(c);
            char replacement = getReplacement(lower);
            if (replacement != lower) {
                if (Math.random() < numberChance) {
                    chars[i] = replacement;
                    numberChance *= 0.5;
                }
            }
        }

        return new String(chars);
    }

    private static char getReplacement(char c) {
        if (c == 'a') {
            return '4';
        } else if (c == 'e') {
            return '3';
        } else if (c == 'i') {
            return '1';
        } else if (c == 'o') {
            return '0';
        } else if (c == 't') {
            return '7';
        } else {
            return c;
        }
    }
    
    public static boolean validate(String name) {
        if (name.length() < 3 || name.length() > 16) {
            return false;
        }

        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }

        return true;
    }
}

