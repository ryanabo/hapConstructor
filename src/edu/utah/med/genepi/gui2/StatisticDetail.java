package edu.utah.med.genepi.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class StatisticDetail extends JPanel
                             implements ItemListener, ActionListener
{
  JCheckBox[] statisticCB;
  Statistic[] statistics;
  String title;

  public StatisticDetail()
  {
    super();
    title = "Statistic";
  }

  public void buildPanel()
  {
    boolean checkitem = true;
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    TitledEtched titledEtched = new TitledEtched(title);
    this.setBorder(titledEtched.title);
    if ( statistics == null || statistics.length == 0 )
    {
      statistics = Statistic.initializeStats();
      checkitem = false;
    }

    statisticCB = new JCheckBox[statistics.length];

    for ( int i = 0; i < statistics.length; i++ )
    {
      statisticCB[i] = new JCheckBox(statistics[i].displayName, checkitem);
      statisticCB[i].addItemListener(this);
      this.add(statisticCB[i]);
    }
  }

  public void setStatistic( Statistic[] inStats )
  {
    statistics = inStats;
  }
  
  /*
  public class Statistic
  {
    int index;
    String programName;
    String displayName;
    String group;

    public Statistic ( int inNum, String inProg,
                           String inDisplay, String inGroup )
    {
      index = inNum;
      programName = inProg;
      displayName = inDisplay;
      group = inGroup;
    }
  }
  public Statistic[] initializeStats ()
  {
    Statistic[] s = new Statistic[16];
    s[0] = new Statistic(1,"ChiSquared", "Chi Squared", "s");
    s[1] = new Statistic(2, "ChiSquaredTrend",
                         "Chi Squared Trend", "s");
    s[2] = new Statistic(3, "OddsRatios", "Odds Ratios", "s");
    s[3] = new Statistic(4, "OddsRatiosWithCI", 
                         "Odds Ratios With Confidence Intervals", "s");
    s[4] = new Statistic(5, "TrioTDT", "Trio TDT", "s");
    s[5] = new Statistic(6, "SibTDT", "Sib TDT", "s");
    s[6] = new Statistic(7, "CombTDT", "Combine TDT", "s");
    s[7] = new Statistic(8, "Quantitative",
                         "Quantitative","s");
    s[8] = new Statistic(9, "CMHChiSquared",
                         "CMH Chi Squared", "m");
    s[9] = new Statistic(10, "CMHChiSqTrend",
                         "CMH Chi Squared Trend", "m");
    s[10] = new Statistic(11, "MetaOddsRatios",
                         "Meta Odds Ratios", "m");
    s[11] = new Statistic(12, "MetaOddsRatiosWithCI",
                         "Meta Odds Ratios With Confidence Intervals", "m");
    s[12] = new Statistic(13, "MetaSibTDT", "Meta Sib TDT", "m");
    s[13] = new Statistic(14, "MetaTrioTDT", "Meta Trio TDT", "m");
    s[14] = new Statistic(15, "MetaCombTDT", "Meta Combine TDT", "m");
    s[15] = new Statistic(16, "MetaInteractionOR", 
                          "2-Loci Interaction statistic","m");
    s[16] = new Statistic(17, "MetaInteractionORWithCI", 
                          "2-Loci Interaction with Confidence Intervals","m");
    s[17] = new Statistic(18, "HWE", 
                          "Hardy Weinberg Equilibrium","s");
    s[18] = new Statistic(19, "MetaQtestOR", 
                          "Q test Odds Ratio","m");
    return s;
  }
  **/

  public void itemStateChanged(ItemEvent e)
  {
  }

  public void actionPerformed(ActionEvent e)
  {
  }

  //Quit the application.
  protected void quit()
  {
      System.exit(0);
  }
  public Statistic[] getSelectedStats()
  {
    ArrayList<Statistic> statisticl = new ArrayList<Statistic>();
    for ( int i = 0; i < statistics.length; i++ )
    {
      if ( statisticCB[i].isSelected() )
        statisticl.add(statistics[i]);
    }
    return (Statistic[]) statisticl.toArray(new Statistic[0]);
  }

  public void setDisplayOnly()
  {
     Util.disable(statisticCB);
  }
}

