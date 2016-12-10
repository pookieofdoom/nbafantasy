import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class FantasyFrame extends JFrame
{
   private int DimSizeX = 500; // 1500 for windows, 750 for other
   private int DimSizeY = 200; // 450 for windows, 350 for other
   private int mCurrentRound;
   private Player player1, player2;
   private Player mCurrentPlayer;
   private Connection mConn;
   private String mSelectedFN, mSelectedLN;
   private JTable table;
   private EditableTableModel playerModel;      //player stats table, east side
   private JList player1List, player2List;
   private JPanel WestPanel, EastPanel;
   private JLabel title, currentPlayerLabel;
   private int mInternalRoundCount;
   private int mSelectedRow;
   private boolean toggle_PG = false;
   private boolean toggle_SG = false;
   private boolean toggle_SF = false;
   private boolean toggle_PF = false;
   private boolean toggle_C = false;
   private NonEditableModel model;
   
   public FantasyFrame(Connection conn, int currentRound, Player player1, Player player2)
   {
      mConn = conn;
      mCurrentRound = currentRound;
      this.player1 = player1;
      this.player2 = player2;
      if (player1.getCurrentTurn())
      {
         mCurrentPlayer = player1;
         mInternalRoundCount = 1;
      }
         
      else if (player2.getCurrentTurn())
      {
         mCurrentPlayer = player2;
         mInternalRoundCount = 2;
      }
         
      
      //setLayout(new GridLayout(2,2));
      //setLayout(new GridLayout(3,1));
      setLayout(new GridLayout(1,2));
      setSize(new Dimension(DimSizeX, DimSizeY)); // 1500, 450
      WestPanel = new JPanel(new GridLayout(2,1));
      EastPanel = new JPanel(new GridLayout(1,1));
      loadCurrentTeam();
      loadList();
      getContentPane().add(WestPanel);
      getContentPane().add(EastPanel);
      setResizable(false);
      pack();
      
      WindowAdapter exitListener = new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent e) {
             int confirm = JOptionPane.showOptionDialog(
                  null, "Are You Sure to Close Application?", 
                  "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                  JOptionPane.QUESTION_MESSAGE, null, null, null);
             if (confirm == 0) 
             {
                setVisible(false);
                dispose();
                try
                {
                   mConn.close();
                }
                catch (Exception ee)
                {
                   System.out.println("Unable to close Connection");
                }
                System.out.println("Connection Closed");
                System.exit(0);
             }
             else
             {
                System.out.println("do nothing");
             }
         }
     };
     addWindowListener(exitListener);
   }
   
   private void loadCurrentTeam()
   {     
      JPanel playerInfoPanel = new JPanel(new GridLayout(1, 2));
      GridBagLayout gridbag = new GridBagLayout();
      JPanel player1Info = new JPanel(gridbag);
      GridBagConstraints c = new GridBagConstraints();
      //c.fill = GridBagConstraints.HORIZONTAL; 
      
      title = new JLabel("Round " + mCurrentRound);
      title.setFont(title.getFont().deriveFont(32f));
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 0;
      c.weighty = .33;
      gridbag.setConstraints(title, c);
      player1Info.add(title);

      JLabel player1Name = new JLabel("Player: " +player1.getName());
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 1;
      c.weighty = .33;
      gridbag.setConstraints(player1Name, c);
      player1Info.add(player1Name);
      
      player1List = new JList(new DefaultListModel<String>());
      try
      {
         int userId = player1.getUserId(mConn);
         Statement s1 = mConn.createStatement();
         ResultSet result = s1.executeQuery("SELECT * "
                                          + "FROM GameRoster G, Players P "
                                          + "WHERE G.Athlete = P.Id "
                                          + "AND G.UserId = " + userId);
         while (result.next())
         {
            ((DefaultListModel)player1List.getModel())
               .addElement(result.getString("P.FirstName") + " " + result.getString("P.LastName"));
         }
      } 
      catch (SQLException e)
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
      
     
      //player 2 side
      //JPanel player2Info = new JPanel(new GridLayout(3,1));
      JPanel player2Info = new JPanel(gridbag);
      
      currentPlayerLabel = new JLabel("Current Turn :" + mCurrentPlayer.getName());
      currentPlayerLabel.setFont(title.getFont().deriveFont(32f));
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 0;
      c.weighty = .33;
      gridbag.setConstraints(currentPlayerLabel, c);
      player2Info.add(currentPlayerLabel);
      JLabel player2Name = new JLabel("Player: " +player2.getName());
      c.weightx = 0.0;
      c.gridx = 0;
      c.gridy = 1;
      c.weighty = .33;
      gridbag.setConstraints(player2Name, c);
      player2Info.add(player2Name);
      player2List = new JList(new DefaultListModel<String>());
      try
      {
         int userId = player2.getUserId(mConn);
         Statement s1 = mConn.createStatement();
         ResultSet result = s1.executeQuery("SELECT * "
                                          + "FROM GameRoster G, Players P "
                                          + "WHERE G.Athlete = P.Id "
                                          + "AND G.UserId = " + userId);
         while (result.next())
         {
            ((DefaultListModel)player2List.getModel())
               .addElement(result.getString("P.FirstName") + " " + result.getString("P.LastName"));
         }
      } 
      catch (SQLException e)
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
              
      playerInfoPanel.add(player1Info);
      playerInfoPanel.add(player2Info);
      WestPanel.add(playerInfoPanel);
      
   }
   
   /*what does this function do
    * 	loads the Fantasy Frame?
    */
   private void loadList()
   {
      //AddRelScore();
      JPanel listPanel = new JPanel(new BorderLayout()); 
      model = initData();
      table = new JTable(model);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
      JScrollPane scrollPane = new JScrollPane(table);
      JTextField tableFilter = new JTextField();
      table.setRowSorter(rowSorter);
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(new JLabel("Specify a word to match:"),BorderLayout.WEST);
      panel.add(tableFilter, BorderLayout.CENTER);
      
      //generate Position Sort Panel -> Radio Buttons (current dont do anything)      
      panel.add(loadPositionSort(), BorderLayout.SOUTH);
      
      JPanel detailPanel = new JPanel(new BorderLayout());
      JLabel detailLabel = new JLabel("Information will go here about athlete with all seasons listed");
      JLabel detailFN = new JLabel();
      detailPanel.add(detailLabel, BorderLayout.NORTH);
      JPanel detailInfo = new JPanel();
      detailInfo.add(detailFN);
      JButton testButton = new JButton("TEST BUTTON :D");
      testButton.addActionListener(new ActionListener()
      {
         
         @Override
         public void actionPerformed(ActionEvent e)
         {
            RelativeScore();
            
         }
      });
      detailInfo.add(testButton);
      JButton restartEverything = new JButton("Restart Everything");
      restartEverything.addActionListener(new Restart());
      detailInfo.add(restartEverything);
      detailPanel.add(detailInfo, BorderLayout.NORTH);
      
      JButton draftButton = new JButton("Draft");
      draftButton.setBackground(Color.ORANGE);      
      detailPanel.add(draftButton, BorderLayout.SOUTH);
      
      listPanel.setPreferredSize(new Dimension(DimSizeX, DimSizeY)); // 1500, 450
      listPanel.add(panel, BorderLayout.SOUTH);
      
      /*init and load individual Athelte table*/
      playerModel = new EditableTableModel();
      JTable pTable = new JTable(playerModel);
      //JScrollPane playerScrollPane = new JScrollPane(pTable);
      detailPanel.add(pTable, BorderLayout.CENTER);
      //detailPanel.add(Hello, BorderLayout.WEST);
      //EastPanel.add(myTable, BorderLayout.EAST);
      
      EastPanel.add(detailPanel);
      listPanel.add(scrollPane, BorderLayout.CENTER);
      WestPanel.add(listPanel, 0,0);
      
      tableFilter.getDocument().addDocumentListener(new DocumentListener()
      {
         
         @Override
         public void removeUpdate(DocumentEvent e)
         {
            String text = tableFilter.getText();

            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
            
         }
         
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            String text = tableFilter.getText();

            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
            
         }
         
         @Override
         public void changedUpdate(DocumentEvent e)
         {
            throw new UnsupportedOperationException("Not supported yet.");
            
         }
      });
     
      table.addMouseListener(new MouseAdapter() 
      {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt) {
             int row = table.convertRowIndexToModel(table.rowAtPoint(evt.getPoint()));
             int col = table.convertColumnIndexToModel(table.columnAtPoint(evt.getPoint()));
             if (row >= 0 && col >= 0) 
             {
                mSelectedFN = (String) table.getModel().getValueAt(row, 0);
                mSelectedLN = (String) table.getModel().getValueAt(row, 1);
                detailFN.setText(mSelectedFN);
                mSelectedRow = row;

             }
             playerModel.alertTable();
         }
     });
     draftButton.addActionListener(new DraftOnClickListener());
      
   }
   
   private JPanel loadPositionSort()
   {
      JPanel positionSortPanel = new JPanel(new GridLayout(1, 0));
      JLabel positionSortLabel = new JLabel("Positions:");
      JRadioButton pgRB = new JRadioButton("Point Guard");
      JRadioButton sgRB = new JRadioButton("Shooting Guard");
      JRadioButton sfRB = new JRadioButton("Small Forward");
      JRadioButton pfRB = new JRadioButton("Power Forward");
      JRadioButton cRB = new JRadioButton("Center");
      pgRB.addActionListener(new TogglePointGuard());
      sgRB.addActionListener(new ToggleShootingGuard());
      sfRB.addActionListener(new ToggleSmallForward());
      pfRB.addActionListener(new TogglePowerForward());
      cRB.addActionListener(new ToggleCenter());
      positionSortPanel.add(positionSortLabel, BorderLayout.SOUTH);
      positionSortPanel.add(pgRB);
      positionSortPanel.add(sgRB);
      positionSortPanel.add(sfRB);
      positionSortPanel.add(pfRB);
      positionSortPanel.add(cRB);
      return positionSortPanel;
   }


   
   private NonEditableModel initData()
   {
      String[] colNames = {"First Name", "Last Name", "Team", "Games",
            "Points", "Assists", "Rebounds", "Steals", "Blocks", "TurnOver",
            "Field Goals", "Three Pointers", "Free Throws"};
      Object[][] data = null;
      int rowCount = 0;
      try
      {
         // max min avg
         // overall score?
         // relative rating
         Statement s1 = mConn.createStatement();
         ResultSet result = s1.executeQuery("select * "
                                          + "From Stats S, Players P, Teams T "
                                          + "WHERE P.Id = S.PlayerId AND T.Id = P.TeamId "
                                          + "AND S.Season = 2015 "
                                          + GetToggleSettings()
                                          + "AND P.Id NOT IN (SELECT Athlete FROM GameRoster)");
         result.last();
         rowCount = result.getRow();
         data = new Object[rowCount][colNames.length];
         result.first();
         int colIndex = 0;
         data[result.getRow()-1][colIndex++] = result.getString("P.FirstName");
         data[result.getRow()-1][colIndex++] = result.getString("P.LastName");
         data[result.getRow()-1][colIndex++] = result.getString("T.Abbrev");
         data[result.getRow()-1][colIndex++] = result.getInt("S.Games");   // starts         
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Points") / result.getDouble("S.Games"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Assists") / result.getDouble("S.Games"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Rebounds") / result.getDouble("S.Games"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Steals") / result.getDouble("S.Games"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Blocks") / result.getDouble("S.Games"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.TurnOver") / result.getDouble("S.Games"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.FGM") / result.getDouble("S.FGA"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.TPM") / result.getDouble("S.TPA"));
         data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.FTM") / result.getDouble("S.FTA"));
    
         while (result.next())
         {
            colIndex = 0;
            data[result.getRow()-1][colIndex++] = result.getString("P.FirstName");
            data[result.getRow()-1][colIndex++] = result.getString("P.LastName");
            data[result.getRow()-1][colIndex++] = result.getString("T.Abbrev");
            data[result.getRow()-1][colIndex++] = result.getInt("S.Games");   // starts
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Points") / result.getDouble("S.Games"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Assists") / result.getDouble("S.Games"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Rebounds") / result.getDouble("S.Games"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Steals") / result.getDouble("S.Games"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.Blocks") / result.getDouble("S.Games"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.TurnOver") / result.getDouble("S.Games"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.FGM") / result.getDouble("S.FGA"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.TPM") / result.getDouble("S.TPA"));
            data[result.getRow()-1][colIndex++] = roundMyNum(result.getDouble("S.FTM") / result.getDouble("S.FTA"));
         }
         model.setRowCount(rowCount);
      
         
      }
      catch (Exception e)
      {
         System.out.println(e);
      }
      return new NonEditableModel(data, colNames);
   }
   
   private void insertIntoGameRoster()
   {
      System.out.println(mSelectedFN + " " + mSelectedLN);
      try
      {
         int rowCount = 0;
         int athleteId = -1;
         Statement s1 = mConn.createStatement();
         //need to query the values needed to insert this given firstname and lastname of athlete
         ResultSet result = s1.executeQuery("SELECT ID "
                                          + "From Players "
                                          + "WHERE FirstName = '"  +mSelectedFN +"' "
                                          + "AND LastName = '" + mSelectedLN + "'");
         
         //get ID of athelete about to get inserted into gameroster table
         while (result.next())
         {
            athleteId = result.getInt("ID");
         }
         //get current row count
         result = s1.executeQuery("SELECT * FROM GameRoster");
         result.last();
         rowCount = result.getRow();
         
         String psText = "INSERT INTO GameRoster VALUES(?, ?, ?, ?)";
         PreparedStatement ps = mConn.prepareStatement(psText);
         ps.setInt(1, rowCount + 1);
         ps.setInt(2, mCurrentPlayer.getUserId(mConn));
         ps.setInt(3, athleteId);
         ps.setInt(4, mCurrentRound);
         
         ps.executeUpdate();
         
      }
      catch (Exception ee)
      {
         System.out.println(ee);
      }
   }
   
   private class DraftOnClickListener implements ActionListener
   {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
         //insert into GameRoster table
         insertIntoGameRoster();
         //remove row from Table since athlete has now been chosen
         ((NonEditableModel)table.getModel()).removeRow(mSelectedRow);
         if (mCurrentPlayer.getName().equals(player1.getName()))
         {
            System.out.println(mCurrentPlayer.getName());
            ((DefaultListModel<String>)player1List.getModel())
            .addElement(mSelectedFN + " " + mSelectedLN);
         }
         else
         {
            System.out.println(mCurrentPlayer.getName());
            ((DefaultListModel<String>)player2List.getModel())
            .addElement(mSelectedFN + " " + mSelectedLN);
         }
         
         player1.setCurrentTurn(!player1.getCurrentTurn());
         player2.setCurrentTurn(!player2.getCurrentTurn());
         if (player1.getCurrentTurn())
            mCurrentPlayer = player1;     
         
         else if (player2.getCurrentTurn())
            mCurrentPlayer = player2;
         
         currentPlayerLabel.setText("Current Turn :" + mCurrentPlayer.getName());

         mInternalRoundCount++;           

         if (mInternalRoundCount % 3 == 0)
         {
            mInternalRoundCount = 1;
            mCurrentRound++;
            //update current playrsr round? field might not be needed.
            title.setText(("Round " + mCurrentRound));
         }
         
         
      }
      
   }
   
   
   private class Restart implements ActionListener
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
         Point currentLoc = getLocation();
         dispose();
         //setVisible(false);
         JFrame appFrame = new PlayerInfoFrame(mConn);
         appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         appFrame.setLocation(currentLoc);
         appFrame.setVisible(true);
         
      }
      
   }
   
   private void RelativeScore(){
   
         try{
         Statement s = mConn.createStatement();
         ResultSet result = s.executeQuery("select  "
                                          + "max(Points/Games) as ppg, "
                                          + "max(Assists/Games) as apg, "
                                          + "max(Rebounds/Games) as rpg, "
                                          + "max(Steals/Games) as spg, "
                                          + "max(Blocks/Games) as bpg, "
                                          + "max(Turnover/Games) as tov "
                                          //+ "max() as fgp, "
                                          //+ "max(Points/Games) as tpp, "
                                          //+ "max(Points/Games) as ftp"
                                          + "From Stats S, Players P "
                                          + "WHERE S.Season = 2015 && P.id = S.playerid"
                                          + GetToggleSettings()
                                          + "AND P.Id NOT IN (SELECT Athlete FROM GameRoster)");                                 
         result.next();
         
         double ppg = result.getDouble("ppg");
         double apg = result.getDouble("apg");
         double rpg = result.getDouble("rpg");
         double spg = result.getDouble("spg");
         double bpg = result.getDouble("bpg");
         double tov = result.getDouble("tov");
         
         result = s.executeQuery("select "
                                          + "max(TPM/TPA) as tpp "
                                          + "From Stats S, Players P "
                                          + "WHERE S.Season = 2015 && P.id = S.playerid && S.tpa > 20"
                                          + GetToggleSettings()
                                          + "AND P.Id NOT IN (SELECT Athlete FROM GameRoster)");                                 
         result.next();
         
         double tpp = result.getDouble("tpp");
         
         result = s.executeQuery("select  "
                                          + "max(FGM/FGA) as fgp "
                                          + "From Stats S, Players P "
                                          + "WHERE S.Season = 2015 && P.id = S.playerid && S.fga > 100"
                                          + GetToggleSettings()
                                          + "AND P.Id NOT IN (SELECT Athlete FROM GameRoster)");                                 
         result.next();
         
         double fgp = result.getDouble("fgp"); 
         
         result = s.executeQuery("select  "
                                          + "max(FTM/FTA) as ftp "
                                          + "From Stats S, Players P "
                                          + "WHERE S.Season = 2015 && P.id = S.playerid && S.fta > 100"
                                          + GetToggleSettings()
                                          + "AND P.Id NOT IN (SELECT Athlete FROM GameRoster)"); 
         result.next();
         
         double ftp = result.getDouble("ftp");
         
     
         
         System.out.println("lol");
         } catch (SQLException s){
            s.printStackTrace();
            System.out.println("LOL-messedup");
         }

   }
   
   private class TogglePointGuard implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent rad){
         toggle_PG = !toggle_PG;
         refreshList();
      }
   }
   
   private class ToggleShootingGuard implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent rad){
         toggle_SG = !toggle_SG;
         refreshList();
      }
   }
   
   private class ToggleSmallForward implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent rad){
         toggle_SF = !toggle_SF;
         refreshList();
      }
   }
   
   private class TogglePowerForward implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent rad){
         toggle_PF = !toggle_PF;
         refreshList();
      }
   }
   
   private class ToggleCenter implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent rad){
         toggle_C = !toggle_C;
         refreshList();
      }
   }
   
   private void refreshList(){
      model = initData();
      table.setModel(model);
      //dfgdfgtable.();
   }
   
   private String GetToggleSettings(){
      String s = "";
      if(toggle_C || toggle_PG || toggle_SF || toggle_PF || toggle_SG){
         s = "AND (";
         int toggleCount = 0;
         if(toggle_PG){
            toggleCount++;
            s = s + "P.Position1 = 1";
         }
         if(toggle_SG){
            if(toggleCount > 0){
               s = s + " OR ";
            }
            toggleCount++;
            s = s + "P.Position1 = 2";
         }
         if(toggle_SF){
            if(toggleCount > 0){
               s = s + " OR ";
            }
            toggleCount++;
            s = s + "P.Position1 = 3";
         }
         if(toggle_PF){
            if(toggleCount > 0){
               s = s + " OR ";
            }
            toggleCount++;
            s = s + "P.Position1 = 4";
         }
         if(toggle_C){
            if(toggleCount > 0){
               s = s + " OR ";
            }
            toggleCount++;
            s = s + "P.Position1 = 5";
         }
         s = s + ")";
      }
      System.out.println(s);
      return s + " ";
   }
   
   public double roundMyNum (double tooLong) {
	   return Math.round(tooLong * 100)/100.0d;
   }
   
   class EditableTableModel extends AbstractTableModel {
	    private String[] singelAthleteColumns = {"Season", "Games", "Points", "Assists", "Rebounds", "Steals", "Blocks"};
	    private Object[][] singleAthleteData = new Object[4][7];
	    
	    public void alertTable() {
	    	System.out.println("talking to table" + mSelectedFN+ "\n");
	    	//need to call clear table, because if players only have one year
	    	resetPlayerStatTable();
	    	//data[0][0] = mSelectedFN;
	    	//data[0][1] = mSelectedLN;
	        //build sql statement
	    	try
	        {
	           Statement s1 = mConn.createStatement();
//	           System.out.println("SELECT * "
//                       + "FROM GameRoster G, Players P "
//                       + "WHERE G.Athlete = P.Id "
//                       + "AND P.FirstName = " + "\"" + mSelectedFN + "\""
//                       + " AND P.LastName = "  + "\"" + mSelectedLN + "\"");
	           ResultSet result = s1.executeQuery("SELECT * "
	                                            + "FROM Stats S, Players P "
	                                            + "WHERE S.PlayerId = P.ID "
	                                            + "AND P.FirstName = " + "'" + mSelectedFN + "'"
	                                            + " AND P.LastName = " + "'" + mSelectedLN + "'");
	           int row = 0;
	           if (result.last()) {
	        	   do
	        	   {
	        		   System.out.println("Player, Season: " + row);
	        		   this.setValueAtt(row++, result.getString("S.Season"), result.getInt("S.Games"),
	        				   result.getInt("S.Points"), result.getInt("S.Assists"), result.getInt("S.Rebounds"),
	        				   result.getInt("S.Steals"), result.getInt("S.Blocks"));
	        	   } while (result.previous());
	           }
	        } 
	        catch (SQLException e)
	        {
	           // TODO Auto-generated catch block
	           e.printStackTrace();
	        }  	

	       
	    	fireTableDataChanged();
	    }
	    public void setValueAtt(int row, String season, int games, int points, 
	    		int assists, int rebounds, int steals, int blocks) {
	        singleAthleteData[row][0] = season;
	        singleAthleteData[row][1] = games;
	        singleAthleteData[row][2] = roundMyNum (points / games);
	        singleAthleteData[row][3] = roundMyNum (assists / games);
	        singleAthleteData[row][4] = roundMyNum (rebounds / games);
	        singleAthleteData[row][5] = roundMyNum (steals / games);
	        singleAthleteData[row][6] = roundMyNum (blocks / games);
	        fireTableDataChanged();
	    }
	    
	    private void resetPlayerStatTable () {
	    	int row = 0;
	    	int col = 0;
	    	for(row = 0; row < getRowCount(); row++) {
	    		for(col = 0; col < getColumnCount(); col++) {
	    			singleAthleteData[row][col] = "";
	    		}
	    	}
	    	
	    }
	    /*Mandatory functions to implement Abtractclass*/
	    public int getColumnCount() {
	        return singelAthleteColumns.length;
	    }
	    public int getRowCount() {
	        return singleAthleteData.length;
	    }

	    public String getColumnName(int col) {
	        return singelAthleteColumns[col];
	    }

	    public Object getValueAt(int row, int col) {
	        return singleAthleteData[row][col];
	    }
	}

}
