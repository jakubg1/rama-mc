package skycat.ramamc;

public class RunnableTimer {
    public final Runnable runnable;
    public long ticks;

    public RunnableTimer(Runnable runnable, long ticks) {
        this.runnable = runnable;
        this.ticks = ticks;
    }

    public void expire() {
        runnable.run();
    }
}
