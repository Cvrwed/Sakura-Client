package cc.unknown.script.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.ScriptException;

import jdk.nashorn.api.scripting.JSObject;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.component.impl.player.PingSpoofComponent;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.module.Module;
import cc.unknown.script.api.wrapper.impl.ScriptBlockPos;
import cc.unknown.script.api.wrapper.impl.ScriptCommand;
import cc.unknown.script.api.wrapper.impl.ScriptModule;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector2f;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.script.util.ScriptModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.interfaces.ThreadAccess;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import net.minecraft.client.Minecraft;

/**
 * @author Strikeless
 * @since 11.06.2022
 */
public class ScriptAPI {
    private static final Map<Module, ScriptModule> SCRIPT_MODULE_MAP = new HashMap<>();

    private static final Map<Command, ScriptCommand> SCRIPT_COMMAND_MAP = new HashMap<>();

    private static ScriptModule getModule(final Module module) {
        SCRIPT_MODULE_MAP.putIfAbsent(module, new ScriptModule(module, true));
        return SCRIPT_MODULE_MAP.get(module);
    }

    private static ScriptCommand getCommand(final Command command) {
        SCRIPT_COMMAND_MAP.putIfAbsent(command, new ScriptCommand(command));
        return SCRIPT_COMMAND_MAP.get(command);
    }

    public ScriptModule registerModule(final String name, final String description) {
        // Sometimes my genius is almost frightening // True
        final AtomicReference<ScriptModule> scriptModuleReference = new AtomicReference<>(null);

        final Module module = new Module(new ScriptModuleInfo(name, description)) {
            @Override
            public void onEnable() {
                final ScriptModule scriptModule = scriptModuleReference.get();
                if (scriptModule == null) return;

                scriptModule.call("onEnable");
            }

            @Override
            public void onDisable() {
                final ScriptModule scriptModule = scriptModuleReference.get();
                if (scriptModule == null) return;

                scriptModule.call("onDisable");
            }
        };

        scriptModuleReference.set(getModule(module));
        Sakura.instance.getModuleManager().add(module);

        if (Sakura.instance.getClickGui() != null) Sakura.instance.getClickGui().rebuildModuleCache();
        return scriptModuleReference.get();
    }

    public ScriptModule[] getModules() {
        final List<Module> modules = Sakura.instance.getModuleManager().getAll();
        final ScriptModule[] scriptModules = new ScriptModule[modules.size()];

        for (int i = 0; i < modules.size(); i++) {
            scriptModules[i] = new ScriptModule(modules.get(i));
        }

        return scriptModules;
    }

    public ScriptModule getModule(final String name) {
        return new ScriptModule(Sakura.instance.getModuleManager().get(name));
    }

    public void rotate(float yaw, float pitch, double speed) {
        RotationComponent.setRotations(new Vector2f(yaw, pitch), speed, MovementFix.OFF);
    }

    public float[] getRotations(int entity) {
        Vector2f rotations = RotationUtil.calculate(Minecraft.getMinecraft().world.getEntityByID(entity));

        return new float[]{rotations.x, rotations.y};
    }

    public float[] getRotations(ScriptVector3d vector3d) {
        Vector2f rotations = RotationUtil.calculate(new Vector3d(vector3d.getX(), vector3d.getY(), vector3d.getZ()));

        return new float[]{rotations.x, rotations.y};
    }

    public ScriptCommand registerCommand(final String name, final String description) {
        // Sometimes my genius is almost frightening
        final AtomicReference<ScriptCommand> scriptCommandReference = new AtomicReference<>(null);

        final Command command = new Command(description, name) {
            @Override
            public void execute(final String[] args) {
                final ScriptCommand scriptCommand = scriptCommandReference.get();
                if (scriptCommand == null) return;

                scriptCommand.call("onExecute", (Object[]) args);
            }
        };

        scriptCommandReference.set(getCommand(command));
        Sakura.instance.getCommandManager().getCommandList().add(command);

        return scriptCommandReference.get();
    }

    public ScriptCommand[] getCommands() {
        final List<Command> commands = Sakura.instance.getCommandManager().getCommandList();
        final ScriptCommand[] scriptCommands = new ScriptCommand[commands.size()];

        for (int i = 0; i < commands.size(); ++i) {
            scriptCommands[i] = getCommand(commands.get(i));
        }

        return scriptCommands;
    }

    public ScriptCommand getCommand(final String name) {
        return getCommand(Sakura.instance.getCommandManager().get(name));
    }

    public void displayChat(final String message) {
        ChatUtil.display(message);
    }

    public void displayInfoNotification(final String title, final String message) {
        NotificationComponent.post(title, message);
    }

    public void displayInfoNotification(final String title, final String message, final int time) {
        NotificationComponent.post(title, message, time);
    }

    public void pingspoof(final int delay, final boolean normal, final boolean teleport,
                          final boolean velocity, final boolean entity) {
        PingSpoofComponent.spoof(delay, normal, teleport, velocity, entity);
    }

    public void blink() {
        PingSpoofComponent.blink();
    }

    public void dispatch() {
        PingSpoofComponent.dispatch();
    }

    public void pingspoof(final int delay) {
        PingSpoofComponent.spoof(delay, true, false, false, false);
    }

    public long getSystemMillis() {
        return System.currentTimeMillis();
    }

    public ScriptVector2f newVec2(float x, float y) {
        return new ScriptVector2f(x, y);
    }

    public ScriptVector3d newVec3(double x, double y, double z) {
        return new ScriptVector3d(x, y, z);
    }

    public int getFPS() {
        return Minecraft.getDebugFPS();
    }

    public boolean isBot(int entityID){
        return Sakura.instance.getBotManager().contains(Minecraft.getMinecraft().world.getEntityByID(entityID));
    }

    public boolean isFriend(String name){
        return FriendAndTargetComponent.isFriend(name);
    }

    public ScriptBlockPos newBlockPos(int x, int y, int z) {
        ChatUtil.display("Please use world.newBlockPos(), instead of sakura.newBlockPos().");
        return null;
    }

    public void threadPool(JSObject function) throws ScriptException {
        if (!function.isFunction()) throw new ScriptException("Not a function!");
        ThreadAccess.threadPool.execute(() -> function.call(null));
    }

    public void thread(JSObject function) throws ScriptException {
        if (!function.isFunction()) throw new ScriptException("Not a function!");
        new Thread(() -> function.call(null)).start();
    }
}
