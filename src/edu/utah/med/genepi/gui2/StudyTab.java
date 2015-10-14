package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Enumeration;

public class StudyTab extends TabImp
{
  //JButton commitB;
  StudyTab studytab;
  StudyPanel studyPanel;
  int nLocus = 0;
  String[] locusName;
  
  public StudyTab()
  { 
    super();
    studytab = this;
    title = "Study";
  }

  public void build(GenieGUI inGUI)
  {
    super.build(inGUI);
    setLayout(new BorderLayout());
    studyPanel = new StudyPanel(title);
    add(studyPanel);
  }

  public class StudyPanel extends TreexDetailPanel 
  {
    boolean filehasheader = true;
    StudyDetail[] studylist;
    StudyDetail studydetail;

    public StudyPanel(String inTitle)
    {
      super(inTitle);
      studyPanel = this;
      detailpanelname = "StudyDetail";
      try 
      {
        studydetail = (StudyDetail) createDetailPanel();
      }
      catch ( Exception e )
      {
        System.out.println("Error in creating Study Detail Panel : " + e.getMessage() );
      }
      layeredPane.add(studydetail, studydetail.getIndex());
      displaySelectedScreen(studydetail);
      studydetail.setOpaque(true);
    }
    
    public void setLocusInfo() throws IOException
    {
      File file = null;
      // retrieve the first genotype file from the first Study Panel 
      DetailPanelImp[] dpi = getDetailPanel();
      StudyDetail sd = (StudyDetail) dpi[0];
      file = new File(sd.getGenoFile());
                   
      if ( file == null )
        JOptionPane.showMessageDialog(this, "No Genotype File selected");
      else 
      {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = in.readLine();
        if ( line != null )
        {
          String[] tokens = line.trim().split("\\s+");
      
          if ( filehasheader )
          {
            nLocus = tokens.length - 7;
            locusName = new String[nLocus];
            for ( int i = 0; i < nLocus; i++ )
            {
              locusName[i] = tokens[i+7];
            }
          }
          else 
          {
            nLocus = (int) ((tokens.length - 7) / 2);
            for ( int i = 0; i < nLocus; i++ )
              locusName[i] = new String("Maker "+i);
          }
        }
        in.close();
      }
    } // end setLocusInfo

    public int getNumLocus()
    { return nLocus; }

    public String[] getLocusName()
    { return locusName; }

    public void commitedAction()
    {
      try
      {
        setLocusInfo();
        if ( nLocus != 0 )
        {
          gui.addTab("Locus");
          setDisplayOnly();
        }
      }
      catch ( Exception e )
      {
        JOptionPane.showMessageDialog
          (this, "Failed to read Genotype Data File");
      }
    }

    public void setDisplayOnly()
    {
      Util.disable(addButton);
      Util.disable(removeButton);
      Util.disable(commitButton);
      DetailPanelImp[] detailpanel = studyPanel.getDetailPanel();
      for ( int i = 0; i < detailpanel.length; i++ )
      {
        StudyDetail sd = (StudyDetail) detailpanel[i];
        sd.setDisplayOnly();
      }
    }

  } // end studyPanel


  /*
  private static void createUI()
  {
    JFrame frame = new JFrame("Study");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(new StudyPanel());
    frame.pack();
    frame.setVisible(true);
    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(
        new Runnable()
        {
          public void run()
          {
            createUI();
          }
        } );
  }
  */
}
