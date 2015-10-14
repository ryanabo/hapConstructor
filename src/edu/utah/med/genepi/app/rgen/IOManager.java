//******************************************************************************
// IOManager.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Map;

import alun.genio.LinkageFormatter;
import alun.genio.LinkageParameterData;
import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.io.LpedParser;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedDocument;
import edu.utah.med.genepi.ped.QuantDocument;
import edu.utah.med.genepi.ped.QuantIndiv;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class IOManager {

  private final Specification         theSpec;
  private static File                 specfile;
  private static GDef	    	      gdef;
  private static AlleleFormat	      alleleformat;
  private final String                app_id, app_ver, outpathStem;
  private final PedData.Printer       dataDumper;
  private static LinkageParameterData lpd;
  private static Study[]              study;
  private int[] 		      covar_ids;
  private static int 		      numGDef;
  private int 			      numSimData = 1;
  private Calendar 		      indate;
  private boolean 		      hasHeader;

  //----------------------------------------------------------------------------
  IOManager( String appid,
             String appver,
             File fspec, 
             Map<String, String> prior_spec_params )
  throws GEException
  {
	Calendar inDate = Calendar.getInstance();
    app_id = appid;
    app_ver = appver;
    specfile = fspec;
    theSpec = loadSpec(specfile, prior_spec_params);
    study = theSpec.getStudy();
    gdef = theSpec.getGDef();
    covar_ids  = theSpec.getCovarIds();
    GSimulator.Top tSim = theSpec.getTopSim();
    hasHeader = theSpec.hasHeader();

    for ( int i = 0;  i < study.length; i++ )
    {
      File genofile   = study[i].getGenotypeFile();
      File quantfile = study[i].getQuantitativeFile();
      File hapfile   = study[i].getHaplotypeFile();
      File parfile   = study[i].getLinkageParFile();

      study[i].setPedData(loadPedData( genofile, quantfile ));

      if ( parfile != null )
        study[i].setLinkageParameterData( loadLinkageParameterData(parfile) ); 
    }
    outpathStem = Ut.stemOf(specfile);
    dataDumper = theSpec.getDataDumper();
    numGDef = gdef.getLocusCount();
    alleleformat = gdef.getAlleleFormat();
    indate = inDate;
  }

  //----------------------------------------------------------------------------
  public Specification getSpecification() { return theSpec; }
  
  //----------------------------------------------------------------------------
  public String getAppId() { return app_id; }
  
  //----------------------------------------------------------------------------
  public String getAppVer() { return app_ver; }

  //----------------------------------------------------------------------------
  public Study[] getStudy() // Why we need this method?
  { return study; }
  
  //----------------------------------------------------------------------------
  boolean dumpingEnabled() { return dataDumper != null; }

  //----------------------------------------------------------------------------
  void dumpData(int idump, int index) throws GEException, IOException
  {
    if (!dumpingEnabled())
      return;
    int nsims = theSpec.getNumberOfSimulations();
    if ( Ut.uqNameOf(dataDumper).equals("IndivDumper") && idump < nsims )
      return;
    String[] dumppathStem = new String[study.length];

    for ( int i = 0; i < study.length; i++ )
    {
      dumppathStem[i] = Ut.stemOf(study[i].getGenotypeFile()) + '.' + Ut.stemOf(specfile);
      File f = Ut.fExtended(
        dumppathStem[i] + "_" + idump, dataDumper.getType() );
      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));

      System.out.println("Dumping to '" + f + "'...");

      dataDumper.print(study[i].getPedData(),
                       idump == 0 ? Indiv.GtSelector.OBS : Indiv.GtSelector.SIM,
                       alleleformat, numGDef, pw, index, hasHeader);
      pw.close();
    }
  }
  
  //----------------------------------------------------------------------------
  void dumpData(int idump) throws IOException, GEException
  {
    if (!dumpingEnabled())
      return;
    //if ( Ut.uqNameOf(dataDumper).equals("IndivDumper") )
    //  return;

    String[] dumppathStem = new String[study.length];

    for ( int i = 0; i < study.length; i++ )
    {
      dumppathStem[i] = Ut.stemOf(study[i].getGenotypeFile()) + '.' + Ut.stemOf(specfile);
      File f = Ut.fExtended(
        dumppathStem[i] + "_" + idump, dataDumper.getType() );
      PrintWriter pw = new PrintWriter(new BufferedWriter(
                       new FileWriter(f)), true);

      System.out.println("Dumping to '" + f + "'...");

      dataDumper.print(study[i].getPedData(),
                  idump == 0 ? Indiv.GtSelector.OBS : Indiv.GtSelector.SIM,
                  alleleformat, numGDef, pw);
      pw.close();
    }
  }

  //----------------------------------------------------------------------------
  void writeReport() throws IOException
  {
    CCAnalysis[] analyses = theSpec.getCCAnalyses();
    /**
    String[] filesource   = new String[study.length];
    String[] studyname    = new String[study.length];

    for ( int i = 0; i < study.length; i++ )
    {
      filesource[i] = study[i].getPedData().getID();
      studyname[i]  = study[i].getStudyName();
    }

    Map m = theSpec.getAllGlobalParameters();
  
    if ( "full".equals(m.get("report")) )
    {
      String[] ext = new String[2];
      ext[0]       = "report";
      ext[1]       = "summary";
      for ( int i = 0; i < ext.length; kkkkkkkkkkk
      File f = Ut.fExtended (outpathStem, "report");
      Reporter r   
      reportext[0] = "report";
      reportext[1] = "summary";
    }
    else if ( "summary".equals(m.get("report");

    File f = Ut.fExtended(outpathStem, reportext[i])
    Reporter r = new Reporter();
    r.reportHeaderInfo(appID, theSpec, filesource,
                       studyname, indate);
  
    for (int i = 0; i < analyses.length; ++i)
      r.reportCCAnalysis(analyses[i]);

    */
    Reporter r = new Reporter ( app_id, app_ver, theSpec, study, indate, 
                                analyses, outpathStem );
    //r.closeReport();

    //System.out.println("Report written to '" + f + "'." + Ut.N);
  }

  //----------------------------------------------------------------------------
  private Specification loadSpec(File f, Map<String, String> prior_params) 
          throws GEException
  {
    System.out.println("Loading specification from '" + f + "'...");
    return new Specification(f, app_id, prior_params);
  }

  //----------------------------------------------------------------------------
  private PedData loadPedData(File pedf, File quantf) throws GEException
  {
    System.out.println("Loading data from '" + pedf + "'...");

    PedDocument doc = new PedDocument(gdef);
    QuantIndiv[] quantIndiv = null;
    if ( quantf != null )
      quantIndiv = loadQuantData(quantf);

    doc.read(pedf, quantIndiv, covar_ids,
             numSimData, new LpedParser(), hasHeader);
    System.out.println("Loaded "+doc.getPedigrees().length+" pedigrees.");
    return doc;
  }
 
  //----------------------------------------------------------------------------
  private QuantIndiv[] loadQuantData ( File f ) throws GEException
  {
    System.out.println("Loading quantative data from '"+ f + "'...");
    QuantDocument qdoc = new QuantDocument();
    try { qdoc.read (f, covar_ids); }
    catch ( Exception e ) 
    { throw new GEException ("failed to read file : '" + f + "', " + e); }

    return qdoc.getQuantIndiv();
  }

  //----------------------------------------------------------------------------
  public LinkageParameterData loadLinkageParameterData ( File parf )
  {
    try
    {
      LinkageFormatter f = new LinkageFormatter ( new BufferedReader (
                                        new FileReader ( parf )), "Par file");
      LinkageParameterData pd= new LinkageParameterData(f);
      return pd;
    }
    catch ( Exception e )
    {
      System.out.println("Failed to read Linkage Parameter file : " + parf.getName());
      System.out.println(e.getMessage());
      return null;
    }
  }

  //---------------------------------------------------------------------------
  public GDef getGDef()
  { return theSpec.getGDef(); }
  
  //Ryan 06-21-07 Temporary function to get inDate
  public Calendar getinDate(){
    return indate;
  }
  
  //Ryan added 09/03/07 added function to get outpathstem variable to use as the file stem
  //for hapBuilder output files. The report function for hapBuilder should probably 
  //be placed in this file or Reporter.java.
  public String getoutpathStem(){
    return outpathStem;
  }
 
}
