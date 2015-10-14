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
import edu.utah.med.genepi.hapconstructor.analysis.ValueCombo;
import edu.utah.med.genepi.hapconstructor.analysis.ValueUnit;
import edu.utah.med.genepi.util.Ut;

public class IntxResult extends hapCResultImp {
	
	public IntxResult( Analysis analysis, boolean logTableCounts )
	{
		super(analysis,logTableCounts);
	}
	
	//---------------------------------------------------------------------------
	public void newMarkerSet( int newIndex, GenieDataSet gd, hapCParamManager params, HaplotypeResultStorage sighaps )
	{
		//System.out.println("IntxResult - newMarkerSet " );
		DynamicAnalysis[] analyses = params.getAnalyses();
		Locus l = gd.getLocus(-1,newIndex);
		int geneId = l.getGeneId();
		
		boolean foundGene = false;
		List<ComboAddress> addresses = new ArrayList<ComboAddress>();
		GeneUnit[] geneunits = comboset.getGeneUnits();
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
						addresses.add(new ComboAddress(new int[]{newIndex,i,j},new boolean[]{false,false},"compositebetween","extendhaplotype"));
					}
				}
				//addresses.add(new ComboAddress(new int[]{newIndex,i,1},new boolean[]{false,true},"composite","compositewithin"));
				break;
			}
		}
		
//		if ( !foundGene )
//		{
//			addresses.add(new ComboAddress(new int[]{newIndex,1,0},new boolean[]{true,true},"composite","compositebetween"));
//		}
		
		long alleleValues = comboset.getPositionBasedHaplotype();
		for ( int i=0; i < addresses.size(); i++ )
		{
			ComboAddress ca = addresses.get(i);
			int nloci = comboset.getLoci().length;
			if ( nloci == 2 )
			{
					//System.out.println(" TWO locus ");
				// Expand all two locus haplotypes
				for ( int j=0; j < 4; j++ )
				{
					// change to long, to allow more than 31 markers
					//int allele1 = (j & 1);
					//int allele2 = (j >> 1) & 1;
					long allele1 = (j & 1);
					long allele2 = (j >> 1) & 1;
					MarkerCombo mcCopy = comboset.copyMarkerCombo();
					GeneUnit gu = comboset.getGeneUnits()[ca.getGeneUnitIndex()];
					MarkerUnit mu = gu.getMarkerUnit(0);
					int locusIndex1 = Math.min(ca.getIndex(),mu.getLoci()[0]);
					int locusIndex2 = Math.max(ca.getIndex(),mu.getLoci()[0]);
					
					mcCopy.add(ca);
					long newAlleleValues = 0 | (allele1 << locusIndex1);
                                        //System.out.println(" allele1 = " + allele1 + " locusIndex1 " + locusIndex1 + ",  newAlleleValues " + newAlleleValues );
					newAlleleValues |= (allele2 << locusIndex2);
                                        //System.out.println(" allele2 = " + allele2 + " locusIndex2 " + locusIndex2 + ",  newAlleleValues " + newAlleleValues );
					newAlleleValues = ((((1 << locusIndex1 ) | (1 << locusIndex2 )) & alleleValues) ^ (alleleValues)) | newAlleleValues;
					//System.out.println( "  Intx line 85 - alleleValues " +alleleValues + " locusIndex 1 " + locusIndex1 + " locusIndex2 " + locusIndex2 + " newAllele " + newAlleleValues);
					ModelCombo modelComboCopy = comboset.copyModelCombo();
					modelComboCopy.add(ca);
					ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelComboCopy,ca.getType(),ca.getAction());
					Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
					for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
				}
					//System.out.println(" TWO locus END");
			}
			else
			{
				//change to long
				//int[] alleles = new int[]{1,0};
				long[] alleles = new long[]{1,0};
				//if ( ca.getAction().contains("composite") ) alleles = new int[]{1};
				if ( ca.getAction().contains("composite") ) alleles = new long[]{1};

				for ( int j=0; j < alleles.length; j++ )
				{
					MarkerCombo mcCopy = comboset.copyMarkerCombo();
					ModelCombo modelComboCopy = comboset.copyModelCombo();
					mcCopy.add(ca);
					modelComboCopy.add(ca);
					long newAlleleValues = alleleValues | (alleles[i] << ca.getIndex());
					//System.out.println( "  Intx line 104 - alleleValues " +alleleValues + " allele["+i+"]" + alleles[i] + " << " + ca.getIndex() + "newAllele" + newAlleleValues);
					ComboSet cset = new ComboSet(mcCopy,newAlleleValues,modelComboCopy,ca.getType(),ca.getAction());
					Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
					for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
				}
			}
		}
		//System.out.println("IntxResult - newMarkerSet END" );
	}

	//---------------------------------------------------------------------------	
	public void removeMarkers(GenieDataSet gd, hapCParamManager params,
			LocusResultStorage testedLoci, int nLoci) 
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
	public String getOutput()
	{
		StringBuffer markerstr = new StringBuffer();
		MarkerCombo mc = comboset.getMarkerCombo();
		ModelCombo modelcombo = comboset.getModelCombo();
		GeneUnit[] gu = mc.getGeneUnits();
		for ( int i=0; i < gu.length; i++ )
		{
			MarkerUnit[] mu = gu[i].getMarkerUnits();
			StringBuffer gustr = new StringBuffer();
			for ( int j=0; j < mu.length; j++ )
			{
				int[] indices = mu[j].getLoci();
				StringBuffer markerunitstr = new StringBuffer();
				for ( int k=0; k < indices.length; k++ )
				{
					markerunitstr.append(indices[k]+1);
					if ( k != (indices.length-1) ) markerunitstr.append("-");
				}
				gustr.append(markerunitstr);
			}
			markerstr.append("[");
			markerstr.append(gustr);
			markerstr.append("]");
		}

		ValueCombo vc = comboset.getValueCombo();
		ValueUnit[] vu = vc.getValueUnits();
		StringBuffer valuestr = new StringBuffer();
		for ( int i=0; i < vu.length; i++ )
		{
			byte[][] values = vu[i].getValues();
			StringBuffer unitstr = new StringBuffer();
			for ( int j=0; j < values.length; j++ )
			{
				unitstr.append(values[j][0]);
				if ( j != (values.length-1) ) unitstr.append("-");
			}
			valuestr.append("[");
			valuestr.append(unitstr);
			valuestr.append("]");
		}		

		StringBuffer modelstr = new StringBuffer();
		String[] models = modelcombo.getModels();
		for ( int i=0; i < models.length; i++ )
		{
			if ( models[i] != null )
			{
				modelstr.append(models[i]);
				modelstr.append(" ");
			}
		}

		String[] resultstr = new String[]{"-","-"};
		if ( !statFailed ) resultstr = new String[]{result[0].toString(),result[1].toString()}; 

		String[] resultValues = new String[]{markerstr.toString(),valuestr.toString(),statName,resultstr[0],resultstr[1]};
		String rValues = Ut.array2str(resultValues,":");
		if ( statName.contains("Odds") )
		{
			resultValues = new String[]{markerstr.toString(),valuestr.toString(),modelstr.toString(),statName,resultstr[0],resultstr[1]};
			rValues = Ut.array2str(resultValues,":");
			if ( this.at != null ) rValues += "\t" + addTableCounts(at);
		}
		
		return rValues;
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
								// Remove markerunit and 
								if ( nMarkerUnits == 2 )
								{
									ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"haplotype","removecompositeunit");
									removeUnitIndex = markerUnitIter;
								}
								else if ( markerunits.length == 1 )
								{
									ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"compositewithin","removecompositeunit");
									removeUnitIndex = markerUnitIter;
								}
								else if ( markerunits.length > 1 )
								{
									ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"compositebetween","removecompositeunit");
									removeUnitIndex = markerUnitIter;								
								}
							}
							else ca = new ComboAddress(new int[]{removalIndex,i,j},new boolean[]{false,false},"compositebetween","reducehaplotype");
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
		//System.out.println("New intx markerset " + cset.getBufferKey());
		Integer[] analysisIndices = params.getAnalysisIndex(cset.getType(),cset.getAction());
		for ( int k=0; k < analysisIndices.length; k++) analyses[analysisIndices[k]].bufferAdd(cset);
	}

}
