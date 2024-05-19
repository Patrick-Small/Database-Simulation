//Imports
import java.sql.*;
import java.util.Scanner;

public class LogIn
{
    public static void main(String args[])
    {
        //Declarations
        String username;
        String password;
        String role = null;
        Boolean didLogin = false;
        String roleInput = null;
        String tempUsername = null;
        String tempPassword = null;
        String tempRole = null;
        String tempTableName = null;
        Scanner input = new Scanner (System.in);
        Boolean exitProgram = false;
        String[] adminPermR = {"LogIn_Credential", "SE_Data", "Emp_SE", "HR_Data", "Emp_HR", "PR_Data", "Emp_PR"};
        String[] adminPermRW = {"SE_Data", "Emp_SE", "HR_Data", "Emp_HR", "PR_Data", "Emp_PR"};
        //No SEPermR needed (all tables SE can read, they can also write to)
        String[] SEPermRW = {"SE_Data", "Emp_SE"};
        String[] HRPermR = {"Emp_SE", "HR_Data", "Emp_HR", "Emp_PR"};
        String[] HRPermRW = {"HR_Data", "Emp_HR", "Emp_PR"};
        String[] PRPermR = {"Emp_SE", "Emp_HR", "PR_Data", "Emp_PR"};
        String[] PRPermRW = {"PR_Data", "Emp_PR"};
        String[] generalPermR = {"Emp_SE", "Emp_HR", "Emp_PR"};
        //No generalPermRW needed (general cannot write to any tables)

        //Establish connection
        Connection conn = RootConnection.establishConnection();

        //Loop to make sure if log in didn't work, the user can try again
        while(!didLogin)
        {
            //Talk to the user
            System.out.println("\nPlease log in with your username and password");
            System.out.print("Username: ");
            username = input.nextLine();
            System.out.print("Password: ");
            password = input.nextLine();

            //Check login information
            try
            {
                //Finding the password in database
                PreparedStatement prepState = conn.prepareStatement("select role from LogIn_Credential where username = ? and password = ?");
                prepState.setString(1, username);
                prepState.setString(2, password);
                ResultSet results = prepState.executeQuery();
                if (!results.next())
                {
                    didLogin = false;
                    System.out.println("\nLogin not found, please try again");

                    //Audit
                    Audit.writeToAudit ("Login attempted (Failed)");
                }
                else
                {
                    didLogin = true;
                    role = results.getString("role");
                    //System.out.println(role); //Test to make sure it executed correctly

                    //Audit
                    String auditMessage = "User with username: " + username + " logged in (Success)";
                    Audit.writeToAudit(auditMessage);
                }

            }

            catch(SQLException except)
            {
                System.out.println("\nERROR: " + except.getMessage());
            }

        }

        //Log in successful, see what role it is and bring to right place
        if (didLogin)
        {
            System.out.println ("\nLogin successful");

            //Admin permissions
            if (role.equals("Admin"))
            {
                //Welcome!
                System.out.println("\nWelcome to the Admin screen!");

                //Audit
                Audit.writeToAudit ("Admin Login (Success)");

                //Loop until user wants to exit
                while(!exitProgram)
                {
                    //Ask the user what they want to do
                    System.out.print("\nDo you want to:\n\nEdit an existing user's role [edit]\nAdd a new user [add]\n");
                    System.out.print("View a table [view]\nInsert new data [insert]\nUpdate existing data [update]\n");
                    System.out.println("Delete existing data [delete]\nOr exit the program [exit]?");
                    roleInput = input.nextLine();

                    //Execute action based on input
                    switch (roleInput)
                    {
                        case ("edit"):
                            //Ask for inputs
                            System.out.print("\nEnter the username: ");
                            tempUsername = input.nextLine();
                            System.out.print("Enter the role you would like the user to have: ");
                            tempRole = input.nextLine();
                            Admin.editUser(tempUsername, tempRole, conn);
                            break;

                        case ("add"):
                            //Ask for inputs
                            System.out.print("\nEnter new username: ");
                            tempUsername = input.nextLine();
                            System.out.print("Enter password for user: ");
                            tempPassword = input.nextLine();
                            System.out.print("Enter role for user: ");
                            tempRole = input.nextLine();
                            Admin.addUser(tempUsername, tempPassword, tempRole, conn);
                            break;

                        case ("view"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nLogIn_Credential\nSE_Data\nEmp_SE\nHR_Data\nEmp_HR\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to view: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, adminPermR);
                            break;

                        case ("insert"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nSE_Data\nEmp_SE\nHR_Data\nEmp_HR\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to insert new data into: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Insert.insertData(tempTableName, conn, adminPermRW);
                            break;

                        case ("update"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nLogIn_Credential\nSE_Data\nEmp_SE\nHR_Data\nEmp_HR\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to update existing data within: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, adminPermR);
                            Update.updateData(tempTableName, conn, adminPermR);
                            break;

                        case ("delete"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nLogIn_Credential\nSE_Data\nEmp_SE\nHR_Data\nEmp_HR\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to delete existing data from: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, adminPermR);
                            Delete.deleteData(tempTableName, conn, adminPermR);
                            break;

                        case ("exit"):
                            //Stops the while loop
                            exitProgram = true;
                            break;

                        default:
                            //Invalid input
                            System.out.println("Invalid input, try again");
                    }

                }

            }

            //SE permissions
            else if (role.equals("SE"))
            {

                //Audit
                Audit.writeToAudit ("SE Login (Success)");

                while(!exitProgram)
                {
                    //Ask whether user what they want to do
                    System.out.print("\nDo you want to:\n\nView a table [view]\nInsert new data [insert]\nUpdate existing data [update]\n");
                    System.out.println("Delete existing data [delete]\nOr exit the program [exit]? ");
                    roleInput = input.nextLine();

                    //Execute action based on input
                    switch (roleInput)
                    {

                        case ("view"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nSE_Data\nEmp_SE\n");
                            System.out.print("Enter the name of the table you'd like to view: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, SEPermRW);
                            break;

                        case ("insert"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nSE_Data\nEmp_SE\n");
                            System.out.print("Enter the name of the table you'd like to insert new data into: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Insert.insertData(tempTableName, conn, SEPermRW);
                            break;

                        case ("update"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nSE_Data\nEmp_SE\n");
                            System.out.print("Enter the name of the table you'd like to update existing data within: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, SEPermRW);
                            Update.updateData(tempTableName, conn, SEPermRW);
                            break;

                        case ("delete"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nSE_Data\nEmp_SE\n");
                            System.out.print("Enter the name of the table you'd like to delete existing data from: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, SEPermRW);
                            Delete.deleteData(tempTableName, conn, SEPermRW);
                            break;

                        case ("exit"):
                            //Stops the while loop
                            exitProgram = true;
                            break;

                        default:
                            //Invalid input
                            System.out.println("Invalid input, try again");
                    }

                }

            }

            //HR permissions
            else if (role.equals("HR"))
            {

                //Audit
                Audit.writeToAudit ("HR Login (Success)");

                while(!exitProgram)
                {
                    //Ask whether user what they want to do
                    System.out.print("\nDo you want to:\n\nView a table [view]\nInsert new data [insert]\nUpdate existing data [update]\n");
                    System.out.println("Delete existing data [delete]\nOr exit the program [exit]? ");
                    roleInput = input.nextLine();

                    //Execute action based on input
                    switch (roleInput)
                    {

                        case ("view"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nEmp_SE\nHR_Data\nEmp_HR\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to view: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, HRPermR);
                            break;

                        case ("insert"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nHR_Data\nEmp_HR\n");
                            System.out.print("Enter the name of the table you'd like to insert new data into: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Insert.insertData(tempTableName, conn, HRPermRW);
                            break;

                        case ("update"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nHR_Data\nEmp_HR\n");
                            System.out.print("Enter the name of the table you'd like to update existing data within: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, HRPermRW);
                            Update.updateData(tempTableName, conn, HRPermRW);
                            break;

                        case ("delete"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nHR_Data\nEmp_HR\n");
                            System.out.print("Enter the name of the table you'd like to delete existing data from: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, HRPermRW);
                            Delete.deleteData(tempTableName, conn, HRPermRW);
                            break;

                        case ("exit"):
                            //Stops the while loop
                            exitProgram = true;
                            break;

                        default:
                            //Invalid input
                            System.out.println("Invalid input, try again");
                    }

                }

            }

            //PR permissions
            else if (role.equals("PR"))
            {

                //Audit
                Audit.writeToAudit ("PR Login (Success)");

                while(!exitProgram)
                {
                    //Ask whether user what they want to do
                    System.out.print("\nDo you want to:\n\nView a table [view]\nInsert new data [insert]\nUpdate existing data [update]\n");
                    System.out.println("Delete existing data [delete]\nOr exit the program [exit]? ");
                    roleInput = input.nextLine();

                    //Execute action based on input
                    switch (roleInput)
                    {

                        case ("view"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nEmp_SE\nEmp_HR\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to view: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, PRPermR);
                            break;

                        case ("insert"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to insert new data into: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Insert.insertData(tempTableName, conn, PRPermRW);
                            break;

                        case ("update"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to update existing data within: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, PRPermRW);
                            Update.updateData(tempTableName, conn, PRPermRW);
                            break;

                        case ("delete"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nPR_Data\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to delete existing data from: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, PRPermRW);
                            Delete.deleteData(tempTableName, conn, PRPermRW);
                            break;

                        case ("exit"):
                            //Stops the while loop
                            exitProgram = true;
                            break;

                        default:
                            //Invalid input
                            System.out.println("Invalid input, try again");
                    }

                }

            }

            //General permissions
            else
            {

                //Audit
                Audit.writeToAudit ("General Login (Success)");

                while(!exitProgram)
                {
                    //Ask whether user wants to view a page or exit
                    System.out.println("\nDo you want to:\n\nView a table [view], or exit the program [exit]? ");
                    roleInput = input.nextLine();

                    //Execute action based on input
                    switch (roleInput)
                    {

                        case ("view"):
                            //Ask for inputs
                            System.out.println("\nTables available: ");
                            System.out.println("\nEmp_SE\nEmp_HR\nEmp_PR\n");
                            System.out.print("Enter the name of the table you'd like to view: ");
                            tempTableName = input.nextLine();
                            System.out.println();
                            Table.viewTable(tempTableName, conn, generalPermR);
                            break;

                        case ("exit"):
                            //Stops the while loop
                            exitProgram = true;
                            break;

                        default:
                            //Invalid input
                            System.out.println("Invalid input, try again");
                    }

                }

            }

        }

        //Say goodbye!
        System.out.println ("\nThank you for using this database! Logging out now.");

        //Audit
        Audit.writeToAudit ("User exited database, program closed (Success)");

    }

}