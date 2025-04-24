package org.example.services;

import org.example.entities.SessionManager;
import org.example.entities.User;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class ServiceUser implements IService<User> {
    private Connection connection;

    public ServiceUser() {
        this.connection = MyDataBase.getInstance().getConnection();
        if (this.connection == null) {
            System.err.println("❌ Erreur : La connexion à la base de données est NULL !");
        }
    }

    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("🔄 Reconnexion à la base de données...");
            connection = MyDataBase.getInstance().getConnection();
        }
    }

    @Override
    public boolean ajouteru(User user) {
        try {
            checkConnection();

            String sql = "INSERT INTO user (email, roles, password, is_verified, username, lastname) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, "[\"ROLE_USER\"]");
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setBoolean(4, user.isVerified());
                preparedStatement.setString(5, user.getUsername());
                preparedStatement.setString(6, user.getLastName());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    sendVerificationEmail(user.getEmail());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            return false;
        }
        return false;
    }
    public boolean updatePassword(User user) {
        try {
            checkConnection();

            // SQL to update only the password
            String updateSQL = "UPDATE user SET password = ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                preparedStatement.setString(1, user.getPassword()); // New password
                preparedStatement.setInt(2, user.getId()); // User ID

                return preparedStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du mot de passe : " + e.getMessage());
            return false;
        }
    }


    private void sendVerificationEmail(String email) {
        String verificationLink = "http://localhost:8085/verify?email=" + email; // Change URL if needed
        final String senderEmail = "your-email@example.com"; // Change to your email
        final String senderPassword = "your-email-password"; // Change to your email application password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Vérification de votre compte");
            message.setText("Cliquez sur le lien pour vérifier votre compte : " + verificationLink);

            Transport.send(message);
            System.out.println("✅ Email de vérification envoyé à " + email);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    @Override
    public boolean modifieru(User user) {
        try {
            checkConnection();

            // Updated SQL to include the image field
            String updateSQL = "UPDATE user SET Username = ?, LastName = ?, email = ?, phonenumber = ?, image = ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPhoneNumber());

                // Check if image path exists and set it, otherwise set it as null
                if (user.getImage() != null && !user.getImage().isEmpty()) {
                    preparedStatement.setString(5, user.getImage());
                } else {
                    preparedStatement.setNull(5, java.sql.Types.VARCHAR);  // If no image, set it to null
                }

                preparedStatement.setInt(6, user.getId()); // Only setting 6 parameters, matching the query columns

                return preparedStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean supprimeru(int userId) {
        try {
            checkConnection();
            String deleteSQL = "DELETE FROM user WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                preparedStatement.setInt(1, userId);
                return preparedStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> afficher_t() throws SQLException {
        List<User> users = new ArrayList<>();
        try {
            checkConnection();
            String sql = "SELECT id, username, lastname, email, roles, password, blocked FROM user";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setLastName(rs.getString("lastname"));
                    user.setEmail(rs.getString("email"));
                    user.setRoles(rs.getString("roles"));
                    user.setPassword(rs.getString("password"));
                    SessionManager.getInstance().setCurrentUser(user); // ✅ Store full user in session
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }

    public boolean emailExists(String email) {
        try {
            checkConnection();
            String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de l'email : " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        try {
            checkConnection();
            String sql = "UPDATE user SET mdp = ? WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, newPassword);
                ps.setString(2, email);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
            return false;
        }
    }

    public boolean blockUser(int userId) {
        return updateUserBlockStatus(userId, true);
    }

    public boolean unblockUser(int userId) {
        return updateUserBlockStatus(userId, false);
    }

    private boolean updateUserBlockStatus(int userId, boolean block) {
        try {
            checkConnection();
            String sql = "UPDATE user SET blocked = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setBoolean(1, block);
                ps.setInt(2, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du blocage de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    public boolean isUserBlocked(String email) {
        try {
            checkConnection();
            String sql = "SELECT blocked FROM user WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                return rs.next() && rs.getBoolean("blocked");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification du blocage : " + e.getMessage());
            return false;
        }
    }

    public boolean verifyUser(String email) {
        return updateVerificationStatus(email, true);
    }

    public boolean isUserVerified(String email) {
        try {
            checkConnection();
            String sql = "SELECT is_verified FROM user WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                return rs.next() && rs.getBoolean("is_verified");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification du statut de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    private boolean updateVerificationStatus(String email, boolean status) {
        try {
            checkConnection();
            String sql = "UPDATE user SET is_verified = ? WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setBoolean(1, status);
                ps.setString(2, email);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la vérification : " + e.getMessage());
            return false;
        }
    }
}