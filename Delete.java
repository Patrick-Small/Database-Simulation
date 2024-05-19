import java.sql.*;
import java.util.Arrays; //In order to check permissions
import java.util.Scanner;

//This one just displays the table then asks which one the user wants to delete. That's it
public class Delete
{
    public static void deleteData(String tableName, Connection conn, String[] perm)
    {

        Scanner input = new Scanner (System.in);

        //Ensure tableName is in array
        if (Arrays.asList(perm).contains(tableName))
        {
            try
            {
                //Prompt the user for the id they want to delete
                System.out.print("\nEnter the id you want to delete: ");
                String idToDelete = input.nextLine();
                System.out.println();

                //Prepare and execute the SQL query to retrieve the row based on the provided id
                String query = "SELECT * FROM " + tableName + " WHERE id = ?";
                PreparedStatement prepState = conn.prepareStatement(query);
                prepState.setString(1, idToDelete);
                ResultSet results = prepState.executeQuery();

                ResultSetMetaData metaData = results.getMetaData();
                int columnCount = metaData.getColumnCount();

                //Check if the row exists
                if (!results.next())
                {
                    System.out.println("No rows found with the specified id.");
                    return;
                }

                //Display the current values for the selected row
                System.out.printf("%-20s %-20s %-20s%n", "Column Name", "Current Value", "Column Type");

                for (int i = 1; i <= columnCount; i++)
                {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);
                    String columnValue = results.getString(i);
                    System.out.printf("%-20s %-20s %-20s%n", columnName, columnValue, columnType);
                }

                //Confirm with the user before deletion
                System.out.print("\nDo you really want to delete this row? (yes/no): ");
                String confirmation = input.nextLine().toLowerCase();

                if (confirmation.equals("yes"))
                {
                    //Prepare and execute the SQL DELETE statement
                    String deleteStatement = "DELETE FROM " + tableName + " WHERE id = ?";
                    PreparedStatement deletePreparedStatement = conn.prepareStatement(deleteStatement);
                    deletePreparedStatement.setString(1, idToDelete);

                    //Execute the DELETE statement
                    int rowsAffected = deletePreparedStatement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        System.out.println("\nRow with id " + idToDelete + " deleted successfully.");

                        //Audit
                        String auditQuery = "DELETE FROM " + tableName + " WHERE id = " + idToDelete;
                        Audit.writeToAuditQuery ("Attempted to delete data (Success)", auditQuery);
                    }

                    else
                    {
                        System.out.println("\nNo rows found with the specified id.");
                    }

                }

                else
                {
                    System.out.println("Deletion cancelled.");
                }

            }

            catch (SQLException except)
            {
                System.out.println("\nERROR: " + except.getMessage());

                //Audit
                Audit.writeToAudit ("Attempted to delete data (Failed)");
            }

        }

        else
        {
            System.out.println("\nInvalid input: You are either not allowed to access this table, or the table doesn't exist.");
        }

    }

}
