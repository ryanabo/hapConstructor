package edu.utah.med.genepi.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class LeftPanel extends JPanel
                       implements ActionListener
{

  public LeftPanel()
  {
    setLayout(new BorderLayout());
    JButton button1 = new JButton("1");
    button1.addActionListener(this);
    add(button1);
  }
 
  public void actionPerformed( ActionEvent e )
  {}
      
}
