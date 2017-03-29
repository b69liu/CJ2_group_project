package cs446Server;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Schedules {
	private static final String BASE_URL = "https://api.uwaterloo.ca/v2/terms/";
    private static String term = "1171/";
    private static final String SCHEDULE_URL = "/schedule";
    private static final String KEY_URL = ".json?key=2d5402f20d57e1dd104101f9fa7dae27";
    

	HttpHandler sh;
	String jsonStr;
    
	public void connectAPI(String url) {
		sh = new HttpHandler();
        // Making a request to url and getting response
        jsonStr = sh.makeServiceCall(url);
        System.out.println("Response from url: " + jsonStr);
	}
	
    void JSONtoSQL() {
    	for (int j = 0; j < Server.subjectList.size(); j++) {
    		String url = BASE_URL+term+Server.subjectList.get(j)+SCHEDULE_URL+KEY_URL;
    		connectAPI(url);
    		
    		if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray dataArray = jsonObj.getJSONArray("data");
                    //JSONObject dataObj = dataArray.getJSONObject(0);
                    
                    //JSONObject location = classes.getJSONObject("location");

                    // looping through All Subjects
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject c = dataArray.getJSONObject(i);
                        JSONArray classArray = c.getJSONArray("classes");
                        JSONObject classObj = classArray.getJSONObject(0);
                        JSONObject dateObj = classObj.getJSONObject("date");
                        JSONObject locationObj = classObj.getJSONObject("location");
                        //JSONArray instructorsArray = classObj.getJSONArray("instructors");
                        /*
                        CourseModel cm = new CourseModel();
                        cm.setSubject(c.getString("subject"));
                        cm.setCatalog_number(c.getString("catalog_number"));
                        cm.setTitle(c.getString("title"));
                        courses.add(cm);
                        */
                        String query = " insert into schedules (subject, catalog_number, title, section, campus, building, room, start_time, end_time, weekdays )" + " values (?,?,?,?,?,?,?,?,?,?)";
                    	      // create the mysql insert preparedstatement
                	    PreparedStatement preparedStmt;
    					
                	    try {
    						preparedStmt = Server.conn.prepareStatement(query);
    						preparedStmt.setString(1, c.getString("subject"));
    						preparedStmt.setString(2, c.getString("catalog_number"));
    						preparedStmt.setString(3, c.getString("title"));
    						preparedStmt.setString(4, c.getString("section"));
    						//preparedStmt.setString(5, instructorsArray.get(0).toString());
    						preparedStmt.setString(5, c.getString("campus"));
    						preparedStmt.setString(6, (locationObj.has("building") && !locationObj.isNull("building")) ? locationObj.getString("building") : null);
    						preparedStmt.setString(7, (locationObj.has("room") && !locationObj.isNull("room")) ? locationObj.getString("room") : null);
    						preparedStmt.setString(8, (dateObj.has("start_time") && !dateObj.isNull("start_time")) ? dateObj.getString("start_time") : null);
    						preparedStmt.setString(9, (dateObj.has("end_time") && !dateObj.isNull("end_time")) ? dateObj.getString("end_time") : null);
    						preparedStmt.setString(10, (dateObj.has("weekdays") && !dateObj.isNull("weekdays")) ? dateObj.getString("weekdays") : null);

                    	      // execute the preparedstatement
                    	    preparedStmt.execute();
    					} catch (SQLException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                    	//System.out.println(i + " " + c.getString("subject"));
                    }
                } catch (final JSONException e) {
                    System.out.println("Json parsing error: " + e.getMessage());
                }
            } else {
                System.out.println("Couldn't get json from server.");
            }
    	}
    	
    }
    
}
