//Imports
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Audit
{
    //Open the audit trail file
    private static final String FILE_PATH = "AuditTrail.txt";

    //Function to write a new line in the audit trail file
    public static void writeToAudit (String text)
    {
        //try to write to the file
        try (BufferedWriter writer = new BufferedWriter (new FileWriter (FILE_PATH, true)))
        {
            //Date and Time
            SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format (new Date());

            //Now write to the file
            writer.write(dateTime + " - " + text);
            writer.newLine();
            writer.newLine();
            //System.out.println("ARE YOU WORKING?"); //Test to make sure function was being run correctly

        }

        //This will be weird if the user gets this error since it's not really meant for them. Leave it just in case
        catch (IOException exception)
        {
            System.err.println("Error writing to file: " + exception.getMessage());
        }

    }

    //Same function but this time it tells what the user did in case it was an action
    public static void writeToAuditQuery (String text, String query)
    {
        //try to write to the file
        try (BufferedWriter writer = new BufferedWriter (new FileWriter (FILE_PATH, true)))
        {
            //Date and Time
            SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format (new Date());

            //Now write to the file
            writer.write(dateTime + " - " + text);
            writer.newLine();
            writer.write(dateTime + " - Query: " + query);
            writer.newLine();
            writer.newLine();
            //System.out.println("ARE YOU WORKING?"); //Test to make sure function was being run correctly

        }

        //This will be weird if the user gets this error since it's not really meant for them. Leave it just in case
        catch (IOException exception)
        {
            System.err.println("Error writing to file: " + exception.getMessage());
        }

    }
}