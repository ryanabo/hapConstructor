package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import edu.utah.med.genepi.gm.LocusImp;

public class LocusTab extends TabImp 
                      implements ActionListener
{
  JButton commitB;
  int numLocus;
  String[] locusName;
  LocusDetail locusdetail;
  LocusImp[] locus;

  public LocusTab()
  {  
    super();
    title = "Locus";
  }

  public void build(GenieGUI inGUI)
  {
    super.build(inGUI);
    commitB = new JButton("Commit and Build Statistic Tab");
    commitB.addActionListener(this);
    locusdetail = createDetailPanel();
    if (locusdetail != null)
    {
      locusdetail.setOpaque(true);
      add(locusdetail, BorderLayout.CENTER);
      add(commitB, BorderLayout.SOUTH);
    }
    else 
      JOptionPane.showMessageDialog(this, 
                                    "Failed to create Locus Detail Screen");
  }
   
  public LocusDetail createDetailPanel()
  {
    StudyTab studytab = (StudyTab) gui.getTab("Study"); 
    numLocus = studytab.studyPanel.getNumLocus();
    locusName = studytab.studyPanel.getLocusName();
 
    if ( numLocus == 0 )
      JOptionPane.showMessageDialog( this,
                  "No locus info, please selected genotype file");
    else
    {
      //getInfoB.setVisible(false);
      locusdetail = new LocusDetail( "Locus Information from Genotype File",
                                     numLocus, 
                                     locusName,
                                     null,
                                     null );
    }
    return locusdetail;
  }

  public void actionPerformed (ActionEvent e)
  {
    Object source = e.getSource();
    if ( source == commitB )
    {
      // set all locusdetail textfields to static
      locus = locusdetail.getSelectedLocus();     
      locusdetail.setDisplayOnly();
      commitB.setVisible(false);
      gui.addTab("Statistic");
    }
  }

  public LocusDetail getLocusDetail()
  {  return locusdetail; }

  public LocusImp[] getSelectedLocus()
  {
    return locus;
  }
}

