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
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class FantasyFrame extends JFrame
{
   private int mCurrentRound;
   private Player player1, player2;
   private Player mCurrentPlayer;
   private Connection mConn;
   private String mSelectedFN, mSelectedLN;
   private JTable table;
   private JList player1List, player2List;
   private JPanel WestPanel, EastPanel;
   private JLabel title, currentPlayerLabel;
   private int mInternalRoundCount;
   private int mSelectedRow;
   
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
      setSize(new Dimension(1500, 450));
      WestPanel = new JPanel(new GridLayout(2,1));
      EastPanel = new JPanel(new GridLayout(1,1));
      loadCurrentTeam();
      loadList();
      getContentPane().add(WestPanel);
      getContentPane().add(EastPanel);
      setResizable(false);
      pack();
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
   
   private void loadList()
   {
      JPanel listPanel = new JPanel(new BorderLayout()); 
      NonEditableModel model = initData();
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
      JButton closeConnectionsForTesting = new JButton("CLOSE CONNECTION");
      closeConnectionsForTesting.addActionListener(new CloseConnection());
      detailInfo.add(closeConnectionsForTesting);
      JButton restartEverything = new JButton("Restart Everything");
      restartEverything.addActionListener(new Restart());
      detailInfo.add(restartEverything);
      detailPanel.add(detailInfo, BorderLayout.CENTER);
      
      JButton draftButton = new JButton("Draft");
      draftButton.setBackground(Color.ORANGE);      
      detailPanel.add(draftButton, BorderLayout.SOUTH);
      listPanel.setPreferredSize(new Dimension(1500, 450));
      listPanel.add(panel, BorderLayout.SOUTH);
      
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
      String[] colNames = {"First Name", "Last Name", "Age", "Team", "Games",
            "Starts", "Points", "Assists", "Rebounds", "Steals", "Blocks", "TurnOver",
            "Field Goals", "Three Pointers", "Free Throws"};
      Object[][] data = null;
      int rowCount = 0;
      try
      {
         Statement s1 = mConn.createStatement();
         ResultSet result = s1.executeQuery("select * "
                                          + "From Stats S, Players P, Teams T "
                                          + "WHERE P.Id = S.PlayerId AND T.Id = S.TeamId "
                                          + "AND S.Season = 2015 "
                                          + "AND P.Id NOT IN (SELECT Athlete FROM GameRoster)");
         result.last();
         rowCount = result.getRow();
         data = new Object[rowCount][colNames.length];
         result.first();
         data[result.getRow()-1][0] = result.getString("P.FirstName");
         data[result.getRow()-1][1] = result.getString("P.LastName");
         data[result.getRow()-1][2] = result.getInt("S.Age");
         data[result.getRow()-1][3] = result.getString("T.Name");
         data[result.getRow()-1][4] = result.getInt("S.Games");
         data[result.getRow()-1][5] = result.getInt("S.Starts");
         data[result.getRow()-1][6] = result.getInt("S.Points");
         data[result.getRow()-1][7] = result.getInt("S.Assists");
         data[result.getRow()-1][8] = result.getInt("S.Rebounds");
         data[result.getRow()-1][9] = result.getInt("S.Steals");
         data[result.getRow()-1][10] = result.getInt("S.Blocks");
         data[result.getRow()-1][11] = result.getInt("S.TurnOver");
         data[result.getRow()-1][12] = result.getDouble("S.FGM") / result.getDouble("S.FGA");
         data[result.getRow()-1][13] = result.getDouble("S.TPM") / result.getDouble("S.TPA");
         data[result.getRow()-1][14] = result.getDouble("S.FTM") / result.getDouble("S.FTA");
         while (result.next())
         {
            data[result.getRow()-1][0] = result.getString("P.FirstName");
            data[result.getRow()-1][1] = result.getString("P.LastName");
            data[result.getRow()-1][2] = result.getInt("S.Age");
            data[result.getRow()-1][3] = result.getString("T.Name");
            data[result.getRow()-1][4] = result.getInt("S.Games");
            data[result.getRow()-1][5] = result.getInt("S.Starts");
            data[result.getRow()-1][6] = result.getInt("S.Points");
            data[result.getRow()-1][7] = result.getInt("S.Assists");
            data[result.getRow()-1][8] = result.getInt("S.Rebounds");
            data[result.getRow()-1][9] = result.getInt("S.Steals");
            data[result.getRow()-1][10] = result.getInt("S.Blocks");
            data[result.getRow()-1][11] = result.getInt("S.TurnOver");
            data[result.getRow()-1][12] = result.getDouble("S.FGM") / result.getDouble("S.FGA");
            data[result.getRow()-1][13] = result.getDouble("S.TPM") / result.getDouble("S.TPA");
            data[result.getRow()-1][14] = result.getDouble("S.FTM") / result.getDouble("S.FTA");
         }
      
         
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
   
   private class CloseConnection implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
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
      
   }
   
   private class Restart implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
         NBACreateTable.removeTables(mConn);
         Point currentLoc = getLocation();
         dispose();
         //setVisible(false);
         JFrame appFrame = new PlayerInfoFrame(mConn);
         appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         appFrame.setLocation(currentLoc);
         appFrame.setVisible(true);
         
      }
      
   }

}
