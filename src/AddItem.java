

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Servlet implementation class AddItem
 */
@WebServlet("/AddItem")
public class AddItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Connection connection;
	Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddItem() {
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
		
		System.out.println("add item Test!");
		System.out.println(request.getContentType());
		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String inputString = input.readLine();
		JSONObject inputJson = new JSONObject(inputString);
		JSONObject reply = new JSONObject();
		
		
		String username = inputJson.getString("userName");
		String title = inputJson.getString("title");
		String type = inputJson.getString("type");
		String subType = inputJson.getString("subType");
		String produceTime = inputJson.getString("proDate");
		String expiredDate = inputJson.getString("expDate");
		int shelfLife = inputJson.getInt("shelfLife");
		String userImageContent = inputJson.getString("image");
		//System.out.println(userImageContent);
		if(!userImageContent.equals("")){
			UUID imgId = UUID.randomUUID();
			
			byte[] bitmapArray;
			try {
				bitmapArray = Base64.decode(userImageContent);
				File imageFile = new File("/home/azureuser/Desktop/Asg2/images");
				if(!imageFile.exists())
					imageFile.mkdirs();
				
				FileOutputStream outStream = new FileOutputStream(imageFile.getPath() 
						+ "/"+ imgId + ".png");
				outStream.write(bitmapArray);
				outStream.close();
				
			} catch (Base64DecodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
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
			String insertItem = "INSERT INTO items(title, type, producetime, expiredDate, shelflife, photo, userid, subType) VALUES ('"
			                    + title + "','" + type +  "','" + produceTime + "','" + expiredDate + "','" + shelfLife 
			                    + "','" + imgId + "','" + userId + "','" + subType + "')";
			
			int rs = statement.executeUpdate(insertItem);
			if(rs > 0) {
				System.out.println("Insert new item seccuss!");
				reply.put("flag", "true");
				
			} else {
				System.out.println("Insert new item has some issue!");
				reply.put("flag", "false");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} else {
			System.out.println("The photo is null!");
		}
		
		System.out.println(reply.toString());
		response.getWriter().append(reply.toString());
		System.out.println("finish!");
	}

}
