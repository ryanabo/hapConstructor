package edu.utah.med.genepi.hapconstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.utah.med.genepi.analysis.ModelCombo;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.gm.Gene;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisFiller;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisFormat;
import edu.utah.med.genepi.hapconstructor.analysis.CGAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.CTAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.ComboSet;
import edu.utah.med.genepi.hapconstructor.analysis.CompositeFormat;
import edu.utah.med.genepi.hapconstructor.analysis.DynamicAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.HaplotypeAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.HaplotypeFormat;
import edu.utah.med.genepi.hapconstructor.analysis.InteractionFormat;
import edu.utah.med.genepi.hapconstructor.analysis.IntxLDAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.IntxORAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.MarkerCombo;
import edu.utah.med.genepi.hapconstructor.analysis.SingleMarkerAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.SingleMarkerFormat;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.StatInterface;

public class hapCParamManager {

	private boolean significanceTesting = false;
	private boolean onlySignificanceTesting = false;
	private int significanceTestingStart = 0;
	private boolean loadNullData = false;
	private boolean compositeHaplotypes = false;
	private boolean useBackSets = false;
	//	private boolean caseOnlyIntx = false;
	private double[] thresholds = new double[] {0.1,0.05,0.005,0.0005};
	// Models: HAdd, HDom, HRec, MSpecRed, CG, CT, IntxLD, IntxOR
	private String[] models = new String[] {"HDom","HRec","MSpecRed"};
	private boolean stopMostSignificant = true;
	private double[] stopValues = new double[]{0.5,100.0};
	private String[] startModels = new String[] {"Dom","Rec","Allele"};
	// 0 = haplotype, 1 = compositeT, 2 = compositeG, 3 = interactionLD, 4 = interactionOR
	private DynamicAnalysis[] analyses = new DynamicAnalysis[5];
	private StatInterface statinterface = null;
	private hapCAnalysisStep analysisStep = new hapCAnalysisStep();
	private boolean logTableCounts = false;

	public hapCParamManager( StatInterface si, Map<String,String> inputParams )
	{
		statinterface = si;
		for ( Iterator<String> it = inputParams.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next();

			if ( key.equals("hapc_sigtesting") )
			{
				String value = inputParams.get(key);
				if (value.equals("true")) significanceTesting = true;
				else significanceTesting = false;
			}
			else if ( key.equals("hapc_sigtesting_only") )
			{
				String value = inputParams.get(key);
				String[] valueSplit = value.split(",");

				if (valueSplit[0].equals("true")) onlySignificanceTesting = true;
				else onlySignificanceTesting = false;

				significanceTestingStart = Integer.parseInt(valueSplit[1]);

			}
			else if ( key.equals("hapc_loadnulls") )
			{
				String value = inputParams.get(key);
				if (value.equals("true")) loadNullData = true;
				else loadNullData = false;
			}
			else if ( key.equals("hapc_backsets") )
			{
				String value = inputParams.get(key);
				if (value.equals("true")) useBackSets = true;
				else useBackSets = false;
			}
			else if ( key.equals("hapc_threshold") )
			{
				String[] value = inputParams.get(key).split(",");
				double[] m = new double[value.length];
				for( int i=0; i < value.length; i++ ) m[i] = Double.parseDouble(value[i]);
				thresholds = m;
			}
			else if ( key.equals("hapc_stopvalues") )
			{
				String[] value = inputParams.get(key).split(",");
				double[] m = {};
				for( int i=0; i < value.length; i++ ) m[i] = Double.parseDouble(value[i]);
				stopValues = m;
			}
			else if ( key.equals("hapc_models") )
			{
				String[] value = inputParams.get(key).split(",");
				models = value;
			}
			else if ( key.equals("hapc_startmodels") )
			{
				String[] value = inputParams.get(key).split(",");
				startModels = value;
			}
			else if ( key.equals("hapc_check_mostsignificant") )
			{
				String value = inputParams.get(key);
				if (value.equals("true")) stopMostSignificant = true;
				else stopMostSignificant = false;
			}
			else if ( key.equals("hapc_compositehaplotypes") )
			{
				String value = inputParams.get(key);
				if (value.equals("true")) compositeHaplotypes = true;
				else compositeHaplotypes = false;
			}
			else if ( key.equals("hapc_logtablecounts") )
			{
				String value = inputParams.get(key);
				if (value.equals("true")) logTableCounts = true;
				else logTableCounts = false;
			}
			//			else if ( key.equals("hapc_caseonlyIntx") )
			//			{
			//				String value = inputParams.get(key);
			//				if (value.equals("true")) caseOnlyIntx = true;
			//				else caseOnlyIntx = false;
			//			}
		}
		setupAnalyses();
	}
	
	//----------------------------------------------------------------------------
	public boolean getSigTesting(){ return significanceTesting; }
	
	//----------------------------------------------------------------------------
	public boolean getLogTableCounts(){ return logTableCounts; }

	//----------------------------------------------------------------------------
	public double[] getStoppingValues(){ return stopValues; }

	//----------------------------------------------------------------------------
	public boolean checkStopAtMostSignificant(){ return stopMostSignificant; }

	//----------------------------------------------------------------------------
	public boolean runSignificance(){ return significanceTesting; }

	//----------------------------------------------------------------------------
	public boolean useBackSets(){ return useBackSets; }

	//----------------------------------------------------------------------------
	public String[] getStartModels(){ return startModels; }

	//----------------------------------------------------------------------------
	public boolean onlyHaplotypeModels()
	{
		boolean haplotypeModel = false;
		for ( int i=0; i < models.length; i++ )
		{
			boolean haplotyping = models[i].equals("HDom") || models[i].equals("HRec") || models[i].equals("HAdd") || models[i].equals("MSpecRed");
			if (haplotyping)
			{
				haplotypeModel = true;
				break;
			}
		}
		return haplotypeModel;
	}

	//----------------------------------------------------------------------------
	public boolean haplotypeModels()
	{
		boolean haplotypeModel = false;
		for ( int i=0; i < models.length; i++ )
		{
			boolean haplotyping = models[i].equals("HDom") || models[i].equals("HRec") || models[i].equals("HAdd") || models[i].equals("MSpecRed");
			haplotyping = haplotyping || ((models[i].equals("CT1") || models[i].equals("CG1") || models[i].contains("Intx")) && compositeHaplotypes);
			if (haplotyping)
			{
				haplotypeModel = true;
				break;
			}
		}
		return haplotypeModel;
	}

	//----------------------------------------------------------------------------
	public DynamicAnalysis getStartAnalyses()
	{
 		//System.out.println("getStartAnalyses ()");
		SingleMarkerFormat[] formats = new SingleMarkerFormat[startModels.length];
		for ( int i=0; i < startModels.length; i++ )
		{
			String model = startModels[i];
			int ncols = 2;
			String type = "Genotype";
			if ( model.equals("Trend") || model.equals("Add") ) ncols = 3;
			else if ( model.equals("Allele") ) type = "Allele";
			Integer[] selectedstats = whichStats(model,ncols,false);
			Integer[] selectedmetas = whichStats(model,ncols,true);
			SingleMarkerFormat af = new SingleMarkerFormat(model,selectedstats,selectedmetas,type,ncols);
			formats[i] = af;
		}
 		//System.out.println("getStartAnalyses () -   END");
		return new SingleMarkerAnalysis(formats);
	}

	//----------------------------------------------------------------------------
	public DynamicAnalysis[] getStartAnalyses( GenieDataSet gs )
	{
 		//System.out.println("getStartAnalyses (gs)");
		InteractionFormat[] formats = new InteractionFormat[1];
		Gene[] genes = gs.getGenes();
		if ( genes.length > 1 )
		{
			Locus[] loci1 = genes[0].getLoci();
			Locus[] loci2 = genes[1].getLoci();
			int ncombos = loci1.length * loci2.length;
			AnalysisFiller[] afs = new AnalysisFiller[ncombos];
			int iter = 0;
			for ( int i=0; i < loci1.length; i++ )
			{
				for ( int j=0; j < loci2.length; j++ )
				{
					MarkerCombo mc = new MarkerCombo(loci1[i],loci2[j]);
					ModelCombo modelcombo = new ModelCombo( new String[]{null,null} );

					long newAlleleValues = (1 << loci1[i].getIndex()) | (1 << loci2[j].getIndex());
                                //System.out.println("for " + i + " - " + j + " " +  loci1[i].getIndex() + " " +  loci2[j].getIndex() ); 
				//System.out.println(" Or the above 2 values, new allele value is " + newAlleleValues );
					ComboSet cset = new ComboSet(mc,newAlleleValues,modelcombo,"compositebetween","addmarker");
					Integer[] analysisIndices = getAnalysisIndex("interaction","addmarker");
					for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
//					AnalysisFiller af = new AnalysisFiller(cset.getValueCombo(),new MarkerCombo[]{mc},new ModelCombo[]{modelcombo});
//					afs[iter] = af;
//					iter++;
				}
			}
//			Integer[] analysisIndices = getAnalysisIndex("interaction","addmarker");
//			for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].setFillers(afs);
			for ( int k=0; k < analyses.length; k++)
			{
				if ( analyses[k] != null ) analyses[k].resetFillers();
			}
		}
		else
		{
			System.err.print("Need two genes for interaction analysis");
			System.exit(1);			
		}
		return analyses;
	}

	//----------------------------------------------------------------------------
	public Integer[] getAnalysisIndex( String type, String action )
	{
		List<Integer> indices = new ArrayList<Integer>();
		if ( type.equals("haplotype") )
		{
			if ( analyses[0] != null )indices.add(0);
		}
		else if ( type.contains("composite") )
		{
			if ( analyses[1] != null ) indices.add(1);
			if ( analyses[2] != null ) indices.add(2);
		}
		if ( type.contains("compositebetween") || type.contains("interaction") )
		{
			if ( analyses[3] != null ) indices.add(3);
			if ( analyses[4] != null ) indices.add(4);
		}
		return indices.toArray(new Integer[0]);
	}

	//----------------------------------------------------------------------------
	public double getThreshold( int step )
	{
		return step < thresholds.length ? thresholds[step] : thresholds[thresholds.length-1];
	}

	//----------------------------------------------------------------------------
	public void setupAnalyses()
	{
		Map<String,List<AnalysisFormat>> analysisBuffer = new HashMap<String,List<AnalysisFormat>>();

		for ( int i=0; i < models.length; i++ )
		{
			String model = models[i];
			String firstLetter = model.substring(0, 1);
			String secondLetter = model.substring(1, 2);
			int ncols = 2;
			String formatType = "Haplotype";
			String type = "Genotype";
			if ( firstLetter.equals("H") && secondLetter.equals("A") ) ncols = 3;
			else if ( firstLetter.equals("M") ) type = "Allele";
			else if ( firstLetter.equals("C") )
			{
				formatType = "CompositeG";
				if ( secondLetter.equals("T") ) 
				{
					formatType = "CompositeT";
					ncols = 5;
				}
			}
			else if ( firstLetter.equals("I") )
			{
				formatType = "InteractionLD";
				if ( model.contains("O") )
				{
					ncols = 4;
					formatType = "InteractionOR";
				}
				else ncols = 0;
			}

			Integer[] selectedstats = whichStats(model,ncols,false);
			Integer[] selectedmetas = whichStats(model,ncols,true);

			if ( formatType.equals("Haplotype") )
			{
				HaplotypeFormat af = new HaplotypeFormat(model,selectedstats,selectedmetas,type,ncols);
				addFormat(analysisBuffer,formatType,(AnalysisFormat) af);
			}
			else if ( formatType.contains("Composite") )
			{
				CompositeFormat af = new CompositeFormat(model,selectedstats,selectedmetas,type,ncols);
				addFormat(analysisBuffer,formatType,(AnalysisFormat) af);
			}
			else if ( formatType.contains("Interaction") )
			{
				InteractionFormat af = new InteractionFormat(model,selectedstats,selectedmetas,type,ncols);
				addFormat(analysisBuffer,formatType,(AnalysisFormat) af);
			}
		}

		for ( Iterator<String> it = analysisBuffer.keySet().iterator(); it.hasNext(); )
		{
			String formatType = it.next();
			List<AnalysisFormat> formats = analysisBuffer.get(formatType);
			if ( formatType.equals("Haplotype") ) 
			{
				analyses[0] = new HaplotypeAnalysis(formats.toArray(new HaplotypeFormat[0]));	
			}
			else if ( formatType.equals("CompositeT") )
			{
				analyses[1] = new CTAnalysis(formats.toArray(new AnalysisFormat[0]));
			}
			else if ( formatType.equals("CompositeG") )
			{
				analyses[2] = new CGAnalysis(formats.toArray(new CompositeFormat[0]));
			}
			else if ( formatType.equals("InteractionLD") )
			{
				analyses[3] = new IntxLDAnalysis(formats.toArray(new InteractionFormat[0]));
			}
			else if ( formatType.equals("InteractionOR") )
			{
				analyses[4] = new IntxORAnalysis(formats.toArray(new InteractionFormat[0]));
			}	
		}
	}

	//----------------------------------------------------------------------------
	public void addFormat(Map<String,List<AnalysisFormat>> analysisBuffer,String formatType,AnalysisFormat af)
	{
		if ( !analysisBuffer.containsKey(formatType) )
		{
			List<AnalysisFormat> analysisList = new ArrayList<AnalysisFormat>();
			analysisBuffer.put(formatType,analysisList);
		}
		List<AnalysisFormat> alist = analysisBuffer.get(formatType);
		alist.add(af);
		analysisBuffer.put(formatType,alist);
	}

	//----------------------------------------------------------------------------
	public DynamicAnalysis[] getAnalyses(){ return analyses; }

	//----------------------------------------------------------------------------
	public Integer[] whichStats( String model, int ncols, boolean metaStats )
	{
		List<Integer> selectedStats = new ArrayList<Integer>();
		CCStat[] stats = null;
		if ( ! metaStats ) stats = statinterface.getStats();
		else stats = statinterface.getMetas();

		for ( int i=0; i < stats.length; i++ )
		{
			CCStat ccstat = stats[i];
			String statName = ccstat.getName();

			if ( statName.contains("Int") )
			{
				if ( model.equals("IntxOR") && statName.contains("Interaction O") ) selectedStats.add(i);
				if ( model.equals("IntxLD") && statName.contains("IntxLD") ) selectedStats.add(i);
			}
			else if ( statName.contains("Trend") )
			{
				if ( ncols > 2 ) selectedStats.add(i);
			}
			else if ( ncols == 2 || ncols == 3 ) selectedStats.add(i);
		}
		return selectedStats.toArray(new Integer[0]);
	}

	//----------------------------------------------------------------------------
	public void resetStep(){ analysisStep.reset(); }

	//----------------------------------------------------------------------------
	public void incrementSigTestingIndex(){ analysisStep.incrementSigTestingIndex(); }

	//----------------------------------------------------------------------------
	public int getStep(){ return analysisStep.getStep(); }

	//----------------------------------------------------------------------------
	public int getSigTestingIndex(){ return analysisStep.getSigTestingIndex(); }

	//----------------------------------------------------------------------------
	public hapCAnalysisStep getAnalysisStep(){ return analysisStep; }

	//----------------------------------------------------------------------------
	public void incrementStep(){ analysisStep.incrementStep(useBackSets()); }

	//----------------------------------------------------------------------------
	public int getStepDirection(){ return analysisStep.getDirection(); }

	//----------------------------------------------------------------------------
	public boolean anyCompositeModels()
	{
		boolean compositeModel = false;
		for ( int i=0; i < models.length; i++ )
		{
			boolean composite = models[i].contains("CG") || models[i].contains("CT");
			if (composite)
			{
				compositeModel = true;
				break;
			}
		}
		return compositeModel;
	}

	//----------------------------------------------------------------------------
	public boolean anyInteractionModels()
	{
		boolean intxModel = false;
		for ( int i=0; i < models.length; i++ )
		{
			boolean intx = models[i].contains("Intx");
			if (intx)
			{
				intxModel = true;
				break;
			}
		}
		return intxModel;
	}

	//----------------------------------------------------------------------------
	public boolean useCompositeHaplotypes(){ return compositeHaplotypes; }

	//----------------------------------------------------------------------------
	public String getAllPvalFileName()
	{
		String fileName = "all_obs.final";
		if ( analysisStep.getSigTestingIndex() > -1 ) fileName = "all_sims.final";

		return fileName;
	}

	//----------------------------------------------------------------------------
	public String getBuildFileName( String pathName )
	{
		StringBuffer sb = new StringBuffer();
		sb.append(pathName);
		sb.append("_");
		sb.append(getStep());
		sb.append("-");
		sb.append(analysisStep.getSigTestingIndex()+1);
		sb.append(".build");
		String buildFileName = sb.toString(); 
		return buildFileName;
	}


}
