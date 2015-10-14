package edu.utah.med.genepi.genie;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import edu.utah.med.genepi.analysis.ExpandedAnalysis;
import edu.utah.med.genepi.gm.Gene;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.gm.MarkerManager;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.StatInterface;
import edu.utah.med.genepi.util.GEException;

public class GenieParameterData {

	private String appId = null;
	private String appVersion = null;
	private String rgenName = null;
	private int nSims = 1000;
	private int nWeightedCycle = 0;
	public int rSeed = 0;
	private int nloci = 0;
	public double percent = 0.0;
	private GSimulator.Top topSim = null;
	private GSimulator.Drop dropSim = null;
	private String phaser = null;
	private String missingDataValue = null;
	private PedData.Printer dataDumper = null;
	private String[] tableTypes = null;
	private int[] covarIDs = null;
	private MarkerManager markermanager = null;
	private StatInterface statinterface = null;
	private boolean header = true;
	private boolean phaseData = false;
	private boolean caseOnlyIntx = false;
	private String topSample = null;
	private ExpandedAnalysis[] rgenAnalyses = null;
	private RgenProcessor rgenProcessor = null;
	private String outpathStem = null;
	private String rgenFileName = null;
	private String reportType = "report";
	private String distribution = null;
	private Calendar inDate = null;
	private boolean logNullData = false;
	private int startNullLogIndex = 1;
	private boolean loadNullData = false;
	private boolean phaseTogether = false;
	
	private Map<String, String> intx_params = new HashMap<String, String>();

	public GenieParameterData( RgenProcessor rp, String ai, String version ) throws GEException
	{
		appId = ai;
		appVersion = version;
		inDate = Calendar.getInstance();
		topSample = rp.getTopSample();
		rgenName = rp.getRgenFilePath();
		nSims = rp.getNSims();
		nWeightedCycle = rp.getNWeightedCycles();
		rSeed = rp.getRSeed();
		phaseData = checkPhaseData(rp);
		topSim = rp.getTopSim();
		dropSim = rp.getDropSim();
		percent = rp.getPercent();
		missingDataValue = rp.getMissingDataValue();
		dataDumper = rp.getDataDumper();
		tableTypes = rp.getTableTypes();
		covarIDs = rp.getCovarIDs();
		markermanager = new MarkerManager(rp.getLoci());
		statinterface = rp.getStats();
		rgenAnalyses = rp.getRgenAnalyses(statinterface,markermanager);
		rgenProcessor = rp;
		outpathStem = rp.getoutpathStem();
		caseOnlyIntx = rp.getCaseOnlyIntx();
		phaser = rp.getPhaser();
		rgenFileName = rp.getRgenFileName();
		distribution = rp.getDistribution();
		reportType = rp.getReportType();
		logNullData = rp.getLogNullData();
		startNullLogIndex = rp.getStartLogNullIndex();
		loadNullData = rp.loadNullData();
		phaseTogether = rp.getPhaseTogether();
	}
	
	private boolean checkPhaseData( RgenProcessor rp )
	{
		boolean phaseData = false;
		if ( appId.equals("hapmc") ) phaseData = true;
		else phaseData = rp.phaseData();
		return phaseData;
	}
	
	//----------------------------------------------------------------------------
	public int[] getMarkerIndices( int geneIndex )
	{
		return markermanager.getMarkerIndices(geneIndex);
	}
	
	//----------------------------------------------------------------------------
	public boolean getPhaseTogether(){ return phaseTogether; }
	
	//----------------------------------------------------------------------------
	public boolean getLoadNullData(){ return loadNullData; }
	
	//----------------------------------------------------------------------------
	public boolean logNullData()
	{
		if ( appId.equals("simnull") ) logNullData = true;
		return logNullData; 
	}
	
	//----------------------------------------------------------------------------
	public int startNullLogIndex(){ return startNullLogIndex; }
	
	//----------------------------------------------------------------------------
	public String[] getTableTypes(){ return tableTypes; }
	
	//----------------------------------------------------------------------------
	public String getDistribution(){ return distribution; }
	
	//----------------------------------------------------------------------------
	public Calendar getInDate(){ return inDate; }
	
	//----------------------------------------------------------------------------
	public int getRSeed(){ return rSeed; }
	
	//----------------------------------------------------------------------------
	public String getReportType(){ return reportType; }
	
	//----------------------------------------------------------------------------
	public String getRgenFileName(){ return rgenFileName; }
	
	//----------------------------------------------------------------------------
	public String getAppId(){ return appId; }
	
	//----------------------------------------------------------------------------
	public String getAppVersion(){ return appVersion; }
	
	//----------------------------------------------------------------------------
	public Gene[] getGenes(){ return markermanager.getGenes(); }
	
	//----------------------------------------------------------------------------
	public String getPhaser(){ return phaser; }
	
	//----------------------------------------------------------------------------
	public boolean getCaseOnlyIntx(){ return caseOnlyIntx; }
	
	//----------------------------------------------------------------------------
	public double getPercentGt(){ return percent; }
	
	//----------------------------------------------------------------------------
	public String getoutpathStem(){ return outpathStem; }
	
	//----------------------------------------------------------------------------
	public boolean phaseData(){ return phaseData; }
	
	//----------------------------------------------------------------------------
	public int getNSims(){ return nSims; }
	
	//----------------------------------------------------------------------------
	public int[] getCovarIDs(){ return covarIDs; }
	
	//----------------------------------------------------------------------------
	public String getTopSample(){ return topSample; }
	
	//----------------------------------------------------------------------------
	public boolean hasDataHeader(){ return header; }
	
	//----------------------------------------------------------------------------
	public CCStat getStat( int statIndex, boolean isMeta ){ return statinterface.getStat(statIndex,isMeta); }
	
	//----------------------------------------------------------------------------
	public int getAlleleCode( int studyIndex, int locusIndex, String alleleValue ){ return markermanager.getAlleleCode(studyIndex,locusIndex,alleleValue); }
	
	//----------------------------------------------------------------------------
	public void storeLocusMap( HashMap<String,Integer>[] locusMap, int studyIndex )
	{ 
		markermanager.storeLocusMap(locusMap,studyIndex); 
	}
	
	//----------------------------------------------------------------------------
	public ExpandedAnalysis[] getRgenAnalyses(){ return rgenAnalyses; }
	
	//----------------------------------------------------------------------------
	public void setLocusMap( int nstudies ){ markermanager.setLocusMap(nstudies); }
	
	//----------------------------------------------------------------------------
	public int getLociCount( int geneId ){ return markermanager.getLociCount(geneId); }
	
	//----------------------------------------------------------------------------
	public Locus getLocus( int geneId, int index ){ return markermanager.getLocus( geneId, index ); }
	
	//----------------------------------------------------------------------------
	public Map<String,String> getHapConstructorParameters(){ return rgenProcessor.getHapConstructorParameters(); }
	
	//----------------------------------------------------------------------------
	public StatInterface getStatInterface(){ return statinterface; }

}
