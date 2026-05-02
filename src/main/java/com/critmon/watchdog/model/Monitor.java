package com.critmon.watchdog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Monitor {

    private String id;
    private int timeout;
    private String alertEmail;
    private String status;

    @JsonIgnore  // ← add this line
    private java.util.concurrent.ScheduledFuture<?> scheduledFuture;

    public Monitor(String id, int timeout, String alertEmail) {
        this.id = id;
        this.timeout = timeout;
        this.alertEmail = alertEmail;
        this.status = "active";
    }

    public String getId() { return id; }
    public int getTimeout() { return timeout; }
    public String getAlertEmail() { return alertEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @JsonIgnore  // ← add this line too
    public java.util.concurrent.ScheduledFuture<?> getScheduledFuture() { return scheduledFuture; }
    public void setScheduledFuture(java.util.concurrent.ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}