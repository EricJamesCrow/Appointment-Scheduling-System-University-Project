package helper;

import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authentication {

    public static User login(String userName, String password) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            int userId = rs.getInt("User_ID");
            String name = rs.getString("User_Name");
            user = new User(userId, name);
        } else {
        }
        return user;
    }
}
