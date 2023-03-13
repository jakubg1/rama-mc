package skycat.ramamc;

public interface RunnableTimerAccess {
    void rama_mc_setRunnableTimer(RunnableTimer timer);
    void rama_mc_setRunnableTimer(Runnable runnable, long ticks);
}
