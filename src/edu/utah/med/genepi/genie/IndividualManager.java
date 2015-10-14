package edu.utah.med.genepi.genie;

import edu.utah.med.genepi.ped.Individual;

public class IndividualManager {

	// List of all individuals in order
	private Individual[] allInds = null;
	// Ordered indices of individuals that match
	// the phen, quant, and gt data indices.
	// So indice number 1 in the list indicates the individual
	// in allInds with phen, quant, gt information in the 
	// first position in all those structures.
	private int[] dataInds = null;
	private int[] analysisIndices = null;
	private int nGtCases = 0;
	private int nGtControls = 0;
	private int nAnalysisInds = 0;
	
	public IndividualManager( int totalInds, int nPhenoGenoInds, int nDataInds, int nTypedCases, int nTypedControls )
	{
		allInds = new Individual[totalInds];
		dataInds = new int[nDataInds];
		analysisIndices = new int[nPhenoGenoInds];
		nAnalysisInds = nPhenoGenoInds;
		nGtCases = nTypedCases;
		nGtControls = nTypedControls;
	}
	
	//----------------------------------------------------------------------------
	public int[] getCaseIndices( Phenotype[] phens )
	{		
		int[] caseIndices = new int[nGtCases];
		int caseIter = 0;
		for ( int i =0; i < dataInds.length; i++ )
		{
			if ( phens[i].getPhenotype() == 2 )
			{
				caseIndices[caseIter] = i;
				caseIter++;
			}
		}
		return caseIndices;
	}
	
	//----------------------------------------------------------------------------
	public void setDataIndex( int dataIndex, int allIndex )
	{ 
		dataInds[dataIndex] = allIndex;
		allInds[allIndex].setDataIndex(dataIndex); 
	}
	
	//----------------------------------------------------------------------------
	public void setAnalysisIndex( int analysisIndex, int dataIndex )
	{ 
		analysisIndices[analysisIndex] = dataIndex;
	}
	
	//----------------------------------------------------------------------------
	public void setInd( int index, Individual ind ){ allInds[index] = ind; }
	
	//----------------------------------------------------------------------------
	public Individual[] getAllInds(){ return allInds; }
	
	//----------------------------------------------------------------------------
	public int[] getDataInds(){ return dataInds; }
	
	//----------------------------------------------------------------------------
	public int[] getAnalysisIndices( )
	{
		return analysisIndices;
	}
}
