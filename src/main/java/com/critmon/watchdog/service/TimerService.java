package com.critmon.watchdog.service;

import com.critmon.watchdog.model.Monitor;
import com.critmon.watchdog.store.MonitorStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TimerService {

    private final MonitorStore store;

    // This is the background thread pool that runs our timers
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public TimerService(MonitorStore store) {
        this.store = store;
    }

    // Called when registering OR when a heartbeat arrives
    public void startTimer(Monitor monitor) {
        cancelTimer(monitor);
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            monitor.setStatus("down");

            System.out.println("{\"ALERT\": \"Device " + monitor.getId() + " is down!\", " +
                    "\"time\": \"" + Instant.now() + "\", " +
                    "\"email\": \"" + monitor.getAlertEmail() + "\"}");

        }, monitor.getTimeout(), TimeUnit.SECONDS);
        monitor.setScheduledFuture(future);
        monitor.setStatus("active");
    }
    public void cancelTimer(Monitor monitor) {
        ScheduledFuture<?> existing = monitor.getScheduledFuture();
        if (existing != null && !existing.isDone()) {
            existing.cancel(false);
        }
    }
    public void pauseTimer(Monitor monitor) {
        cancelTimer(monitor);
        monitor.setStatus("paused");
    }
}