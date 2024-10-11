package cc.unknown.util.sound.radio;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.sound.sampled.FloatControl;

import cc.unknown.Sakura;
import cc.unknown.module.impl.other.MusicPlayer;
import cc.unknown.util.time.StopWatch;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class RadioPlayer {
    private Thread thread;
    private Player player = null;
    private FloatControl control = null;
    private final StopWatch timer = new StopWatch();

    public void start(final String url) {
        if (this.timer.reached(2000L)) {
            (this.thread = new Thread(() -> {
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                    try {
                        this.player = new Player(new URL(url).openStream());
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }

                    this.player.play();
                } catch (JavaLayerException | NoSuchAlgorithmException | KeyManagementException e2) {
                    e2.printStackTrace();
                }
            })).start();
            this.timer.reset();
        }
    }

    public void stop() {
    	Runnable musicTask = () -> {
	        if (this.thread != null) {
	            this.thread.interrupt();
	            this.thread = null;
	        }
	        if (this.player != null) {
	            this.player.close();
	            this.player = null;
	        }
        };

        new Thread(musicTask).start();
    }
}