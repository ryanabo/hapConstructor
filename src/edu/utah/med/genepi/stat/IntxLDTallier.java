package edu.utah.med.genepi.stat;

import java.util.List;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.genie.Phenotype;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

public class IntxLDTallier extends TallierImp{

	protected ColumnManager columnManager = null;
	protected int[] individualIndices = null;
	protected List<Integer> permutedIndices = null;
	protected int simIndex = 0;
	protected boolean caseOnly = false;
	protected Phenotype[] phenotypes;
	
	public IntxLDTallier( ColumnManager cm, int[] indIndices, boolean casesOnly, Phenotype[] phens, int[] simIndices )
	{
		super(indIndices.length,2);
		theCounters = createCounters(0);
		columnManager = cm;
		individualIndices = indIndices;
		simIndex = simIndices[0];
		caseOnly = casesOnly;
		phenotypes = phens;
		
//		if ( caseOnly && simIndex > 0 )
//		{
//			for ( int i=0; i < individualIndices.size(); i++ ) permutedIndices.add(individualIndices.get(i));
//			Collections.shuffle(permutedIndices);
//		}
	}
	
	//----------------------------------------------------------------------------
	public boolean screenMissing( int indIndex )
	{
		Phenotype p = phenotypes[indIndex];
		int phen = p.getPhenotype();
		boolean missing = phen == 0 ? true : false;
		
		if ( caseOnly ) missing = phen == 1 ? true : missing;
		return missing;
	}
	
	//----------------------------------------------------------------------------
	public boolean processQueryResult( int[] queryResult, int indIndex, int queryIndex )
	{
		boolean foundColumn = false;
		if ( queryResult != null )
		{
			int storeIndex = individualIndices[indIndex];
			//if ( simIndex > 0 && caseOnly && queryIndex == 1 ) storeIndex = permutedIndices.get(indIndex);
			//theCounters[storeIndex][queryIndex].add(queryResult[0]-1);
			theCounters[indIndex][queryIndex].add(queryResult[0]-1);
		}
		return foundColumn;
	}
	
	//----------------------------------------------------------------------------
	public GenotypeDataQuery[] getDataQuery(){ return columnManager.getDataQuery(); }

	//----------------------------------------------------------------------------
	public AnalysisTable extractTable()
	{
		return new IntxLDTable(phenotypes,theCounters,caseOnly,individualIndices);
	}
}
