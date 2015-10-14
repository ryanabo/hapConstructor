package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainPanel extends JPanel
                       implements ActionListener
{

  public MainPanel()
  {
    setLayout(new BorderLayout());
    JButton button2 = new JButton("2");
    button2.addActionListener(this);
    add(button2);
  }
 
  public void displayPanel( String pkgName, String panelName )
  {
    Object newPanel = Class.forName(pkgName + "." + panelName);
    this.setVisible(false);
    newPanel.setVisible(true);
  }

  public void actionPerformed( ActionEvent e )
  {}
      
}
