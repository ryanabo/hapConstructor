package edu.utah.med.genepi.analysis;

import java.util.Vector;

import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.ResultImp;

public class AnalysisReport implements Report {

	private Analysis analysis = null;
	private CCStat stat = null;
	private CCStat.Result r0Result = null;
	private CCStat.Result observedResult = null;
	private CCStat.Result pValue = null;
	private CCStat.ComparisonTracker compTracker = null;
	
	public AnalysisReport( Analysis a )
	{
		analysis = a;
		r0Result = a.getR0Result();
		stat = a.getStat();
		compTracker = a.getCompTracker();
	    if ( r0Result instanceof ResultImp.StringResult ) pValue = new ResultImp.StringResult("-");
	    else pValue = stat.getInferentialResult(compTracker,a.getRefColIndex());
	}

	//---------------------------------------------------------------------------
    public String getTitle(){ return stat.getName(); }

	//---------------------------------------------------------------------------
    public String getCompStatTitle(){ return stat.getCompStatTitle(); }
    
	//---------------------------------------------------------------------------
    public String getObsValue(){ return r0Result.toString(); }

	//---------------------------------------------------------------------------
    public int[] getNumSimulationReport(){ return compTracker.getComparisonCount(); }

	//---------------------------------------------------------------------------
    public String getFullObservationalReport(){ return "Observed statistic : " + observedResult; }
   
	//---------------------------------------------------------------------------
    public String getObservationalReport() 
    {
    	CCStat.Result observedResult = analysis.getObservedResult();
    	if ( observedResult == null ) return "null";
    	else return observedResult.toString();
    }

	//---------------------------------------------------------------------------
    public String getObsExtraReport()
    {
      String extraStat = null;
      CCStat.Result obsExtra = stat.getObsExtraStat(r0Result);
      if ( obsExtra != null ) extraStat = stat.getObsExtraStatTitle() + obsExtra;
      return extraStat;
    }

	//---------------------------------------------------------------------------
    public String getFullInferentialReport(){ return "Empirical p-value : " + pValue; }

	//---------------------------------------------------------------------------
    public String getInferentialReport(){ return pValue.toString(); }

	//---------------------------------------------------------------------------
    public String getInfExtraStatTitle(){ return stat.getInfExtraStatTitle(); }

	//---------------------------------------------------------------------------
    public String getInfExtraReport()
    {
      String extraStat = null;
      if ( stat.getInfExtraStat(compTracker) != null )
        extraStat = " " + stat.getInfExtraStat(compTracker);
      return extraStat;
    }

	//---------------------------------------------------------------------------
    public Vector<Vector<Double>> getInfSimReport(){ return stat.getInfSimStat(compTracker); }

	//---------------------------------------------------------------------------
    public String getWarning(){ return compTracker.getMessages(); }
}
