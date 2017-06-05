import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库工具类
 * @author hzf
 *
 */
public class SQLiteUtil {

	public static void main(String[] args) {
		try {
			createTableMsg();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建表msg
	 * @param table 表名
	 * @param sql
	 * @throws ClassNotFoundException
	 */
	public static void createTableMsg() throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");  
		  
	    Connection connection = null;  
	    try  
	    {  
	      // create a database connection  
	      connection = DriverManager.getConnection("jdbc:sqlite:hitwearable.db");  
	      Statement statement = connection.createStatement();  
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.  
	  
	      statement.executeUpdate("drop table if exists msg"); 
	      statement.executeUpdate("create table msg "
	      		+ "(id integer primary key , "
	      		+ "path string, "
	      		+ "time long, "
	      		+ "type integer, "
	      		+ "catagory integer)");
	    }  
	    catch(SQLException e)  
	    {  
	      // if the error message is "out of memory",   
	      // it probably means no database file is found  
	      System.err.println(e.getMessage());  
	    }  
	    finally  
	    {  
	      try  
	      {  
	        if(connection != null)  
	          connection.close();  
	      }  
	      catch(SQLException e)  
	      {  
	        // connection close failed.  
	        System.err.println(e);  
	      }  
	    }
	}

	/**
	 * 插入数据
	 * @param sql
	 * @throws ClassNotFoundException
	 */
	public static void insert(Msg msg) throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");  
		  
	    Connection connection = null;  
	    try  
	    {  
	      // create a database connection  
	      connection = DriverManager.getConnection("jdbc:sqlite:hitwearable.db");
	  
	      PreparedStatement prep = connection.prepareStatement("insert into msg values(?, ?, ?, ?, ?);");
	      prep.setObject(1, null);
	      prep.setString(2, msg.getPath());
	      prep.setLong(3, msg.getTime());
	      prep.setInt(4, msg.getType());
	      prep.setInt(5, msg.getCatagory());
	      prep.addBatch();
	      connection.setAutoCommit(false);  
          prep.executeBatch();  
          connection.setAutoCommit(true);
	    }  
	    catch(SQLException e)  
	    {  
	      // if the error message is "out of memory",   
	      // it probably means no database file is found  
	      System.err.println(e.getMessage());  
	    }  
	    finally  
	    {  
	      try  
	      {  
	        if(connection != null)  
	          connection.close();  
	      }  
	      catch(SQLException e)  
	      {  
	        // connection close failed.  
	        System.err.println(e);  
	      }  
	    }
	}
	
	/**
	 * 查询表msg所有数据
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List<Msg> queryAll() throws ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");  
		  
	    Connection connection = null;  
	    try  
	    {  
	      // create a database connection  
	      connection = DriverManager.getConnection("jdbc:sqlite:hitwearable.db");  
	      Statement statement = connection.createStatement();  
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.  
	  
	      ResultSet rs = statement.executeQuery("select * from msg");  
	      List<Msg> list = new ArrayList<Msg>();
	      while(rs.next())  
	      {  
	        Msg msg = new Msg(rs.getString("path"), rs.getInt("type"), rs.getLong("time"), rs.getInt("catagory"));
	        list.add(msg); 
	      }
	      return list;
	    }  
	    catch(SQLException e)  
	    {  
	      // if the error message is "out of memory",   
	      // it probably means no database file is found  
	      System.err.println(e.getMessage());  
	    }  
	    finally  
	    {  
	      try  
	      {  
	        if(connection != null)  
	          connection.close();  
	      }  
	      catch(SQLException e)  
	      {  
	        // connection close failed.  
	        System.err.println(e);  
	      }  
	    }
		return null;
	}
}
