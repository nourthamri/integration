package org.example.entities;

public class User {

    private int id;
    private String email;
    private Integer eventId;
    private String roles = "ROLE_USER"; // Default value for roles
    private String password;
    private boolean isVerified = false; // Default value for isVerified
    private String username;
    private String lastName;
    private String phoneNumber;
    private String image;

    public User() {}

    // Constructor with the default values for roles and isVerified
    public User(String email, String password, String username, String lastName,
                String phoneNumber, String image) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;

        // roles and isVerified will be set to default values
    }
    public User(int id, String email, String password, String username, String lastName, String phoneNumber, String image) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }


    public User(int id, String email, Integer eventId, String roles, String password, boolean isVerified,
                String username, String lastName, String phoneNumber, String image) {
        this.id = id;
        this.email = email;
        this.eventId = eventId;
        this.roles = (roles == null || roles.isEmpty()) ? "ROLE_USER" : roles; // Set default to "ROLE_USER" if null or empty
        this.password = password;
        this.isVerified = (isVerified == false) ? false : isVerified; // Set default to false if null
        this.username = username;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", eventId=" + eventId +
                ", roles='" + roles + '\'' +
                ", password='" + password + '\'' +
                ", isVerified=" + isVerified +
                ", username='" + username + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
