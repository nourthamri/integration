package org.example.services;

import org.example.entities.SessionManager;
import org.example.model.LoginHistory;
import org.example.entities.User;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryService {

    private final Connection connection;

    public LoginHistoryService() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    public void save(LoginHistory loginHistory) {
        String sql = "INSERT INTO login_history (user_id, login_time, ip_address, device_info) VALUES (?, ?, ?, ?)";
        User currentUser = SessionManager.getInstance().getCurrentUser();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(loginHistory.getLoginTime()));
            pstmt.setString(3, loginHistory.getIpAddress());
            pstmt.setString(4, loginHistory.getDeviceInfo());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<LoginHistory> getAll() {
        List<LoginHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE user_id = ? ORDER BY login_time DESC";
        User currentUser = SessionManager.getInstance().getCurrentUser();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LoginHistory history = new LoginHistory();
                history.setLoginTime(rs.getTimestamp("login_time").toLocalDateTime());
                history.setIpAddress(rs.getString("ip_address"));
                history.setDeviceInfo(rs.getString("device_info"));
                list.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
