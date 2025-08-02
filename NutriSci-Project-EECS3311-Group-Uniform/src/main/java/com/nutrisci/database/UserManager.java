package com.nutrisci.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import com.nutrisci.model.Gender;
import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalType;
import com.nutrisci.model.Units;
import com.nutrisci.model.User;

/**
 * UserManager handles all database operations for users.
 */
public class UserManager {
    /**
     * Saves a new user with password hashing and goal information.
     * helped by AI
     */
    public boolean saveUser(User user, Connection connection) {
        try {
            System.out.println("DatabaseManager.saveUser() called for user: " + user.getEmail());
            
            String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            long userID = Instant.now().getEpochSecond();

            System.out.println("Generated userID: " + userID);
            System.out.println("User goal: " + (user.getGoal() != null ? user.getGoal().toString() : "No goal"));

            String saveUserSQL = "INSERT INTO Meal_User (UserID, Username, Email, UserPassword, Gender, DoB, Height, Weight, Units, GoalType, GoalDescription) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            System.out.println("Executing SQL: " + saveUserSQL);

            try (PreparedStatement ps = connection.prepareStatement(saveUserSQL)) {
                ps.setLong(1, userID);
                ps.setString(2, user.getName());
                ps.setString(3, user.getEmail());
                ps.setString(4, password);
                ps.setString(5, user.getGender().name());
                ps.setDate(6, java.sql.Date.valueOf(user.getDateOfBirth()));
                ps.setDouble(7, user.getHeight());
                ps.setDouble(8, user.getWeight());
                ps.setString(9, user.getUnits().name());
                ps.setString(10, user.getGoal().getType().name());
                ps.setString(11, user.getGoal().toString());

                System.out.println("Executing prepared statement...");
                ps.executeUpdate();
                
                // Set the user ID on the user object
                user.setId(userID);
                
                System.out.println("User saved successfully with ID: " + userID);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in saveUser: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("General error in saveUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing user profile with change tracking.
     * @param user User object
     * @return true if successful, false if error
     */
    public boolean updateUserProfile(User user, Connection connection) {
        String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        long userID = user.getId();

        String updateUserSQL = "UPDATE Meal_User SET " +
        "UserPassword=?, " +
        "Gender=?, " +
        "DoB=?, " +
        "Height=?, " +
        "Weight=?, " +
        "Units=?, " +
        "GoalType=?, " +
        "GoalDescription=? " +
        "WHERE UserID=?";

        try (PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.setString(1, password);
            ps.setString(2, user.getGender().name());
            ps.setDate(3, java.sql.Date.valueOf(user.getDateOfBirth()));
            ps.setDouble(4, user.getHeight());
            ps.setDouble(5, user.getWeight());
            ps.setString(6, user.getUnits().name());
            ps.setString(7, user.getGoal().getType().name());
            ps.setString(8, user.getGoal().toString());
            ps.setLong(9, userID);
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a user is already using an email in the database
     * @param email The provided email
     * @return true if a user exists, otherwise false if the user does not exist
     */
    public boolean checkIfUserExists(String email, Connection connection) {
        String checkForUser = "Select COUNT(*) as NUM from Meal_User where Email=?";

        try (PreparedStatement ps = connection.prepareStatement(checkForUser)) {
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            int number = 0;

            if (rs.next()) {
                number = rs.getInt("NUM");
            }

            if (number > 0) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticates user credentials and returns User object if valid.
     * @param email User email
     * @param password Plain text password
     * @return User object if valid, null if not
     */
    public User authenticateUser(String email, String password, Connection connection) {
        String checkForUser = "Select * from Meal_User where Email=?";

        try (PreparedStatement ps = connection.prepareStatement(checkForUser)) {
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("No user found in database for email: " + email);
                return null; // No matching user
            }

            // Get the hashed password from database
            String hashedPasswordFromDB = rs.getString("UserPassword");
            
            // Check if the plain text password matches the hashed password
            if (!BCrypt.checkpw(password, hashedPasswordFromDB)) {
                System.out.println("Password mismatch for email: " + email);
                return null; // Password doesn't match
            }

            User user = new User();
            user.setId(rs.getLong("UserID")); // Set User ID from database
            user.setName(rs.getString("Username"));
            user.setEmail(rs.getString("Email"));
            user.setPassword(password);
            user.setGender(Gender.valueOf(rs.getString("Gender")));
            user.setDateOfBirth(rs.getDate("DoB").toLocalDate());
            user.setHeight(rs.getDouble("Height"));
            user.setWeight(rs.getDouble("Weight"));
            user.setUnits(Units.valueOf(rs.getString("Units")));

            /**
             * Parse goal information from database
             */
            String goalDescription = rs.getString("GoalDescription");
            GoalType goalType = GoalType.valueOf(rs.getString("GoalType"));
            
            System.out.println("Loading goal from database:");
            System.out.println("  GoalType: " + rs.getString("GoalType"));
            System.out.println("  GoalDescription: " + goalDescription);
            
            // Parse the goal description to extract direction and percentage
            // Format: "Decrease Calories by 10%" or "Increase Protein by 5%"
            boolean increase = true; // default
            int percent = 5; // default
            
            if (goalDescription != null && !goalDescription.isEmpty()) {
                // Check if it's "Decrease" or "Increase"
                if (goalDescription.toLowerCase().contains("decrease")) {
                    increase = false;
                } else if (goalDescription.toLowerCase().contains("increase")) {
                    increase = true;
                }
                
                // Extract percentage using regex
                Matcher matcher = Pattern.compile("\\d+").matcher(goalDescription);
                if (matcher.find()) {
                    percent = Integer.parseInt(matcher.group());
                }
            }
            
            System.out.println("  Parsed goal - Type: " + goalType + ", Direction: " + (increase ? "Increase" : "Decrease") + ", Percent: " + percent);
            
            // Create goal with proper direction and percentage
            Goal userGoal = new Goal(goalType, increase, percent);
            user.setGoal(userGoal);
            
            System.out.println("  Final goal object: " + userGoal.toString());

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
