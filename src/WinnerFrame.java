import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class WinnerFrame extends JFrame
{
   private int             DimSizeX = 1100;                   // 1500 for
                                                             // windows, 750 for
                                                             // other
   private int             DimSizeY = 700;                   // 450 for windows,
                                                             // 350 for other
   private Connection      mConn;
   private Player          mPlayer1;
   private Player          mPlayer2;
   private JLabel          winnerLabel;
   private JPanel          WestPanel, EastPanel, CenterPanel;
   private FinalScoreModel finalScoreModel;
   private Player          mWinnerPlayer;

   public WinnerFrame(Connection conn, Player player1, Player player2)
   {
      mConn = conn;
      mPlayer1 = player1;
      mPlayer2 = player2;

      WindowAdapter exitListener = new WindowAdapter()
      {

         @Override
         public void windowClosing(WindowEvent e)
         {
            int confirm = JOptionPane.showOptionDialog(null, "Are You Sure to Close Application?", "Exit Confirmation",
                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (confirm == 0)
            {
               setVisible(false);
               dispose();
               try
               {
                  NBACreateTable.removeTables(mConn);
                  mConn.close();
               } catch (Exception ee)
               {
                  System.out.println("Unable to close Connection");
               }
               System.out.println("Connection Closed");
               System.exit(0);
            } else
            {
               System.out.println("do nothing");
            }
         }
      };
      addWindowListener(exitListener);

      setLayout(new BorderLayout());
      WestPanel = new JPanel();
      EastPanel = new JPanel();
      CenterPanel = new JPanel(new BorderLayout());
      //winnerLabel = new JLabel("The winner is ...");
      //JButton winnerButton = new JButton("WINNER?");
      createEastPanel();
      createWestPanel();
      createCenterPanel();
      getContentPane().add(WestPanel, BorderLayout.WEST);
      getContentPane().add(CenterPanel, BorderLayout.CENTER);
      getContentPane().add(EastPanel, BorderLayout.EAST);
      setResizable(true);
      // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      pack();
      if (System.getProperty("os.name").toString().contains("Windows"))
      {
         DimSizeX = 800;
         DimSizeY = 800;
      }
      setSize(new Dimension(DimSizeX, DimSizeY)); // 1500, 450

   }

   private void createEastPanel()
   {

      GridBagLayout gridbag = new GridBagLayout();
      JPanel player2Info = new JPanel(gridbag);
      GridBagConstraints c = new GridBagConstraints();
      JList player2List;

      JLabel player2Name = new JLabel("Player: " + mPlayer2.getName());
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 1;
      c.weighty = .33;
      gridbag.setConstraints(player2Name, c);
      player2Info.add(player2Name);
      player2List = new JList(new DefaultListModel<String>());
      try
      {
         int userId = mPlayer2.getUserId(mConn);
         Statement s1 = mConn.createStatement();
         ResultSet result = s1.executeQuery(
               "SELECT * " + "FROM GameRoster G, Players P " + "WHERE G.Athlete = P.Id " + "AND G.UserId = " + userId);
         while (result.next())
         {
            ((DefaultListModel) player2List.getModel())
                  .addElement(result.getString("P.FirstName") + " " + result.getString("P.LastName"));
         }
      } catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 2;
      c.weighty = 1;
      gridbag.setConstraints(player2List, c);
      player2Info.add(player2List);
      EastPanel.add(player2Info);
      

      //CenterPanel.add(a);


   }
   
   public class Restart implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
         NBACreateTable.removeTables(mConn);
         NBACreateTable a = new NBACreateTable(mConn);
         for (int i =0; i < a.getTableNames().size(); i++)
         {
            a.createTables(a.getTableNames().get(i));
         }
         a.AddOverallScore();
         Point currentLoc = getLocation();
         dispose();
         //setVisible(false);
         JFrame appFrame = new PlayerInfoFrame(mConn);
         appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         appFrame.setLocation(currentLoc);
         appFrame.setVisible(true);
         
      }
      
   }

   private void createWestPanel()
   {
      GridBagLayout gridbag = new GridBagLayout();
      JPanel player1Info = new JPanel(gridbag);
      GridBagConstraints c = new GridBagConstraints();
      JList player1List;

      JLabel player1Name = new JLabel("Player: " + mPlayer1.getName());
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 1;
      c.weighty = .33;
      gridbag.setConstraints(player1Name, c);
      player1Info.add(player1Name);

      player1List = new JList(new DefaultListModel<String>());
      try
      {
         int userId = mPlayer1.getUserId(mConn);
         Statement s1 = mConn.createStatement();
         ResultSet result = s1.executeQuery(
               "SELECT * " + "FROM GameRoster G, Players P " + "WHERE G.Athlete = P.Id " + "AND G.UserId = " + userId);
         while (result.next())
         {
            ((DefaultListModel) player1List.getModel())
                  .addElement(result.getString("P.FirstName") + " " + result.getString("P.LastName"));
         }
      } catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 2;
      c.weighty = 1;
      gridbag.setConstraints(player1List, c);
      player1Info.add(player1List);
      WestPanel.add(player1Info);

   }

   private void createCenterPanel()
   {
      JPanel a = new JPanel(new BorderLayout());
      finalScoreModel = new FinalScoreModel();
      JTable finalScoreTable = new JTable(finalScoreModel);
      finalScoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      JScrollPane playerScrollPane = new JScrollPane(finalScoreTable);
      finalScoreTable.setPreferredScrollableViewportSize(new Dimension(800,50));
      finalScoreTable.doLayout();
      
      CenterPanel.add(playerScrollPane, BorderLayout.NORTH);
      //double player1Score = (double) finalScoreModel.getValueAt(0, 10);
      JPanel wat = new JPanel(new GridLayout(2,1));
      String winner = findWinnerFromModel(finalScoreModel);
      winnerLabel = new JLabel("The winner is " + winner);
      winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
      wat.add(winnerLabel);
      JButton restartEverything = new JButton("Play Again");
      restartEverything.addActionListener(new Restart());
      wat.add(restartEverything);
      CenterPanel.add(wat, BorderLayout.CENTER);
      
      String imageUrl = "";
      if ((System.getProperty("os.name").toString().contains("Windows"))) {
         imageUrl = "src\\Images\\trophy2.jpg";
      }
      else
      {
         imageUrl = "src/Images/trophy2.jpg";
      }
      
      JLabel sup = new JLabel(new ImageIcon(imageUrl));

      
      if (mWinnerPlayer == null) 
         sup.setHorizontalAlignment(SwingConstants.CENTER);
      
      else if (mWinnerPlayer.equals(mPlayer1)) 
         sup.setHorizontalAlignment(SwingConstants.LEFT);
      
      else if (mWinnerPlayer.equals(mPlayer2)) 
         sup.setHorizontalAlignment(SwingConstants.RIGHT);

      CenterPanel.add(sup, BorderLayout.SOUTH);

      
   }
   
   private String findWinnerFromModel (FinalScoreModel model) {
	   int winAscCols[] = {1,2,3,4,5,7,8,9};
	   int winDescCol = 6;
	   int player1ColWins = 0, player2ColWins = 0;
	   String retVal = "";
	   //comparing ascending column value wins
	   for (int col = 0; col < winAscCols.length; col++) {
		   if ((double) model.getValueAt(0, winAscCols[col]) > (double) model.getValueAt(1, winAscCols[col])) {
			   player1ColWins++;
		   }
		   else if ((double) model.getValueAt(0, winAscCols[col]) < (double) model.getValueAt(1, winAscCols[col])) {
			   player2ColWins++;
		   }
	   }
	   //comparing descending column value wins. turnovers
	   if ((double) model.getValueAt(0, winDescCol) > (double) model.getValueAt(1, winDescCol)) {
		   player2ColWins++;
	   }
	   else if ((double) model.getValueAt(0, winDescCol) < (double) model.getValueAt(1, winDescCol)) {
		   player1ColWins++;
	   }
	   
	   if (player1ColWins > player2ColWins) {
		   retVal = mPlayer1.getName() + "! " + player1ColWins + " column wins versus " + player2ColWins + " column wins.";
		   mWinnerPlayer = mPlayer1;
	   }
	   else if (player1ColWins < player2ColWins) {
		   retVal = mPlayer2.getName() + "! " + player2ColWins + " column wins versus " + player1ColWins + " column wins.";
		   mWinnerPlayer = mPlayer2;
	   }
	   else {
		   retVal = "a tie! " + mPlayer1.getName() + " " + " and " + "  " + mPlayer2.getName() + " tied";
		   mWinnerPlayer = null;
	   }
	   return retVal;
	   
   }

   class FinalScoreModel extends AbstractTableModel
   {
      private String[]   singelAthleteColumns = { "PlayerName", "Points", "Assists", "Rebounds", "Steals", "Blocks", "TurnOver",
             "Three Pointers", "Field Goals", "Free Throws", "Overall" };

      private Object[][] singleAthleteData    = new Object[2][11];

      public FinalScoreModel()
      {
         try
         {
            Statement s1 = mConn.createStatement();
            ResultSet result = s1.executeQuery("SELECT g.*, "
                                          + "sum(s.points/s.games) as ppg, "
                                          + "sum(s.assists/s.games) as apg, "
                                          + "sum(s.rebounds/s.games) as rpg, "
                                          + "sum(s.steals/s.games) as spg, "
                                          + "sum(s.blocks/s.games) as bpg, "
                                          + "sum(s.turnover/s.games) as top, "
                                          + "sum(s.tpm/s.games) as tpg, "
                                          + "sum(s.fgm)/sum(s.fga) as fgp, "
                                          + "sum(s.ftm)/sum(s.fta) as ftp, "
                                          + "sum(s.overall) / count(*) as overall "
                                          + "from GameRoster g, Stats s where s.playerid = g.athlete && s.season = 2015 group by g.userid"
                                          );
            int row = 0;
            
            
            while(result.next())
            {
               this.setValueAtt(row++, result.getDouble("ppg"), result.getDouble("apg"), 
                     result.getDouble("rpg"), result.getDouble("spg"), result.getDouble("bpg"),
                     result.getDouble("top"), result.getDouble("tpg"), result.getDouble("fgp"),
                     result.getDouble("ftp"), result.getDouble("overall"));
            }
         }
         catch (SQLException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

      }
      public void setValueAtt(int row, double ppg, double apg, double rpg, double spg, double bpg, double top,
            double tpg, double fgp, double ftp, double overall) {
            if (row == 0)
            {
               singleAthleteData[row][0] = mPlayer1.getName();
            }
            else
            {
               singleAthleteData[row][0] = mPlayer2.getName();
            }
           singleAthleteData[row][1] = roundMyNum (ppg);
           singleAthleteData[row][2] = roundMyNum (apg);
           singleAthleteData[row][3] = roundMyNum (rpg);
           singleAthleteData[row][4] = roundMyNum (spg);
           singleAthleteData[row][5] = roundMyNum (bpg);
           singleAthleteData[row][6] = roundMyNum (top);
           singleAthleteData[row][7] = roundMyNum (tpg);
           singleAthleteData[row][8] = roundMyNum (fgp);
           singleAthleteData[row][9] = roundMyNum (ftp);
           singleAthleteData[row][10] = roundMyNum (overall);
           fireTableDataChanged();
       }
      /* Mandatory functions to implement Abtractclass */
      public int getColumnCount()
      {
         return singelAthleteColumns.length;
      }

      public int getRowCount()
      {
         return singleAthleteData.length;
      }

      public String getColumnName(int col)
      {
         return singelAthleteColumns[col];
      }

      public Object getValueAt(int row, int col)
      {
         return singleAthleteData[row][col];
      }
   }
   
   public double roundMyNum (double tooLong) {
      return Math.round(tooLong * 100)/100.0d;
   }

}
