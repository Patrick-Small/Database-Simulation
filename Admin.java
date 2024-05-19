import java.sql.*;
public class Admin {

    public static void editUser(String username, String newRole, Connection conn)
    {
        //Query to check if the user exists
        String queryCheckUser = "SELECT COUNT(*) AS userCount FROM LogIn_Credential WHERE username = ?";

        //Query to get the current role before the update
        String queryBefore = "SELECT role FROM LogIn_Credential WHERE username = ?";

        //Query to update the user's role
        String queryUpdate = "UPDATE LogIn_Credential SET role = ? WHERE username = ?";

        try
        {
            //Check if the user exists
            PreparedStatement pstmtCheckUser = conn.prepareStatement(queryCheckUser);
            pstmtCheckUser.setString(1, username);
            ResultSet rsCheckUser = pstmtCheckUser.executeQuery();

            if (rsCheckUser.next() && rsCheckUser.getInt("userCount") > 0) {
                //Get the current role before the update
                PreparedStatement pstmtBefore = conn.prepareStatement(queryBefore);
                pstmtBefore.setString(1, username);
                ResultSet rsBefore = pstmtBefore.executeQuery();
                String currentRole = "";

                if (rsBefore.next())
                {
                    currentRole = rsBefore.getString("role");
                    System.out.println("\nCurrent role of user " + username + " before update: " + currentRole);
                }

                //Update the user's role
                PreparedStatement pstmtUpdate = conn.prepareStatement(queryUpdate);
                pstmtUpdate.setString(1, newRole);
                pstmtUpdate.setString(2, username);
                pstmtUpdate.executeUpdate();

                //Display the updated role
                System.out.println("User " + username + " updated successfully.");
                System.out.println("New role: " + newRole);

                //Audit
                String auditQuery = "UPDATE LogIn_Credential SET role = " + newRole + " WHERE username = " + username;
                Audit.writeToAuditQuery("Attempted to edit user's role (Success)", auditQuery);
            }
            else
            {
                System.out.println("\nUser " + username + " does not exist. Cannot update role.");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error editing user: " + e.getMessage());

            //Audit
            Audit.writeToAudit ("Attempted to edit user's role (Failed)");
        }
    }

    public static void addUser(String newUser, String newPassword, String newRole, Connection conn) {
        //Query to check if the user already exists
        String queryCheckUser = "SELECT COUNT(*) AS userCount FROM LogIn_Credential WHERE username = ?";

        //Query to insert a new user
        String queryInsert = "INSERT INTO LogIn_Credential (username, password, role) VALUES (?, ?, ?)";

        //Query to retrieve the new user's information
        String queryRetrieve = "SELECT * FROM LogIn_Credential WHERE username = ?";

        try
        {
            //Check if the user already exists
            PreparedStatement pstmtCheckUser = conn.prepareStatement(queryCheckUser);
            pstmtCheckUser.setString(1, newUser);
            ResultSet rsCheckUser = pstmtCheckUser.executeQuery();

            if (rsCheckUser.next() && rsCheckUser.getInt("userCount") == 0)
            {
                //Insert the new user
                PreparedStatement pstmtInsert = conn.prepareStatement(queryInsert);
                pstmtInsert.setString(1, newUser);
                pstmtInsert.setString(2, newPassword);
                pstmtInsert.setString(3, newRole);
                pstmtInsert.executeUpdate();
                System.out.println("\nUser added successfully.");

                //Retrieve the new user's information
                PreparedStatement pstmtRetrieve = conn.prepareStatement(queryRetrieve);
                pstmtRetrieve.setString(1, newUser);
                ResultSet rsRetrieve = pstmtRetrieve.executeQuery();

                //Display the new user's information
                if (rsRetrieve.next())
                {
                    System.out.println("\nNew User Information:");
                    System.out.println("Username: " + rsRetrieve.getString("username"));
                    System.out.println("Password: " + rsRetrieve.getString("password"));
                    System.out.println("Role: " + rsRetrieve.getString("role"));
                }

                //Audit
                String auditQuery = "INSERT INTO LogIn_Credential (username, password, role) VALUES (" + newUser + ", " + newPassword + ", " + newRole + ")";
                Audit.writeToAuditQuery ("Attempted to add new user (Success)", auditQuery);

            }
            else
            {
                System.out.println("\nUser " + newUser + " already exists. Cannot add a duplicate user.");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error adding user: " + e.getMessage());

            //Audit
            Audit.writeToAudit ("Attempted to add new user (Failed)");
        }
    }
}
