package tn.esprit.entities;

public class SessionManager {
    private static User currentUser;

    private static SessionManager instance;

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public void logout() {
        currentUser = null;
    }

}

