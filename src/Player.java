import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Player
{
   private String mName;
   private boolean mCurrentTurn;
   private HashMap<String, Integer> positionCount;
   
   public Player(String name)
   {
      mName = name;
      mCurrentTurn = false;
      positionCount = new HashMap<>();
      
      positionCount.put("Point Guard", 0);
      positionCount.put("Shooting Guard", 0);
      positionCount.put("Small Forward", 0);
      positionCount.put("Power Forward", 0);
      positionCount.put("Center", 0);
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
   
   public void updateSqlTurn(Connection conn) {
	   int turn = 0;
	   if (mCurrentTurn) {
		   turn = 1;
	   }
       try
       {
         Statement s1 = conn.createStatement();
         s1.executeUpdate("UPDATE CurrentGame SET Turn = " + turn 
                                          + " WHERE UserName = '" + mName + "'");
       } 
       catch (SQLException e)
       {
         // TODO Auto-generated catch block
         e.printStackTrace();
       }	      
   }
   
   public void incrementPositionCount(String position)
   {
      positionCount.put(position, positionCount.get(position) + 1);
   }
   
   public int getPositionCount(String position)
   {
      return positionCount.get(position);
   }
   
   public void setPositionCount(String position, int value)
   {
      positionCount.put(position, value);
   }
   
   public void grabPositionsOnStartUp(Connection conn)
   {
      try
      {
        Statement s1 = conn.createStatement();
        String[] s = {"Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"};
        ResultSet result = null;
        for (int i = 0; i < s.length; i++)
        {
           result = s1.executeQuery("SELECT COUNT(*) AS PosCount "
                 + "From Players, Positions Pos, GameRoster G "
                 + "WHERE Players.Position1 = Pos.pos AND G.Athlete = Players.Id "
                 + "AND Pos.Position = '" + s[i] + "' "
                 + "AND G.UserId = " + getUserId(conn));
            while (result.next())
            {
               positionCount.put(s[i], result.getInt("PosCount"));
            }
        }

      } 
      catch (SQLException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }       
   }
   
}
