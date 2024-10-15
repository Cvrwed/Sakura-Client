package cc.unknown.util.time;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class StopWatch {
    public long millis;

    public StopWatch() {
        reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= millis;
    }
    public boolean finished2(float milliSec) {
        return (float) (getCurrentTime() - millis) >= milliSec;
    }
    
    public boolean hasFinished() {
        return System.currentTimeMillis() >= millis;
    }
    
    public boolean reached(final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - millis) >= currentTime;
    }
    
    public boolean reached(final long lastTime, final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - millis + lastTime) >= currentTime;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
	
	public long getCurrentTime() {
		return System.nanoTime() / 1000000;
	}
}