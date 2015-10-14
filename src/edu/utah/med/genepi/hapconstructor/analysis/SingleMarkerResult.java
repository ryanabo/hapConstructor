package edu.utah.med.genepi.hapconstructor.analysis;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.analysis.ModelCombo;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.hapconstructor.HaplotypeResultStorage;
import edu.utah.med.genepi.hapconstructor.hapCParamManager;
import edu.utah.med.genepi.hapconstructor.hapCResultImp;

public class SingleMarkerResult extends hapCResultImp {
	
	public SingleMarkerResult( Analysis analysis, boolean logTableCounts )
	{
		super(analysis,logTableCounts);
	}
	
	//---------------------------------------------------------------------------	
	public void newMarkerSet(int newIndex, GenieDataSet gd, hapCParamManager params, HaplotypeResultStorage significantHaplotypes )
	{
		DynamicAnalysis[] analyses = params.getAnalyses();
		Locus l = gd.getLocus(-1,newIndex);
		int geneId = l.getGeneId();
		
		List<ComboAddress> addresses = new ArrayList<ComboAddress>();
		
		GeneUnit geneunit = comboset.getGeneUnits()[0];
		// Extend haplotype within a gene
		if ( geneId == geneunit.getId(gd) )
		{
			//System.out.println("New haplotype with marker" + newIndex);
			if ( params.onlyHaplotypeModels() ) addresses.add(new ComboAddress(new int[]{newIndex,0,0},new boolean[]{false,false},"haplotype","extendhaplotype"));
			if ( params.anyCompositeModels() )
			{
				//System.out.println("New composite with marker" + newIndex);
				addresses.add(new ComboAddress(new int[]{newIndex,0,1},new boolean[]{false,true},"compositewithin","addmarker"));
			}
		}
		else 
		{
			if ( params.anyInteractionModels() || params.anyCompositeModels() )
			{
				//System.out.println("New composite with marker" + newIndex);
				addresses.add(new ComboAddress(new int[]{newIndex,1,0},new boolean[]{true,true},"compositebetween","addmarker"));
			}
		}
		
		long alleleValues = comboset.getPositionBasedHaplotype();
		
		for ( int i=0; i < addresses.size(); i++ )
		{
			ComboAddress ca = addresses.get(i);
			
			if ( ca.getType().contains("composite") )
			{
				// Composite model
				MarkerCombo mcCopy = comboset.copyMarkerCombo();
				ModelCombo modelComboCopy = comboset.copyModelCombo();
				mcCopy.add(ca);
				modelComboCopy.add(ca);
				// change to long to allow more than 31 markers
				//long newAlleleValues = alleleValues | (1 << ca.getIndex());
				long one = 1;
				long newAlleleValues = alleleValues | ( one << ca.getIndex());
				ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelComboCopy,ca.getType(),ca.getAction());
				Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
				for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
			}
			else
			{
				// Expand all two locus haplotypes
				for ( int j=0; j < 4; j++ )
				{
					// changed to long to allow > 31 markers
					//int allele1 = (j & 1);
					//int allele2 = (j >> 1) & 1;
					long allele1 = (j & 1);
					long allele2 = (j >> 1) & 1;
					MarkerCombo mcCopy = comboset.copyMarkerCombo();
					int locusIndex1 = Math.min(ca.getIndex(),comboset.getMarkerUnits()[0].getLoci()[0]);
					int locusIndex2 = Math.max(ca.getIndex(),comboset.getMarkerUnits()[0].getLoci()[0]);
					
					mcCopy.add(ca);
					long newAlleleValues = 0 | (allele1 << locusIndex1);
					newAlleleValues |= (allele2 << locusIndex2);
					ModelCombo modelcombo = new ModelCombo(new String[]{null});
					ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelcombo,ca.getType(),ca.getAction());
					analyses[0].bufferAdd(cset);
				}
			}
		}
	}

	//---------------------------------------------------------------------------
	public long getHaplotype(boolean left2right) { return comboset.getHaplotype(left2right); }

	//---------------------------------------------------------------------------
	public String getObservedReport(){ return result[0].toString(); }
	
	//---------------------------------------------------------------------------
	public String getInferentialReport(){ return result[1].toString(); }

	public long getLocusAddress() { return locusAddress; }

	//---------------------------------------------------------------------------
	public boolean metSignificance( double sigThreshold )
	{
		double[] pvals = result[1].doubleValues();
		boolean metThreshold = false;
		for ( int i=0; i < pvals.length; i++ )
		{
			if ( (pvals[i] <= sigThreshold ) ) metThreshold = true;
		}
		return metThreshold;
	}
}
