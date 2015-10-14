 package edu.utah.med.genepi.genie;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ExpandedAnalysis;
import edu.utah.med.genepi.gm.Gene;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.StatInterface;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

/**
 * This class holds the information from Genie rgen parameter data file
 * and Genie data files (i.e. genotype, quantitative data).
 * 
 */
public class GenieDataSet {

	private String rgenFile = null;
	private GenieParameterData gp = null;
	private GenieData gd = null;
	
	public GenieDataSet()
	{
	}

	public GenieDataSet( String[] args, String version ) throws IOException, GEException
	{
		if ( !checkArgs(args) ) System.exit(1);
		else
		{
			rgenFile = args[1];
			String appId = args[0];
			File f = Ut.fExtended(rgenFile,"rgen");
			RgenProcessor rProcessor = new RgenProcessor(f);
			GenieParameterData genieParameterData = new GenieParameterData(rProcessor,appId.toLowerCase(),version);
			GenieData genieData = new GenieData(genieParameterData,(String[][]) rProcessor.getDataFiles());
			set(genieParameterData,genieData);
		}
	}
	
	//--------------------------------------------------------------------------------
	public boolean checkLoadNullData()
	{
		return gp.getLoadNullData();
	}
	
	//--------------------------------------------------------------------------------
	public void loadNullData()
	{
		gd.loadNullData();
	}
	
	//--------------------------------------------------------------------------------
	public String getRgenFileName(){ return gp.getRgenFileName(); }
	
	//--------------------------------------------------------------------------------
	public String getReportType(){ return gp.getReportType(); }
	
	//--------------------------------------------------------------------------------
	public String getStudyName( int studyIndex ){ return gd.getStudyName(studyIndex); }
	
	//--------------------------------------------------------------------------------
	public String getDataSourceName( int studyIndex ){ return gd.getDataSourceName(studyIndex); }
	
	//--------------------------------------------------------------------------------
	public int getRSeed(){ return gp.getRSeed(); }
	
	//--------------------------------------------------------------------------------
	public String getAppVersion(){ return gp.getAppVersion(); }
	
	//--------------------------------------------------------------------------------
	public String getDistribution(){ return gp.getDistribution(); }
	
	//--------------------------------------------------------------------------------
	public String getAppId(){ return gp.getAppId(); }
	
	//--------------------------------------------------------------------------------
	public Calendar getInCal(){ return gp.getInDate(); }
	
	//--------------------------------------------------------------------------------
	public String getoutpathStem(){ return gp.getoutpathStem(); }
	
	//--------------------------------------------------------------------------------
	public AnalysisTable getTable( int studyIndex, int[] simIndices, String table, ColumnManager cm )
	{ 
		return gd.getTable(studyIndex,simIndices,table,cm,gp); 
	}
	
	//--------------------------------------------------------------------------------
	public String[] getTableTypes(){ return gp.getTableTypes(); }
	
	//--------------------------------------------------------------------------------
	public boolean anyRelatedDataSets(){ return gd.anyRelatedDataSets(); }
	
	//--------------------------------------------------------------------------------
	public boolean anyUnrelatedDataSets(){ return gd.anyUnrelatedDataSets(); }

	//--------------------------------------------------------------------------------
	private boolean checkArgs( String[] args )
	{
		boolean argsOK = true;
		if ( args.length < 2 )
		{
			argsOK = false;
			System.err.print("Usage: java -jar Genie.jar application_id rgenfile[.rgen]");
		}
		if ( ! Ut.fExtended(args[1],"rgen").exists() )
		{
			argsOK = false;
			System.err.print("Cannot locate rgen file");
		}
		return argsOK;
	}
	
	//--------------------------------------------------------------------------------
	private void set( GenieParameterData geniePar, GenieData genieData )
	{
		gp = geniePar;
		gd = genieData;
	}
	
	//--------------------------------------------------------------------------------
	public boolean getCaseOnlyIntx(){ return gp.getCaseOnlyIntx(); }
	
	//--------------------------------------------------------------------------------
	public ExpandedAnalysis[] getRgenAnalyses()
	{
		return gp.getRgenAnalyses();
	}
	
	//--------------------------------------------------------------------------------
	public int getNStudy(){ return gd.getNStudy(); }
	
	//--------------------------------------------------------------------------------
	public CCStat getStat( int statIndex, boolean isMeta ){ return gp.getStat(statIndex,isMeta); }
	
	//--------------------------------------------------------------------------------
	public int getNSims(){ return gp.getNSims(); }
	
	//--------------------------------------------------------------------------------
	public Gene[] getGenes(){ return gp.getGenes(); }
	
	//--------------------------------------------------------------------------------
	public int getLociCount( int geneId ){ return gp.getLociCount(geneId); }
	
	//--------------------------------------------------------------------------------
	public Locus getLocus( int geneId, int index ){ return gp.getLocus(geneId, index); }
	
	/** Simulate null data for studies with either independent subjects only or studies with 
	 * related subjects only.
	 * 
	 * @param relateds Boolean value to indicate to simulate studies with relateds or not. 
	 */
	//--------------------------------------------------------------------------------
	public void simNullData( boolean relateds, boolean simulateAll )
	{ 
		//if ( gp.getCaseOnlyIntx() ) gd.setIndShuffle();	
		gd.simNullData(gp,relateds,simulateAll);
	}
	
	//--------------------------------------------------------------------------------
	public void simNullData( boolean simulateAll )
	{
		gd.simNullData(gp,simulateAll);
	}
	
	/**
	 * 
	 */
//	//--------------------------------------------------------------------------------
//	public void simNullData( boolean simulateAll )
//	{ 
//		simNullData(true,simulateAll);
//		simNullData(false,simulateAll);
//	}
	
	//--------------------------------------------------------------------------------
	public void phase( int simIndex ){ gd.phase(gp,simIndex); }
	
	//--------------------------------------------------------------------------------
	public Map<String,String> getHapConstructorParameters(){ return gp.getHapConstructorParameters(); }
	
	//--------------------------------------------------------------------------------
	public StatInterface getStatInterface(){ return gp.getStatInterface(); }
}
