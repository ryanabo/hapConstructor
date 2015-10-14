package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import edu.utah.med.genepi.app.rgen.MainManager;

public class ExecuteTab extends TabImp 
                        implements ActionListener
{
  ParameterFile rgenfile = null;
  ButtonGroup runBG;
  JRadioButton fileOnlyB, executeB;
  JFileChooser filechooser;
  JLabel fileL, executeMessageL;
  GlobalTab globaltab; 
  StudyTab studytab;
  StatisticTab statistictab;
  LocusTab locustab;
  AnalysisTab analysistab;
  static JTextArea outputArea = null;
  BufferedReader err = null;
  BufferedReader in = null;

  public ExecuteTab ()
  {
    super();
    title = "Execute";
  }
  
  public void build(GenieGUI inGUI)
  {
    super.build(inGUI);
    globaltab = (GlobalTab) gui.getTab("Global");
    studytab = (StudyTab) gui.getTab("Study");
    statistictab = (StatisticTab) gui.getTab("Statistic");
    locustab = (LocusTab) gui.getTab("Locus");
    analysistab = (AnalysisTab) gui.getTab("Analysis");
    //setLayout(new GridLayout(2,2));
    fileOnlyB = new JRadioButton("Save Parameter File only");
    fileOnlyB.addActionListener(this);
    fileOnlyB.setSelected(false);
    fileL = new JLabel("");
    executeB = new JRadioButton("Save Parameter File and Run Genie");
    executeB.addActionListener(this);
    executeB.setSelected(false);
    executeMessageL = new JLabel("");
    runBG = new ButtonGroup();
    runBG.add(fileOnlyB);
    runBG.add(executeB);
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setDividerLocation(100);
    JPanel topPanel = new JPanel();
    TitledEtched titledEtched = new TitledEtched("Exeute Genie");
    topPanel.setBorder(titledEtched.title);
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    topPanel.add(fileOnlyB);
    topPanel.add(executeB);
    JScrollPane bottomPane = new JScrollPane();
    {
      outputArea = new JTextArea();
      outputArea.setEditable(false);
      //outputArea.setPreferredSize(new Dimension(875,350));
      PrintStream out = new PrintStream(new TextAreaOutputStream(outputArea));
      System.setOut(out);
      System.setErr(out);
      bottomPane.setViewportView(outputArea);
    }
    bottomPane.setPreferredSize(new Dimension(900, 400));
    splitPane.setTopComponent(topPanel);
    splitPane.setBottomComponent(bottomPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerSize(15);
    add(splitPane, BorderLayout.CENTER);
  }

  public ParameterFile saveFile() 
	//throws IOException
  {
    filechooser = new JFileChooser();
    filechooser.setDialogTitle("Save .rgen parameter file");
    int returnVal = filechooser.showSaveDialog(ExecuteTab.this);
    ParameterFile pf = null;
    
    if ( returnVal == JFileChooser.APPROVE_OPTION )
    {
      File file = filechooser.getSelectedFile();
      try 
      {
        pf = new ParameterFile( file,
                                globaltab,
                                studytab,
                                statistictab,
                                locustab,
                                analysistab);
      }
      catch ( IOException ioe )
      {
        System.out.println("Failed to create parameter file. See following details. Restart GUI with corrected input.");
        JOptionPane.showMessageDialog(this, "Failed to create parameter file. See detail panel error message. Restart GUI with corrected input.");
      }
    }
    return pf;
  }

  public void actionPerformed( ActionEvent ae )
  {
    Object source = ae.getSource();
    if ( source == fileOnlyB )
    {
      if ((rgenfile = saveFile()) != null )
        System.out.println("Parameter file : " + rgenfile.getName() + " saved");
      else
        JOptionPane.showMessageDialog(this, UIConstants.NO_FILE_SAVED);
    }
    else if ( source == executeB )
    {
      if ( (rgenfile = saveFile()) != null )
      {
          String pkg = globaltab.getPkg();
          MainManager mm = new MainManager();
          mm.executeGenie(new String[] {pkg, rgenfile.getName()}); 
      } // end if rgenfile != null 
      else
        JOptionPane.showMessageDialog(this, UIConstants.NO_FILE_SAVED);
    }
  }
}
