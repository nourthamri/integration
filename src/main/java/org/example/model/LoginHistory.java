package org.example.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;

public class LoginHistory {

    private Long id;
    private User user;
    private LocalDateTime loginTime;
    private String ipAddress;
    private String deviceInfo;

    private final StringProperty ipAddressProperty = new SimpleStringProperty();
    private final StringProperty deviceInfoProperty = new SimpleStringProperty();
    private final StringProperty loginTimeProperty = new SimpleStringProperty();

    // Constructors
    public LoginHistory() {
    }

    public LoginHistory(User user, LocalDateTime loginTime, String ipAddress, String deviceInfo) {
        this.user = user;
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;

        this.ipAddressProperty.set(ipAddress);
        this.deviceInfoProperty.set(deviceInfo);
        this.loginTimeProperty.set(loginTime.toString());
    }

    // Getters and setters for the properties
    public StringProperty ipAddressProperty() {
        return ipAddressProperty;
    }

    public StringProperty deviceInfoProperty() {
        return deviceInfoProperty;
    }

    public StringProperty loginTimeProperty() {
        return loginTimeProperty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
        this.loginTimeProperty.set(loginTime.toString());
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        this.ipAddressProperty.set(ipAddress);
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
        this.deviceInfoProperty.set(deviceInfo);
    }
}
