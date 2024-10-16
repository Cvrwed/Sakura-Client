package cc.unknown.component.impl.player;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.module.impl.exploit.AntiExploit;
import cc.unknown.util.packet.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.util.IChatComponent;

public class SecurityComponent extends Component {

	private final Pattern PATTERN = Pattern.compile(".*\\$\\{[^}]*}.*");
    private int particles;
    
    private AntiExploit sf = Sakura.instance.getModuleManager().get(AntiExploit.class);

    @EventLink
    public final Listener<PacketEvent> onPacketReceive = event -> {
    	assert sf != null;
    	if (event.isReceive()) {
	        event.setCancelled(getAntiCrash(
	        		event.getPacket(), 
	        		sf.getDemoCheck(), 
	        		sf.getExplosionCheck(), 
	        		sf.getLog4jCheck(), 
	        		sf.getParticlesCheck(), 
	        		sf.getResourceCheck(), 
	        		sf.getTeleportCheck(), 
	        		sf.getBookCheck())
	        		);
    	}
    };

    private boolean getAntiCrash(final Packet<?> packet, boolean demo, boolean explosion, boolean log4j, boolean particle, boolean resource, boolean teleport, boolean book) {
        if (demo && packet instanceof S2BPacketChangeGameState) {
            final S2BPacketChangeGameState wrapper = ((S2BPacketChangeGameState) packet);

            return wrapper.getGameState() == 5 && wrapper.func_149137_d() == 0;
        }
        
        if (explosion && packet instanceof S27PacketExplosion) {
            final S27PacketExplosion wrapper = ((S27PacketExplosion) packet);

            return wrapper.func_149149_c() >= Byte.MAX_VALUE
                    || wrapper.func_149144_d() >= Byte.MAX_VALUE
                    || wrapper.func_149147_e() >= Byte.MAX_VALUE;
        }
    	
        if (log4j && packet instanceof S29PacketSoundEffect) {
            final S29PacketSoundEffect wrapper = (S29PacketSoundEffect) packet;
            final String name = wrapper.getSoundName();

            return PATTERN.matcher(name).matches();
        }

        if (log4j && packet instanceof S02PacketChat) {
            final S02PacketChat wrapper = ((S02PacketChat) packet);
            final IChatComponent component = wrapper.getChatComponent();

            return PATTERN.matcher(component.getUnformattedText()).matches()
                    || PATTERN.matcher(component.getFormattedText()).matches();
        }
    	
        if (particle && packet instanceof S2APacketParticles) {
            final S2APacketParticles wrapper = ((S2APacketParticles) packet);

            particles += wrapper.getParticleCount();
            particles -= 6;
            particles = Math.min(particles, 150);

            return particles > 100 || wrapper.getParticleCount() < 1 || Math.abs(wrapper.getParticleCount()) > 20 ||
                    wrapper.getParticleSpeed() < 0 || wrapper.getParticleSpeed() > 1000;
        }
    	
        if (resource && packet instanceof S48PacketResourcePackSend) {
            final S48PacketResourcePackSend wrapper = ((S48PacketResourcePackSend) packet);

            final String url = wrapper.getURL();
            final String hash = wrapper.getHash();

            if (url.toLowerCase().startsWith("level://")) {
                return check(url, hash);
            }
        }
    	
    	if (teleport && packet instanceof S08PacketPlayerPosLook) {
            final S08PacketPlayerPosLook wrapper = ((S08PacketPlayerPosLook) packet);

            return Math.abs(wrapper.x) > 1E+9 || Math.abs(wrapper.y) > 1E+9 || Math.abs(wrapper.z) > 1E+9;
        }
    	
    	if (book && packet instanceof S3FPacketCustomPayload) {
    		S3FPacketCustomPayload wrapper = ((S3FPacketCustomPayload) packet);
    		return wrapper.getChannelName() == "MC|BOpen";
    		
    	}

        return false;
    }
    
    private boolean check(String url, final String hash) {
        try {
            final URI uri = new URI(url);

            final String scheme = uri.getScheme();
            final boolean isLevelProtocol = "level".equals(scheme);

            if (!("http".equals(scheme) || "https".equals(scheme) || isLevelProtocol)) {
                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                System.out.println("Server tried to access the path: " + url);

                throw new URISyntaxException(url, "Invalid levelstorage resource pack path");
            }

            return false;
        } catch (final Exception e) {
            PacketUtil.sendNoEvent(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return true;
        }
    }
}