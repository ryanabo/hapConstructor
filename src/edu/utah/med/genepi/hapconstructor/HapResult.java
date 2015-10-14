package edu.utah.med.genepi.hapconstructor;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.analysis.ModelCombo;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.hapconstructor.analysis.ComboAddress;
import edu.utah.med.genepi.hapconstructor.analysis.ComboSet;
import edu.utah.med.genepi.hapconstructor.analysis.DynamicAnalysis;
import edu.utah.med.genepi.hapconstructor.analysis.GeneUnit;
import edu.utah.med.genepi.hapconstructor.analysis.MarkerCombo;
import edu.utah.med.genepi.util.Ut;

public class HapResult extends hapCResultImp {
	
	//---------------------------------------------------------------------------	
	public HapResult( Analysis analysis, boolean logTableCounts )
	{
		super(analysis,logTableCounts);
	}
	
	//---------------------------------------------------------------------------
	public String getOutput()
	{
		int[] loci = comboset.getLoci();
		Long left2rightHap = comboset.getHaplotype(true); 
		int[] nonzeroIndex = new int[loci.length];
		for ( int i=0; i < loci.length; i++ ) nonzeroIndex[i] = loci[i]+1;
		String markerStr = Ut.array2str(nonzeroIndex,"-");
		
		String[] resultstr = new String[]{"-","-"};
		if ( !statFailed ) resultstr = new String[]{result[0].toString(),result[1].toString()}; 
		
//		if ( model == null || result[0] == null || result[1] == null )
//		{
//			//System.out.println("Error");
//		}
		String[] resultValues = new String[]{markerStr,model+""+(left2rightHap+1),statName,resultstr[0].toString(),resultstr[1].toString()};
		String rValues = Ut.array2str(resultValues,":");
		if ( at != null ) rValues += "\t" + addTableCounts(at);
		return rValues;
	}
	
	//---------------------------------------------------------------------------	
	public void addMarkers( GenieDataSet gd, hapCParamManager params, HaplotypeResultStorage sigHaplotypes, int nLoci )
	{
		long allLociMask = (long)(Math.pow(2,nLoci)-1);
		long bits2add = allLociMask ^ locusAddress;
		for ( int i = 0; i < nLoci; i++ )
		{
			if ( ((bits2add >> i) & 1) == 1 )
			{

 				//System.out.println("hapResults - addMarkers");
				newMarkerSet(i,gd,params,sigHaplotypes);
 				//System.out.println("hapResults - addMarkers END");
			}
		}
	}
	
	//---------------------------------------------------------------------------	
	public void newMarkerSet( int newIndex, GenieDataSet gd, hapCParamManager params, HaplotypeResultStorage sigHaplotypes )
	{
		DynamicAnalysis[] analyses = params.getAnalyses();
		Locus l = gd.getLocus(-1,newIndex);
		int geneId = l.getGeneId();
		
		List<ComboAddress> addresses = new ArrayList<ComboAddress>();
		
		GeneUnit geneunit = comboset.getGeneUnits()[0];
		// Extend haplotype within a gene
		if ( geneId == geneunit.getId(gd) )
		{
			addresses.add(new ComboAddress(new int[]{newIndex,0,0},new boolean[]{false,false},"haplotype","extend"));
			if ( params.anyCompositeModels() )
			{
				addresses.add(new ComboAddress(new int[]{newIndex,0,1},new boolean[]{false,true},"compositewithin", "addmarker"));
			}
		}
		else 
		{
			if ( params.anyInteractionModels() || params.anyCompositeModels() )
			{
				addresses.add(new ComboAddress(new int[]{newIndex,1,0},new boolean[]{true,true},"compositebetween","addmarker"));
			}
		}
		
		long alleleValues = comboset.getPositionBasedHaplotype();
		
		for ( int i=0; i < addresses.size(); i++ )
		{
			ComboAddress ca = addresses.get(i);
			
			int[] alleles = new int[]{1,0};
			if ( ca.getType().contains("composite") ) alleles = new int[]{2};
			
			for ( int j=0; j < alleles.length; j++ )
			{
				ModelCombo modelComboCopy = comboset.copyModelCombo();
				modelComboCopy.add(ca);
				MarkerCombo mcCopy = comboset.copyMarkerCombo();
				mcCopy.add(ca);
				long newAlleleValues = alleleValues | (alleles[j] << ca.getIndex());
				ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelComboCopy,ca.getType(),ca.getAction());
				Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
				for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
			}
		}
	}
	
	//---------------------------------------------------------------------------	
	public void removeMarkers( GenieDataSet gd, hapCParamManager params, LocusResultStorage testedLoci, int nLoci )
	{
		int step = params.getStep();
		Long[] previousStepLoci = testedLoci.getLociAddresses();
		long[] tempStorage = new long[step];
		int matchIter = 0;
		for ( int i=0; i < previousStepLoci.length; i++ )
		{
			long lA = previousStepLoci[i];
			if ( (locusAddress & lA) == lA ) 
			{
				tempStorage[matchIter] = ~lA;
				matchIter++;
				if ( matchIter == step ) break;
			}
		}
		
		long orBits = 0;
		for ( int i=0; i < step; i++ ) orBits |= tempStorage[i];
		if ( orBits != locusAddress )
		{
			for ( int i=0; i < step; i++ )
			{
				if ( ((locusAddress & (1 << i)) == 1) && ((orBits & (1 << i)) == 0) )
				{
					reducedMarkerSet(i,gd,params);
				}
			}
		}
	}
	
	//---------------------------------------------------------------------------	
	public void reducedMarkerSet( int removalIndex, GenieDataSet gd, hapCParamManager params )
	{
		DynamicAnalysis[] analyses = params.getAnalyses();			
		ComboAddress ca = new ComboAddress(new int[]{removalIndex,0,0},new boolean[]{false,false},"haplotype","reducehaplotype");

		long alleleValues = comboset.getPositionBasedHaplotype();
		ModelCombo modelComboCopy = comboset.copyModelCombo();
		modelComboCopy.remove(0);
		MarkerCombo mcCopy = comboset.copyMarkerCombo();
		mcCopy.remove(ca);
		long newAlleleValues = alleleValues ^ (1 << ca.getIndex());
		ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelComboCopy,ca.getType(),ca.getAction());
		analyses[0].bufferAdd(cset);
	}

}
