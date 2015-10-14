package edu.utah.med.genepi.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;

public class SimulationPanel extends JPanel
                             implements ActionListener
{
  VariablesPanel variablesPanel = new VariablesPanel();
  MethodsPanel   methodsPanel   = new MethodsPanel();

  public SimulationPanel()
  {
    super();
    setLayout(new BorderLayout());
    add(variablesPanel, BorderLayout.NORTH);
    add(methodsPanel, BorderLayout.SOUTH);
  }
  
  public void actionPerformed ( ActionEvent e )
  {}

  public class VariablesPanel extends JPanel
                             implements ActionListener
  {
    JSpinner rseedSpinner, numSimSpinner;

    public VariablesPanel()
    {
      super(); 
      setLayout(new GridLayout(1,2));
      JLabel rseedL  = new JLabel("Random Number Seed ", JLabel.TRAILING);
      JLabel numSimL = new JLabel("Number of Simulation ", JLabel.TRAILING);
      Integer defRseedVal      = new Integer(777);
      Integer minRseedVal      = new Integer(0);
      Integer maxRseedVal      = new Integer(999);
      Integer stepRseedVal     = new Integer(1);
      Integer defNumSimVal     = new Integer(100);
      Integer minNumSimVal     = new Integer(1);
      Integer maxNumSimVal     = new Integer(10000);
      Integer stepNumSimVal    = new Integer(10);
      SpinnerNumberModel rseedModel = new SpinnerNumberModel(defRseedVal,
                                                             minRseedVal,
                                                             maxRseedVal,
                                                             stepRseedVal );
      SpinnerNumberModel numSimModel = new SpinnerNumberModel(defNumSimVal,
                                                              minNumSimVal,
                                                              maxNumSimVal,
                                                              stepNumSimVal );
      //JTextField rseedF  = new JTextField(20);
      //rseedL.setLabelFor(rseedF);
      //rseedF.addActionListener(this);
      //JTextField numSimF = new JTextField(20);
      rseedSpinner =  new JSpinner(rseedModel);
      numSimSpinner =  new JSpinner(numSimModel);
      rseedL.setLabelFor(rseedSpinner);
      numSimL.setLabelFor(numSimSpinner);

      add(rseedL);
      add(rseedSpinner);
      add(numSimL);
      add(numSimSpinner);
    }

    public void actionPerformed(ActionEvent e) 
    {}
  }

  public class MethodsPanel extends JPanel
                           implements ActionListener
  {
    TopSimulation  topPanel  = new TopSimulation();
    DropSimulation dropPanel = new DropSimulation();
    FreqMethod     freqPanel = new FreqMethod();
    Dumper         dumpPanel = new Dumper();
    public MethodsPanel()
    {
      super();
      setLayout( new GridLayout(1, 4) );
      setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      // setup 4 sections within this panel
      add(topPanel);
      add(dropPanel);
      add(freqPanel);
      add(dumpPanel);
    }
    
    public void actionPerformed(ActionEvent e)
    {}
  }
 
  public class TopSimulation extends JPanel
                             implements ActionListener
  {
    ButtonGroup topBgroup;
    public TopSimulation()
    {
      super();
      TitledEtched titledEtched = new TitledEtched("Top Simulation");
      setBorder(titledEtched.title);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JRadioButton alleleFreqRB = new JRadioButton("AlleleFreqTopSim");
      JRadioButton hapFreqRB    = new JRadioButton("HapFreqTopSim");
      alleleFreqRB.setActionCommand("AlleleFreqTopSim");
      hapFreqRB.setActionCommand("HapFreqTopSim");
      alleleFreqRB.setSelected(true);
      topBgroup  = new ButtonGroup();
      topBgroup.add(alleleFreqRB);
      topBgroup.add(hapFreqRB);
      alleleFreqRB.addActionListener(this);
      hapFreqRB.addActionListener(this);
      add(alleleFreqRB);
      add(hapFreqRB);
    }

    public void actionPerformed(ActionEvent e)
    {}
  }

  public class DropSimulation extends JPanel
                              implements ActionListener
  {
    ButtonGroup dropBgroup;
    public DropSimulation()
    {
      TitledEtched titledEtched = new TitledEtched("Drop Simulation");
      setBorder(titledEtched.title);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JRadioButton dropSimRB          = new JRadioButton("DropSim");
      dropSimRB.setActionCommand("DropSim");
      dropSimRB.setSelected(true);
      dropBgroup = new ButtonGroup();
      dropBgroup.add(dropSimRB);
      dropSimRB.addActionListener(this);
      add(dropSimRB);
    }
    
    public void actionPerformed(ActionEvent e)
    {}
  }

  public class FreqMethod extends JPanel
                          implements ActionListener
  {
    ButtonGroup freqBgroup;
    public FreqMethod()
    {
      TitledEtched titledEtched = new TitledEtched
                                      ("Allele Frequency Calculation");
      setBorder(titledEtched.title);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      JRadioButton geneCounterRB      = new JRadioButton("GeneCounter");
      JRadioButton allRB              = new JRadioButton("all");
      JRadioButton founderRB          = new JRadioButton("founder");
      geneCounterRB.setActionCommand("GeneCounter");
      allRB.setActionCommand("all");
      founderRB.setActionCommand("founder");
      allRB.setSelected(true);
      freqBgroup = new ButtonGroup();
      freqBgroup.add(geneCounterRB);
      freqBgroup.add(allRB);
      freqBgroup.add(founderRB);
      add(geneCounterRB);
      add(allRB);
      add(founderRB);
      geneCounterRB.addActionListener(this);
      allRB.addActionListener(this);
      founderRB.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)
    {}
  }

  public class Dumper extends JPanel
                      implements ActionListener
  {
    ButtonGroup dumpBgroup;
    public Dumper()
    {
      super();
      TitledEtched titledEtched = new TitledEtched("Dump Simulated Data");
      setBorder(titledEtched.title);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JRadioButton dumpYesRB        = new JRadioButton("Yes");
      JRadioButton dumpNoRB         = new JRadioButton("No");
      dumpYesRB.setActionCommand("Yes");
      dumpNoRB.setActionCommand("No");
      dumpNoRB.setSelected(true);
      dumpBgroup = new ButtonGroup();
      dumpBgroup.add(dumpYesRB);
      dumpBgroup.add(dumpNoRB);
      dumpYesRB.addActionListener(this);
      dumpNoRB.addActionListener(this);
      add(dumpYesRB);
      add(dumpNoRB);
    }

    public void actionPerformed(ActionEvent e)
    {}
  }

  public Integer getRseed()
  { return (Integer) variablesPanel.rseedSpinner.getValue(); }

  public Integer getNsim()
  { return (Integer) variablesPanel.numSimSpinner.getValue(); }

  public String getTop()
  { return  methodsPanel.topPanel.topBgroup.getSelection().getActionCommand(); }

  public String getDrop()
  { return methodsPanel.dropPanel.dropBgroup.getSelection().getActionCommand();}

  public String getFreq()
  { return methodsPanel.freqPanel.freqBgroup.getSelection().getActionCommand();}

  public String getDump()
  { return methodsPanel.dumpPanel.dumpBgroup.getSelection().getActionCommand(); }

}
