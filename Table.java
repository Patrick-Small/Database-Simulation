import java.sql.*;
import java.util.Arrays; //In order to check permissions

public class Table
{
    public static void viewTable(String tableName, Connection conn, String[] perm)
    {
        //Ensure tableName is in array
        if (Arrays.asList(perm).contains(tableName))
        {
            try
            {
                //Prepare and execute the SQL query
                String query = "SELECT * FROM " + tableName;
                PreparedStatement prepState = conn.prepareStatement(query);
                ResultSet results = prepState.executeQuery();

                //Get metadata about the result set
                ResultSetMetaData metaData = results.getMetaData();
                int columnCount = metaData.getColumnCount();

                //Initialize an array to store the maximum length for each column
                int[] columnLengths = new int[columnCount];

                //Find the maximum length for each column
                for (int i = 1; i <= columnCount; i++)
                {
                    columnLengths[i - 1] = metaData.getColumnName(i).length();
                }

                //Iterate through the result set to find the maximum length of each column's value
                while (results.next())
                {
                    for (int i = 1; i <= columnCount; i++)
                    {
                        String value = results.getString(i);
                        if (value != null && value.length() > columnLengths[i - 1])
                        {
                            columnLengths[i - 1] = value.length();
                        }
                    }
                }

                //Display column names
                for (int i = 1; i <= columnCount; i++)
                {
                    System.out.printf("%-" + (columnLengths[i - 1] + 2) + "s", metaData.getColumnName(i));
                }
                System.out.println();

                //Display table data
                results.beforeFirst(); //Move the result set cursor back to the beginning
                while (results.next())
                {
                    for (int i = 1; i <= columnCount; i++)
                    {
                        String value = results.getString(i);
                        System.out.printf("%-" + (columnLengths[i - 1] + 2) + "s", value);
                    }
                    System.out.println();
                }

                //Audit
                String auditMessage = "Attempted to view table: " + tableName + " (Success)";
                Audit.writeToAuditQuery (auditMessage, query);

            }
            catch (SQLException except)
            {
                System.out.println("\nERROR: " + except.getMessage());
                Audit.writeToAudit("Attempted to view table (Failed)");
            }
        }
        else
        {
            System.out.println("\nInvalid input: You are either not allowed to access this table, or the table doesn't exist.");
        }
    }
}
