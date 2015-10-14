package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class StatisticTab extends TabImp 
                          implements ActionListener
{
  String title = "Statistics";
  StatisticDetail statisticdetail;
  JButton commitB;

  public StatisticTab()
  {
    super();
    title = "Statistic";
  }
  
  public void setTitle ( String inTitle )
  {
    title = inTitle;
  }

  public void build(GenieGUI inGUI) 
  {
    super.build(inGUI);
    commitB = new JButton("Commit and build Analysis Tab");
    commitB.addActionListener(this);
    statisticdetail = new StatisticDetail();
    if ( statisticdetail != null )
    {
      statisticdetail.buildPanel();
      statisticdetail.setOpaque(true);
      add(statisticdetail, BorderLayout.CENTER);
      add(commitB, BorderLayout.PAGE_END);
    }
    else
      JOptionPane.showMessageDialog
                  ( this, 
                    "Failed to create Statistic Detail Screen");
  }
 
  public void actionPerformed(ActionEvent e) 
  {
    Object source = e.getSource();
    if ( source == commitB )
    {
      int nStats = (statisticdetail.getSelectedStats()).length;
      if ( nStats > 0 )
      {
        setDisplayOnly();
        gui.addTab("Analysis");
      }
      else
        JOptionPane.showMessageDialog
                   ( this,
                     "Select Statistic for Analysis");
    }
  }
  
  public void setDisplayOnly()
  {
    statisticdetail.setDisplayOnly();
    Util.disable(commitB);
  }

  //Quit the application.
  protected void quit() 
  {
      System.exit(0);
  }

  public StatisticDetail getStatisticDetail()
  {
    return statisticdetail;
  }

}
