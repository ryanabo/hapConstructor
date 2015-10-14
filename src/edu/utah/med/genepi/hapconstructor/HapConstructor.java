package edu.utah.med.genepi.hapconstructor;

import java.io.IOException;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ModelCombo;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisFiller;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisFormat;
import edu.utah.med.genepi.hapconstructor.analysis.DynamicAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.MarkerCombo;
import edu.utah.med.genepi.hapconstructor.analysis.ValueCombo;
import edu.utah.med.genepi.hapconstructor.analysis.ValueUnit;
import edu.utah.med.genepi.hc.Evalsigs;
import edu.utah.med.genepi.stat.CCStat.Result;

public class HapConstructor {

	private GenieDataSet genieDataSet = null;
	private hapCParamManager hapconparameters = null;
	private hapCResultsManager hapconresults = null;
	
	public HapConstructor( GenieDataSet gs )
	{
		genieDataSet = gs;
		hapconparameters = new hapCParamManager(gs.getStatInterface(),gs.getHapConstructorParameters());
		hapconresults = new hapCResultsManager(gs,hapconparameters,genieDataSet.getLociCount(-1));
		
		// Check if only doing single marker composite genotypes.
		System.out.println("Phasing data");
		if( hapconparameters.haplotypeModels() ) genieDataSet.phase(0);
		// Sim datasets with related subjects and store them.
		//if ( gs.anyRelatedDataSets() ){ gs.simNullData(true,true); }
		//gs.simNullData(true,true);
		
		if ( !gs.checkLoadNullData() )
		{
			System.out.println("Simulating " + genieDataSet.getNSims() + " nulls." );
			gs.simNullData(true);
		}
		else
		{
			gs.loadNullData();
		}
		
		System.out.println("Begin construction." );
		construct();
		if ( hapconparameters.runSignificance() )
		{
			for ( int i = 0; i < Math.min(1000,genieDataSet.getNSims()); i++ )
			{
				hapconparameters.resetStep();
				//genieDataSet.simNullData(false,false);
				hapconparameters.incrementSigTestingIndex();
				System.out.println("Sig testing " + i);
				construct();
			}
			Evalsigs es = new Evalsigs();
			es.readfile("all_obs.final");
			es.readfile("all_sims.final");
			try {
			    es.write_efdr(es.efdr());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//----------------------------------------------------------------------------
	public void construct( )
	{	
		boolean continueAnalysis = true;
		while( continueAnalysis ) continueAnalysis = run();
	}
	
	//----------------------------------------------------------------------------
	public boolean run()
	{
		boolean continueAnalyses = true;
		DynamicAnalysis[] analyses = buildAnalyses();
	    
	    // Run analyses for observed data and assess their significance as you go.
		System.out.println("\nStep " + hapconparameters.getStep() + " ");
		continueAnalyses = runAnalysis(analyses);
		hapconparameters.incrementStep();	    
	    return hapconparameters.getStep() > genieDataSet.getLociCount(-1) ? false : continueAnalyses;
	}
	
	//----------------------------------------------------------------------------
	public boolean runAnalysis( DynamicAnalysis[] analyses )
	{
		int nanalyses = estimateNAnalyses(analyses);
		// column 0 = analysisIndex, column 1 = progress index
		int[] iter = new int[]{0,0};
		for ( int i=0; i < analyses.length; i++ )
		{
			if ( analyses[i] != null )
			{
				AnalysisFormat[] formats = analyses[i].getFormats();
				AnalysisFiller[] fillers = analyses[i].getFillers();
				for ( int j=0; j < formats.length; j++ )
				{
					for ( int k=0; k < fillers.length; k++)
					{
						iter[1] = checkProgress(iter[0],nanalyses,iter[1]);
						expandAnalysis(formats[j],fillers[k]);
						iter[0]++;
					}
				}
			}
		}
		//System.out.println("Processing results - hapCResultsManager .processResults Next step");
		return hapconresults.processResults();
	}
	
	//----------------------------------------------------------------------------
	private void expandAnalysis( AnalysisFormat format, AnalysisFiller filler )
	{
		//System.out.println("expandAnalysis");
		int nStudies = genieDataSet.getNStudy();
		Integer[] stats = format.getStatIndices();
		Integer[] metas = format.getMetaIndices();
		String model = format.getModel();
		
		for ( int i = 0; i < filler.getNMarkerCombos(); i++ ) 
		{
			ColumnManager[] columnmanagers = format.getColumnManager(filler,i);
			for ( int j=0; j < columnmanagers.length; j++ )
			{
				for ( int k = 0; k < nStudies; k++ ) 
				{
					for (int l = 0; l < stats.length; ++l ) 
					{
						int[] studyIndex = new int[]{k};
						Analysis a = new Analysis(genieDataSet,columnmanagers[j],"original",
								studyIndex,genieDataSet.getStat(stats[l],false),model);
						analyze(a);
					}
				}
				for ( int k = 0 ; k < metas.length; k++ ) 
				{
					int[] studyIndices = new int[nStudies];
					for ( int l = 0; l < nStudies; l++ ) studyIndices[l] = l;
					Analysis a = new Analysis(genieDataSet,columnmanagers[j],"original",
							studyIndices,genieDataSet.getStat(metas[k],true),model);
					analyze(a);
				}
			}
		}	
	}
	
	//----------------------------------------------------------------------------
	private void analyze( Analysis analysis )
	{		
		int[] simIndices = new int[]{0,hapconparameters.getSigTestingIndex()};
	    boolean continueSigTesting = !analysis.analyze(simIndices);
	    int nSims = genieDataSet.getNSims()+1;
	    simIndices[0]++;
	    while ( (simIndices[0] < nSims) && continueSigTesting )
	    {	    	
	    	//System.out.println("Sim index" + simIndices[0]);
	    	// If significance testing, don't use the simulation as 'observed data' and use the same null data
	    	// set to assess it. This would be the case with datasets with related subjects.
	    	if ( !((simIndices[1] > -1) && (simIndices[0] == (simIndices[1]+1))) ) analysis.analyze(simIndices);
	    	continueSigTesting = checkStopping(analysis,simIndices[0]);
	    	simIndices[0]++;
	    }
		if ( !analysis.failed() ) hapconresults.processAnalysis(analysis,hapconparameters.getAnalysisStep(),nSims);
	}
	
	//----------------------------------------------------------------------------
	private boolean checkStopping( Analysis analysis, int nthSim )
	{
		boolean continueSims = false;
		Result analysisComparisonResult = (Result) analysis.getComparisonResult();
		double[] pvals = analysisComparisonResult.doubleValues();
		
		double[] stopValues = hapconparameters.getStoppingValues();
		if ( nthSim >= stopValues[1] )
		{
			// If enough sims have been done to assess whether analysis is worth
			// continued evaluation.
			
			for ( int i=0; i < pvals.length; i++ )
			{
				// If there is one pvalue in the series that is below the threshold
				// then keep evaluating.
				if ( (pvals[i] < stopValues[0]) ) continueSims = true;
			}
		}
		else continueSims = true;
		return continueSims;
	}
	
	//----------------------------------------------------------------------------
	private int estimateNAnalyses(DynamicAnalysis[] analyses)
	{
		int n = 0;
		for ( int i=0; i < analyses.length; i++ )
		{
			if ( analyses[i] != null )
			{
				int nformats = analyses[i].getFormats().length;
				int nfillers = analyses[i].getFillers().length;
				n += nformats*nfillers;
			}
		}
		return n;
	}
	
	//----------------------------------------------------------------------------
	public int checkProgress( int analysisIndex, int nAnalyses, int progress )
	{
		if ( (analysisIndex/(double)nAnalyses) >= (progress * 0.1) )
		{
			System.out.print(progress % 2 == 0 ? "/" : "\\");
			progress++;
		}
		return progress;
	}
	
	//----------------------------------------------------------------------------
//	public void report( ExpandedAnalysis[] analyses )
//	{
//		for ( int i=0; i < analyses.length; i++ ) analyses[i].report();
//	}
	
	//----------------------------------------------------------------------------
	public DynamicAnalysis[] buildAnalyses()
	{
		DynamicAnalysis[] analyses = null;
		AnalysisFiller[] aFillers = null;
		boolean caseonly = genieDataSet.getCaseOnlyIntx();
		if ( hapconparameters.getStep() == 1 )
		{
			if ( !caseonly )
			{
				analyses = new DynamicAnalysis[1];
				DynamicAnalysis da = hapconparameters.getStartAnalyses();
				aFillers = new AnalysisFiller[] {buildSingleMarkerFiller()};
				da.setFillers(aFillers);
				analyses[0] = da;
			}
			else
			{
				analyses = hapconparameters.getStartAnalyses(genieDataSet);
			}
		}
		else
		{
			analyses = hapconparameters.getAnalyses();
			for ( int i=0; i < analyses.length; i++ )
			{
				DynamicAnalysis da = analyses[i];
				if ( da != null ) da.resetFillers();
			}
		}
		return analyses;
	}
	
	//----------------------------------------------------------------------------
	public AnalysisFiller buildSingleMarkerFiller()
	{
		int nLoci = genieDataSet.getLociCount(-1);
		MarkerCombo[] mus = new MarkerCombo[nLoci];		
		for ( int i=0; i < nLoci; i++ )
		{
			Locus l = genieDataSet.getLocus(-1,i);
			mus[i] = new MarkerCombo(l);
		}
		byte[][] values = new byte[1][1];
		values[0] = new byte[]{2};
		ValueUnit vu = new ValueUnit(values,"singlemarker");
		ValueCombo vc = new ValueCombo(new ValueUnit[]{vu});
		
		ModelCombo modelcombo = new ModelCombo(new String[]{null});
		AnalysisFiller afiller = new AnalysisFiller(vc,mus,new ModelCombo[]{modelcombo});
		return afiller;
	}
}
