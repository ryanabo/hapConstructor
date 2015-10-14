package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class GlobalPanel extends JPanel
{
  PkgPanel pkgPanel               = new PkgPanel();
  SimulationPanel simulationPanel = new SimulationPanel();

  public GlobalPanel()
  {
    setLayout(new BorderLayout());
    setMinimumSize(new Dimension(300,300));
    add(pkgPanel, BorderLayout.WEST);
    add(simulationPanel, BorderLayout.CENTER);
  }

  public class PkgPanel extends JPanel
                        implements ActionListener
  {
    JRadioButton hapmcRB, pedGenieRB;
    ButtonGroup pkgBgroup;
    
    public PkgPanel()
    {
      super();
      TitledEtched titledEtched = new TitledEtched("Package");
      setBorder(titledEtched.title);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      pedGenieRB = new JRadioButton("PedGenie", true);
      hapmcRB = new JRadioButton("hapMC");
      pkgBgroup = new ButtonGroup();
      pkgBgroup.add(pedGenieRB);
      pkgBgroup.add(hapmcRB);
      pedGenieRB.addActionListener(this);
      hapmcRB.addActionListener(this);
      add(pedGenieRB);
      add(hapmcRB);
    }
  
    public void actionPerformed(ActionEvent e)
    {
      simulationPanel.methodsPanel.setDisplay(e.getActionCommand());
      simulationPanel.methodsPanel.updateUI();
    }
  }

  public class SimulationPanel extends JPanel
  {
    VariablesPanel variablesPanel = new VariablesPanel();
    MethodsPanel methodsPanel     = new MethodsPanel();
    public SimulationPanel()
    {
      setLayout(new BorderLayout());
      //super();
      add(variablesPanel, BorderLayout.NORTH);
      add(methodsPanel, BorderLayout.SOUTH);
    }
  }

  public class VariablesPanel extends JPanel
                              implements ActionListener
  {
    JTextField rseedF, numSimF;

    public VariablesPanel()
    {
      setLayout(new GridLayout(2,2)); 
      JLabel rseedL   = new JLabel("Random Number Seed ", JLabel.TRAILING);
      JLabel numSimL  = new JLabel("Number of Simulation ", JLabel.TRAILING);
      /*
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
      rseedSpinner =  new JSpinner(rseedModel);
      numSimSpinner =  new JSpinner(numSimModel);
      rseedL.setLabelFor(rseedSpinner);
      numSimL.setLabelFor(numSimSpinner);
      */

      rseedF  = new JTextField("666");
      rseedL.setLabelFor(rseedF);
      rseedF.addActionListener(this);
      numSimF = new JTextField("1000");
      numSimL.setLabelFor(numSimF);
      numSimF.addActionListener(this);
      add(rseedL);
      add(rseedF);
      add(numSimL);
      add(numSimF);
    }

    public void actionPerformed(ActionEvent e) 
    {}
  }

  public class MethodsPanel extends JPanel
                           implements ActionListener
  {
    TopSimulation  topPanel      = new TopSimulation();
    DropSimulation dropPanel     = new DropSimulation();
    SamplingMethod samplingPanel = new SamplingMethod();
    Dumper         dumpPanel     = new Dumper();

    public MethodsPanel()
    {
      super();
      setLayout( new GridLayout(2, 2) );
      setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      // setup 4 sections within this panel
      add(topPanel);
      add(dropPanel);
      add(samplingPanel);
      add(dumpPanel);
    }
  
    public void setDisplay(String pkgName)
    {
      removeAll();
      if (pkgName.equals("hapMC"))
      {
        add(samplingPanel);
        add(dumpPanel);
      }
      else 
      {
        add(topPanel);
        add(dropPanel);
        add(samplingPanel);
        add(dumpPanel);
      }
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

      JRadioButton alleleFreqRB = new JRadioButton("AlleleFreqTopSim", true);
      JRadioButton hapFreqRB    = new JRadioButton("HapFreqTopSim");
      alleleFreqRB.setActionCommand("AlleleFreqTopSim");
      hapFreqRB.setActionCommand("HapFreqTopSim");
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

  public class SamplingMethod extends JPanel
                          implements ActionListener
  {
    ButtonGroup SamplingBgroup;

    public SamplingMethod()
    {
      TitledEtched titledEtched = new TitledEtched
                                      ("Sampling Method");
      setBorder(titledEtched.title);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      //JRadioButton geneCounterRB      = new JRadioButton("GeneCounter");
      JRadioButton allRB              = new JRadioButton("all");
      JRadioButton founderRB          = new JRadioButton("founder");
      //geneCounterRB.setActionCommand("GeneCounter");
      allRB.setActionCommand("all");
      founderRB.setActionCommand("founder");
      allRB.setSelected(true);
      SamplingBgroup = new ButtonGroup();
      //SamplingBgroup.add(geneCounterRB);
      SamplingBgroup.add(allRB);
      SamplingBgroup.add(founderRB);
      //add(geneCounterRB);
      add(allRB);
      add(founderRB);
      //geneCounterRB.addActionListener(this);
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

  public String getPkg()
  { return pkgPanel.pkgBgroup.getSelection().getActionCommand(); }
  
  public String getRseed()
  { return simulationPanel.variablesPanel.rseedF.getText(); }

  public String getNsim()
  { return simulationPanel.variablesPanel.numSimF.getText(); }

  public String getTop()
  { 
    if (getPkg().equals("hapMC"))
      return new String("hapMCTopSim");
    else
      return simulationPanel.methodsPanel.topPanel.topBgroup.getSelection().getActionCommand(); 
  }

  public String getDrop()
  {
    if (getPkg().equals("hapMC"))
      return new String("hapMCDropSim");
    else 
      return simulationPanel.methodsPanel.dropPanel.dropBgroup.getSelection().getActionCommand();
  }

  public String getSampling()
  { return simulationPanel.methodsPanel.samplingPanel.SamplingBgroup.getSelection().getActionCommand();}

  public String getDump()
  { return simulationPanel.methodsPanel.dumpPanel.dumpBgroup.getSelection().getActionCommand(); }

}
