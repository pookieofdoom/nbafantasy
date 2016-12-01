import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JFrame;

/*hello andrew*/
public class TestMysql
{
   private static Connection conn;
   private static String     url = "jdbc:mysql://";
   private static String     host;
   private static String     userid;
   private static String     password;

   public static void main(String args[])
   {
      NBACreateTable createTable = null;
      //get database config
      loadConfig();
      
      try
      {
         Class.forName("com.mysql.jdbc.Driver").newInstance();
      }
      catch (Exception e)
      {
         System.out.println("Driver not found");
         System.out.println(e);
      }
      
      conn = null;
      try
      {
         conn = DriverManager.getConnection(url+host+"/"+userid+"?user="+userid+"&password="+password+"&");
         createTable = new NBACreateTable(conn);
      }
      catch (Exception e)
      {
         System.out.println("Could not open connection");
         System.out.println(e);
      }
      System.out.println("Connected");
      
      try
      {
         DatabaseMetaData dbm = conn.getMetaData();
         ArrayList<String> tableNames = createTable.getTableNames();
         boolean flag = false;
         for (int i = 0; i < tableNames.size(); i++)
         {
            ResultSet tables = dbm.getTables(null, null, tableNames.get(i), null);
            flag = false;
            while (tables.next()) 
            { 
               String tName = tables.getString("TABLE_NAME");
               if (tName != null && tName.equals(tableNames.get(i))) 
               {
                  System.out.println(tableNames.get(i));
                  flag = true;
                  break;
               }
           }
            if (flag == false)
            {
               createTable.createTables(tableNames.get(i));
            }
         }
         
      } 
      catch (SQLException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      
      //create gui here
      JFrame appFrame = new NBAFantasyFrame();
      appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      appFrame.setVisible(true);
      
      
      try
      {
         conn.close();
      }
      catch (Exception e)
      {
         System.out.println("Unable to close Connection");
      }
      System.out.println("Connection Closed");
     
   }
   
   public static void loadConfig()
   {
      Properties prop = new Properties();
      InputStream input = null;
      try
      {

         input = new FileInputStream("dbconf.365");

         // load a properties file
         prop.load(input);

         // get the property value
         host = prop.getProperty("host");
         userid = prop.getProperty("userid");
         password = prop.getProperty("password");

      } 
      catch (IOException ex)
      {
         ex.printStackTrace();
      } 
      finally
      {
         if (input != null)
         {
            try
            {
               input.close();
            } 
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }

      
   }
}
