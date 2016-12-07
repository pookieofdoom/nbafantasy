import java.sql.*;
import java.util.ArrayList;

public class Player
{
   private String mName;
   private boolean mCurrentTurn;
   
   public Player(String name)
   {
      mName = name;
      mCurrentTurn = false;
   }
   
   public void setName(String name)
   {
      mName = name;
   }
   
   public String getName()
   {
      return mName;
   }
   
   
   public void setCurrentTurn(boolean turn)
   {
      mCurrentTurn = turn;
   }
   
   public boolean getCurrentTurn()
   {
      return mCurrentTurn;
   }
   
   public int getUserId(Connection conn)
   {
      int userId = -1;
      try
      {
         Statement s1 = conn.createStatement();
         ResultSet result = s1.executeQuery("SELECT * "
                                          + "FROM CurrentGame "
                                          + "WHERE UserName = '" + mName + "'");
         while(result.next())
         {
            userId = result.getInt("UserId");
         }
      } 
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return userId;
   }
   
   
}
