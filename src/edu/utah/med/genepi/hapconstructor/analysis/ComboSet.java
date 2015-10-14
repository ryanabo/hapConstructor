package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ModelCombo;

public class ComboSet {

	private MarkerCombo markercombo = null;
	private ValueCombo valuecombo = null;
	private ModelCombo modelcombo = null;
	private String type = null;
	private String action = null;
        public String alleleV = null;
	
	public ComboSet( MarkerCombo mc, ValueCombo vc, ModelCombo mdlc )
	{
		markercombo = mc;
		valuecombo = vc;
		modelcombo = mdlc;
	}
	
	//---------------------------------------------------------------------------
	public int[] getLoci(){ return markercombo.getLoci(); }

	//---------------------------------------------------------------------------	
	public ComboSet( MarkerCombo mc, long alleleValues, ModelCombo modelcmbo, String comboType, String actionType ) 
	{
		alleleV = new String(" " + alleleValues);
		markercombo = mc;
                //System.out.println("Constructor - alleleVAlues : " + alleleValues + " allelev " + alleleV);
		valuecombo = buildValueCombo(alleleValues);
                //System.out.println("after buildVAlueCombo : " + valuecombo.getHaplotype(true));
		modelcombo = modelcmbo;
		type = comboType;
		action = actionType;
	}
	
	//---------------------------------------------------------------------------	
	public String getBufferKey(){ return markercombo.getBufferKey(); }
	
	//---------------------------------------------------------------------------	
	public MarkerCombo getMarkerCombo(){ return markercombo; }
	
	//---------------------------------------------------------------------------	
	public ValueCombo getValueCombo(){ return valuecombo; }
	
	//---------------------------------------------------------------------------	
	public ModelCombo getModelCombo(){ return modelcombo; }
	
	//---------------------------------------------------------------------------	
	public MarkerUnit[] getMarkerUnits(){ return markercombo.getMarkerUnits(); }
	
	//---------------------------------------------------------------------------	
	public ValueUnit[] getValueUnits(){ return valuecombo.getValueUnits(); }
	
	/** Process positionbased haplotype to pull out alleles at each locus in the 
	 * markercombo and store in a new valuecombo.
	 * 
	 * @param alleleValues long value with positional based alleles
	 * @return ValueCombo object with new allelic values (non-zero based). The 0th 
	 * allele goes with the 0th locus.
	 */
	//---------------------------------------------------------------------------	
	public ValueCombo buildValueCombo( long alleleValues )
	{
                //System.out.println("buildVAlueCombo - " + alleleValues);
		MarkerUnit[] mu = markercombo.getMarkerUnits();
		
		ValueUnit[] vu = new ValueUnit[mu.length];
		for ( int i = 0; i < mu.length; i++ )
		{
			int[] loci = mu[i].getLoci();
			byte[][] value = new byte[loci.length][1];
                        long one = 1;
			for ( int j =0; j < loci.length; j++) {
				//value[j][0] = (byte) (((alleleValues & (1 << loci[j])) >> loci[j]) + 1); 
				value[j][0] = (byte) (((alleleValues & (one << loci[j])) >> loci[j]) + one); 
                            //if ( loci[j] > 30)
				//System.out.println("locus " + loci[j] + " value " + value[j][0] + "  bit " + ( alleleValues & ( 1<< loci[j])) + " allv " + alleleValues);
			}
			vu[i] = new ValueUnit(value,null);
		}
		ValueCombo vc = new ValueCombo(vu);	
		return vc;
	}

	//---------------------------------------------------------------------------	
	public long getLocusAddress(){ return markercombo.getLocusAddress(); }

	/** Get the allelevalues in the comboset in compact form.
	 * 
	 * @return long value of the allelevalues that correspond to the markers in the comboset.
	 * The ith allelevalue corresponds to the ith marker index.
	 */
	//---------------------------------------------------------------------------	
	public long getHaplotype(boolean left2right){ return valuecombo.getHaplotype(left2right); }
	
	/** Return haplotype with alleles in their relative positions.
	 *  
	 * @return long value with allele values in correct locus positions along haplotype
	 */
	//---------------------------------------------------------------------------	
	public long getPositionBasedHaplotype()
	{ 
		MarkerUnit[] mu = markercombo.getMarkerUnits();
		ValueUnit[] vu = valuecombo.getValueUnits();
		long haplotype = 0;
		for ( int i = 0; i < mu.length; i++ )
		{
			int[] loci = mu[i].getLoci();
			byte[][] values = vu[i].getValues();
 			//System.out.println("--- comboset getpositionBaseHap: ");
			for ( int j =0; j < loci.length; j++) {
			  haplotype |= ((values[j][0]-1) << loci[j]); 
                          //if ( loci[j] >= 30 ) 
			  //System.out.println(" ---value["+j+"] " + (values[j][0]-1)+ " << POST " + loci[j] + " = " + ((values[j][0] -1) << loci[j]) + " haplotype " + haplotype );
			}
			//System.out.println(" ---comboset end");
		}
		return haplotype;
	}

	//---------------------------------------------------------------------------	
	public GeneUnit[] getGeneUnits(){ return markercombo.getGeneUnits(); }

	//---------------------------------------------------------------------------	
	public MarkerCombo copyMarkerCombo(){ return markercombo.copy(); }	
	
	//---------------------------------------------------------------------------	
	public ModelCombo copyModelCombo(){ return modelcombo.copy(); }
	
	//---------------------------------------------------------------------------	
	public String getType(){ return type; }
	
	//---------------------------------------------------------------------------	
	public String getAction(){ return action; }
	
	//---------------------------------------------------------------------------	
        public String getAlleleV() { return alleleV; }
}
