package cs446Server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs446Server.Model.CourseModel;

public class Courses {
	private static final String courseAPIurl = "https://api.uwaterloo.ca/v2/terms/1171/courses.json?key=2d5402f20d57e1dd104101f9fa7dae27";
	
	//List<String> subjectList = new ArrayList<String>();
	HttpHandler sh;
	String jsonStr;
	
	public void getUniqueSubjects() throws SQLException {
		String query = "select distinct subject from courses";
		
		Statement st = Server.conn.createStatement();
	      
	      // execute the query, and get a java resultset
	      ResultSet rs = st.executeQuery(query);
	      
	      // iterate through the java resultset
	      while (rs.next())
	      {
	        String subject = rs.getString("subject");
	        
	        Server.subjectList.add(subject);
	        // print the results
	        //System.out.format("%s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
	      }
	      st.close();
		
	}
	
	public void connectAPI() {
		sh = new HttpHandler();
        // Making a request to url and getting response
        jsonStr = sh.makeServiceCall(courseAPIurl);
        System.out.println("Response from url: " + jsonStr);
	}
	
	public void JSONtoSQL() {
		if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray subjectArray = jsonObj.getJSONArray("data");

                // looping through All Subjects
                for (int i = 0; i < subjectArray.length(); i++) {
                    JSONObject c = subjectArray.getJSONObject(i);
                    /*
                    CourseModel cm = new CourseModel();
                    cm.setSubject(c.getString("subject"));
                    cm.setCatalog_number(c.getString("catalog_number"));
                    cm.setTitle(c.getString("title"));
                    courses.add(cm);
                    */
                    String query = " insert into courses (subject, catalog_number, title, term )" + " values (?,?,?,?)";
                	      // create the mysql insert preparedstatement
            	    PreparedStatement preparedStmt;
					
            	    try {
						preparedStmt = Server.conn.prepareStatement(query);
						preparedStmt.setString(1, c.getString("subject"));
						preparedStmt.setString(2, c.getString("catalog_number"));
						preparedStmt.setString(3, c.getString("title"));
						preparedStmt.setInt(4, 1171);

                	      // execute the preparedstatement
                	    preparedStmt.execute();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	      
                }
            } catch (final JSONException e) {
                System.out.println("Json parsing error: " + e.getMessage());
            }
        } else {
            System.out.println("Couldn't get json from server.");
        }
	}
	
}
