import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class FantasyFrame extends JFrame
{
   private int mCurrentRound;
   private Player player1, player2;
   private ArrayList<Athlete> fakeList;
   private Player mCurrentPlayer;
   private Connection mConn;
   private String mSelectedFN, mSelectedLN;
   
   public FantasyFrame(Connection conn, int currentRound, Player player1, Player player2)
   {
      mConn = conn;
      mCurrentRound = currentRound;
      this.player1 = player1;
      this.player2 = player2;
      if (player1.getCurrentTurn())
         mCurrentPlayer = player1;
      else if (player2.getCurrentTurn())
         mCurrentPlayer = player2;
      
      setLayout(new GridLayout(2,2));
      setSize(new Dimension(1500, 450));
      loadCurrentTeam();
      loadList();
      setResizable(false);
      pack();
   }
   
   private void loadCurrentTeam()
   {
      
      JPanel player1Info = new JPanel();
      player1Info.setPreferredSize(new Dimension(500, 150));
      player1Info.setLayout(new GridLayout(4, 1));
      JLabel title = new JLabel("Round " + mCurrentRound);
      title.setFont(title.getFont().deriveFont(36f));
      player1Info.add(title);
      JLabel player1Name = new JLabel("Player: " +player1.getName());
      player1Info.add(player1Name);
      
      mCurrentPlayer.addAthlete(new Athlete("Bob", "Saget", "C", 69));
      String player1AthleteNames = "";
      for (int i = 0; i < player1.getAllAthletes().size(); i++)
      {
         player1AthleteNames += mCurrentPlayer.getAllAthletes().get(i).getFirstName() + " "
               + mCurrentPlayer.getAllAthletes().get(i).getLastName() + " ";
      }
      JLabel player1Athletes = new JLabel("Current Roster: " + player1AthleteNames);
      player1Info.add(player1Athletes);
      JButton closeConnectionsForTesting = new JButton("CLOSE CONNECTION");
      closeConnectionsForTesting.addActionListener(new CloseConnection());
      player1Info.add(closeConnectionsForTesting);
            
      
      getContentPane().add(player1Info, 1,0);
      
   }
   
   private void loadList()
   {
      JPanel listPanel = new JPanel(new BorderLayout()); 
      NonEditableModel model = initData();
      JTable table = new JTable(model);
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
      detailPanel.add(detailFN, BorderLayout.CENTER);
      JButton draftButton = new JButton("Draft");
      draftButton.setBackground(Color.ORANGE);
      draftButton.addActionListener(new DraftOnClickListener());
      detailPanel.add(draftButton, BorderLayout.SOUTH);
      listPanel.setPreferredSize(new Dimension(1500, 450));
      listPanel.add(panel, BorderLayout.SOUTH);
      listPanel.add(detailPanel, BorderLayout.EAST);
      listPanel.add(scrollPane, BorderLayout.CENTER);
      getContentPane().add(listPanel, 0,0);
      
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
             int row = table.rowAtPoint(evt.getPoint());
             int col = table.columnAtPoint(evt.getPoint());
             if (row >= 0 && col >= 0) 
             {
                mSelectedFN = (String) table.getModel().getValueAt(row, 0);
                mSelectedLN = (String) table.getModel().getValueAt(row, 1);
                detailFN.setText(mSelectedFN);

             }
         }
     });
 
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

   public class NonEditableModel extends DefaultTableModel 
   {
      NonEditableModel(Object[][] data, String[] columnNames) 
      {
         super(data, columnNames);
      }

      @Override
      public boolean isCellEditable(int row, int column) {
          return false;
      }
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
                                          + "AND S.Season = 2015");
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
   
   private class DraftOnClickListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
         System.out.println(mSelectedFN + " " + mSelectedLN);
         try
         {
            Statement s1 = mConn.createStatement();
            //need to query the values needed to insert this given firstname and lastname of athlete
            //s1.executeUpdate("INSERT INTO GameRoster VALUES()");
            
         }
         catch (Exception ee)
         {
            System.out.println(ee);
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

}
