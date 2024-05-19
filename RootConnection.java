//Imports
import java.sql.*;

public class RootConnection {
    //You put the username and password in and check that they're okay
    private final static String url = "jdbc:mysql://localhost:3306/infocom";
    private final static String rootEntry = "root";
    private final static String rootPass = "341377PCSQ";

    public static Connection establishConnection()
    {
        try
        {
            Connection conn = DriverManager.getConnection(url, rootEntry, rootPass);
            return conn;
        }
        //Catch an error in the connection
        catch (SQLException connectFail)
        {
            System.out.println("Connection fail: " + connectFail.getMessage() + "\n");
        }
        System.out.println("Connection Failed");

        //Exit
        return null;
    }
}
