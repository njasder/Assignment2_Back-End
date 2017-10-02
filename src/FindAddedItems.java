

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

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Servlet implementation class FindAddedItems
 */
@WebServlet("/FindAddedItems")
public class FindAddedItems extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Connection connection;
	Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindAddedItems() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
	public void init(ServletConfig config) throws ServletException {

		String hostName = "mobileserver2.database.windows.net";
		String dbName = "mydb";
		String user = "hxiao1@mobileserver2";
		String password = "11261218Xhy";
		String url = String.format(
				"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;selectmethod=cursor;loginTimeout=30;",
				hostName, dbName, user, password);
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
		
		System.out.println("Find all items Test!");
		System.out.println(request.getContentType());
		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String inputString = input.readLine();
		JSONObject inputJson = new JSONObject(inputString);
		JSONObject reply = new JSONObject();
		
		String username = inputJson.getString("userName");
		String subType = inputJson.getString("subType");
		
		String findUserId = "SELECT * FROM users WHERE username = '" + username + "'";
		ResultSet resultSet;
		int userId = 0;
		
		try {
			resultSet = statement.executeQuery(findUserId);
			if(resultSet.next()) {
				userId = resultSet.getInt("userid");
			} else {
				System.out.println("Cann't find the userid!");
			}
			
			String findAllItems = "SELECT * FROM items WHERE userid = '" + userId 
					+ "' AND subtype = '" + subType + "'";
			int i = 0;
			JSONArray items = new JSONArray();
			resultSet = statement.executeQuery(findAllItems);
			
			while(resultSet.next()) {
				String proDate = resultSet.getString("producetime");
				String expDate = resultSet.getString("expireddate");
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
				
				try {
					Date date2 = formatter.parse(expDate);
					Date now = new Date();
					int dDiff = dateDiff(now, date2);
					
					JSONObject item = new JSONObject();
					item.put("itemId", resultSet.getInt("itemid"));
					item.put("title", resultSet.getString("title"));
					item.put("proDate", proDate);
					item.put("expDate", expDate);
					item.put("type", resultSet.getString("type"));
					item.put("subType", resultSet.getString("subtype"));
					item.put("shelfLife", resultSet.getInt("shelflife"));
					item.put("remainingDays", dDiff);
					
					String imgId = resultSet.getString("photo");
					System.out.println("the imgid is:" + imgId );
					File file = new File("/home/azureuser/Desktop/Asg2/images/" + imgId + ".png");
					FileInputStream inputStream = new FileInputStream(file);
					byte[] bitmapArray = new byte[(int)file.length()];
					inputStream.read(bitmapArray);	
					inputStream.close();
					String userImageContent = Base64.encode(bitmapArray);
					item.put("image", userImageContent);
					items.put(i++, item);
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			reply.put("array", items);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(reply.toString());
		response.getWriter().append(reply.toString());
		System.out.println("The find added items is finished!");
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
