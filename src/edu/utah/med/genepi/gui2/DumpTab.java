package edu.utah.med.genepi.gui2;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class DumpTab extends JPanel
                    implements ActionListener
{
  JRadioButton nodumpB, genodumpB, haplodumpB, tdtdumpB, descentdumpB;
  ButtonGroup dumperBG;

  public DumpTab ()
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

