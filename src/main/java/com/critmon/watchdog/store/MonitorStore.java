package com.critmon.watchdog.store;

import com.critmon.watchdog.model.Monitor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MonitorStore {

    // ConcurrentHashMap is thread-safe — important because timers run on background threads
    private final Map<String, Monitor> monitors = new ConcurrentHashMap<>();

    public void save(Monitor monitor) {
        monitors.put(monitor.getId(), monitor);
    }

    public Monitor findById(String id) {
        return monitors.get(id);
    }

    public Collection<Monitor> findAll() {
        return monitors.values();
    }

    public boolean exists(String id) {
        return monitors.containsKey(id);
    }
}