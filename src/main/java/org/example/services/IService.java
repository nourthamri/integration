package org.example.services;

import org.example.entities.User;

import java.sql.SQLException;
import java.util.List;

public interface IService<User> {

    // User Management methods
    boolean ajouteru(User user) throws SQLException;   // Add a new user
    boolean modifieru(User user) throws SQLException;   // Update an existing user
    boolean supprimeru(int userId) throws SQLException;  // Delete a user by ID
    List<User> afficher_t() throws SQLException;         // Display all users

    // User Verification methods
    boolean emailExists(String email) throws SQLException; // Check if the email is already registered
    boolean verifyUser(String email) throws SQLException;  // Verify a user based on email
    boolean isUserVerified(String email) throws SQLException; // Check if a user is verified

    // Password Management methods
    boolean updatePassword(String email, String newPassword) throws SQLException; // Update user's password

    // User Block/Unblock methods
    boolean blockUser(int userId) throws SQLException;    // Block a user by ID
    boolean unblockUser(int userId) throws SQLException;  // Unblock a user by ID
    boolean isUserBlocked(String email) throws SQLException; // Check if the user is blocked
}