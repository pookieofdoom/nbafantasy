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
                     new FileReader("SQLInserts/build-" + tableName.toLowerCase() + ".sql")
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
   
   public void RefreshRelativeScore(int roundsleft, int turn, int maxRounds){
      try {
      System.out.println(roundsleft + ", " + turn + " / " + maxRounds);
      
         Statement s = mConn.createStatement();
         ResultSet result = s.executeQuery(  "select " +
                                             "max(s.points/s.games) as ppg, " +
                                             "max(s.assists/s.games) as apg, " +
                                             "max(s.rebounds/s.games) as rpg, " +
                                             "max(s.steals/s.games) as spg, " +
                                             "max(s.blocks/s.games) as bpg, " +
                                             "max(s.turnover/s.games) as tov, " +
                                             "max(s.tpm/s.games) as tpm " +
                                             "from Stats s, Players p " + 
                                             "where p.id = s.playerid && s.season = 2015 && s.games >= 60 " +
                                             "AND p.Id NOT IN (SELECT Athlete FROM GameRoster)" 
                                          );
         result.next();
         double mppg = result.getDouble("ppg");
         double mapg = result.getDouble("apg");
         double mrpg = result.getDouble("rpg");
         double mspg = result.getDouble("spg");
         double mbpg = result.getDouble("bpg");
         double mtov = result.getDouble("tov");
         double mtpm = result.getDouble("tpm");
         
         result = s.executeQuery( "select g.*, " 
                                + "sum(s.points/s.games) as ppg, "
                                + "sum(s.assists/s.games) as apg, "
                                + "sum(s.rebounds/s.games) as rpg, "
                                + "sum(s.steals/s.games) as spg, "
                                + "sum(s.blocks/s.games) as bpg, "
                                + "sum(s.turnover/s.games) as tpg, "
                                + "sum(s.tpm/s.games) as 3pg, "
                                + "sum(s.fgm)/sum(s.fga) as fgp, "
                                + "sum(s.ftm)/sum(s.fta) as ftp, "
                                + "sum(s.overall)/count(*) as overall "
                                + "from GameRoster g, Stats s where s.playerid = g.athlete && s.season = 2015 group by g.userid;" );
             
         System.out.println(result.getRow()); 
         double dppg = 0;
         double dapg = 0;
         double drpg = 0;
         double dspg = 0;
         double dbpg = 0;
         double dtov = 0;
         double dtpm = 0;
         boolean works = false;
                           
         if(turn > 0){
            if(result.next()){
               dppg = result.getDouble("ppg");
               dapg = result.getDouble("apg");
               drpg = result.getDouble("rpg");
               dspg = result.getDouble("spg");
               dbpg = result.getDouble("bpg");
               dtov = result.getDouble("tpg");
               dtpm = result.getDouble("3pg");
               if(result.next()){
                  dppg = Math.abs(dppg - result.getDouble("ppg"));
                  dapg = Math.abs(dapg - result.getDouble("apg"));
                  drpg = Math.abs(drpg - result.getDouble("rpg"));
                  dspg = Math.abs(dspg - result.getDouble("spg"));
                  dbpg = Math.abs(dbpg - result.getDouble("bpg"));
                  dtov = Math.abs(dtov - result.getDouble("tpg"));
                  dtpm = Math.abs(dtpm - result.getDouble("3pg"));
                  works = true;
               }
            } 
         } 
         
         double ppg_rat = 1.00;
         double apg_rat = 1.00;
         double rpg_rat = 1.00;
         double spg_rat = 1.00;
         double bpg_rat = 1.00;
         double tpg_rat = 1.00;
         double pg3_rat = 1.00;
         double fgp_rat = 1.00;
         double ftp_rat = 1.00;
         
         int pl = (maxRounds - roundsleft) * 2;
         if(turn == 2){
            pl /= 2;
            if(pl <= 0){
               pl++;
            }
         }
         
         if(works == true){
            System.out.println("Players Left: " + pl);
            ppg_rat = Math.sqrt(mppg/(dppg/pl));
            apg_rat = Math.sqrt(mapg/(dppg/pl));
            rpg_rat = Math.sqrt(mrpg/(dppg/pl));
            spg_rat = Math.sqrt(mspg/(dppg/pl));
            bpg_rat = Math.sqrt(mbpg/(dppg/pl));
            tpg_rat = Math.sqrt(mtov/(dtov/pl));
            pg3_rat = Math.sqrt(mtpm/(dtpm/pl));
            System.out.println("PPG: " + ppg_rat + "\nAPG: " + apg_rat + "\nRPG: " + rpg_rat + "\nSPG: " + spg_rat + "\nBPG: " + bpg_rat + "\nTPG: " + tpg_rat + "\n3PG: " + pg3_rat); 
            System.out.println("Done?");
         }
         
         s.executeUpdate(  "update Stats s set RELATIVE = (-10);");
         
         s.executeUpdate(  "update Stats s set RELATIVE = (" +
                               "((((s.points/s.games) / " + mppg + ") * 10) * "+ ppg_rat +") + " +
                               "((((s.assists/s.games) / " + mapg + ") * 10) * "+ apg_rat +") + " +
                               "((((s.rebounds/s.games) / " + mrpg + ") * 10) * "+ rpg_rat +") + " +
                               "((((s.steals/s.games) / " + mspg + ") * 10) * "+ spg_rat +") + " +
                               "((((s.blocks/s.games) / " + mbpg + ") * 10) * "+ bpg_rat +") - " +
                               "((((s.turnover/s.games) / " + mtov + ") * 10) * "+ tpg_rat +") + " +
                               "((((s.tpm/s.games) / " + mtpm + ") * 10) * "+ pg3_rat +") " +
                           ") where s.season = 2015 AND s.playerid NOT IN (SELECT Athlete FROM GameRoster);");                 
                           
         s.executeUpdate(  "update Stats s set RELATIVE = ( RELATIVE + ((((s.FGM/s.FGA) / .7) * 20) - 10)) where s.FGA > 40 AND s.season = 2015 AND s.playerid NOT IN (SELECT Athlete FROM GameRoster);");
         s.executeUpdate(  "update Stats s set RELATIVE = ( RELATIVE + ((((s.FTM/s.FTA) / .9) * 20) - 10)) where s.FTA > 40 AND s.season = 2015 AND s.playerid NOT IN (SELECT Athlete FROM GameRoster);");
         
         result = s.executeQuery("select max(relative) as rel from Stats where season = 2015;");
         result.next();
         double maxOverall = result.getDouble("rel");
         s.executeUpdate(  "update Stats s set RELATIVE = ((RELATIVE / " + maxOverall + ") * 100) where s.season = 2015 AND s.playerid NOT IN (SELECT Athlete FROM GameRoster);");
      } catch(SQLException e){
         e.printStackTrace();
      }
   }
   
   public void AddOverallScore(){
      try {
         Statement s = mConn.createStatement();
         s.executeUpdate(  "alter table Stats add OVERALL float;");
         s.executeUpdate(  "alter table Stats add RELATIVE float;");
         
         ResultSet result = s.executeQuery(  "select " +
                                             "max(s.points/s.games) as ppg, " +
                                             "max(s.assists/s.games) as apg, " +
                                             "max(s.rebounds/s.games) as rpg, " +
                                             "max(s.steals/s.games) as spg, " +
                                             "max(s.blocks/s.games) as bpg, " +
                                             "max(s.turnover/s.games) as tov, " +
                                             "max(s.tpm/s.games) as tpm " +
                                             "from Stats s, Players p " + 
                                             "where p.id = s.playerid && s.season = 2015 && s.games >= 60 " +
                                             "AND p.Id NOT IN (SELECT Athlete FROM GameRoster)" 
                                          );
         result.next();
         double mppg = result.getDouble("ppg");
         double mapg = result.getDouble("apg");
         double mrpg = result.getDouble("rpg");
         double mspg = result.getDouble("spg");
         double mbpg = result.getDouble("bpg");
         double mtov = result.getDouble("tov");
         double mtpm = result.getDouble("tpm");
         
         s.executeUpdate(  "update Stats s set OVERALL = (" +
                               "((((s.points/s.games) / " + mppg + ") * 10) * 1) + " +
                               "((((s.assists/s.games) / " + mapg + ") * 10) * 1) + " +
                               "((((s.rebounds/s.games) / " + mrpg + ") * 10) * 1) + " +
                               "((((s.steals/s.games) / " + mspg + ") * 10) * 1) + " +
                               "((((s.blocks/s.games) / " + mbpg + ") * 10) * 1) - " +
                               "((((s.turnover/s.games) / " + mtov + ") * 10) * 1) + " +
                               "((((s.tpm/s.games) / " + mtpm + ") * 10) * 1) " +
                           ");"); 
         s.executeUpdate(  "update Stats s set OVERALL = ( OVERALL + ((((s.FGM/s.FGA) / .7) * 20) - 10)) where s.FGA > 40;");
         s.executeUpdate(  "update Stats s set OVERALL = ( OVERALL + ((((s.FTM/s.FTA) / .9) * 20) - 10)) where s.FTA > 40;");
         
         result = s.executeQuery("select max(overall) as over from Stats;");
         result.next();
         double maxOverall = result.getDouble("over");
         s.executeUpdate(  "update Stats s set OVERALL = ((OVERALL / " + maxOverall + ") * 100);");
         
         
         RefreshRelativeScore(0, 1, 1);
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

}
