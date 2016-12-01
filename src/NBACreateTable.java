import java.util.ArrayList;
import java.sql.*;

public class NBACreateTable
{
   private ArrayList<String> mTableNames;
   private Connection mConn;
   
   public NBACreateTable(Connection conn)
   {
      mConn = conn;
      mTableNames = new ArrayList<String>();
      mTableNames.add("Positions");
      mTableNames.add("Coaches");
      mTableNames.add("Teams");
      mTableNames.add("Players");
      mTableNames.add("Stats");
      
   }
   
   public ArrayList<String> getTableNames()
   {
      return mTableNames;
   }
   
   public void createTables(String tableName)
   {
      try
      {
         Statement s1 = mConn.createStatement();
         String table = "CREATE TABLE " + tableName;
         
         if (tableName.equals("Positions"))
         {
            table = table + "(Pos INT,"
                          + "Position VARCHAR(16),"
                          + "PRIMARY KEY (Pos)"
                          + ")";
         }
         
         
         else if (tableName.equals("Coaches"))
         {
            table = table + "(ID INT,"
                          + "FirstName VARCHAR(16),"
                          + "LastName VARCHAR(16),"
                          + "PRIMARY KEY (Id)"
                          + ")";
         }
         
         else if (tableName.equals("Teams"))
         {
            table = table + "(ID INT,"
                          + "Name VARCHAR(12),"
                          + "State CHAR(2),"
                          + "City VARCHAR(16),"
                          + "CoachID INT,"
                          + "Abbrev VARCHAR(3),"
                          + "PRIMARY KEY (ID),"
                          + "FOREIGN KEY (CoachId) REFERENCES Coaches(Id)"
                          + ")";
         }
         
         else if (tableName.equals("Players"))
         {
            table = table + "(ID INT,"
                          + "FirstName VARCHAR(16),"
                          + "LastName VARCHAR(16),"
                          + "TeamID INT,"
                          + "Position INT,"  
                          + "Height INT,"
                          + "Weight INT,"
                          + "ShootingHand CHAR(1),"
                          + "PRIMARY KEY (Id),"
                          + "FOREIGN KEY (TeamId) REFERENCES Teams(ID),"
                          + "FOREIGN KEY (Position) REFERENCES Positions(Pos)"
                          + ")";
         }
         
         else if (tableName.equals("Stats"))
         {
            table = table + "(PlayerId INT,"
                          + "Season INT,"
                          + "Age INT,"
                          + "TeamId INT,"
                          + "Points INT,"
                          + "Assists INT,"
                          + "Rebounds INT,"
                          + "Steals INT,"
                          + "Blocks INT,"
                          + "FGM INT,"
                          + "FGA INT,"
                          + "TPM INT,"
                          + "TPA INT,"
                          + "FTM INT,"
                          + "FTA INT,"
                          + "TurnOver INT,"
                          + "PRIMARY KEY(PlayerId, Season, TeamId),"
                          + "FOREIGN KEY(PlayerId) REFERENCES Players(ID),"
                          + "FOREIGN KEY(TeamId) REFERENCES Teams(ID)"
                          + ")";
         }
         
         
         System.out.println(table);
         s1.executeUpdate(table);
         
         
      } 
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
}
