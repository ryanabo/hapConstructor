package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class GlobalTab extends TabImp 
                       implements ActionListener
{
  PkgPanel       pkgPanel;
  VariablesPanel variablesPanel = new VariablesPanel();
  TopSimulation  topsimPanel    = new TopSimulation();
  SamplingMethod samplingPanel  = new SamplingMethod();
  ReportPanel    reportPanel    = new ReportPanel();
  //DumpPanel      dumpPanel      = new DumpPanel();
  JButton commitB;

  //SimulationPanel simulationPanel = new SimulationPanel();
  String rSeed = new String("random");
  String numSim = new String("100"); 

  public GlobalTab()
  {
    super();
    title = "Global";
  }

  public void build(GenieGUI inGUI)
  {
    super.build(inGUI);
   
    setLayout(new VerticalLayout());
    pkgPanel = new PkgPanel();
    add(pkgPanel);
    //add(variablesPanel);
    //add(dumpPanel);
    //add(samplingPanel);
    //add(topsimPanel);
  }

  public void actionPerformed ( ActionEvent ae )
  {
    Object source = ae.getSource();
    if ( source == commitB )
    {
      setDisplayOnly();
      gui.addTab("Study");
    }
  }

  public class PkgPanel extends JPanel
                        implements ActionListener
  {
    JRadioButton hapmcRB, pedGenieRB, hapConstructorRB;
    ButtonGroup pkgBgroup;
    
    public PkgPanel()
    {
      super();
      TitledEtched titledEtched = new TitledEtched("Package");
      setBorder(titledEtched.title);
      setPreferredSize(new Dimension(800, 600));
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      pedGenieRB = new JRadioButton("PedGenie");
      hapmcRB = new JRadioButton("hapMC");
      //hapConstructorRB = new JRadioButton("hapConstructor");
      pkgBgroup = new ButtonGroup();
      pkgBgroup.add(pedGenieRB);
      pkgBgroup.add(hapmcRB);
      //pkgBgroup.add(hapConstructorRB);
      pedGenieRB.setActionCommand("PedGenie");
      pedGenieRB.addActionListener(this);
      hapmcRB.setActionCommand("hapMC");
      hapmcRB.addActionListener(this);
      //hapConstructorRB.setActionCommand("hapConstructor");
      //hapConstructorRB.addActionListener(this);
      add(pedGenieRB);
      add(hapmcRB);
      //add(hapConstructorRB);
    }
  
    public void actionPerformed(ActionEvent e)
    {
      changeDisplay(e.getActionCommand());
      //updateUI();
    }
  }

  public class VariablesPanel extends JPanel
                              implements PropertyChangeListener
  {
    JTextField rSeedT, numSimT;
 
    public VariablesPanel()
    {
      GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        constraints.ipadx = 0;
        setLayout(gridbag); 
        TitledEtched titledEtched = new TitledEtched("Variables");
        setBorder(titledEtched.title);
        setPreferredSize(new Dimension(300, 95));
        JLabel rSeedL   = new JLabel("Random Number Seed ", JLabel.TRAILING);
        JLabel numSimL  = new JLabel("Number of Simulation ", JLabel.TRAILING);

        rSeedT  = new JTextField("Random", 10);
        rSeedL.setLabelFor(rSeedT);
        rSeedT.addPropertyChangeListener(this);
        numSimT = new JTextField("100", 10);
        numSimL.setLabelFor(numSimT);
        numSimT.addPropertyChangeListener(this);
        gridbag.setConstraints(rSeedL, constraints);
        gridbag.setConstraints(numSimL, constraints);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(rSeedT, constraints);
        gridbag.setConstraints(numSimT, constraints);
        add(rSeedL);
        add(rSeedT);
        add(numSimL);
        add(numSimT);
      }

      public void propertyChange(PropertyChangeEvent pce)
      {
        Object source = pce.getSource();
        if ( source == rSeedT )
          rSeed = rSeedT.getText();
        else if ( source == numSimT )
          numSim = numSimT.getText();
      }
    }

    public class TopSimulation extends JPanel
                               implements ActionListener
    {
      ButtonGroup topBgroup;
      JRadioButton alleleFreqRB, hapFreqRB, xRB;

      public TopSimulation()
      {
        super();
        TitledEtched titledEtched = new TitledEtched("Top Simulation");
        setBorder(titledEtched.title);
        setPreferredSize(new Dimension(300, 95));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  
        alleleFreqRB = new JRadioButton("AlleleFreqTopSim", true);
        hapFreqRB    = new JRadioButton("HapFreqTopSim");
        xRB    = new JRadioButton("XTopSim");
        alleleFreqRB.setActionCommand("AlleleFreqTopSim");
        hapFreqRB.setActionCommand("HapFreqTopSim");
        xRB.setActionCommand("XTopSim");
        topBgroup  = new ButtonGroup();
        topBgroup.add(alleleFreqRB);
        topBgroup.add(hapFreqRB);
        topBgroup.add(xRB);
        alleleFreqRB.addActionListener(this);
        hapFreqRB.addActionListener(this);
        xRB.addActionListener(this);
        add(alleleFreqRB);
        add(hapFreqRB);
        add(xRB);
      }

      public void actionPerformed(ActionEvent e)
      {}

    }

    public class SamplingMethod extends JPanel
                                implements ActionListener
    {
      JRadioButton allRB, founderRB;
      ButtonGroup samplingBgroup;

      public SamplingMethod()
      {
        TitledEtched titledEtched = new TitledEtched
                                        ("Sampling Method");
        setBorder(titledEtched.title);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 95));
        allRB              = new JRadioButton("all");
        founderRB          = new JRadioButton("founder");
        allRB.setActionCommand("all");
        founderRB.setActionCommand("founder");
        allRB.setSelected(true);
        samplingBgroup = new ButtonGroup();
        samplingBgroup.add(allRB);
        samplingBgroup.add(founderRB);
        add(allRB);
        add(founderRB);
        allRB.addActionListener(this);
        founderRB.addActionListener(this);
      }

      public void actionPerformed(ActionEvent e)
      {}
    }

    public class ReportPanel extends JPanel
                             implements ActionListener
    {
      JRadioButton detailRB, summaryRB, bothRB;
      ButtonGroup reportBgroup;

      public ReportPanel()
      {
        TitledEtched titledEtched = new TitledEtched
                                        ("Report");
        setBorder(titledEtched.title);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 95));
        detailRB           = new JRadioButton("Detail Report");
        summaryRB          = new JRadioButton("Summary Report");
        bothRB             = new JRadioButton("Summary and Detail Reports");
        detailRB.setActionCommand("report");
        summaryRB.setActionCommand("summary");
        bothRB.setActionCommand("both");
        detailRB.setSelected(true);
        reportBgroup = new ButtonGroup();
        reportBgroup.add(detailRB);
        reportBgroup.add(summaryRB);
        reportBgroup.add(bothRB);
        add(detailRB);
        add(summaryRB);
        add(bothRB);
        detailRB.addActionListener(this);
        summaryRB.addActionListener(this);
        bothRB.addActionListener(this);
      }

      public void actionPerformed(ActionEvent e)
      {}
    }


/*
    public class DumpPanel extends JPanel
                           implements ActionListener
    {
      JRadioButton nodumpB, genodumpB, haplodumpB, tdtdumpB, descentdumpB;
      ButtonGroup dumperBG;

      public DumpPanel ()
      {
        TitledEtched titledEtched = new TitledEtched ("Dump Data");
        setBorder(titledEtched.title);
        setLayout(new GridLayout(5,1));
        nodumpB      = new JRadioButton("no dumping", true);
        genodumpB    = new JRadioButton("genotype data pre-makeped file");
        haplodumpB   = new JRadioButton("haplotype with frequency");
        tdtdumpB     = new JRadioButton("genotype with variable for TDT input");
        descentdumpB = new JRadioButton("genotype data in html format");
        dumperBG     = new ButtonGroup();
        dumperBG.add(nodumpB);
        dumperBG.add(genodumpB);
        dumperBG.add(haplodumpB);
        dumperBG.add(tdtdumpB);
        dumperBG.add(descentdumpB);
        nodumpB.addActionListener(this);
        genodumpB.addActionListener(this);
        haplodumpB.addActionListener(this);
        tdtdumpB.addActionListener(this);
        descentdumpB.addActionListener(this);
        add(nodumpB);
        add(genodumpB);
        add(tdtdumpB);
        add(descentdumpB);
        add(haplodumpB);
        haplodumpB.setVisible(false);
      }
 
      public void updateDisplay ( boolean isHapMC )
      {
        haplodumpB.setVisible(isHapMC);
        updateUI();
      }

      public void actionPerformed ( ActionEvent ae )
      {}
    }
*/
        
    public void changeDisplay(String pkgName)
    {
      removeAll();
      JPanel panel = new JPanel();
      panel.setPreferredSize(new Dimension(UIConstants.SCREEN_WIDTH, 
					   UIConstants.SCREEN_HEIGHT));
      panel.setLayout(new BorderLayout());
      panel.setOpaque(true);
      JPanel pkginfo = new JPanel();
      pkginfo.setLayout(new VerticalLayout());
      pkginfo.setOpaque(true);
      commitB = new JButton("Commit and Build Study Tab");
      commitB.addActionListener(this);
      pkginfo.add(variablesPanel);
      pkginfo.add(samplingPanel);
      //add(dumpPanel);
      if (pkgName.equals("PedGenie"))
      {
        pkginfo.add(topsimPanel);
      }
      pkginfo.add(reportPanel);
      panel.add(pkginfo, BorderLayout.CENTER);
      panel.add(commitB, BorderLayout.SOUTH);
      add(panel);
    }
  //}

  public String getPkg()
  { return pkgPanel.pkgBgroup.getSelection().getActionCommand(); }
  
  public String getRseed()
  { return variablesPanel.rSeedT.getText(); }

  public String getNsim()
  { return variablesPanel.numSimT.getText(); }

  public String getTop()
  { 
    if (getPkg().equals("hapMC"))
      return new String("hapMCTopSeparate");
    else
      return topsimPanel.topBgroup.getSelection().getActionCommand(); 
  }

  public String getDrop()
  {
    if (getPkg().equals("hapMC"))
      return new String("hapMCDropSeparate");
    else if ( getTop().equals("XTopSim") )
      return new String("XDropSim");
    else
      return new String("DropSim");
  }

  public String getSampling()
  { return samplingPanel.samplingBgroup.getSelection().getActionCommand();}

  public String getReport()
  { return reportPanel.reportBgroup.getSelection().getActionCommand(); }

  public VariablesPanel getVariablesPanel()
  { System.out.println("getVariablesPanel");
    return variablesPanel; }
  /*
  public String getDump()
  { return dumpPanel.dumpBgroup.getSelection().getActionCommand(); }
  */

  public void setDisplayOnly() 
  {
    Util.disable(commitB);
    Util.disable(variablesPanel.rSeedT);
    Util.disable(variablesPanel.numSimT);
    Util.disable(samplingPanel.samplingBgroup);
    Util.disable(reportPanel.reportBgroup);
    Util.disable(topsimPanel.topBgroup);
  }
}
