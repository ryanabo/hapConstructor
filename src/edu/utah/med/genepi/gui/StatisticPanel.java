package edu.utah.med.genepi.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatisticPanel extends JPanel
                               implements ActionListener, ItemListener
{
  Vector<String> statisticV = new Vector<String>();
  JFileChooser filechooser;
  JCheckBox[] statisticCB; 
  JLabel quantfileL;
  int numStats = 7;
  String[] statsName;
  private final String quantfile = "         quantfile : ";
  String filename = "no file selected";
  File file;
  public StatisticPanel() 
  {
    super();
    //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    TitledEtched titledEtched = new TitledEtched("Statistic");
    setBorder(titledEtched.title);

    statisticCB = new JCheckBox[numStats];
    statsName = new String[] {"Chi Squared", "Chi Squared Trend", "Odds Ratios",
                          "Trio TDT", "Sib TDT", "Comb TDT", "Quantitative"};
    quantfileL        = new JLabel(quantfile + filename);
    for ( int i = 0; i < statisticCB.length; i++ )
    {
      statisticCB[i] = new JCheckBox(statsName[i]);
      statisticCB[i].addItemListener(this);
      add(statisticCB[i]);
    }
    add(quantfileL);
  }

  public void itemStateChanged(ItemEvent e)
  {
    JCheckBox source = (JCheckBox) e.getItemSelectable();
    if (e.getStateChange() == ItemEvent.SELECTED)
    {
      if ( source == statisticCB[6] )
      {
        filechooser = new JFileChooser();
        filechooser.setDialogTitle("Select quantitative file");
        int returnVal = filechooser.showOpenDialog(StatisticPanel.this);
        if ( returnVal == JFileChooser.APPROVE_OPTION)
        {
          file = filechooser.getSelectedFile();
          quantfileL.setText(quantfile + file.getName());
        }
        else 
        { statisticCB[6].setSelected(false); }
      }
    }
    else if (e.getStateChange() == ItemEvent.DESELECTED )
    { 
      if ( source == statisticCB[6] )
      {
        quantfileL.setText(quantfile + "no file selected"); 
      }
    }
  }
 
  public void actionPerformed(ActionEvent e) 
  {
  }

  //Quit the application.
  protected void quit() 
  {
      System.exit(0);
  }

  public File getquantfile()
  { 
    if ( file != null )
      return file; 
    else return null;
  }

  public String[] getStatistics()
  {
    for ( int i = 0; i < numStats; i++ )
    {
      if ( statisticCB[i].isSelected() )
        statisticV.addElement(statsName[i].replaceAll(" ", ""));
    }
    String[] statistics = (String[]) statisticV.toArray(new String[0]);
    return statistics;
  }
}
