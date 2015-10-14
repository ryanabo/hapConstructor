package edu.utah.med.genepi.gui2;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
//import java.io.IOException;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Enumeration;

public class StudyDetail extends DetailPanelImp
                         implements ActionListener
{
  JFileChooser filechooser;
  JButton genofileB, freqfileB, covariatefileB, linkageparfileB;
  JLabel studynameL, genofileL, freqfileL, covariatefileL, linkageparfileL;
  JTextField studynameT, genofileT, freqfileT, covariatefileT, linkageparfileT;
  File genofile, freqfile, covariatefile, linkageparfile;
  String title = "Study";
  int childPaneCount = 0;

  public StudyDetail()
  {
    super();
    allowsChildren = false;
   }
 
  public void buildDetail(int value)
  {
        index = value;
        setPreferredSize(new Dimension(600, 400));
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraintsL = new GridBagConstraints();
        GridBagConstraints constraintsT = new GridBagConstraints();
        GridBagConstraints constraintsB = new GridBagConstraints();
        setLayout(gridbag);
        TitledEtched etchedtitle = new TitledEtched(title + " " + index);
        setBorder(etchedtitle.title);
        String nfs = new String("No File Selected");
        studynameL = new JLabel("Study Name", JLabel.TRAILING);
        studynameT = new JTextField("Study_" + index + "  ", 30);
        studynameT.setPreferredSize(new Dimension(15, 27));
        studynameL.setLabelFor(studynameT);
        genofileL  = new JLabel("Genotype Data File", JLabel.TRAILING);
        genofileT  = new JTextField(nfs, 30);
        genofileT.setPreferredSize(new Dimension(15, 27));
        genofileL.setLabelFor(genofileT);
        genofileB  = new JButton("Browse");;
        freqfileL = new JLabel("Frequency File", JLabel.TRAILING);
        freqfileT = new JTextField(nfs, 30);
        freqfileT.setPreferredSize(new Dimension(15, 27));
        freqfileL.setLabelFor(freqfileT);
        freqfileB = new JButton("Browse");
        covariatefileL  = new JLabel("Covariate File ", JLabel.TRAILING);
        covariatefileT  = new JTextField(nfs, 30);
        covariatefileL.setLabelFor(covariatefileT);
        covariatefileT.setPreferredSize(new Dimension(15, 27));
        covariatefileB  = new JButton("Browse");
        linkageparfileL = new JLabel("Linkage Parameter File", JLabel.TRAILING);
        linkageparfileT = new JTextField(nfs, 30);
        linkageparfileT.setPreferredSize(new Dimension(15, 27));
        linkageparfileL.setLabelFor(linkageparfileT);
        linkageparfileB = new JButton("Browse");

        // add listeners
        genofileB.addActionListener(this);
        freqfileB.addActionListener(this);
        covariatefileB.addActionListener(this);
        linkageparfileB.addActionListener(this);

        genofile   = null;
        freqfile   = null;
        covariatefile    = null;
        linkageparfile = null;

        // constraints for filetype button
        int gridyVal = 0;
        constraintsL.ipadx = 20;
        constraintsL.ipady = 20;
        constraintsL.gridx = 0;
        constraintsL.gridwidth = 50;
        constraintsL.gridheight = 4;
        constraintsT.ipadx = 20;
        constraintsL.ipady = 20;
        constraintsT.gridx = 60;
        constraintsT.gridwidth = 30;
        constraintsT.gridheight = 4;
        constraintsB.ipadx = 20;
        constraintsL.ipady = 20;
        constraintsB.gridx = 100;
        constraintsB.gridwidth = 3;
        constraintsB.gridheight = 4;

        gridbag.setConstraints(studynameL, constraintsL);
        gridbag.setConstraints(studynameT, constraintsT);
        add(studynameL);
        add(studynameT);

        gridyVal+=6;
        constraintsL.gridy = gridyVal;
        constraintsT.gridy = gridyVal;
        constraintsB.gridy = gridyVal;
        gridbag.setConstraints(genofileL, constraintsL);
        gridbag.setConstraints(genofileT, constraintsT);
        gridbag.setConstraints(genofileB, constraintsB);
        add(genofileL);
        add(genofileT);
        add(genofileB);

        gridyVal+=4;
        constraintsL.gridy = gridyVal;
        constraintsT.gridy = gridyVal;
        constraintsB.gridy = gridyVal;
        gridbag.setConstraints(freqfileL, constraintsL);
        gridbag.setConstraints(freqfileT, constraintsT);
        gridbag.setConstraints(freqfileB, constraintsB);
        add(freqfileL);
        add(freqfileT);
        add(freqfileB);

        gridyVal+=4;
        constraintsL.gridy = gridyVal;
        constraintsT.gridy = gridyVal;
        constraintsB.gridy = gridyVal;
        gridbag.setConstraints(covariatefileL, constraintsL);
        gridbag.setConstraints(covariatefileT, constraintsT);
        gridbag.setConstraints(covariatefileB, constraintsB);
        add(covariatefileL);
        add(covariatefileT);
        add(covariatefileB);

        gridyVal+=4;
        constraintsL.gridy = gridyVal;
        constraintsT.gridy = gridyVal;
        constraintsB.gridy = gridyVal;
        gridbag.setConstraints(linkageparfileL, constraintsL);
        gridbag.setConstraints(linkageparfileT, constraintsT);
        gridbag.setConstraints(linkageparfileB, constraintsB);
        add(linkageparfileL);
        add(linkageparfileT);
        add(linkageparfileB);
    } // filedetail constructor

    public void actionPerformed(ActionEvent ae)
    {
        Object source = ae.getSource();
        if ( source == genofileB )
        {
          filechooser = new JFileChooser();
          filechooser.setDialogTitle("Genotype Data File");
          int returnVal = filechooser.showOpenDialog(StudyDetail.this);

          if ( returnVal == JFileChooser.APPROVE_OPTION )
          {
            genofile = filechooser.getSelectedFile();
            genofileT.setText(genofile.toString());
          }
        }
        else if ( source == freqfileB )
        {
          filechooser = new JFileChooser();
          filechooser.setDialogTitle("Frequency File");
          int returnVal = filechooser.showOpenDialog(StudyDetail.this);

          if ( returnVal == JFileChooser.APPROVE_OPTION )
          {
            freqfile = filechooser.getSelectedFile();
            freqfileT.setText(freqfile.toString());
          }
        }
        else if ( source == covariatefileB )
        {
          filechooser = new JFileChooser();
          filechooser.setDialogTitle("Variable File");
          int returnVal = filechooser.showOpenDialog(StudyDetail.this);

          if ( returnVal == JFileChooser.APPROVE_OPTION )
          {
            covariatefile = filechooser.getSelectedFile();
            covariatefileT.setText(covariatefile.toString());
          }
        }
        else if ( source == linkageparfileB )
        {
          filechooser = new JFileChooser();
          filechooser.setDialogTitle("Linkage Parameter File");
          int returnVal = filechooser.showOpenDialog(StudyDetail.this);

          if ( returnVal == JFileChooser.APPROVE_OPTION )
          {
            linkageparfile = filechooser.getSelectedFile();
            linkageparfileT.setText(linkageparfile.toString());
          }
        }

    }  // actionPerformed

    public String toString()
    { return studynameT.getText(); }

    public String getStudyName()
    { return studynameT.getText(); }

    public String getGenoFile()
    { return genofileT.getText(); }

    public String getHapFreq()
      { return freqfileT.getText(); }
  
    public String getVariableFile()
    { return covariatefileT.getText(); } 

    public String getLinkageParFile()
    {  return linkageparfileT.getText(); }

    public void setDisplayOnly()
    {
      Util.disable( studynameT );
      Util.disable( genofileT );
      Util.disable( freqfileT );
      Util.disable( covariatefileT );
      Util.disable( linkageparfileT );
      Util.disable( genofileB );
      Util.disable( freqfileB );
      Util.disable( covariatefileB );
      Util.disable( linkageparfileB );
    }
} 

