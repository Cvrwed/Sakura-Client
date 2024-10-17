package cc.unknown.module.api.manager;

import java.util.ArrayList;
import java.util.Arrays;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.module.impl.combat.*;
import cc.unknown.module.impl.exploit.*;
import cc.unknown.module.impl.latency.*;
import cc.unknown.module.impl.movement.*;
import cc.unknown.module.impl.other.*;
import cc.unknown.module.impl.player.*;
import cc.unknown.module.impl.visual.*;
import cc.unknown.module.impl.world.*;
import cc.unknown.util.AdaptiveMap;

public final class ModuleManager {

    private AdaptiveMap<Class<Module>, Module> moduleMap = new AdaptiveMap<>();

    public void init() {
        moduleMap = new AdaptiveMap<>();

        // Combat
        this.put(Criticals.class, new Criticals());
        this.put(KillAura.class, new KillAura());
        this.put(Velocity.class, new Velocity());
        this.put(AimAssist.class, new AimAssist());
        this.put(HitSelect.class, new HitSelect());
        this.put(AutoClicker.class, new AutoClicker());
        this.put(AutoBlock.class, new AutoBlock());
        this.put(TickRange.class, new TickRange());
        this.put(HitBox.class, new HitBox());
        this.put(STap.class, new STap());
        this.put(Regen.class, new Regen());
        this.put(Reach.class, new Reach());
        this.put(WTap.class, new WTap());
        
        // Latency
        this.put(BackTrack.class, new BackTrack());
        this.put(PingSpoof.class, new PingSpoof());
        /*this.put(LegitBlink.class, new LegitBlink());
        this.put(Lag.class, new Lag());*/
        
        // Exploit
        this.put(Disabler.class, new Disabler());
        this.put(GodMode.class, new GodMode());
        this.put(NoRotate.class, new NoRotate());
        this.put(AntiExploit.class, new AntiExploit());

        // Movement
        this.put(Flight.class, new Flight());
        this.put(InventoryMove.class, new InventoryMove());
        this.put(NoClip.class, new NoClip());
        this.put(AutoExtinguisher.class, new AutoExtinguisher());
        this.put(NoSlow.class, new NoSlow());
        this.put(NoJumpDelay.class, new NoJumpDelay());
        this.put(Speed.class, new Speed());
        this.put(Sneak.class, new Sneak());
        this.put(Sprint.class, new Sprint());
        this.put(Strafe.class, new Strafe());
        this.put(Parkour.class, new Parkour());
        this.put(Stuck.class, new Stuck());
        this.put(NoWeb.class, new NoWeb());
        this.put(TargetStrafe.class, new TargetStrafe());
        this.put(Spider.class, new Spider());
        
        // World
        this.put(Scaffold.class, new Scaffold());
        this.put(FastBreak.class, new FastBreak());
        this.put(AntiBot.class, new AntiBot());
        this.put(FastPlace.class, new FastPlace());
        this.put(LegitScaffold.class, new LegitScaffold());
        this.put(SafeWalk.class, new SafeWalk());

        // Other
        this.put(AntiAFK.class, new AntiAFK());
        this.put(AutoPlay.class, new AutoPlay());
        this.put(AutoRefill.class, new AutoRefill());
        this.put(Insults.class, new Insults());
        this.put(AutoLeave.class, new AutoLeave());
        this.put(MurderMystery.class, new MurderMystery());
        this.put(RichPresence.class, new RichPresence());
        this.put(NoGuiClose.class, new NoGuiClose());
        this.put(BedWarsUtils.class, new BedWarsUtils());

        // Player
        this.put(AntiFireBall.class, new AntiFireBall());
        this.put(AntiVoid.class, new AntiVoid());
        this.put(AutoPot.class, new AutoPot());
        this.put(AutoTool.class, new AutoTool());
        this.put(Blink.class, new Blink());
        this.put(NoClickDelay.class, new NoClickDelay());
        this.put(Breaker.class, new Breaker());
        this.put(FastUse.class, new FastUse());
        this.put(InventoryManager.class, new InventoryManager());
        this.put(NoFall.class, new NoFall());
        this.put(InventoryManager.class, new InventoryManager());
        this.put(NoFall.class, new NoFall());
        this.put(Timer.class, new Timer());
        this.put(ChestStealer.class, new ChestStealer());

        // Render
        this.put(Ambience.class, new Ambience());
        this.put(Animations.class, new Animations());
        this.put(BPSCounter.class, new BPSCounter());
        this.put(ChestESP.class, new ChestESP());
        this.put(ClickGUI.class, new ClickGUI());
        this.put(CPSCounter.class, new CPSCounter());
        this.put(FPSCounter.class, new FPSCounter());
        this.put(FreeCam.class, new FreeCam());
        this.put(FreeLook.class, new FreeLook());
        this.put(FullBright.class, new FullBright());
        this.put(HurtCamera.class, new HurtCamera());
        this.put(HUD.class, new HUD());
        this.put(PacketDebug.class, new PacketDebug());
        this.put(ItemPhysics.class, new ItemPhysics());
        this.put(NameTags.class, new NameTags());
        this.put(NoCameraClip.class, new NoCameraClip());
        this.put(ExtraSensoryPerception.class, new ExtraSensoryPerception());
        this.put(ScoreBoard.class, new ScoreBoard());
        this.put(Streamer.class, new Streamer());
        this.put(MusicPlayer.class, new MusicPlayer());
        this.put(TargetInfo.class, new TargetInfo());
        this.put(Tracers.class, new Tracers());
        this.put(UnlimitedChat.class, new UnlimitedChat());
        this.put(SessionStats.class, new SessionStats());
        this.put(JumpCirclesModule.class, new JumpCirclesModule());

        // Automatic initializations
        this.getAll().stream().filter(module -> module.getModuleInfo().autoEnabled()).forEach(module -> module.setEnabled(true));

        // Has to be a listener to handle the key presses
        Sakura.instance.getEventBus().register(this);
    }

    public ArrayList<Module> getAll() {
        return this.moduleMap.values();
    }

    public <T extends Module> T get(final Class<T> clazz) {
        return (T) this.moduleMap.get(clazz);
    }

    public <T extends Module> T get(final String name) {
        // noinspection unchecked
        return (T) this.getAll().stream()
                .filter(module -> Arrays.stream(module.getAliases()).anyMatch(alias ->
                        alias.replace(" ", "")
                                .equalsIgnoreCase(name.replace(" ", ""))))
                .findAny().orElse(null);
    }

    public void put(Class clazz, Module module) {
        this.moduleMap.put(clazz, module);
    }
    
    public void put(Class<? extends Module>[] clazzArray, Module[] moduleArray) {
        if (clazzArray.length != moduleArray.length) {
            throw new IllegalArgumentException("Class and Module arrays must have the same length");
        }
        
        for (int i = 0; i < clazzArray.length; i++) {
            this.moduleMap.put((Class<Module>) clazzArray[i], moduleArray[i]);
        }
    }

    public void remove(Module key) {
        this.moduleMap.removeValue(key);
        this.updateArraylistCache();
    }

    public boolean add(final Module module) {
        this.moduleMap.put(module);
        this.updateArraylistCache();

        return true;
    }

    private void updateArraylistCache() {
        final HUD interfaceModule = this.get(HUD.class);

        if (interfaceModule == null) {
            return;
        }

        interfaceModule.createArrayList();
    }
}