package util;

import model.User;

public class SessionManager {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
    
    public static boolean isKasir() {
        return currentUser != null && "kasir".equals(currentUser.getRole());
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getIdUser() : -1;
    }
    
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }
    
    public static String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getNamaLengkap() : "";
    }
    
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }
}