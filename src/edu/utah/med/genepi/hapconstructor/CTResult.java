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
import edu.utah.med.genepi.hapconstructor.analysis.MarkerUnit;

public class CTResult extends hapCResultImp {
	
	public CTResult( Analysis analysis, boolean logTableCounts )
	{
		super(analysis,logTableCounts);
	}
	
	//---------------------------------------------------------------------------	
	public void newMarkerSet( int newIndex, GenieDataSet gd, hapCParamManager params )
	{
 		//System.out.println("CTResult - newMarkerSet");
		DynamicAnalysis[] analyses = params.getAnalyses();
		Locus l = gd.getLocus(-1,newIndex);
		int geneId = l.getGeneId();
		
		boolean foundGene = false;
		List<ComboAddress> addresses = new ArrayList<ComboAddress>();
		GeneUnit[] geneunits = comboset.getGeneUnits();
		String csetType = "compositewithin";
		if ( geneunits.length > 1 ) csetType = "compositebetween";
		for ( int i=0; i < geneunits.length; i++ )
		{
			// Extend haplotype within a gene
			if ( geneunits[i].getId(gd) == geneId )
			{
				foundGene = true;
				MarkerUnit[] markerunits = geneunits[i].getMarkerUnits();
				for ( int j=0; j < markerunits.length; j++ )
				{
					// Check if composite models should be extended to haplotypes.
					if ( params.useCompositeHaplotypes() ) 
					{						
						addresses.add(new ComboAddress(new int[]{newIndex,i,j},new boolean[]{false,false},csetType,"extendhaplotype"));
					}
				}
				addresses.add(new ComboAddress(new int[]{newIndex,i,1},new boolean[]{false,true},csetType,"addmarker"));
				break;
			}
		}
		
		if ( !foundGene )
		{
			addresses.add(new ComboAddress(new int[]{newIndex,1,0},new boolean[]{true,true},"compositebetween","addmarker"));
		}
		
		long alleleValues = comboset.getPositionBasedHaplotype();
		
		for ( int i=0; i < addresses.size(); i++ )
		{
			ComboAddress ca = addresses.get(i);
			
			int[] alleles = new int[]{1,0};
			if ( ca.getAction().equals("addmarker") ) alleles = new int[]{1};
			
			for ( int j=0; j < alleles.length; j++ )
			{
				MarkerCombo mcCopy = comboset.copyMarkerCombo();
				ModelCombo modelComboCopy = comboset.copyModelCombo();
				mcCopy.add(ca);
				modelComboCopy.add(ca);
				long newAlleleValues = alleleValues | (alleles[i] << ca.getIndex());
				ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelComboCopy,ca.getType(),ca.getAction());
				Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
				for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
			}
		}
 		//System.out.println("CTResult - newMarkerSet END");
	}
	
	//---------------------------------------------------------------------------	
	public void reducedMarkerSet( int removalIndex, GenieDataSet gd, hapCParamManager params )
	{
		DynamicAnalysis[] analyses = params.getAnalyses();
		ComboAddress ca  = null;
		// Need to check the location of the removalIndex
		Locus l = gd.getLocus(-1, removalIndex);
		int geneId = l.getGeneId();
		
		GeneUnit[] geneunits = comboset.getGeneUnits();
		int nMarkerUnits = comboset.getMarkerUnits().length;
		int markerUnitIter = 0;
		int removeUnitIndex = -1;
		String csetType = "compositewithin";
		if ( geneunits.length > 1 ) csetType = "compositebetween";
		for ( int i=0; i < geneunits.length; i++ )
		{
			if ( geneunits[i].getId(gd) == geneId )
			{
				MarkerUnit[] markerunits = geneunits[i].getMarkerUnits();
				for ( int j=0; j < markerunits.length; j++ )
				{
					markerUnitIter++;
					int[] loci = markerunits[j].getLoci();
					for ( int k=0; k < loci.length; k++ )
					{
						if ( loci[k] == removalIndex )
						{
							if ( loci.length == 1 )
							{
								if ( nMarkerUnits == 2 )
								{
									// Transforms to a single marker unit within one gene
									ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"haplotype","removecompositeunit");
									removeUnitIndex = markerUnitIter;
								}
								else if ( markerunits.length == 1 )
								{
									// Remove the only marker unit in a gene, reduces to one geneunit
									ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"compositewithin","removecompositeunit");
									removeUnitIndex = markerUnitIter;
								}
								else if ( markerunits.length > 1 )
								{
									// Remove only marker unit in a gene
									ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"compositebetween","removecompositeunit");
									removeUnitIndex = markerUnitIter;
								}
							}
							else
							{
								ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},csetType,"reducehaplotype");
							}
							break;
						}
					}
				}
			}
		}
		long alleleValues = comboset.getPositionBasedHaplotype();
		MarkerCombo mcCopy = comboset.copyMarkerCombo();
		ModelCombo modelCopy = comboset.copyModelCombo();
		mcCopy.remove(ca);
		modelCopy.remove(removeUnitIndex);
		long newAlleleValues = alleleValues ^ (1 << ca.getIndex());
		ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelCopy,ca.getType(),ca.getAction());
		Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
		for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
	}
}
