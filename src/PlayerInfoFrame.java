import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;


public class PlayerInfoFrame extends JFrame
{
   private JPanel top, bottom;
   private JTextField player1Name, player2Name;
   private Player player1, player2;
   private Connection mConn;

   public PlayerInfoFrame(Connection conn)
   {
      mConn = conn;
      setLayout(new GridLayout(2, 1));
      setSize(new Dimension(1500, 800));
      createPlayer1Panel();
      createPlayer2Panel();

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

   private void createPlayer1Panel()
   {
      // top size + border + design
      top = new JPanel();
      top.setPreferredSize(new Dimension(1500, 400));
      top.setLayout(new BorderLayout());
      top.setBorder(new EtchedBorder());
      
      //welcome label
      JLabel welcome = new JLabel("Welcome to NBA Fantasy Draft!");
      welcome.setHorizontalAlignment(SwingConstants.CENTER);
      top.add(welcome, BorderLayout.NORTH);

      // top label
      JPanel header = new JPanel(new GridLayout(2, 1));
      JLabel player1Label = new JLabel("Enter Player 1 Information");
      player1Label.setHorizontalAlignment(SwingConstants.CENTER);
      header.add(player1Label);


      // enter player name
      JPanel namePanel = new JPanel();
      JLabel playerNameLabel = new JLabel("Enter Player Name");
      player1Name = new JTextField(16);
      namePanel.add(playerNameLabel);
      namePanel.add(player1Name);
      header.add(namePanel);
      top.add(header, BorderLayout.CENTER);
      
      
      JLabel image1 = new JLabel(new ImageIcon("src/Images/image1.jpg"));
      top.add(image1, BorderLayout.WEST);

      // add to frame
      getContentPane().add(top, BorderLayout.NORTH);
      
   }

   private void createPlayer2Panel()
   {
      // bottom size + border + design
      bottom = new JPanel();
      bottom.setPreferredSize(new Dimension(1500, 400));
      bottom.setLayout(new BorderLayout());
      bottom.setBorder(new EtchedBorder());
      
      JLabel welcome = new JLabel("");
      welcome.setHorizontalAlignment(SwingConstants.CENTER);
      bottom.add(welcome, BorderLayout.NORTH);
      
      //bottom label
      JPanel header = new JPanel(new GridLayout(2,1));
      JLabel player2Label = new JLabel("Enter Player 2 Information");
      player2Label.setHorizontalAlignment(SwingConstants.CENTER);
      header.add(player2Label);

      // enter player name
      JPanel namePanel = new JPanel();
      JLabel playerNameLabel = new JLabel("Enter Player Name");
      player2Name = new JTextField(16);
      namePanel.add(playerNameLabel);
      namePanel.add(player2Name);
      header.add(namePanel);
      bottom.add(header, BorderLayout.CENTER);
      //submit button
      JPanel submitPanel = new JPanel();
      JButton submit = new JButton("Submit");
      submit.setSize(new Dimension(1,50));
      submit.setToolTipText("Click when both Players' information has been entered");
      submitPanel.add(submit);
      bottom.add(submitPanel, BorderLayout.SOUTH);
      submit.addActionListener(new SubmitOnClickListener());
      
      JLabel image2 = new JLabel(new ImageIcon("src/Images/image2.jpg"));
      bottom.add(image2, BorderLayout.EAST);

      // add to frame
      getContentPane().add(bottom, BorderLayout.SOUTH);
   }
   
   private class SubmitOnClickListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         if (player1Name.getText() != null && !player1Name.getText().isEmpty()
               && player2Name.getText() != null && !player2Name.getText().isEmpty()
               && !player1Name.getText().equals(player2Name.getText()))
         {
            player1 = new Player(player1Name.getText());
            player1.setCurrentTurn(true);

            player2 = new Player(player2Name.getText());
            System.out.println("player 1 : " + player1.getName() + "\nplayer 2 : " + player2.getName());
            try
            {
               Statement s1 = mConn.createStatement();
               s1.executeUpdate("INSERT INTO CurrentGame VALUES(1, " + "'" + player1.getName() + "', "
                     + "0, '1')");
               s1.executeUpdate("INSERT INTO CurrentGame VALUES(2, " + "'" + player2.getName() + "', "
                     + "0, '0')");
               
            }
            catch (Exception ee)
            {
               System.out.println(ee);
            }
   //         try
   //         {
   //            mConn.close();
   //         }
   //         catch (Exception ee)
   //         {
   //            System.out.println("Unable to close Connection");
   //         }
   //         System.out.println("Connection Closed");
            
            //setVisible(false);
            Point currentLoc = getLocation();
            setVisible(false);
            dispose();
            JFrame appFrame = new FantasyFrame(mConn, 0, player1, player2);
            appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            appFrame.setLocation(currentLoc);
            appFrame.setVisible(true);
         }
         else 
         {
            JOptionPane.showMessageDialog(null, "Names cannot be blank or the same!!");
         }
         
      }  
      
   }
   
      
}
