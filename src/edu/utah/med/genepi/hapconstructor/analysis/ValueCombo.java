package edu.utah.med.genepi.hapconstructor.analysis;

public class ValueCombo {

	private ValueUnit[] vu = null;
	
	public ValueCombo( ValueUnit[] valueUnits )
	{
		vu = valueUnits;
	}
	
	//---------------------------------------------------------------------------
	public ValueUnit[] getValueUnits(){ return vu; }
	
	/** Return the haplotype value reading from right to left, meaning the first locus
	 * allele value is the right most bit of the value. 
	 * 
	 * @return long value of haplotype alleles
	 */
	//---------------------------------------------------------------------------
	public long getHaplotype(){ return getHaplotype(false); }
	
	/** Return the haplotype value reading either left to right or right to left.
	 * In processing and storing haplotypes for hapConstructor the haplotypes will
	 * usually read from right to left to correspond with the loci order, while the output
	 * will need to be left to right ordered.
	 * 
	 * @param left2right boolean value designating whether the hapltoype is return in right to left
	 * or left to right order.
	 * @return long value of haplotype alleles
	 */
	//---------------------------------------------------------------------------
	public long getHaplotype( boolean left2right )
	{
		
		ValueUnit v = vu[0];
		byte[][] alleles = v.getValues();
		long haplotype = 0;
		for ( int i=0; i < alleles.length; i++ )
		{
			int locusindex = i;
			if ( left2right ) locusindex = alleles.length-1-i;
			//haplotype |= (((int)alleles[locusindex][0]-1) << i);
			haplotype |= (((long)alleles[locusindex][0]-1) << i);
		}
		return haplotype;
	}
	
	/** Returns the alleles in the position corresponding to the locus position. The 
	 * allele for locus j will be in the jth position along the haplotype.
	 * 
	 * @return long value with positional based alleles for the haplotype in this
	 * value combo
	 */
	//---------------------------------------------------------------------------
	public long getPositionBasedHaplotype()
	{
		ValueUnit v = vu[0];
		byte[][] alleles = v.getValues();
		long haplotype = 0;
		for ( int i=0; i < alleles.length; i++ )
		{
			int locusindex = i;
			//haplotype |= (((int)alleles[i][0]-1) << i);
			haplotype |= (((long)alleles[i][0]-1) << i);
		}
		return haplotype;
	}
	
	
	//---------------------------------------------------------------------------
	public ValueCombo copy()
	{
		ValueUnit[] vuCopy = new ValueUnit[vu.length];
		for ( int i=0; i < vu.length; i++ )
		{
			byte[][] values = vu[i].getValues();
			String type = vu[i].getType();
			vuCopy[i] = new ValueUnit(values,type);
		}
		return new ValueCombo(vuCopy);
	}
}
