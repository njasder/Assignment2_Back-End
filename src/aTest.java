

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class aTest
 */
@WebServlet("/aTest")
public class aTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	//private static String dbAddress = "jdbc:mysql://rtfdb.cti98hk7xcqr.ap-southeast-2.rds.amazonaws.com:3306/realtimedb?user=xduan&password=881024dx";
	//private static String dbAddress = "jdbc:sqlserver://mobileserver2.database.windows.net:1433;database=mydb;user=hxiao1@mobileserver2;password=11261218Xhy;loginTimeout=30;";
	
	
	Connection connection;
	Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public aTest() {
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
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		System.out.println("Login Test!");
		System.out.println(request.getContentType());
		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String inputString = input.readLine();
		JSONObject inputJson = new JSONObject(inputString);
		
		String password = inputJson.getString("passWord");
		String username = inputJson.getString("userName");
		String sql = "SELECT * FROM users WHERE username = '" + username + "'";
		JSONObject reply = new JSONObject();
		
		try {
			ResultSet resultSet  = statement.executeQuery(sql);
			if(resultSet.next()){
				if(password.equals(resultSet.getString("Password"))){
					reply.put("flag", "success");
					System.out.println("success");
					
				}else{
					reply.put("flag", "fail");
					reply.put("type", "password");
					System.out.println("password");
				}
			}else{
				reply.put("flag", "fail");
				reply.put("type", "username");
				System.out.println("username");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		response.getWriter().append(reply.toString());
		
	}

}
