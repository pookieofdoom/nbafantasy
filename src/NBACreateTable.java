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
                          + "PRIMARY KEY (UserId),"
                          + "UNIQUE (UserName)"
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
                          + "FOREIGN KEY(Athlete) REFERENCES Players(ID),"
                          + "UNIQUE(Athlete)"
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
      BufferedReader bufferedReader = null;
      try
      {
    	 if ((System.getProperty("os.name").toString().contains("Windows"))) {
          bufferedReader = new BufferedReader(
                                         new FileReader("src\\SQLInserts\\build-" + tableName.toLowerCase() + ".sql")
                                         );
    	 }
    	 else {
             bufferedReader = new BufferedReader(
                     new FileReader("src/SQLInserts/build-" + tableName.toLowerCase() + ".sql")
                                        );    		 	 
    	 }
    	 
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
   public static void removeTables(Connection conn)
   {
      try
      {
         Statement s1 = conn.createStatement();
         s1.executeUpdate("DROP TABLE GameRoster");
         s1.executeUpdate("DROP TABLE CurrentGame");
         s1.executeUpdate("DROP TABLE Stats");
         s1.executeUpdate("DROP TABLE Players");
         s1.executeUpdate("DROP TABLE Teams");
         s1.executeUpdate("DROP TABLE Coaches");
         s1.executeUpdate("DROP TABLE Positions");
      } 
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
   
   public void AddOverallScore(){
      try {
         Statement s = mConn.createStatement();
         ResultSet result = s.executeQuery(  "select " +
                                             "max(s.points/s.games) as ppg, " +
                                             "max(s.assists/s.games) as apg, " +
                                             "max(s.rebounds/s.games) as rpg, " +
                                             "max(s.steals/s.games) as spg, " +
                                             "max(s.blocks/s.games) as bpg, " +
                                             "max(s.turnover/s.games) as tov, " +
                                             "max(s.tpm) as tpm " +
                                             "from Stats s, Players p " + 
                                             "where p.id = s.playerid && s.season = 2015 && s.games >= 60;" 
                                          );
         result.next();
         double mppg = result.getDouble("ppg");
         double mapg = result.getDouble("apg");
         double mrpg = result.getDouble("rpg");
         double mspg = result.getDouble("spg");
         double mbpg = result.getDouble("bpg");
         double mtov = result.getDouble("tov");
         double mtpm = result.getDouble("tpm");
         System.out.println(mppg + " " + mtpm);
         
         s.executeUpdate(  "alter table Stats add OVERALL float;");
         
         s.executeUpdate(  "update Stats s set OVERALL = (" +
                               "((((s.points/s.games) / " + mppg + ") * 10) * 1) + " +
                               "((((s.assists/s.games) / " + mapg + ") * 10) * 1) + " +
                               "((((s.rebounds/s.games) / " + mrpg + ") * 10) * 1) + " +
                               "((((s.steals/s.games) / " + mspg + ") * 10) * 1) + " +
                               "((((s.blocks/s.games) / " + mbpg + ") * 10) * 1) - " +
                               "((((s.turnover/s.games) / " + mtov + ") * 10) * 1) + " +
                               "(((s.tpm / " + mtpm + ") * 10) * 1) " +
                           ");"); 
         s.executeUpdate(  "update Stats s set OVERALL = ( OVERALL + ((((s.FGM/s.FGA) / .7) * 20) - 10)) where s.FGA > 40;");
         s.executeUpdate(  "update Stats s set OVERALL = ( OVERALL + ((((s.FTM/s.FTA) / .9) * 20) - 10)) where s.FTA > 40;");
         
         result = s.executeQuery("select max(overall) as over from Stats;");
         result.next();
         double maxOverall = result.getDouble("over");
         System.out.println(maxOverall);
         s.executeUpdate(  "update Stats s set OVERALL = ((OVERALL / " + maxOverall + ") * 100);");
                                    
         
         
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
}
