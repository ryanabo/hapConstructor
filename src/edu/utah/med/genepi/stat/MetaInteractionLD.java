package edu.utah.med.genepi.stat;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.genie.Phenotype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.util.Counter;

public class MetaInteractionLD extends CCStatImp {

	public MetaInteractionLD()
	{
		title = "MetaIntxLD"; 
	}
	
	//---------------------------------------------------------------------------
	public Result computeStat( Analysis a, AnalysisTable[] tables, int[] simIndices )
	{
		double[][] ldVals = new double[tables.length][2];
		
		double numer = 0.0;
		double denom = 0.0;
		for ( int i=0; i < tables.length; i++ )
		{
			// i,0 = r-value,i,1 = weight 
			ldVals[i] = calcLD((IntxLDTable)tables[i]);
			numer += ldVals[i][0] * ldVals[i][1];
			denom += ldVals[i][1];
		}
		double res = (numer / denom) / Math.sqrt((1.0 / denom));
		double r = ( Math.exp(2.0*res) -1 ) / ( Math.exp(2.0*res) + 1);
		resultAt0 = new ResultImp.Real(r);
		return resultAt0;
	}
	
	
	//---------------------------------------------------------------------------
	public double[] calcLD( IntxLDTable t )
	{
		Phenotype[] phens = t.getPhenotypes();
		Counter[][] c = t.getCounters();
		int[] indLst = t.getIndividuals();
		double[] metaValues = new double[2];
		if ( t.getCaseOnly() )
		{
			int[][] caseCounts = new int[c.length][2];
			int nInds = 0;
			for ( int i=0; i < c.length; i++ )
			{
				if ( c[i] != null )
				{
					caseCounts[i] = new int[]{ (Integer) c[i][0].current(),(Integer) c[i][1].current()};
					nInds++;
				}
				else caseCounts[i] = new int[]{-2,-2};
			}
			// 0 = correlation, 1 = sample size
			double[] values = ldCorrelation(caseCounts,nInds);
			metaValues[0] = values[0];
			metaValues[1] = values[1] - 3;
		}
		else
		{
			List<int[]> lstcaseCounts = new ArrayList<int[]>();
			List<int[]> lstcontrolCounts = new ArrayList<int[]>();
			int[] nInds = new int[]{0,0};
			for ( int i=0; i < indLst.length; i++ )
			{
				int phen = phens[i].getPhenotype();
				int[] count = new int[]{-2,-2};
				
				if ( (phen > 0) && (c[i] != null) )
				{
					count = new int[]{ (Integer) c[i][0].current(),(Integer) c[i][1].current()};
					nInds[phen-1]++;
				}
				if ( phen == 1 ) lstcontrolCounts.add(count);
				else if ( phen == 2 ) lstcaseCounts.add(count);
			}
			int[][] caseCounts = lstcaseCounts.toArray(new int[0][0]);
			int[][] controlCounts = lstcontrolCounts.toArray(new int[0][0]);
			double[] caseValues = ldCorrelation(caseCounts,nInds[1]);
			double[] controlValues = ldCorrelation(controlCounts,nInds[0]);
			// Transform to Z-stat
			double caseZ = 0.5*(Math.log((1+caseValues[0])/(1-caseValues[0])));
			double controlZ = 0.5*(Math.log((1+controlValues[0])/(1-controlValues[0])));
			
			double zDiff = caseZ - controlZ;
			double ztor = (Math.exp(2*zDiff) - 1) / (Math.exp(2*zDiff)+1);
			metaValues[0] = ztor;
			double denom = (1.0/(caseValues[1]-3)) + (1.0 / (controlValues[1]-3));
			metaValues[1] = 1.0 / denom;
		}
		return metaValues;
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker()
	{
		compTracker = new ScalarCompTracker();
		return compTracker;
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker(int df)
	{
		return this.newComparisonTracker();
	}

	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getIntxLDTable(studyIndex,simIndices); 
	}
	
	//---------------------------------------------------------------------------
	private double[] ldCorrelation( int[][] counts, int nInds )
	{
		//double r2 = 0.0, dpr = 0.0;
		double[] tr = Cor(counts);
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
	private double[] Cor( int[][] x )
	{
		double ssqx = 0, ssqy = 0, ssq_xy = 0;
		double xbar = x[0][0];
		double ybar = x[0][1];
		// x[0][0] contains the number of inds genotyped at both markers
		int count = 0;
		for(int i=1; i < x.length; i++) 
		{
			if ( x[i][0] > -2 )
			{
				count++;
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
		return new double[]{r,count};
	}
}
