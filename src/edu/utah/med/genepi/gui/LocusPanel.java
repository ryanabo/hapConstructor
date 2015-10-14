package edu.utah.med.genepi.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.utah.med.genepi.gm.LocusImp;

public class LocusPanel extends JPanel
                        implements ItemListener, ActionListener
{
  Vector<LocusImp> locusV = new Vector<LocusImp>();
  JCheckBox[] locusCB;
  JTextField[] distText;
  JTextField[] locusNameText;
  int numLocus;

  public LocusPanel(int nLocus, String[] name)
  {
    super();
    numLocus = nLocus;
    setLayout(new GridLayout(numLocus + 1, 3));
    TitledEtched titleEtched = new TitledEtched("Locus Infomation");
    setBorder(titleEtched.title);
    JLabel idLabel = new JLabel("Locus id");
    JLabel distLabel = new JLabel("distance to next locus");
    JLabel markerLabel = new JLabel("marker name");
    idLabel.setVerticalAlignment(SwingConstants.TOP);
    distLabel.setVerticalAlignment(SwingConstants.TOP);
    markerLabel.setVerticalAlignment(SwingConstants.TOP);
    add(idLabel);
    add(distLabel);
    add(markerLabel);
    locusCB = new JCheckBox[numLocus];
    distText = new JTextField[numLocus];
    locusNameText = new JTextField[numLocus];
    for ( int i = 0; i < numLocus; i++ )
    {
      locusCB[i] = new JCheckBox(String.valueOf(i+1));
      distText[i] = new JTextField(5);
      locusNameText[i] = new JTextField(name[i]);
      add(locusCB[i]);
      add(distText[i]);
      add(locusNameText[i]);
      locusCB[i].addItemListener(this);
      distText[i].addActionListener(this);
      locusNameText[i].addActionListener(this);
    }
  }

  public void actionPerformed (ActionEvent e)
  {
    /*
    Object source = e.getSource();
    for ( int i = 0; i < numLocus; i++ )
    {
      if ( locusCB[i].isSelected() && 
                ( source == distText[i] || source == locusNameText[i] ) )
      {
        double distVal;
        if ((distText[i].getText()).length() == 0)
          distVal = Double.NaN;
        else
          distVal = new Double(distText[i].getText()).doubleValue();
        LocusImp locus = new LocusImp( i+1,
                             locusNameText[i].getText(),
                             distVal,
                             true ); 
        locusV.remove(locus);
        locusV.addElement(locus);
      }
    }
    */
  }

  public void itemStateChanged (ItemEvent e)
  {
    /*
    Object source = e.getItemSelectable();
    for ( int i = 0; i < numLocus; i++ )
    { 
      if ( source == locusCB[i] )
      { 
        double distVal;
        if ((distText[i].getText()).length() == 0 )
          distVal = Double.NaN;
        else
          distVal = new Double(distText[i].getText()).doubleValue();
        LocusImp locus = new LocusImp( i+1,
                             locusNameText[i].getText(),
                             distVal,
                             true ); 
        if ( e.getStateChange() == ItemEvent.SELECTED )
          locusV.addElement(locus);
        else if ( e.getStateChange() == ItemEvent.DESELECTED )
          locusV.remove(locus);
      }
    }
    */
  }
  
  public LocusImp[] getLocusInfo()
  {
    for ( int i = 0; i < numLocus; i++ )
    {
      if ( locusCB[i].isSelected() )
      {
        double distVal;
        if ((distText[i].getText()).length() == 0)
          distVal = Double.NaN;
        else
          distVal = new Double(distText[i].getText()).doubleValue();
        LocusImp locus = new LocusImp( i+1,
                             locusNameText[i].getText(),
                             distVal,
                             true );
        locusV.addElement(locus);
      }
    }
    return (LocusImp[]) locusV.toArray(new LocusImp[0]);
  }
}

