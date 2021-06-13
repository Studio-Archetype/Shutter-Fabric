package studio.archetype.shutter.client.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

public class TimingUtils {

    private static final List<Job> jobs = new ArrayList<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register((t) -> jobs.removeIf(Job::tick));
    }

    public static void schedule(int ticks, Runnable runnable) {
        jobs.add(new Job(ticks, runnable));
    }

    private static class Job {

        private final Runnable runnable;
        private int timer;

        public Job(int timer, Runnable runnable) {
            this.runnable = runnable;
            this.timer = timer;
        }

        public boolean tick() {
            if(this.timer-- == 0) {
                this.runnable.run();
                return true;
            }
            return false;
        }
    }
}
