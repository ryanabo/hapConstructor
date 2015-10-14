package edu.utah.med.genepi.hapconstructor;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Row;
import edu.utah.med.genepi.hapconstructor.analysis.ComboSet;
import edu.utah.med.genepi.stat.CCStat.Result;
import edu.utah.med.genepi.util.Ut;

public class hapCResultImp implements hapCResult {

	protected ComboSet comboset = null;
	protected Result[] result = new Result[2];
	protected String model = null;
	protected String statName = null;
	protected boolean statFailed = false;
	protected AnalysisTable[] at = null;
	protected long locusAddress = 0;
	
	public hapCResultImp( Analysis analysis, boolean logTableCounts )
	{
		comboset = analysis.getComboSet();
		//String comb = "not null";
                //if ( comboset == null ) 
		//  comb = "null";
		//System.out.println(" hapCResultImp - comboset is " + comb);
		model = analysis.getModel();
		locusAddress = comboset.getLocusAddress();
		statName = analysis.getStatName();
		if ( logTableCounts ) at = analysis.getTables();
		if ( !analysis.failed() )
		{
			result[0] = analysis.getObservedResult();
			result[1] = analysis.getComparisonResult();
		}
		else statFailed = true;
	}
	
	//---------------------------------------------------------------------------	
	public ComboSet getComboSet() { return comboset; }

	//---------------------------------------------------------------------------	
	public String getBufferKey(){ return comboset.getBufferKey(); }
	
	//---------------------------------------------------------------------------
	public long getHaplotype(boolean left2right){ return comboset.getHaplotype(left2right); }
	
	//---------------------------------------------------------------------------
	public String getObservedReport(){ return result[0].toString(); }
	
	//---------------------------------------------------------------------------
	public String getInferentialReport(){ return result[1].toString(); }
	
	//---------------------------------------------------------------------------	
	public long getLocusAddress(){ return locusAddress; }
	
	//---------------------------------------------------------------------------
	public boolean checkFailed(){ return statFailed; }
	
	//---------------------------------------------------------------------------
	public String getOutput()
	{
		int[] loci = comboset.getLoci();
		Long val = comboset.getHaplotype(true);
		int[] nonzeroIndex = new int[loci.length];
		for ( int i=0; i < loci.length; i++ ) nonzeroIndex[i] = loci[i]+1;
		String markerStr = Ut.array2str(nonzeroIndex,"-");
		
		String[] resultValues = new String[]{markerStr,model,statName,result[0].toString(),result[1].toString()};
		String rValues = Ut.array2str(resultValues,":");
		if ( at != null ) rValues += "\t" + addTableCounts(at);
		return rValues;
	}
	
	//---------------------------------------------------------------------------
	public String addTableCounts(AnalysisTable[] at)
	{
		String tableCounts = new String();
		for ( int i =0; i < at.length; i++ )
		{
			if ( i > 0 ) tableCounts.concat(";");
			int nrows = at[i].getRowCount();
			for ( int j=0; j < nrows; j++ )
			{
				Row r = at[i].getRowAt(j);
				Number[] cells = r.getCells();
				for ( int k=0; k<cells.length; k++ ) 
				{
					int v = cells[k].intValue();
					String s = Integer.toString(v);
					String delim = ",";
					if ( j == (nrows-1) && k == (cells.length-1) ) delim = "";
					tableCounts += s + delim;
				}
			}
		}
		return tableCounts;
	}
	
	//---------------------------------------------------------------------------
	public boolean metSignificance( double sigThreshold )
	{
		boolean metThreshold = false;
		if ( result[1] != null && !statFailed )
		{
			double[] pvals = result[1].doubleValues();
			for ( int i=0; i < pvals.length; i++ )
			{
				if ( (pvals[i] <= sigThreshold ) ) metThreshold = true;
			}
		}
		return metThreshold;
	}

	//---------------------------------------------------------------------------
	public void newMarkerSet(int i, GenieDataSet gd, hapCParamManager params,
			HaplotypeResultStorage significantHaplotypes)
	{}

	//---------------------------------------------------------------------------
	public void reducedMarkerSet(int i, GenieDataSet gd, hapCParamManager params) 
	{}

	//---------------------------------------------------------------------------
	public boolean hitMostSignificant(int nSims) 
	{
		boolean hit = false;
		if ( !statFailed )
		{
			double[] pvals = result[1].doubleValues();
			for ( int i=0; i < pvals.length; i++ )
			{
				int hits = (int) (pvals[i] * nSims);
				if ( hits < 2 ) hit = true;
			}
		}
		return hit;
	}
}
