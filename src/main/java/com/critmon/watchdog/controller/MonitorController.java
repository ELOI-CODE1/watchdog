package com.critmon.watchdog.controller;

import com.critmon.watchdog.model.Monitor;
import com.critmon.watchdog.service.TimerService;
import com.critmon.watchdog.store.MonitorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/monitors")
public class MonitorController {

    private final MonitorStore store;
    private final TimerService timerService;

    public MonitorController(MonitorStore store, TimerService timerService) {
        this.store = store;
        this.timerService = timerService;
    }

    // ── Story 1: Register a monitor ──
    @PostMapping
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, Object> body) {

        String id = (String) body.get("id");
        int timeout = (int) body.get("timeout");
        String alertEmail = (String) body.get("alert_email");

        // Don't allow duplicate IDs
        if (store.exists(id)) {
            return ResponseEntity.status(409).body(Map.of("message", "Monitor already exists: " + id));
        }

        Monitor monitor = new Monitor(id, timeout, alertEmail);
        store.save(monitor);
        timerService.startTimer(monitor);

        return ResponseEntity.status(201).body(Map.of(
            "message", "Monitor registered successfully",
            "id", id,
            "timeout", String.valueOf(timeout)
        ));
    }

    // ── Story 2: Heartbeat ──
    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<Map<String, String>> heartbeat(@PathVariable String id) {

        Monitor monitor = store.findById(id);

        if (monitor == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Monitor not found: " + id));
        }

        // Reset the timer (also un-pauses if paused)
        timerService.startTimer(monitor);

        return ResponseEntity.ok(Map.of(
            "message", "Heartbeat received, timer reset",
            "id", id,
            "status", monitor.getStatus()
        ));
    }

    // ── Story 3 (Bonus): Pause ──
    @PostMapping("/{id}/pause")
    public ResponseEntity<Map<String, String>> pause(@PathVariable String id) {

        Monitor monitor = store.findById(id);

        if (monitor == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Monitor not found: " + id));
        }

        if ("down".equals(monitor.getStatus())) {
            return ResponseEntity.status(400).body(Map.of("message", "Cannot pause a monitor that is already down"));
        }

        timerService.pauseTimer(monitor);

        return ResponseEntity.ok(Map.of(
            "message", "Monitor paused",
            "id", id,
            "status", monitor.getStatus()
        ));
    }

    // ── Developer's Choice: GET a single monitor ──
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getMonitor(@PathVariable String id) {

        Monitor monitor = store.findById(id);

        if (monitor == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Monitor not found: " + id));
        }

        return ResponseEntity.ok(Map.of(
            "id", monitor.getId(),
            "status", monitor.getStatus(),
            "timeout", String.valueOf(monitor.getTimeout()),
            "alert_email", monitor.getAlertEmail()
        ));
    }

    // ── Developer's Choice: GET all monitors ──
    @GetMapping
    public ResponseEntity<?> getAllMonitors() {
        return ResponseEntity.ok(store.findAll());
    }
}