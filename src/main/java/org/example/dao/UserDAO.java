package org.example.dao;

import org.example.entities.User;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Method to insert a new user into the database
    public boolean insertUser(User user) {
        String insertSQL = "INSERT INTO user (email, roles, password, is_verified, username, lastname, phone_number, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = MyDataBase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getRoles() != null ? user.getRoles() : "ROLE_USER"); // Always pass a value
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setBoolean(4, user.isVerified());
            preparedStatement.setString(5, user.getUsername());
            preparedStatement.setString(6, user.getLastName());
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getImage());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // Method to update an existing user in the database
    public boolean updateUser(User user) {
        String updateSQL = "UPDATE user SET email = ?, roles = ?, password = ?, is_verified = ?, username = ?, lastname = ?, phone_number = ?, image = ? WHERE id = ?";

        try (Connection connection = MyDataBase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getRoles() != null ? user.getRoles() : "ROLE_USER"); // Always pass a value
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setBoolean(4, user.isVerified());
            preparedStatement.setString(5, user.getUsername());
            preparedStatement.setString(6, user.getLastName());
            preparedStatement.setString(7, user.getPhoneNumber());
            preparedStatement.setString(8, user.getImage());
            preparedStatement.setInt(9, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Method to delete a user from the database by ID
    public boolean deleteUser(int userId) {
        String deleteSQL = "DELETE FROM user WHERE id = ?";

        try (Connection connection = MyDataBase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Method to retrieve a user by ID
    public User getUserById(int userId) {
        String selectSQL = "SELECT * FROM user WHERE id = ?";

        try (Connection connection = MyDataBase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    // Method to fetch all users
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectSQL = "SELECT * FROM user";

        try (Connection connection = MyDataBase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                userList.add(extractUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }
        return userList;
    }

    // Helper method to extract User object from ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String roles = rs.getString("roles");
        String password = rs.getString("password");
        boolean isVerified = rs.getBoolean("is_verified");
        String username = rs.getString("username");
        String lastName = rs.getString("last_name");
        String phoneNumber = rs.getString("phone_number");
        String image = rs.getString("image");
        return new User(id, email, null, roles, password, isVerified, username, lastName, phoneNumber, image); // Presuming eventId can be null
    }
}