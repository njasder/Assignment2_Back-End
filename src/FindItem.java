

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

import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Servlet implementation class FindItem
 */
@WebServlet("/FindItem")
public class FindItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Connection connection;
	Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindItem() {
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
		System.out.println("Find item Test!");
		System.out.println(request.getContentType());
		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String inputString = input.readLine();
		JSONObject inputJson = new JSONObject(inputString);
		JSONObject reply = new JSONObject();
		
		
		int itemId = inputJson.getInt("itemId");
		String select = "SELECT * FROM items WHERE itemid = '" + itemId + "'";
		try {
			ResultSet resultSet = statement.executeQuery(select);
			if(resultSet.next()) {
				String proDate = resultSet.getString("producetime");
				String expDate = resultSet.getString("expireddate");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date2;
				try {
					date2 = formatter.parse(expDate);
					Date now = new Date();
					int dDiff = dateDiff(now, date2);
					
					reply.put("title", resultSet.getString("title"));
					reply.put("proDate", proDate);
					reply.put("expDate", expDate);
					reply.put("type", resultSet.getString("type"));
					reply.put("subType", resultSet.getString("subtype"));
					reply.put("shelfLife", resultSet.getInt("shelflife"));
					reply.put("remainingDays", dDiff);
					
					String imgId = resultSet.getString("photo");
					File file = new File("/home/azureuser/Desktop/Asg2/images/" + imgId + ".png");
					FileInputStream inputStream = new FileInputStream(file);
					byte[] bitmapArray = new byte[(int)file.length()];
					inputStream.read(bitmapArray);	
					inputStream.close();
					String userImageContent = Base64.encode(bitmapArray);
					reply.put("image", userImageContent);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getWriter().append(reply.toString());
		System.out.println("The find item is finished!");
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
