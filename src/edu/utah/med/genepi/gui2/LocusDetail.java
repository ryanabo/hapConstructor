package edu.utah.med.genepi.gui2;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.utah.med.genepi.gm.LocusImp;

public class LocusDetail extends JPanel
                         implements ItemListener,
                                    ActionListener
{
  JCheckBox[] locusCB;
  JTextField[] distText, locusNameText;
  JLabel[] distL, locusNameL;
  int nLocus;
  String[] locusName;
  int screenwidth = 400;

  public LocusDetail( String inTitle, 
                      int numLocus,
                      String[] inName,
                      String[] inDist,
                      String[] inGene )
  {
    super();
    //setPreferredSize( new Dimension(300,200));
    nLocus = numLocus;
    JPanel panel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(panel);
    panel.setLayout(new GridLayout(nLocus + 1, 3));
    TitledEtched titleEtched = new TitledEtched(inTitle);
    panel.setBorder(titleEtched.title);
    JLabel idLabel = new JLabel("ID");
    JLabel markerLabel = new JLabel("Name");
    JLabel distLabel = new JLabel("Distance ");
    //JLabel geneLabel = new JLabel("Gene ID");
    idLabel.setVerticalAlignment(JLabel.TOP);
    distLabel.setVerticalAlignment(JLabel.TOP);
    markerLabel.setVerticalAlignment(JLabel.TOP);
    //geneLabel.setVerticalAlignment(JLabel.TOP);
    panel.add(idLabel);
    panel.add(markerLabel);
    panel.add(distLabel);
    //panel.add(geneLabel);
    locusCB = new JCheckBox[nLocus];
    distText = new JTextField[nLocus];
    locusNameText = new JTextField[nLocus];
    //geneText = new JTextField[nLocus];
    distL = new JLabel[nLocus];
    locusNameL = new JLabel[nLocus];
    //geneL = new JLabel[nLocus];
    int height = (nLocus + 1) * UIConstants.TEXT_HEIGHT;
    if ( height > UIConstants.SCREEN_HEIGHT )
      panel.setPreferredSize( new Dimension(screenwidth, UIConstants.SCREEN_HEIGHT));
    else 
      panel.setPreferredSize( new Dimension(screenwidth, height) );

    for ( int i = 0; i < nLocus; i++ )
    {
      locusCB[i] = new JCheckBox(String.valueOf(i+1), true);
      locusCB[i].setPreferredSize(new Dimension(UIConstants.TEXT_WIDTH, 
 	     		                        UIConstants.TEXT_HEIGHT));
      locusNameText[i] = new JTextField(inName[i]);
      locusNameText[i].setPreferredSize(new Dimension(UIConstants.TEXT_WIDTH, 
 						      UIConstants.TEXT_HEIGHT));
      distText[i] = new JTextField(5);
      distText[i].setPreferredSize(new Dimension(UIConstants.TEXT_WIDTH,
                                                 UIConstants.TEXT_HEIGHT));
      //geneText[i] = new JTextField(2);
      panel.add(locusCB[i]);
      panel.add(locusNameText[i]);
      panel.add(distText[i]);
      //panel.add(geneText[i]);
      distText[i].addActionListener(this);
      locusNameText[i].addActionListener(this);
      //geneText[i].addActionListener(this);
      locusCB[i].addItemListener(this);
    }
    add(scrollPane);
  }

  public String toString()
  { return "Locus Detail"; }

  public void setDisplayOnly()
  {
    Util.disable(locusCB);
    Util.disable(locusNameText);
    Util.disable(distText);
    //Util.disable(geneText);
  }

  //public LocusImp[] getSelectedLocus(boolean subset)
  public LocusImp[] getSelectedLocus()
  {
    Vector<LocusImp> locusV = new Vector<LocusImp>();
    for ( int i = 0; i < locusCB.length; i++ )
    {
      if ( locusCB[i].isSelected() )
      {
        double distVal;
        if ( (distText[i].getText()).length() == 0 )
          distVal = 0;
        else
          distVal = new Double(distText[i].getText()).doubleValue();

        //String geneT = geneText[i].getText();
        //int geneVal = -1;
        //if ( geneT.length() != 0 )
        //  geneVal = new Integer(geneT).intValue(); 
        LocusImp locus = new LocusImp( i+1,
                                       locusNameText[i].getText(),
                                       distVal,
                                       true, 
                                       0 );
        locusV.addElement(locus);
      }
    }
    return (LocusImp[]) locusV.toArray(new LocusImp[0]);
  }

  public void actionPerformed (ActionEvent e)
  {}

  public void itemStateChanged ( ItemEvent ie )
  {}
  
}

