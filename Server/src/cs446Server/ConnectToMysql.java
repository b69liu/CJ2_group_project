package cs446Server;

import java.sql.*;

public class ConnectToMysql {
	
	public void connect() {
		try
	    {
	      // create a mysql database connection
	      String myDriver = "com.mysql.jdbc.Driver";
	      String myUrl = "jdbc:mysql://localhost/cs446";
	      Class.forName(myDriver);
	      Connection conn = DriverManager.getConnection(myUrl, "root", "dnflskan");
	      
	      System.out.println("mysql is connected!");
	      // create a sql date object so we can use it in our INSERT statement
	      //Calendar calendar = Calendar.getInstance();
	      //java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

	      // the mysql insert statement
	      String query = " insert into terms (term)"
	        + " values (?)";

	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt    (1, 1551);

	      // execute the preparedstatement
	      preparedStmt.execute();
	      
	      conn.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
}
