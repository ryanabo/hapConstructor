package edu.utah.med.genepi.gui2;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

public class ColumnGroupDetail extends DetailPanelImp
                               implements ActionListener
{
  JButton patternhelpB;
  //String detailTitle = null;
  //int openChildCount = 0;
  //DefaultMutableTreeNode parentNode;
  int nLocus = 0;
  JTextField[] patternT;
  String[] locusName = null;

  public ColumnGroupDetail()
  {
    super();
    allowsChildren = false;
  }

  public void buildDetail(int value)
  {
    //super(inPanel, inTitle, index);
    //tree = inPanel.tree;
      index = value;
      title = "Column Group";
      setPreferredSize(new Dimension(600, 400));
      //parentNode = inParentNode;
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      setLayout(gridbag);
      TitledEtched subtitle = new TitledEtched(title + " " + index);
      setBorder(subtitle.title);
      // use the same layeredPane from ColumnDetail 
      DefaultMutableTreeNode grandNode = (DefaultMutableTreeNode) parentNode.getParent();
      AnalysisDetail analysisdetail = null;
      try
      {
        analysisdetail = (AnalysisDetail) grandNode.getUserObject();
      }
      catch ( Exception ee )
      {
        System.out.println("error : " + ee.getMessage());
      }
      nLocus = analysisdetail.getnumLocus();;
      JLabel locusL = new JLabel("Locus");
      JLabel patternL = new JLabel("Pattern");
      patternT = new JTextField[nLocus];
      JLabel[] locusNameL = new JLabel[nLocus];
      patternhelpB = new JButton(createImageIcon("images/Help24.gif",
                                    "pattern help button"));
      patternhelpB.setContentAreaFilled(false);
      patternhelpB.setBorderPainted(false);
      patternhelpB.setHorizontalAlignment(JLabel.LEFT);
      patternhelpB.setActionCommand("patternthelp");
      patternhelpB.addActionListener(this);

      // constraints for all objects
      constraints.gridheight = 2;
      constraints.anchor = GridBagConstraints.WEST;

      // constraints for Locus label
      constraints.ipadx = 20;
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.gridwidth = 20;
      gridbag.setConstraints(locusL, constraints);
      add(locusL);
      
      // constraints for pattern Label
      constraints.gridx = 60;
      constraints.gridwidth = 30;
      gridbag.setConstraints(patternL, constraints);
      add(patternL);

      // constraints for pattern help button
      constraints.gridx = 100;
      constraints.gridy = 0;
      constraints.gridwidth = 5;
      gridbag.setConstraints(patternhelpB, constraints);
      add(patternhelpB);


      for ( int i = 0 ; i < nLocus; i++ )
      {
        locusNameL[i] = new JLabel("Marker " + (i+1));
        patternT[i] = new JTextField(20);

        // constraints for locusName label
        constraints.gridx = 0;
        constraints.gridy = 4 * i + 4;
        constraints.gridwidth = 20;
        gridbag.setConstraints(locusNameL[i], constraints);

        // constraints for locusPatternT field
        constraints.gridx = 60;
        //constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridwidth = 30;
        gridbag.setConstraints(patternT[i], constraints);

        add(locusNameL[i]);
        add(patternT[i]);
      }
  }

  public void actionPerformed ( ActionEvent ae )
  {
    Object source = ae.getSource();
    if ( source == patternhelpB )
      JOptionPane.showMessageDialog(this,
                  "Enter allelic or haplotype pattern for this column. Need more details");
  }

  public String[] getPattern()
  {
    String[] pattern = new String[patternT.length];
    for ( int i = 0; i < pattern.length; i++ )
    {
      pattern[i] = patternT[i].getText();
    }
    return pattern;
  }

  public void setDisplayOnly()
  {
    for (  int i = 0; i < patternT.length; i++ )
      Util.disable(patternT[i]);
  }  
} 

