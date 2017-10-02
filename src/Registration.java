

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
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Connection connection;
	Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Registration() {
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
		
		System.out.println("Registration Test!");
		System.out.println(request.getContentType());
		BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String inputString = input.readLine();
		JSONObject inputJson = new JSONObject(inputString);
		JSONObject reply = new JSONObject();
		
		String password = inputJson.getString("password");
		String username = inputJson.getString("username");
		String gender = inputJson.getString("gender");
		
		String compareUser = "SELECT * FROM users";
		boolean flag = true;
		ResultSet resultSet;
		try {
			resultSet = statement.executeQuery(compareUser);
			while(resultSet.next()) {
				if(username.equals(resultSet.getString("username"))) {
					System.out.println(resultSet.getString("username") + " 1111111111111111111111111111111111111111111111111111111111111");
					flag = false;
				}
				
			}  
			if(flag) {
				String sql = "INSERT INTO users(username, password, gender) VALUES('" + username + "','" + password + "','"
						+ gender + "')";
				int rs = statement.executeUpdate(sql);
				if (rs > 0) {
					System.out.println("Registration success!");
					reply.put("flag", "success");
				} else {
					System.out.println("Registration fail!");
					reply.put("flag", "fail");
				}
			} else {
				reply.put("flag", "duplicated");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(reply.toString());
		response.getWriter().append(reply.toString());
		System.out.println("The registration is finished!");
	}

}
