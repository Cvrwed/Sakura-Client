package cc.unknown.component.impl.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class FriendAndTargetComponent {
    private static final ArrayList<String> friends = new ArrayList<>();
    private static final ArrayList<String> targets = new ArrayList<>();

    public static void addFriend(String friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    public static void removeFriend(String friend) {
        friends.remove(friend);
    }
    
    public static void addTarget(String target) {
        if (!targets.contains(target)) {
            targets.add(target);
        }
    }

    public static void removeTarget(String target) {
        targets.remove(target);
    }

    public static boolean isFriend(String friend) {
        return friends.contains(friend);
    }
    
    public static boolean isFriend(EntityPlayer entityPlayer) {
        return !friends.isEmpty() && friends.contains(entityPlayer.getName().toLowerCase());
    }
    
    public static boolean isTarget(EntityPlayer entityPlayer) {
        return !targets.isEmpty() && targets.contains(entityPlayer.getName().toLowerCase());
    }
    
    public static boolean isTarget(String target) {
        return targets.contains(target);
    }

    public static List<String> getFriends() {
        return new ArrayList<>(friends);
    }

    public static List<String> getTargets() {
        return new ArrayList<>(targets);
    }
}
