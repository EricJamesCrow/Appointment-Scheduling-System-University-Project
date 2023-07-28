package helper;

import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class containing a user object and a login method
 */
public class Authentication {
    /**
     * initialize currentUser as null
     */
    private static User currentUser = null;
    /**
     * getter for currentUser
     * @return currentUser
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    /**
     * method for logging in using the provided username and password.
     * Sets currentUser to a new user object if login is successful.
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
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
            currentUser = user;
        } else {
        }
        return user;
    }
}
