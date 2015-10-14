package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.genie.Phenotype;
import edu.utah.med.genepi.util.Counter;

public class IntxLDTable extends TableImp {

	private final Counter[][] myCounters;
	private final Phenotype[] phenotypes;
	private final boolean casesOnly;
	private final int[] individuals;
	
	public IntxLDTable( Phenotype[] phens, Counter[][] theCounters, boolean caseOnly, int[] indLst )
	{
		myCounters = theCounters;
		phenotypes = phens;
		casesOnly = caseOnly;
		individuals = indLst;
	}
	
	public Counter[][] getCounters(){ return myCounters; }
	
	public Phenotype[] getPhenotypes(){ return phenotypes; }
	
	public int[] getIndividuals(){ return individuals; }
	
	public boolean getCaseOnly(){ return casesOnly; }
	
}
