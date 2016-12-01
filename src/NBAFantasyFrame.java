import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class NBAFantasyFrame extends JFrame
{
   private JPanel top, bottom;
   private JTextField player1Name, player2Name;
   private Player player1, player2;

   public NBAFantasyFrame()
   {
      setLayout(new GridLayout(2, 1));
      setSize(new Dimension(1000, 300));
      createPlayer1Panel();
      createPlayer2Panel();

      setResizable(false);
      pack();
   }

   private void createPlayer1Panel()
   {
      // top size + border + design
      top = new JPanel();
      top.setPreferredSize(new Dimension(1000, 300));
      top.setLayout(new GridLayout(2,1));
      top.setBorder(new EtchedBorder());

      // top label
      JPanel header = new JPanel();
      JLabel player1Label = new JLabel("Enter Player 1 Information");
      header.add(player1Label);
      top.add(header);

      // enter player name
      JPanel namePanel = new JPanel();
      JLabel playerNameLabel = new JLabel("Enter Player Name");
      player1Name = new JTextField(16);
      namePanel.add(playerNameLabel);
      namePanel.add(player1Name);
      top.add(namePanel);

      // add to frame
      getContentPane().add(top, BorderLayout.NORTH);
   }

   private void createPlayer2Panel()
   {
      // bottom size + border + design
      bottom = new JPanel();
      bottom.setPreferredSize(new Dimension(1000, 300));
      bottom.setLayout(new GridLayout(3,1));
      bottom.setBorder(new EtchedBorder());
      
      //bottom label
      JPanel header = new JPanel();
      JLabel player2Label = new JLabel("Enter Player 2 Information");
      header.add(player2Label);
      bottom.add(header);

      // enter player name
      JPanel namePanel = new JPanel();
      JLabel playerNameLabel = new JLabel("Enter Player Name");
      player2Name = new JTextField(16);
      namePanel.add(playerNameLabel);
      namePanel.add(player2Name);
      bottom.add(namePanel);
      
      //submit button
      JPanel submitPanel = new JPanel();
      JButton submit = new JButton("Submit");
      submit.setSize(new Dimension(1,50));
      submitPanel.add(submit);
      bottom.add(submitPanel);
      submit.addActionListener(new SubmitOnClickListener());

      // add to frame
      getContentPane().add(bottom, BorderLayout.SOUTH);
   }
   
   private class SubmitOnClickListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         if (player1Name.getText() != null || !player1Name.getText().isEmpty())
         {
            player1 = new Player(player1Name.getText());
         }
         if (player2Name.getText() != null || !player2Name.getText().isEmpty())
         {
            player2 = new Player(player2Name.getText());
         }
         System.out.println("player 1 : " + player1.getName() + "\nplayer 2 : " + player2.getName());
         
      }
      
   }

}
