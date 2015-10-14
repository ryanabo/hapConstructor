package edu.utah.med.genepi.gui2;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import edu.utah.med.genepi.util.GEException;

public class CCTableFrame extends JInternalFrame 
                          implements ActionListener
{
    static int openFrameCount = 0;
    //static final int xOffset = 50, yOffset = 50;
    JPanel panel;
    JButton completedB, cancelB;
    ButtonGroup typeBgroup, modelBgroup;
    JRadioButton genotypeB, alleleB, dominantB, recessiveB, additiveB;
    JTextField lociText, statsText; 
    String cctType, cctLoci, cctStats, cctModel, message = null;
    CCTable cctable;
    boolean quitIndicator = false;

    public CCTableFrame() 
    {
        super("Subset Analyse #" + (++openFrameCount), 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        //...Create the GUI and put it in the window...

        //...Then set the window size or call pack...
        setSize(10,10);

        //Set the window's location.
        //setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        /*Dimension panelSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelwidth = (new Double(panelSize.getWidth() / 2)).intValue();
        int panelheight = (new Double(panelSize.getHeight() / 2)).intValue();
        */

        //Add button and textfield to frame
        panel = new JPanel();
        setContentPane(panel);
        //panel.setSize(panelwidth, panelheight);
        panel.setLayout(new BorderLayout());

        JPanel northPanel = north();
        JPanel southPanel = south();
        JPanel centerPanel = center();
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(southPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);
    }
        
    protected JPanel center()
    {
      //JPanel panel     = new JPanel(new SpringLayout());
      //lociText.setPreferredSize(new Dimension(20, 5));
      JPanel panel     = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      JLabel note1L = new JLabel("Please note you can only select the statistic that was selected in the statistic panel above.");
      JLabel note2L = new JLabel("Enter the Locus # and separate it by a comma. To select the 3rd locus on the locus list which has an id = 4, you should enter 3 in the loci field, not 4");
      JLabel statsL = new JLabel("Stats");
      lociText         = new JTextField(20);
      statsText        = new JTextField(20);
      statsL.setLabelFor(statsText);
      panel.add(note2L );
      panel.add(lociText );
      panel.add(note1L );
      panel.add(statsText);
      lociText.addActionListener(this);
      statsText.addActionListener(this);
      return panel;
    }

    protected JPanel north()
    {
      JPanel panel = new JPanel(new GridLayout(1,2));
      panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      JPanel north1Panel = north1();
      JPanel north2Panel = north2();
      panel.add(north1Panel);
      panel.add(north2Panel);
      return panel;
    }
  
    protected JPanel north1()
    {
      JPanel panel = new JPanel(); 
      panel.setLayout(new GridLayout(3, 1));
      panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
 
      JLabel typeLabel = new JLabel("Type");
      alleleB = new JRadioButton("Allele");
      alleleB.setActionCommand("Allele");
      genotypeB = new JRadioButton("Genotype");
      genotypeB.setActionCommand("Genotype");
      genotypeB.setSelected(true);
      typeBgroup = new ButtonGroup();
      typeBgroup.add(genotypeB);
      typeBgroup.add(alleleB);
      genotypeB.addActionListener(this);
      alleleB.addActionListener(this);
      panel.add(typeLabel);
      panel.add(genotypeB); 
      panel.add(alleleB); 
      return panel;
    }

    protected JPanel north2()
    {
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(5, 1));
      panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

      JLabel typeLabel = new JLabel("Model");
      JRadioButton dominantRB = new JRadioButton("Dominant");
      dominantRB.setActionCommand("Dominant");
      dominantRB.setSelected(true);
      JRadioButton recessiveRB = new JRadioButton("Recessive");
      recessiveRB.setActionCommand("Recessive");
      JRadioButton additiveRB = new JRadioButton("Additive");
      additiveRB.setActionCommand("Additive");
      JRadioButton alleleRB = new JRadioButton("Allele");
      alleleRB.setActionCommand("Allele");
      modelBgroup = new ButtonGroup();
      modelBgroup.add(dominantRB);
      modelBgroup.add(recessiveRB);
      modelBgroup.add(additiveRB);
      modelBgroup.add(alleleRB);
      dominantRB.addActionListener(this);
      recessiveRB.addActionListener(this);
      additiveRB.addActionListener(this);
      alleleRB.addActionListener(this);
      panel.add(typeLabel);
      panel.add(dominantRB);
      panel.add(recessiveRB);
      panel.add(additiveRB);
      panel.add(alleleRB);
      return panel;
    }

    protected JPanel south()
    {
      JPanel panel = new JPanel(); 
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      completedB = new JButton("Completed Analysis " + (openFrameCount));
      cancelB      = new JButton("Cancel");
      completedB.setAlignmentX(Component.CENTER_ALIGNMENT);
      cancelB.setAlignmentX(Component.CENTER_ALIGNMENT);
      // add ActionListener to objects
      completedB.addActionListener(this);
      cancelB.addActionListener(this);
      // add objects to panel
      panel.add(completedB);
      panel.add(cancelB);
      return panel;
    }
   
    public void actionPerformed(ActionEvent e)
    {
      if ( e.getSource() == completedB )
      { 
        quitIndicator = false;
        //if ( genotypeB.isSelected() ) 
        //  cctType = "genotype";
        //else if ( alleleB.isSelected() )
        //  cctType = "allele";
        cctLoci  = lociText.getText();
        cctStats = statsText.getText();
        cctType  = typeBgroup.getSelection().getActionCommand();
        cctModel = modelBgroup.getSelection().getActionCommand();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        completed();
      }
      if ( e.getSource() == cancelB )
      {
        quitIndicator = true;
        try {setClosed(true);}
        catch (Exception ex) {}
        //completed();
      }
    }
    public String returnMessage()
    {
      return "ok";
    }
 
    public void completed()
    { setVisible(false);}

  //-------------------------------------------------------------------------
  public CCTable getcctable() throws GEException
  { 
    //if ( cctModel.length() > 0  )
    //{ 
    try 
    {  return new CCTable(cctType, cctLoci, cctStats, cctModel); }
    catch ( Exception e )
    { throw new GEException ("Can't create CCTable instance." + e ); }
    //}
    //else return null;
  }

  //---------------------------------------------------------------------------
  public boolean getQuitIndicator()
  { return quitIndicator; }
}
