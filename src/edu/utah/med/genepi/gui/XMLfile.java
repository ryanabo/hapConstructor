//*****************************************************************************
// XMLfile.java
//*****************************************************************************
package edu.utah.med.genepi.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.utah.med.genepi.gm.LocusImp;

public class XMLfile 
{
  private PrintWriter xmlrgen;
  LocusImp[] locus ;
  String[] stats;

  public XMLfile ( File rgenFile, 
                   LocusImp[] inlocus,
                   String[] instats    ) throws IOException
  {
    locus = inlocus;
    stats = instats;
    xmlrgen = new PrintWriter( new BufferedWriter
                  ( new FileWriter ( rgenFile )), true);
  } 

//-----------------------------------------------------------------------------
  public void headerInfo()
  {
    //System.out.println("Inside headerinfo");
    xmlrgen.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
    xmlrgen.println("!DOCTYPE ge:rgen SYSTEM \"ge-rgen.dtd\">");
  }

//-----------------------------------------------------------------------------
  public void simInfo ( int rseed, int nsims, String top, String drop,
                        String freq, String dumper ) 
  {
    xmlrgen.println(opentag("rgen rseed=\"" + rseed + "\"" + " nsims=\"" 
      + nsims + "\"" + " top=\"" + top + "\"" + " drop=\"" + drop + "\""));
    xmlrgen.println(opentag("param name=\"top-sample\"") 
      + freq + closetag("param"));
    if ( dumper == "Yes" )
      xmlrgen.println(opentag("param name=\"dumper\"")
        + "DTDDumper" + closetag("param"));
  }

  //---------------------------------------------------------------------------
  public void locusInfo ()
  {
    for ( int i = 0; i < locus.length; i++ )
    {
      String marker = "";
      String dist   = "";
      if ( locus[i].getMarker().length() > 0 )
        marker = new String(" marker=\"" + locus[i].getMarker() + "\"");
      if ( !Double.isNaN(locus[i].getTheta()) ) 
        dist = new String(" dist=\"" +String.valueOf(locus[i].getTheta())+"\"");
      xmlrgen.println(opentag("locus id=\"" + locus[i].getID() + "\"" +
                    dist + marker + "/"));
    }
  }

  //----------------------------------------------------------------------------
  public void statisticInfo ()
  {
    for ( int i = 0; i < stats.length; i++ )
      xmlrgen.println ( opentag("param name=" + "\"ccstat" + (i+1) + "\"") 
        + stats[i] + closetag("param"));
  }
  //----------------------------------------------------------------------------
  public void quantfileInfo( String quantfile )
  {
    if ( quantfile.length() > 0 )
      xmlrgen.println ( opentag("param name=\"quantfile\"")
        + quantfile + closetag("param"));
  }
/*
dumper()
{
  xmlrgen.println(opentag("param name=\"dumper\"") + ".DescentDumper" 
    + closetag("param"));
}

*/
//-----------------------------------------------------------------------------
  void writeCCTable(CCTable cct)
  {
    String cctLoci  = "";
    String cctStats = "";
    String cctType  = "";
    String cctModelName = ""; 
    Model  cctModel;
    int numLocus = locus.length;
    
      if ( cct.getLoci().trim().toLowerCase() != "all" &&
           cct.getLoci().length() > 0 )
      {
        String[] s = cct.getLoci().trim().split(",");
        if ( s.length > 0 )
        {
          numLocus = s.length;
          cctLoci = " Loci = \"" ;
          for ( int i = 0; i < s.length; i++ )
          {
            for ( int l = 0; l < locus.length; l++ )
            {
              if ( String.valueOf(locus[l].getID()) == s[i] )
              {
                cctLoci += l;
                if ( i + 1 < s.length )
                  cctLoci += " ";
              }
            }
          }
          cctLoci += "\"";
        }
      }
       
      String s = cct.getStats();
      if ( cct.getStats().length() > 0 )
        cctStats = " Stats=\"" + cct.getStats() + "\""; 
      cctType = " Type=\"" + cct.getType() + "\"";
      cctModelName = " Model=\"" + cct.getModelName() + "\"";   
      cctModel = cct.getModel();
      
      xmlrgen.println(opentag("cctable" + cctStats + cctLoci + cctType + cctModelName));
      ColumnPattern[] cp = cct.getModel().getColPattern();
      for ( int w = 0; w < cp.length; w++ )
      {
        xmlrgen.println(opentag("col wt=\"" + cp[w].Weight + "\""));
        for ( int g = 0; g < cp[w].Pattern.length; g++ )
        {
          xmlrgen.println(opentag("g"));
          for ( int a = 0 ; a < numLocus; a++ )
            xmlrgen.println(opentag("a") + cp[w].Pattern[g] + closetag("a"));
          xmlrgen.println(closetag("g"));
        }
        xmlrgen.println(closetag("col"));
      }
      xmlrgen.println(closetag("cctable"));
  }
    
  //---------------------------------------------------------------------------
  public void closefile()
  {
    xmlrgen.close();
  }

  public String opentag ( String s )
  {
    String ss = "<ge:" + s + ">";
    return ss;
  }

  public String closetag ( String s )
  {
    String ss = "</ge:" + s + ">";
    return ss;
  }
  
}
