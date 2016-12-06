import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
      mTableNames.add("CurrentGame");
      mTableNames.add("GameRoster");
      
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
                          + "Abbrev VARCHAR(2),"
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
                          + "Position1 INT,"
                          + "Position2 INT,"
                          + "Height INT,"
                          + "Weight INT,"
                          + "PRIMARY KEY (Id),"
                          + "FOREIGN KEY (TeamId) REFERENCES Teams(ID),"
                          + "FOREIGN KEY (Position1) REFERENCES Positions(Pos),"
                          + "FOREIGN KEY (Position2) REFERENCES Positions(Pos)"
                          + ")";
         }
         
         else if (tableName.equals("Stats"))
         {
            table = table + "(PlayerId INT,"
                          + "Season INT,"
                          + "Age INT,"
                          + "TeamId INT,"
                          + "Games INT,"
                          + "Starts INT,"
                          + "Points INT,"
                          + "Assists INT,"
                          + "Rebounds INT,"
                          + "Steals INT,"
                          + "Blocks INT,"
                          + "TurnOver INT,"
                          + "FGM INT,"
                          + "FGA INT,"
                          + "TPM INT,"
                          + "TPA INT,"
                          + "FTM INT,"
                          + "FTA INT,"
                          + "PRIMARY KEY(PlayerId, Season, TeamId),"
                          + "FOREIGN KEY(PlayerId) REFERENCES Players(ID),"
                          + "FOREIGN KEY(TeamId) REFERENCES Teams(ID)"
                          + ")";
         }
         
         else if (tableName.equals("CurrentGame"))
         {
            table = table + "(UserId INT,"
                          + "UserName VARCHAR(16),"
                          + "Round INT,"
                          + "Turn CHAR(1),"
                          + "PRIMARY KEY (UserId)"
                          + ")";
         }
         
         else if (tableName.equals("GameRoster"))
         {
            table = table + "(ID INT,"
                          + "UserId INT,"
                          + "Athlete INT,"
                          + "Round INT,"
                          + "PRIMARY KEY(ID),"
                          + "FOREIGN KEY(UserId) REFERENCES CurrentGame(UserId),"
                          + "FOREIGN KEY(Athlete) REFERENCES Players(ID)"
                          + ")";
         }
         
         
         
         System.out.println(table);
         s1.executeUpdate(table);
         if (!tableName.equals("CurrentGame") && !tableName.equals("GameRoster"))
         {
            insertTuples(tableName);
         }
            
         
         
      } 
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
   
   private void insertTuples(String tableName)
   {

      StringBuilder sb = new StringBuilder();
      String line;
      try
      {
         BufferedReader bufferedReader = new BufferedReader(
                                         new FileReader("src\\SQLInserts\\build-" + tableName.toLowerCase() + ".sql")
                                                            );
         while ((line = bufferedReader.readLine()) != null)
         {
             sb.append(line);
         }
     }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      try
      {
         Statement s1 = mConn.createStatement();
         s1.executeUpdate(sb.toString());
         //System.out.println(sb.toString());
      }
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      
          
   }
}
