import java.sql.*;
import java.util.Arrays; //In order to check permissions
import java.util.Scanner;

public class Insert
{
    public static void insertData(String tableName, Connection conn, String[] perm)
    {

        Scanner input = new Scanner (System.in);

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

                //Tell user what's being displayed
                System.out.println("Below is the data types within the table:\n");

                //Get column count
                int columnCount = metaData.getColumnCount();

                //Print header
                System.out.printf("%-20s %-20s %-20s%n", "Column Name", "Column Type", "Display Size Limit");

                //Iterate through the columns and print the column name, data type, and display size
                for (int i = 1; i <= columnCount; i++)
                {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);
                    int displaySize = metaData.getColumnDisplaySize(i);

                    //Print the values with formatting for alignment
                    System.out.printf("%-20s %-20s %-20s%n", columnName, columnType, displaySize);
                }

                //Add space
                System.out.println();

                //Array to store information
                String[] insertData = new String[columnCount];

                //Now you need to ask for each data individually
                for (int i = 1; i <= columnCount; i++)
                {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);

                    //Ask for input
                    System.out.print("Enter new " + columnName + " (" + columnType + "): ");
                    insertData[i-1] = input.nextLine();
                }

                //Construct the SQL INSERT statement
                StringBuilder insertStatement = new StringBuilder("INSERT INTO " + tableName + " VALUES (");

                //Loop through each column
                for (int i = 0; i < columnCount; i++)
                {
                    //For handling 'varchars' and 'chars'
                    if (metaData.getColumnType(i + 1) == Types.VARCHAR || metaData.getColumnType(i + 1) == Types.CHAR)
                    {
                        insertStatement.append("'").append(insertData[i]).append("'");
                    }

                    //All other data types are numeric
                    else
                    {
                        insertStatement.append(insertData[i]);
                    }

                    if (i < columnCount - 1)
                    {
                        insertStatement.append(", ");
                    }

                }

                insertStatement.append(")");

                //Execute the INSERT statement
                PreparedStatement insertPreparedStatement = conn.prepareStatement(insertStatement.toString());
                insertPreparedStatement.executeUpdate();

                System.out.println("\nData inserted successfully.");

                //Audit
                Audit.writeToAuditQuery ("Attempted to insert data (Success)", insertStatement.toString());

            }

            catch (SQLException except)
            {
                System.out.println("\nERROR: " + except.getMessage());

                //Audit
                Audit.writeToAudit ("Attempted to insert data (Failed)");
            }

        }

        else
        {
            System.out.println("\nInvalid input: You are either not allowed to access this table, or the table doesn't exist.");
        }

    }

}
