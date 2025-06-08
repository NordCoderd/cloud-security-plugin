package dev.protsenko.security.linter.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertResult {
    private String alertMessage;

    public AlertResult() {}

    public AlertResult(String alertMessage){
        this.alertMessage = alertMessage;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }
}
