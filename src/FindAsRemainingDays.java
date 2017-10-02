

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Servlet implementation class FindAsRemainingDays
 */
@WebServlet("/FindAsRemainingDays")
public class FindAsRemainingDays extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Connection connection;
	Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindAsRemainingDays() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    public void init(ServletConfig config) throws ServletException {
    	
    	
  	  String hostName = "mobileserver2.database.windows.net";
        String dbName = "mydb";
        String user = "hxiao1@mobileserver2";
        String password = "11261218Xhy";
        String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;selectmethod=cursor;loginTimeout=30;", hostName, dbName, user, password);
        Connection connection = null;
        try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        try {
      	
			connection = DriverManager.getConnection(url);
			 String schema = connection.getSchema();
	          System.out.println("Successful connection - Schema: " + schema);

	          System.out.println("Query data example:");
	          System.out.println("=========================================");
	          statement = connection.createStatement();
	          System.out.println("Login: Succeeded connecting to the Database!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		System.out.println("find item via remaining days Test!");
		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String inputString = input.readLine();
		JSONObject inputJson = new JSONObject(inputString);
		JSONObject reply = new JSONObject();
		
		String username = inputJson.getString("userName");
		int remainingDays = inputJson.getInt("remainingDays");
		System.out.println("The remaining days is:" + remainingDays);
		ResultSet resultSet;
		String findUserId = "SELECT * FROM users WHERE username = '" + username + "'";
		
		try {
			int userId = 0;
			resultSet = statement.executeQuery(findUserId);
			if(resultSet.next()) {
				userId = resultSet.getInt("userid");
			}
			
			String findItems = "SELECT * FROM items WHERE userid = '" + userId + "'";
			resultSet = statement.executeQuery(findItems);
			JSONArray items = new JSONArray();
			int i = 0;
			while(resultSet.next()) {
				String expDate = resultSet.getString("expireddate");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date1 = null;
				try {
					date1 = formatter.parse(expDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Date now = new Date();
				int dDiff = -dateDiff(now, date1);
				System.out.println("The dDiff is:" + dDiff);
				
				if(dDiff < remainingDays) {
					JSONObject item = new JSONObject();
					item.put("itemId", resultSet.getInt("itemid"));
					item.put("title", resultSet.getString("title"));
					item.put("proDate", resultSet.getString("producetime"));
					item.put("expDate", expDate);
					item.put("type", resultSet.getString("type"));
					item.put("subType", resultSet.getString("subtype"));
					item.put("shelfLife", resultSet.getInt("shelflife"));
					item.put("remainingDays", dDiff);
					
					String imgId = resultSet.getString("photo");
					File file = new File("/home/azureuser/Desktop/Asg2/images/" + imgId + ".png");
					FileInputStream inputStream = new FileInputStream(file);
					byte[] bitmapArray = new byte[(int)file.length()];
					inputStream.read(bitmapArray);	
					inputStream.close();
					String userImageContent = Base64.encode(bitmapArray);
					item.put("image", userImageContent);
					items.put(i++, item);
					System.out.println("Add item!");
				}
			}
			reply.put("array", items);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getWriter().append(reply.toString());
		System.out.println("The find items as remaining days is finished!");
		//System.out.println(reply.toString());
	}
	
	
	public static int dateDiff(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) 
		            + cal1.get(Calendar.DST_OFFSET);
		long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) 
        + cal2.get(Calendar.DST_OFFSET);
		int hr1 = (int)(ldate1 / 3600000);
		int hr2 = (int)(ldate2 / 3600000);
		
		int days1 = hr1 / 24;
		int days2 = hr2 / 24;
		
		int dateDiff = days1 - days2;
		return dateDiff;
	}

}
