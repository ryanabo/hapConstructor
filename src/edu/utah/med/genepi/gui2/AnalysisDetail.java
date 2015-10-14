package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class AnalysisDetail extends DetailPanelImp 
                            implements ActionListener
{
      int childPaneCount = 0;
      String[] type;
      String initialString = "no file selected";
      File[] file;
      ButtonGroup typeBG, locusBG, statsBG, repeatBG;
      JRadioButton genotypeB, alleleB, allStatsB, getStatsB,
                   repeatLociB, repeatSlidingWB, noActionB;
      JLabel locusL, statL, typeL, modelL, numLocusL, repeatL;
      JTextField numLocusT, modelT;
      JButton typeHelpB, repeatHelpB, locusHelpB, statHelpB, infoCommitB, statsCommitB, locusCommitB;
      JTabbedPane tabbedPane;
      JPanel infoPanel;
      JScrollPane scrollLocus;
      LocusDetail subsetLocusDetail;
      StatisticDetail subsetStatsDetail;
      AnalysisTab analysistab;
      AnalysisTab.AnalysisPanel analysispanel;
      HashMap<String, JPanel> tabmap;

      public AnalysisDetail()
      {
        super();
        childname = "Column";
        allowsChildren = true;
        tabmap = new HashMap<String, JPanel>();
      }
      
      public void buildDetail(int value)
      {
        index = value;
        title = "Detail";
        setPreferredSize(new Dimension(600, 400));
        setLayout(new BorderLayout());
        TitledEtched titledEtched = new TitledEtched(title + " " + index);
        setBorder(titledEtched.title);
          
        tabbedPane = new JTabbedPane();
        analysispanel = (AnalysisTab.AnalysisPanel) treexdetailpanel;
        JPanel panel =  buildSubsetStatisticDetail();
        tabbedPane.addTab("Subset Statistics", panel);
        add(tabbedPane);
      } //end buildDetail()
   
      // infoPanel 
      public JPanel buildCCtableInfoPanel()
      {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        infoPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        infoPanel.setLayout(gridbag);
        typeL = new JLabel("Analysis Type");
        genotypeB = new JRadioButton("Genotype");
        genotypeB.setActionCommand("Genotype");
        genotypeB.setSelected(true);
        genotypeB.addActionListener(this);
        alleleB = new JRadioButton("Allelic");
        alleleB.setActionCommand("Allele");
        alleleB.addActionListener(this);
        typeBG = new ButtonGroup();
        typeBG.add(genotypeB);
        typeBG.add(alleleB);
        modelL = new JLabel("Model Name ");
        modelT = new JTextField("", 15);
        modelT.setPreferredSize(new Dimension(15, 4));
        repeatL = new JLabel("Repeat Analysis Method");
        //noActionB = new JRadioButton("No Repeat Method");
        //noActionB.setSelected(true);
        //noActionB.setActionCommand("NoAction");
        //noActionB.addActionListener(this);
        repeatLociB = new JRadioButton("RepeatLoci");
        repeatLociB.setActionCommand("RepeatLoci");
        repeatLociB.addActionListener(this);
        repeatSlidingWB = new JRadioButton("RepeatSlidingWindow");
        repeatSlidingWB.setActionCommand("RepeatSlidingWindow");
        repeatSlidingWB.addActionListener(this);
        repeatBG = new ButtonGroup();
        //repeatBG.add(noActionB);
        repeatBG.add(repeatLociB);
        repeatBG.add(repeatSlidingWB);
        numLocusL = new JLabel("Number of Locus ");
        numLocusT = new JTextField(Integer.toString(getSubsetLocusCount()), 15);
        numLocusT.setPreferredSize(new Dimension(15, 4));
        typeHelpB = new JButton(createImageIcon("images/Help24.gif",
                                                "type help button"));
        typeHelpB.setContentAreaFilled(false);
        typeHelpB.setBorderPainted(false);
        typeHelpB.setHorizontalAlignment(JLabel.LEFT);
        typeHelpB.addActionListener(this);
        typeHelpB.setActionCommand("typeHelp");
        repeatHelpB = new JButton(createImageIcon("images/Help24.gif",
                                                "type help button"));
        repeatHelpB.setContentAreaFilled(false);
        repeatHelpB.setBorderPainted(false);
        repeatHelpB.setHorizontalAlignment(JLabel.LEFT);
        repeatHelpB.addActionListener(this);
        repeatHelpB.setActionCommand("repeatHelp");
        infoCommitB = new JButton("Build Column Weight");
        infoCommitB.addActionListener(this);
        infoCommitB.setActionCommand("infoCommit");
  
        //constraints.gridwidth = GridBagConstraints.REMAINDER;
        //constraints.anchor = GridBagConstraints.LINE_START;
        constraints.ipadx = 10;
        constraints.ipady = 10;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 20;
        constraints.gridheight = 2;
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(typeL, constraints);
        constraints.gridx = 30;
        gridbag.setConstraints(genotypeB, constraints);
        constraints.gridx = 55;
        gridbag.setConstraints(alleleB, constraints);
        constraints.gridx = 80;
        gridbag.setConstraints(typeHelpB, constraints);
        constraints.gridy += 20;
        constraints.gridx = 0;
        gridbag.setConstraints(repeatL, constraints);
        constraints.gridx = 30;
        gridbag.setConstraints(repeatLociB, constraints);
        //gridbag.setConstraints(noActionB, constraints);
        constraints.gridx = 55;
        gridbag.setConstraints(repeatSlidingWB, constraints);
        constraints.gridx = 80;
        gridbag.setConstraints(repeatHelpB, constraints);
        constraints.ipady = 15;
        constraints.gridy += 30;
        constraints.gridx = 0;
        gridbag.setConstraints(modelL, constraints);
        constraints.gridx = 40;
        gridbag.setConstraints(modelT, constraints);
        constraints.gridy += 40;
        constraints.gridx = 0;
        gridbag.setConstraints(numLocusL, constraints);
        constraints.gridx = 40;
        gridbag.setConstraints(numLocusT, constraints);
      
        infoPanel.add(typeL);
        infoPanel.add(genotypeB);
        infoPanel.add(alleleB);
        infoPanel.add(typeHelpB);
        infoPanel.add(modelL);
        infoPanel.add(modelT);
        infoPanel.add(repeatL);
        infoPanel.add(repeatLociB);
        infoPanel.add(repeatSlidingWB);
        infoPanel.add(repeatHelpB);
        infoPanel.add(numLocusL);
        infoPanel.add(numLocusT);
     
	panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(infoCommitB, BorderLayout.SOUTH);

        tabmap.put("info", panel);
        return panel;
      }
  
      public JPanel buildSubsetLocusPanel()
      {
        // subset locus panel
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        subsetLocusDetail = analysispanel.buildSubsetLocusDetail();
        subsetLocusDetail.setOpaque(true);
        panel.setLayout(new BorderLayout());
        locusCommitB = new JButton("Commit and Build Next");
        locusCommitB.addActionListener(this);
        locusCommitB.setActionCommand("locusCommit");
        panel.add(subsetLocusDetail, BorderLayout.CENTER);
        panel.add(locusCommitB, BorderLayout.PAGE_END);
        //scrollLocus = new JScrollPane(locusPanel);
        tabmap.put("locus", panel);
        return panel;
      }
 
      public JPanel buildSubsetStatisticDetail()
      {
        // subset Statistic panel
        JPanel panel = new StatisticDetail();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(true);
        subsetStatsDetail = analysispanel.buildSubsetStatisticDetail();
        subsetStatsDetail.setOpaque(true);
        statsCommitB = new JButton("Commit and Build Subset Locus");
        statsCommitB.addActionListener(this);
        statsCommitB.setActionCommand("statsCommit");
        panel.add(subsetStatsDetail, BorderLayout.CENTER);
        panel.add(statsCommitB, BorderLayout.SOUTH);
        tabmap.put("statistic", panel);
        return panel;
      }

      public JPanel getTab(String tabname)
      {
        return tabmap.get(tabname);
      }

      public int getnumLocus()
      {
        return new Integer(numLocusT.getText()).intValue();
      }

      public int getSubsetLocusCount()
      {
        return subsetLocusDetail.getSelectedLocus().length;
      }

      //************************************************************************
      public void actionPerformed( ActionEvent ae )
      {
        Object source = ae.getSource();
        if ( source == typeHelpB )
          JOptionPane.showMessageDialog(this,
                     "Type of analyze, Genotype or Allele");
        else if ( source == repeatHelpB )
          JOptionPane.showMessageDialog(this, UIConstants.REPEAT_MESSAGE);
        else if ( source == statsCommitB )
        {
           subsetStatsDetail.setDisplayOnly();
           statsCommitB.setVisible(false);
           scrollLocus = new JScrollPane(buildSubsetLocusPanel());
           tabbedPane.addTab("Subset Locus", scrollLocus);
           tabbedPane.setSelectedComponent(scrollLocus);
        }
        else if ( source == locusCommitB )
        {
          infoPanel = buildCCtableInfoPanel();
          tabbedPane.addTab("Analysis Detail", infoPanel);
          
          subsetLocusDetail.setDisplayOnly();
          locusCommitB.setVisible(false);
          tabbedPane.setSelectedComponent(infoPanel);
        }
        else if ( source == infoCommitB )
        {
          //DetailPanelImp columndetail = createChildPanel(this.myNode);
          ColumnDetail columndetail = null;
          try
          {
            columndetail = (ColumnDetail) createChildPanel();
            columndetail.setOpaque(true);
          }
          catch ( Exception ee )
          {
            System.err.println("Error creating Child Panel : " + ee.getMessage());
          }
          disableinfoPanel();
          treexdetailpanel.layeredPane.add(columndetail, columndetail.getIndex());
          treexdetailpanel.layeredPane.validate();
          treexdetailpanel.displaySelectedScreen(columndetail);
        }
        if ( source == repeatLociB )
          numLocusT.setText("1");
      } // actionperformed

      public void disableinfoPanel()
      {
        Util.disable(typeBG);
        Util.disable(repeatBG);
        Util.disable(modelT);
        Util.disable(numLocusT);
        Util.disable(typeHelpB);
        Util.disable(infoCommitB);
      }

      public void updateNumLocusT(int value)
      {
        if ( getnumLocus() > value )
          numLocusT.setText(Integer.toString(value));
      }

      public boolean getFlag()
      {
        return this.locusCommitB.isVisible();
      }
 
      public String getAnalysisType()
      {
        return typeBG.getSelection().getActionCommand();
      }
  
      public String getModelName()
      {
        return modelT.getText();
      }
  
      public String getRepeatMethod()
      {
        ButtonModel bm = repeatBG.getSelection();
        if ( bm == null )
          return "";
        else 
          return bm.getActionCommand();
      }

    } // end AnalysisDetail 

