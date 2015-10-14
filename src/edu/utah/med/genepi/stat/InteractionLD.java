package edu.utah.med.genepi.stat;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.genie.Phenotype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.util.Counter;

public class InteractionLD extends CCStatImp {

	public InteractionLD()
	{
		title = "IntxLD";
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker(){ return new ScalarCompTracker(); }
	
	//---------------------------------------------------------------------------
	public Result computeStat ( Analysis a, AnalysisTable[] tables, int[] simIndices )
	{
		IntxLDTable t = (IntxLDTable) tables[0];
		Phenotype[] phens = t.getPhenotypes();
		Counter[][] c = t.getCounters();
		int[] indLst = t.getIndividuals();
		
		if ( t.getCaseOnly() )
		{
			int[][] caseCounts = new int[c.length][2];
			int nInds = 0;
			for ( int i=0; i < c.length; i++ )
			{
				if ( c[i] != null )
				{
					caseCounts[i] = new int[]{(Integer) c[i][0].current(),(Integer) c[i][1].current()};
					nInds++;
				}
				else caseCounts[i] = new int[]{-2,-2};
			}
			double caseLD = ldCorrelation(caseCounts,nInds);
			resultAt0 = new ResultImp.Real(caseLD);
		}
		else
		{
			int iter = 0;
			List<int[]> lstcaseCounts = new ArrayList<int[]>();
			List<int[]> lstcontrolCounts = new ArrayList<int[]>();
			int[] nInds = new int[]{0,0};
			for ( int i=0; i < indLst.length; i++ )
			{
				int phen = phens[i].getPhenotype();
				int[] count = new int[]{-2,-2};
				
				if ( (phen > 0) && (c[iter] != null) )
				{
					count = new int[]{(Integer) c[iter][0].current(),(Integer) c[iter][1].current()};
					nInds[phen-1]++;
				}
				if ( phen == 1 ) lstcontrolCounts.add(count);
				else if ( phen == 2 ) lstcaseCounts.add(count);
				iter++;
			}
			int[][] caseCounts = lstcaseCounts.toArray(new int[0][0]);
			int[][] controlCounts = lstcontrolCounts.toArray(new int[0][0]);
			double caseLD = ldCorrelation(caseCounts,nInds[1]);
			double controlLD = ldCorrelation(controlCounts,nInds[0]);
			double ldDifference = Math.abs(caseLD - controlLD);
			resultAt0 = new ResultImp.Real(ldDifference);
		}
		
//		AnalysisTable.Totals totals = t.getTotals();

//		// Check Row and Column Totals before calculation.
//		if ( StatUt.checkRowColSum (totals.forRows()[0],totals.forColumns(),totals.forTable()))
//		{
//			resultAt0 = new ResultImp.Real(StatUt.chiSquared(t.getRowFor(Ptype.CASE).getCells(),
//					totals.forRows()[0],
//					totals.forColumns(),
//					totals.forTable()));
//		}
//		else 
//		{
//			resultAt0 = new ResultImp.StringResult
//			("WARNING: row or column sum equals zero, **test** has not been performed.");
//		}
		return resultAt0; 
	}
	
	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getIntxLDTable(studyIndex,simIndices); 
	}
	
	//---------------------------------------------------------------------------
	private double ldCorrelation( int[][] counts, int nInds )
	{
		//double r2 = 0.0, dpr = 0.0;
		double tr = Cor(counts);
		//r2 += tr*tr;
		//double tdpr = DprIJ(counts,nInds);
		//dpr += Math.abs(tdpr);
		//int n1 = 2;
		//int n2 = 2;
		//double df = (n1-1.0)*(n2-1.0);
		//double ncells = n1*n2;
		//double x2 = (nInds*df/ncells)*r2;
		//dpr /= ncells;
		//return new double[]{x2,dpr,r2};
		//return x2;
		return tr;
	}
	
	//---------------------------------------------------------------------------
	private double Cor( int[][] x )
	{
		double ssqx = 0, ssqy = 0, ssq_xy = 0;
		double xbar = x[0][0];
		double ybar = x[0][1];
		// x[0][0] contains the number of inds genotyped at both markers
		for(int i=1; i < x.length; i++) 
		{
			if ( x[i][0] > -2 )
			{
				double dx = x[i][0] - xbar;
				double dy = x[i][1] - ybar;
				double ii = i+1;
				double sweep = (ii - 1.0) / ii;
				ssqx += dx * dx * sweep;
				ssqy += dy * dy * sweep;
				ssq_xy += dx * dy * sweep;
				xbar += dx / ii;
				ybar += dy / ii;
			}
		}
		double Sxx = Math.sqrt(ssqx);
		double Syy = Math.sqrt(ssqy);
		double Sxy = ssq_xy;
		double r = Sxy / (Sxx * Syy);
		return(r);
	}
}
