package com.gertoxq.wynnbuild.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.*;

public class Task {
    private static final Map<Integer, Task> taskIds = new HashMap<>();
    private static final List<Integer> toRemove = new ArrayList<>();
    private static long counter = 0;
    private static int id = 0;
    private static boolean live = false;
    public final long triggerAt;
    public final int delay;
    public final Runnable task;

    public Task(Runnable task, int delay) {
        this.task = task;
        this.delay = delay;
        this.triggerAt = counter + delay;
        live = true;
        taskIds.put(id, this);
        id++;
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!live) return;
            if (taskIds.isEmpty()) {
                live = false;
                return;
            }

            try {
                taskIds.forEach((integer, task) -> {
                    if (task.triggerAt <= counter) {
                        client.execute(task.task);
                        toRemove.add(integer);
                    }
                });
            } catch (ConcurrentModificationException ignored) {
            }
            try {
                toRemove.forEach(taskIds::remove);
            } catch (IllegalStateException ignored) {
            }
            toRemove.clear();
            counter++;
        });
    }

    public void then(Runnable task, int delay) {
        new Task(task, delay + this.delay);
    }
}
