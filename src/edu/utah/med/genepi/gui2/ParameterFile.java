package edu.utah.med.genepi.gui2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.utah.med.genepi.gm.LocusImp;
//==============================================================================
public class ParameterFile 
{
  public File rgen;
  private PrintWriter pw;
  public GlobalTab globaltab;
  private StudyTab studytab;
  private StatisticTab statistictab;
  private LocusTab locustab;
  private AnalysisTab analysistab;
  public StudyDetail[] studydetails;
  public LocusImp[] globalLocus;
  public Statistic[] globalStatistic;

  //----------------------------------------------------------------------------
  public ParameterFile(File infile, 
                       GlobalTab globalt,
                       StudyTab studyt,
                       StatisticTab statistict,
                       LocusTab locust,
                       AnalysisTab analysist ) throws IOException
  {
    rgen = infile;
    globaltab = globalt;
    studytab = studyt;
    statistictab = statistict;
    locustab = locust;
    analysistab = analysist;

    StatisticDetail statisticdetail = statistictab.getStatisticDetail();
    globalLocus = locustab.getSelectedLocus();
    globalStatistic = statisticdetail.getSelectedStats();
    pw = new PrintWriter(new BufferedWriter(new FileWriter(rgen)), true);
    writeHeader();
    writeLocus();
    writeDataFile();
    writeStatistic();
    writeCCtable();
    closepw();
  }

  public String getName()
  { return rgen.toString(); }

  public void writeHeader()
  {
    String nsims  = globaltab.getNsim();
    String seed   = globaltab.getRseed();
    String top    = globaltab.getTop();
    String drop   = globaltab.getDrop();
    String report = globaltab.getReport();
    pw.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
    pw.println("<!DOCTYPE ge:rgen SYSTEM \"ge-rgen.dtd\">");
    pw.println();
    pw.println("<ge:rgen rseed=\"" + seed + "\" nsims=\""+ nsims + "\" top=\"" + top + "\" drop=\"" + drop + "\" report=\"" + report + "\">");
    pw.println();
  }

  public void writeLocus()
  {
    for ( int i = 0; i < globalLocus.length; i++ )
    {
      int locusID = globalLocus[i].getID();
      String locusName = globalLocus[i].getMarker();
      double dist = globalLocus[i].getTheta();
      //int gene = globalLocus[i].getGeneID();
      pw.print("<ge:locus id=\"" + locusID + "\" marker=\"" + locusName + "\"");
      if ( dist != 0.0 )
        pw.print(" dist=\"" + dist + "\"");
      //if ( gene != -1 )
      //  pw.print(" gene=\"" + gene + "\"");
      pw.print("/>");
      pw.println();
    } 
  }

  public void writeDataFile()
  {
    DetailPanelImp[] detailPanel =  studytab.studyPanel.getDetailPanel();
    for ( int i = 0; i < detailPanel.length; i++ )
    {
      StudyDetail studydetail = (StudyDetail) detailPanel[i];
      String study = studydetail.getStudyName().trim();
      String genofile = studydetail.getGenoFile();
      String hapfreqfile = studydetail.getHapFreq();
      String varfile = studydetail.getVariableFile();
      String parfile = studydetail.getLinkageParFile();
   
      // no need to add study i, it appears on the screen
      //if (study.length() == 0)
      //  study = "study " + i;

      if (genofile.length() == 0)
      {
        System.out.println("No genotype data file selected, please reset start the GUI.");
        break;
      }
    
      pw.print("<ge:datafile studyname=\""+ study + "\" genotypedata=\"" +
               genofile + "\"");
      if (!hapfreqfile.equals("No File Selected"))
        pw.print(" haplotype=\"" + hapfreqfile + "\"");
      if (!varfile.equals("No File Selected"))
        pw.print(" quantitative=\"" + varfile + "\"");
      if (!parfile.equals("No File Selected"))
        pw.print(" linkageparameter=\"" + parfile + "\"");
      pw.print("/>");
      pw.println();
    }
  } // end writedatafile

  public void writeStatistic()
  {
    int s = 0;
    int m = 0;
    for ( int i = 0; i < globalStatistic.length; i++ )
    {
      String statID = null;
      if ( globalStatistic[i].group.equals("s") )
      {
        s++;
        statID = "ccstat"+s;
      }
      else if ( globalStatistic[i].group.equals("m") )
      {
        m++;
        statID = "metastat"+m;
      }
      pw.println("<ge:param name=\""+statID+"\">"+globalStatistic[i].programName+"</ge:param>");
    }
  } // end writestatistic

  public void writeCCtable()
  {
    DetailPanelImp[] detailPanel =  analysistab.analysisPanel.getDetailPanel();
    for ( int i = 0; i < detailPanel.length; i++ )
    {
      AnalysisDetail analysisdetail = (AnalysisDetail) detailPanel[i];
      String locus = "";
      String stats = "";
      String space = "";
      //JPanel infoTab = analysisdetail[i].getTab("info");
      String model  = analysisdetail.getModelName();
      String type   = analysisdetail.getAnalysisType();
      String repeat = analysisdetail.getRepeatMethod();
      LocusImp[] locusImp = analysisdetail.subsetLocusDetail.getSelectedLocus();
      if ( locusImp.length < globalLocus.length )
      {
        for ( int j = 0; j < locusImp.length; j++ )
        {
          int locusID = locusImp[j].getID();
          locus += space + locusID;
          space = " ";
        }
        space = "";
      }

      Statistic[] statistic = analysisdetail.subsetStatsDetail.getSelectedStats();
      if ( statistic.length < globalStatistic.length )
      {
        for ( int j = 0; j < statistic.length; j++ )
        {
          int statsID = j+1;
          stats += space + statsID;
          space = " ";
        }
      }

      pw.print("<ge:cctable");
      if ( type.length() > 0 && type.equals("Allele"))
        pw.print(" type=\"allele\"");
      if ( model.length() > 0 )
        pw.print(" model=\""+ model + "\"");
      if ( repeat.length() > 0 )
        pw.print(" repeat=\"" + repeat + "\"");
      if ( locus.length() > 0 ) 
        pw.print(" loci=\"" + locus + "\"");
      if ( stats.length() > 0 )
        pw.print(" stats=\"" + stats + "\"");
      pw.print(">");
      pw.println(); 

      DetailPanelImp[] detailcolumn = analysisdetail.getChildDetail();
      
      for ( int col = 0; col < detailcolumn.length; col++ )
      {
        ColumnDetail columndetail = (ColumnDetail) detailcolumn[col];
        pw.println("<ge:col wt=\""+columndetail.weightT.getText()+"\">");
        DetailPanelImp[] detailgroup = columndetail.getChildDetail();
                                          columndetail.getChildDetail();
        // write group allele pattern 
        for ( int g = 0 ; g < detailgroup.length; g++ )
        {
          ColumnGroupDetail columngroup = (ColumnGroupDetail) detailgroup[g];
          pw.println("<ge:g>");
          for ( int p = 0; p < columngroup.patternT.length; p++ )
          {
            String pattern = columngroup.patternT[i].getText();
            pw.println("<ge:a>"+pattern+"</ge:a>");
          }
          pw.println("</ge:g>");
        }
        pw.println("</ge:col>");
      }
      pw.println("</ge:cctable>");
    }
  } // end writeCCtable

  public void closepw()
  { 
    pw.println("</ge:rgen>");
    pw.close(); 
  }
}
