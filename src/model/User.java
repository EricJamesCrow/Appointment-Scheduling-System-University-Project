package model;
/**
 * class for User. contains a constructor along with
 * getters and setters for userId, and userName.
 */
public class User {
    /**
     * id of the user
     */
    private int userId;
    /**
     * name of the user
     */
    private String userName;
    /**
     * constructor for User
     * @param userId
     * @param userName
     */
    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }
    /**
     * @param userId the id to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName the id to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
