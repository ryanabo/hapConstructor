package edu.utah.med.genepi.gui2;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.utah.med.genepi.gm.LocusImp;

public class AnalysisTab extends TabImp
{
  AnalysisTab analysistab;
  AnalysisPanel analysisPanel;
  DefaultMutableTreeNode analysisNode;
  String errMessage = "";
  //int openAnalysisCount = 0;
  //AnalysisDetail analysisdetail;
  //JButton commitButton;
  //private static String COMMIT_COMMAND = "commit";

  public AnalysisTab()
  { 
    super();
    analysistab = this;
    title = "Analysis";
   }

  public void build(GenieGUI inGUI)
  {
    super.build(inGUI);
    analysisPanel = new AnalysisPanel("Analysis");
    add(analysisPanel);
  }

  public class AnalysisPanel extends TreexDetailPanel
  {
    AnalysisDetail analysisdetail;
    int childPaneCount = 0;

    public AnalysisPanel(String inTitle)
    {
      super(inTitle);
      detailpanelname = "AnalysisDetail";
      try 
      {
        analysisdetail = (AnalysisDetail) createDetailPanel();
      } 
      catch ( Exception e )
      {
        System.out.println("Error in creating Analysis Detail Panel : " + e.getMessage());
      }
      layeredPane.add(analysisdetail, analysisdetail.getIndex());
      displaySelectedScreen(analysisdetail);
    }

    /*
    public AnalysisDetail[] getAnalysisDetail()
    {
      AnalysisDetail[] analysislist = new AnalysisDetail[rootNode.getChildCount()];
      int i = 0;
      for ( Enumeration e = rootNode.children(); e.hasMoreElements(); )
      {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
        analysislist[i] = (AnalysisDetail) node.getUserObject();
        i++;
      }
      return analysislist;
    } // end getAnalysisDetail
    **/

    public void commitedAction()
    {
      if ( checkDetail())
      {
        gui.addTab("Execute");
        setDisplayOnly();
      }
      else 
      {
        JOptionPane.showMessageDialog(this, errMessage);
      }
    }
  
    public boolean checkDetail()
    {
      errMessage = "";
      boolean nextDetail = true;
      boolean nextColumn = true;
      boolean nextGroup = true;
      DetailPanelImp[] detailpanel = this.getDetailPanel();
      for ( int i = 0; i < detailpanel.length; i++ )
      {
        AnalysisDetail ad = (AnalysisDetail) detailpanel[i];
        DetailPanelImp[] columndetail = ad.getChildDetail();
        int nColumnDetail = columndetail.length;
        if ( nColumnDetail > 1 )
        {
          for ( int j = 0; j < nColumnDetail; j++ )
          {
            ColumnDetail cd = (ColumnDetail) columndetail[j];
            int cdIndex = cd.getIndex();
            if ( cd.getWeight().length() == 0 )
            {
              errMessage = "Column " + cdIndex + " missing Weight value";
              nextDetail = false;
              break;
            }

            DetailPanelImp[] groupdetail = cd.getChildDetail();
            for ( int k = 0; k < groupdetail.length; k++ )
            {
              ColumnGroupDetail cgd = (ColumnGroupDetail) groupdetail[k];
              String[] patterns = cgd.getPattern();
              for ( int p = 0; p < patterns.length; p++ )
              {
                if ( patterns[p].length() == 0 )
                {
                  errMessage = "Column " + cdIndex + " Group " + cgd.getIndex() + " missing pattern";
                  nextGroup = false;
                  nextColumn = false;
                  nextDetail = false;
                  break;
                }
              }
              if ( !nextGroup )
                break;
            }
          }
          if ( !nextDetail )
            break;
        }
        else
        {
          errMessage = "Analysis requires more than 1 column";
          nextDetail = false;
          break;
        } // if nColumnDetail > 1
      } // end detailpanel loop
      return nextDetail;
    } //end checkDetail

    public void setDisplayOnly()
    {
      Util.disable(commitButton);
      Util.disable(addButton);
      Util.disable(removeButton);
      DetailPanelImp[] detailpanel = this.getDetailPanel();
      for ( int i = 0; i < detailpanel.length; i++ )
      {
        AnalysisDetail ad = (AnalysisDetail) detailpanel[i];
        DetailPanelImp[] columndetail = ad.getChildDetail();
        for ( int j = 0; j < columndetail.length; j++ )
        {
          ColumnDetail cd = (ColumnDetail) columndetail[j];
          cd.setDisplayOnly();
          DetailPanelImp[] groupdetail = cd.getChildDetail();
          for ( int k = 0; k < groupdetail.length; k++ )
          {
            ColumnGroupDetail cgd = (ColumnGroupDetail) groupdetail[k];
            cgd.setDisplayOnly();
          }
        }
      }
    }

    public LocusDetail buildSubsetLocusDetail()
    {
        LocusTab locustab = (LocusTab) gui.getTab("Locus");
        String messageText2 = new String ("No Locus info");
        if ( locustab != null )
        {
          LocusImp[] locus = locustab.getSelectedLocus();
          int nlocus = locus.length;
          if ( locus.length > 0 )
          {
            String[] locusName = new String[nlocus];
            String[] locusDist = new String[nlocus];
            String[] gene      = new String[nlocus];
            for ( int i = 0; i < nlocus; i++ )
            {
              locusName[i] = locus[i].getMarker();
              locusDist[i] = String.valueOf(locus[i].getTheta());
              gene[i]      = String.valueOf(locus[i].getGeneID());
            }

            //allLocusB.setVisible(false);
            //getLocusB.setVisible(false);
            LocusDetail panel = new LocusDetail
                                    ( "Subset Locus",
                                      nlocus,
                                      locusName,
                                      locusDist,
                                      gene );
            //subsetLocus.add(locusDetailPanel);
            //subsetLocus.updateUI();
            //columnPanel.setLocusName( locusName );
            return panel;
          }
          else
            JOptionPane.showMessageDialog(this, messageText2);
        }
        else
          JOptionPane.showMessageDialog(this, messageText2);
        return null;
      } //end buildSubsetLocus

      public StatisticDetail buildSubsetStatisticDetail()
      {
        StatisticDetail statisticdetail = new StatisticDetail();
        StatisticTab stattab = (StatisticTab) gui.getTab("Statistic");
        StatisticDetail statdetail = stattab.getStatisticDetail();
        if ( statdetail != null )
        {
          Statistic[] stats = statdetail.getSelectedStats();
          statisticdetail.setStatistic(stats);
          statisticdetail.buildPanel();
        }
        return statisticdetail;
      }  // end getSubsetStatistic

 
    //public void increaseOpenPaneCount()
    //{ openPaneCount++; }
  
    //public int getOpenPaneCount()
    //{ System.out.println("within AnalysisPanel");
    //  return openPaneCount; }

    public String toString()
    { return "AnalysisPanel"; }
  } // end AnalysisPanel

  protected static ImageIcon createImageIcon (String path,
                                              String description)
  {
    java.net.URL imgURL = AnalysisTab.class.getResource(path);
    if (imgURL != null) 
      return new ImageIcon(imgURL, description);
    else 
    {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }
}
