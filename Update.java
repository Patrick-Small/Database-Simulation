import java.sql.*;
import java.util.Arrays; //In order to check permissions
import java.util.Scanner;

//This class needs to display the table then ask which the user would like to update, then update it.
public class Update
{
    public static void updateData(String tableName, Connection conn, String[] perm)
    {

        Scanner input = new Scanner (System.in);

        //Ensure tableName is in array
        if (Arrays.asList(perm).contains(tableName))
        {
            try
            {
                //Prompt the user for the id they want to update
                System.out.print("\nEnter the id you want to update: ");
                String idToUpdate = input.nextLine();
                System.out.println();

                //Prepare and execute the SQL query to retrieve the row based on the provided id
                String query = "SELECT * FROM " + tableName + " WHERE id = ?";
                PreparedStatement prepState = conn.prepareStatement(query);
                prepState.setString(1, idToUpdate);
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
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);
                    String columnValue = results.getString(i);
                    System.out.printf("%-20s %-20s %-20s%n", columnName, columnValue, columnType);
                }

                //Add space
                System.out.println();

                //Prompt the user to update each column value (excluding id)
                String[] updateData = new String[columnCount];
                for (int i = 2; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    System.out.print("Enter new " + columnName + ": ");
                    updateData[i - 2] = input.nextLine();
                }

                //Construct the SQL UPDATE statement for the selected row
                StringBuilder updateStatement = new StringBuilder("UPDATE " + tableName + " SET ");
                for (int i = 2; i <= columnCount; i++) {
                    updateStatement.append(metaData.getColumnName(i)).append(" = ?");
                    if (i < columnCount) {
                        updateStatement.append(", ");
                    }
                }
                updateStatement.append(" WHERE id = ?");

                //Set the new values in the prepared statement
                PreparedStatement updatePreparedStatement = conn.prepareStatement(updateStatement.toString());
                for (int i = 1; i <= columnCount - 1; i++) {
                    updatePreparedStatement.setString(i, updateData[i - 1]);
                }
                //Set the id for the WHERE clause
                updatePreparedStatement.setString(columnCount, idToUpdate);

                //Execute the UPDATE statement
                updatePreparedStatement.executeUpdate();

                System.out.println("\nRow with id " + idToUpdate + " updated successfully.");

                //Audit
                String auditQuery = "UPDATE SE_Data SET name = " + updateData[0] + ", projectname = " + updateData[1] + ", supervisor = " + updateData[2] + ", deadline = " + updateData[3] + " WHERE id = " + idToUpdate;
                Audit.writeToAuditQuery ("Attempted to update data (Success)", auditQuery);

            }

            catch (SQLException except)
            {
                System.out.println("\nERROR: " + except.getMessage());

                //Audit
                Audit.writeToAudit ("Attempted to update data (Failed)");
            }

        }

        else
        {
            System.out.println("\nInvalid input: You are either not allowed to access this table, or the table doesn't exist.");
        }

    }

}



            /*
            BELOW IS PREVIOUS CODE THAT DID NOT WORK. IT USED A ROW IDENTIFICATION SYSTEM THAT
            ASKED THE USER WHAT ROW THEY WANTED TO UPDATE AND THEN TRIED TO ALTER IT. IT DID NOT
            WORK, AND INSTEAD WAS ALTERING WHATEVER TUPLE WAS MOST SIMILAR TO THE ID YOU ENTERED.

            EVEN THOUGH I LIKELY COULD HAVE GOTTEN IT TO WORK, I HAD REALIZED AT THIS POINT THAT
            EXPLICITLY ASKING FOR THE ID YOU WANT TO EDIT AND JUST ALTERING ALL THE OTHER ASPECT
            WAS MUCH MORE DOABLE AND EASIER TO IMPLEMENT, SO I OPTED TO START AGAIN AND USE THAT
            METHOD, WHICH WORKED A LOT BETTER.
            */

            /*try
            {
                //Execute a SELECT query to retrieve the number of rows in the table
                String rowCountQuery = "SELECT COUNT(*) AS row_count FROM " + tableName;
                PreparedStatement rowCountStatement = conn.prepareStatement(rowCountQuery);
                ResultSet rowCountResult = rowCountStatement.executeQuery();
                rowCountResult.next(); //Move to the first row
                int rowCount = rowCountResult.getInt("row_count");

                //Display the rows to the user
                System.out.println("\nThere are " + rowCount + " rows in the table.");

                //Ask the user which row they want to update
                System.out.print("\nEnter the row number you want to update (1-" + rowCount + "): ");
                int selectedRow = input.nextInt();

                input.nextLine();

                //Validate the entered row number
                if (selectedRow < 1 || selectedRow > rowCount)
                {
                    System.out.println("\nInvalid row number. Please enter a number between 1 and " + rowCount + ".");
                    return; //Exit the method if the input is invalid
                }

                //Prepare and execute the SQL query
                String query = "SELECT * FROM " + tableName;
                PreparedStatement prepState = conn.prepareStatement(query);
                ResultSet results = prepState.executeQuery();

                //Get metadata about the result set
                ResultSetMetaData metaData = results.getMetaData();

                //Tell user what's being displayed
                System.out.println("\nBelow is the data types within the table:\n");

                //Get column count
                int columnCount = metaData.getColumnCount();

                //Array to store original data before the update
                String[] originalData = new String[columnCount];

                //Check if there are any rows in the result set
                if (results.next()) {
                    //Retrieve the original data
                    for (int i = 1; i <= columnCount; i++)
                    {
                        originalData[i - 1] = results.getString(i);
                    }
                }

                else
                {
                    System.out.println("No rows found in the table.");
                    return; //Exit the method if there are no rows
                }


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

                System.out.println("");

                //Array to store information
                String[] updateData = new String[columnCount];

                //Now you need to ask for each data individually
                for (int i = 1; i <= columnCount; i++)
                {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);

                    //Ask for input
                    System.out.print("Enter new " + columnName + " (" + columnType + "): ");
                    String userInput = input.nextLine();
                    updateData[i - 1] = userInput;
                }

                // Construct the SQL UPDATE statement for the selected row
                StringBuilder updateStatement = new StringBuilder("UPDATE " + tableName + " SET ");
                for (int i = 1; i <= columnCount; i++) {
                    // For handling 'varchars' and 'chars'
                    if (metaData.getColumnType(i) == Types.VARCHAR || metaData.getColumnType(i) == Types.CHAR) {
                        updateStatement.append(metaData.getColumnName(i)).append(" = '").append(updateData[i - 1]).append("'");
                    } else {
                        // All other data types are numeric; no single quotes needed
                        updateStatement.append(metaData.getColumnName(i)).append(" = ").append(updateData[i - 1]);
                    }

                    if (i < columnCount) {
                        updateStatement.append(", ");
                    }
                }

                // Identify the row to update based on the primary key (id)
                updateStatement.append(" WHERE id = '").append(originalData[0]).append("'");

                // Execute the UPDATE statement
                PreparedStatement updatePreparedStatement = conn.prepareStatement(updateStatement.toString());
                updatePreparedStatement.executeUpdate();

                System.out.println("\nRow " + selectedRow + " updated successfully.");


            }

            catch (SQLException except)
            {
                System.out.println("\nERROR: " + except.getMessage());
            }*/