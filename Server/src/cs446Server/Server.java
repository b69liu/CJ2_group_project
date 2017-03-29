package cs446Server;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Server {

	static String myDriver = "com.mysql.jdbc.Driver";
    static String myUrl = "jdbc:mysql://localhost:8889/cs446";
    public static Connection conn = null;
	
    static List<String> subjectList = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException, SQLException {

		ServerSocket serverSocket = null;
		//try to open a server using socket
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }
        
        //try to connect to mysql
        try
	    {
	      Class.forName(myDriver);
	      conn = DriverManager.getConnection(myUrl, "root", "root");
	      
	      System.out.println("mysql is connected!");
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
        
        
        Courses courses = new Courses();
        courses.getUniqueSubjects();
        //courses.connectAPI();
        //courses.JSONtoSQL();

        Schedules schedules = new Schedules();
        schedules.JSONtoSQL();
        
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        
        
        
        
        
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                clientSocket.getInputStream()));
        String inputLine, outputLine;
        KnockKnockProtocol kkp = new KnockKnockProtocol();

        outputLine = kkp.processInput(null);
        out.println(outputLine);

        while ((inputLine = in.readLine()) != null) {
             outputLine = kkp.processInput(inputLine);
             out.println(outputLine);
             if (outputLine.equals("Bye."))
                break;
        }
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
	}

}
