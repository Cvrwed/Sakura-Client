package javazoom.jl.player;

import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;

public class Player {
	private int frame = 0;

	private Bitstream bitstream;

	private Decoder decoder;

	private AudioDevice audio;

	private boolean closed = false;

	private boolean complete = false;

	private int lastPosition = 0;

	public Player(InputStream stream) throws JavaLayerException {
		this(stream, null);
	}

	public Player(InputStream stream, AudioDevice device) throws JavaLayerException {
		bitstream = new Bitstream(stream);
		decoder = new Decoder();

		if (device != null) {
			audio = device;
		} else {
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
		}
		audio.open(decoder);
	}

	public void play() throws JavaLayerException {
		play(Integer.MAX_VALUE);
	}

	public boolean play(int frames) throws JavaLayerException {
		boolean ret = true;

		while (frames-- > 0 && ret) {
			ret = decodeFrame();
		}

		if (!ret) {
			AudioDevice out = audio;
			if (out != null) {
				out.flush();
				synchronized (this) {
					complete = (!closed);
					close();
				}
			}
		}
		return ret;
	}

	public synchronized void close() {
		AudioDevice out = audio;
		if (out != null) {
			closed = true;
			audio = null;
			out.close();
			lastPosition = out.getPosition();
			try {
				bitstream.close();
			} catch (BitstreamException ex) {
			}
		}
	}

	public synchronized boolean isComplete() {
		return complete;
	}

	public int getPosition() {
		int position = lastPosition;

		AudioDevice out = audio;
		if (out != null) {
			position = out.getPosition();
		}
		return position;
	}

	protected boolean decodeFrame() throws JavaLayerException {
		try {
			AudioDevice out = audio;
			if (out == null)
				return false;

			Header h = bitstream.readFrame();

			if (h == null)
				return false;

			SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

			synchronized (this) {
				out = audio;
				if (out != null) {
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}
			}

			bitstream.closeFrame();
		} catch (RuntimeException ex) {
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
		return true;
	}

	public boolean setVolumen(final float volume) {
		if (this.audio instanceof JavaSoundAudioDevice) {
			final JavaSoundAudioDevice jsaudio = (JavaSoundAudioDevice) this.audio;
			jsaudio.setLineGain(volume);
		}
		return false;
	}
}
