import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import javax.swing.*;

public class WinnerFrame extends JFrame
{
   private int DimSizeX = 500; // 1500 for windows, 750 for other
   private int DimSizeY = 200; // 450 for windows, 350 for other
   private Connection mConn;
   private Player mPlayer1;
   private Player mPlayer2;
   private JPanel WestPanel, EastPanel;
   public WinnerFrame(Connection conn, Player player1, Player player2)
   {
      mConn = conn;
      mPlayer1 = player1;
      mPlayer2 = player2;

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
      
      setLayout(new BorderLayout());
      WestPanel = new JPanel();
      WestPanel.setSize(new Dimension(DimSizeX, DimSizeY));
      EastPanel = new JPanel();
      JButton winnerButton = new JButton("WINNER?");
      createEastPanel();
      createWestPanel();
      getContentPane().add(WestPanel, BorderLayout.WEST);
      getContentPane().add(winnerButton, BorderLayout.CENTER);
      getContentPane().add(EastPanel, BorderLayout.EAST);
      setResizable(true);
      //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
      JPanel player2Info = new JPanel(new GridLayout(2,1));
      GridBagConstraints c = new GridBagConstraints();
      JList player2List;
      
      JLabel player2Name = new JLabel("Player: " +mPlayer2.getName());
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
      EastPanel.add(player2Info);
              
   }
   
   private void createWestPanel()
   {
      JPanel playerInfoPanel = new JPanel(new GridLayout(1, 2));
      GridBagLayout gridbag = new GridBagLayout();
      JPanel player1Info = new JPanel(gridbag);
      GridBagConstraints c = new GridBagConstraints();
      JList player1List;
      


      JLabel player1Name = new JLabel("Player: " +mPlayer1.getName());
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
      WestPanel.add(player1Info);
      
   }
         
   

}
