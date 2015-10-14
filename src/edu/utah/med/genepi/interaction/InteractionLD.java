package edu.utah.med.genepi.interaction;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Indiv.GtSelector;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.util.GEException;

public class InteractionLD {

  private int[][] markerCombos;
  private int nullCycles;
  private int[] loci_indices;
  private Study[] study;
  private final GSimulator.Top  tSim;
  private final GSimulator.Drop dSim;
  private marker[][] markers;
  private boolean caseOnly = false;
  private double[][][] results;
	
  public InteractionLD( Study[] std, InteractionParams intx_params )
  {
    markerCombos = intx_params.get_markerCombos();
    nullCycles = intx_params.get_nullcycles();
    caseOnly = intx_params.get_caseOnly();
    Integer[] iloci = intx_params.get_loci_indices();
    loci_indices = new int[iloci.length];
    for ( int i=0; i < iloci.length; i++ ) loci_indices[i] = iloci[i];
    study = std;
    tSim = intx_params.getTsim();
    dSim = intx_params.getDsim();
    results = new double[std.length][markerCombos.length][2];
  }
  
  private class marker
  {
    private int[] maCount = new int[2];
    private int[] hetCount = new int[2];
    private Integer[] alleles;
    private int[] nGt = new int[2];
    private int index = -1;
    private double[] hwd = new double[2];
    
    public marker( int lociIndex, PedData pd, int cycleIndex )
    {
      index = lociIndex;
	  Indiv[] cases = pd.getIndividuals( PedQuery.IS_CASE );
	  Indiv[] controls = pd.getIndividuals( PedQuery.IS_CONTROL );
	  int[] caseCounts = alleleCounts(cases,index,cycleIndex);
      int[] controlCounts = alleleCounts(controls,index,cycleIndex);
      hetCount[1] = caseCounts[2];
      hetCount[0] = controlCounts[2]; 
      maCount[1] = caseCounts[1];
      maCount[0] = controlCounts[1];      
      nGt[0] = controlCounts[3];
      nGt[1] = caseCounts[3];
      
      double[] mafs = new double[2];
      mafs[0] = ((double) maCount[0]) / (2.0*nGt[0]);
      mafs[1] = ((double) maCount[1]) / (2.0*nGt[1]);
      double[] hetfs = new double[2];
      hetfs[0] = (double) hetCount[0] / (nGt[0]);
      hetfs[1] = (double) hetCount[1] / (nGt[1]);
      hwd[0] = ((2*mafs[0]) - hetfs[0]) / 2;
      hwd[1] = ((2*mafs[1]) - hetfs[1]) / 2;
    }
    
    public int getIndex(){ return index; }
    
    public Integer[] getAlleles(){ return alleles; }
    
    public double getAF( int index, int cacoIndex )
    {
      double af = ((double) maCount[cacoIndex]) / (2.0*nGt[cacoIndex]);      
      if ( index == 0 ) af = 1 - af;
      return af;
    }
    
    public double getHWD( int cacoIndex )
    {
      return hwd[cacoIndex];
    }
    
    private int[] alleleCounts(Indiv[] inds, int marker, int cycleIndex )
    {
      GtSelector gs = GtSelector.SIM;
      if ( cycleIndex < 0 ) gs = GtSelector.OBS;
      // 0 = major allele count, 1 = minor allele count, 2 = het count, 3 = genotyped count
      int[] alleleCounts = new int[4];
      List<Integer> markerAlleles = new ArrayList<Integer>();
      for ( int i=0; i < inds.length; i++ )
      {
        Indiv ind = inds[i];
        if ( ind.getIsFounder() )
        {
          Gtype gt = ind.getGtype(gs);
          AllelePair ap = gt.getAllelePairAt(marker);
          if ( ap != null )
          {
            int a1 = ap.getAlleleCode(true)-1;
            int a2 = ap.getAlleleCode(false)-1;
            if ( !markerAlleles.contains(a1) ) markerAlleles.add(a1);
            if ( !markerAlleles.contains(a2) ) markerAlleles.add(a2);
            alleleCounts[a1]++;
            alleleCounts[a2]++;
            alleleCounts[3]++;
            if ( a1 != a2 ) alleleCounts[2]++;
          }
        }
      }
      alleles = markerAlleles.toArray(new Integer[0]);
      return alleleCounts;
    }
  }
  
  public void run() throws GEException
  {
	analyze(-1);
	for ( int i=0; i < nullCycles; i++)
	{
	  tSim.simulateFounderGenotypes(0);
	  dSim.simulateDescendantGenotypes(0);   		
	  analyze(i);
	}
    report();
  }
  
  private void report()
  {
    for ( int i =0; i < study.length; i++ )
    {
      System.out.println("Study: " + i);
      for ( int j = 0; j < markerCombos.length; j++ )
      {
        int[] markerPair = markerCombos[j];
        System.out.println("Markers: " + markerPair[0] + " & " + markerPair[1]);
        System.out.println("Observed stat: " + results[i][j][0] );
        double pval = results[i][j][1] / nullCycles;
        System.out.println("P-value: " + pval );
      }
    }
  }
  
  private void initializeMarkers( int cycleIndex )
  {
    markers = new marker[study.length][loci_indices.length];
	for ( int i=0; i < study.length; i++ )
	{
      PedData peddata = study[i].getPedData();
	  for ( int j = 0; j < loci_indices.length; j++ )
	  {
		marker m = new marker(j,peddata,cycleIndex);
		markers[i][j] = m;
	  }
	}	  
  }
  
  private void analyze( int index )
  {
    initializeMarkers(index);
	for ( int i=0; i < study.length; i++ )
	{
      PedData peddata = study[i].getPedData();
	  Indiv[] caseIndiv = peddata.getIndividuals( PedQuery.IS_CASE );
	  Indiv[] controlIndiv = peddata.getIndividuals( PedQuery.IS_CONTROL );
	  for ( int j = 0; j < markerCombos.length; j++ )
	  {
		int mIndex1 = markerCombos[i][0];
		int mIndex2 = markerCombos[i][1];
		marker m1 = markers[i][mIndex1];
		marker m2 = markers[i][mIndex2];
		double[] caseLD = ldCorrelation(caseIndiv,2,new marker[]{m1,m2},index);
		int testIndex = 2;
		double r0Val = caseLD[testIndex];
		if ( !caseOnly )
		{
		  double[] controlLD = ldCorrelation(controlIndiv,1,new marker[]{m1,m2},index);
		  r0Val = Math.abs(caseLD[testIndex] - controlLD[testIndex]);
		  //if ( controlLD > caseLD ) r0Val = controlLD / caseLD;
		  System.out.println(r0Val + " (" + caseLD[testIndex] + " " + controlLD[testIndex] + ")");
		  //FisherFDist fsd = new FisherFDist(1,1);
	      //double fValue = fsd.barF(r0Val);
		}
		if ( index < 0 ) results[i][j][0] = r0Val;
		else
		{
		  if ( r0Val >= results[i][j][0] ) results[i][j][1]++;
		}
	  }
	}
  }
  
  private double[] ldCorrelation( Indiv[] inds, int cacoIndex, marker[] mPair, int cycleIndex )
  {
	int[] markerIndices = new int[]{mPair[0].getIndex(),mPair[1].getIndex()};
	Integer[] alleles1 = mPair[0].getAlleles();
	Integer[] alleles2 = mPair[1].getAlleles();
	double r2 = 0.0, dpr = 0.0;
	int nInds = gtInds(inds,markerIndices,cycleIndex);
	for ( int i=0; i < alleles1.length; i++ )
	{
	  for ( int j = 0; j < alleles2.length; j++ )
	  {
	    int ii = alleles1[i];
	    int jj = alleles2[j];
	    int[][] x = aCounts(inds,markerIndices,new int[]{ii,jj},cycleIndex);
        double tr = Cor(x);
        r2 += tr*tr;
        double tdpr = DprIJ(x,nInds);
        dpr += Math.abs(tdpr);
	  }
	}
	int n1 = alleles1.length;
	int n2 = alleles2.length;
	double df = (n1-1.0)*(n2-1.0);
	double ncells = n1*n2;
	double x2 = (nInds*df/ncells)*r2;
	dpr /= ncells;
    return new double[]{x2,dpr,r2};
  }
  
  private int gtInds(Indiv[] inds, int[] markerIndices, int cycleIndex)
  {
    GtSelector gs = GtSelector.SIM;
	if ( cycleIndex <  0 ) gs = GtSelector.OBS; 

    int nGt = 0;
    for ( int i=1; i < inds.length+1; i++ )
    {
      Indiv ind = inds[i-1];
      Gtype gt = ind.getGtype(gs);
      AllelePair ap1 = gt.getAllelePairAt(markerIndices[0]);
      AllelePair ap2 = gt.getAllelePairAt(markerIndices[1]);
      if ( (ap1 != null) && (ap2 != null) ) nGt++;
    }
    return nGt;
  }
  
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
  
  private double DprIJ( int[][] q, int n )
  {
	double naa = Cnt(q,0,-1); 
	double nbb = Cnt(q,1,-1); 
	double nAa = Cnt(q,0,0); 
	double nBb = Cnt(q,1,0); 
	double nAA = Cnt(q,0,1); 
	double nBB = Cnt(q,1,1); 
	double pa = 0.5 - Sum(q,0)/(2.0*n);
	double pb = 0.5 - Sum(q,1)/(2.0*n);
	double delta = Cov(q,n);
	double d,s,mxd,ld = (n-1.0)/n * delta/2.0;
	if( delta > 0 ) 
	{
	  d = Math.min(nAA,nBB) + Math.min(naa,nbb);
	  s = Math.max(n-d-nAa-nBb, 0.0);
	  mxd = (d-s)/(2.0*n) - (1-2*pa)*(1-2*pb)/2;
	}
	else
	{
	  d = Math.min(nAA,nbb) + Math.min(naa,nBB);
	  s = Math.max(n-d-nAa-nBb, 0.0);
	  mxd = (d-s)/(2.0*n) + (1-2*pa)*(1-2*pb)/2;
	}
	double dpr = ld/mxd;
	return dpr;	 
  }
  
  private double Cov( int[][] x, int n )
  {
    double ssq_xy = 0;
    double xbar = x[0][0];
    double ybar = x[0][1];
    for(int i=1; i < x.length; i++) 
    {
      double dx = x[i][0] - xbar;
      double dy = x[i][1] - ybar;
      double ii = i+1;
      double sweep = (ii - 1.0) / ii;
      ssq_xy += dx * dy * sweep;
      xbar += dx / ii;
      ybar += dy / ii;
    }
    double Sxy = ssq_xy / (n-1.0);
    return Sxy;
  }

  private int Sum(int[][] q, int dim) {
    int s=0;
    for(int i=1; i<q.length; i++)
    {
      if ( q[i][0] > -2 ) s += q[i][dim];
    }
    return s;
  }

  private int Cnt(int[][] q, int dim, int what) 
  {
    int c=0;
    for(int i=1; i<q.length; i++) if(q[i][dim] == what) ++c;
    return c;
  }
  
//  private double[] getAF( int index, marker[] mPair, int cacoIndex )
//  {
//    int m2Index = index & 1;
//    int m1Index = (index >> 1) & 1;
//    return new double[] {mPair[0].getAF(m1Index,cacoIndex),mPair[1].getAF(m2Index,cacoIndex)};
//  }
  
  private int[][] aCounts( Indiv[] inds, int[] markerIndices, int[] alleles, int cycleIndex )
  {
	GtSelector gs = GtSelector.SIM;
	if ( cycleIndex <  0 ) gs = GtSelector.OBS; 

    int[][] aCounts = new int[inds.length][2];
    for ( int i=0; i < inds.length; i++ )
    {
      Indiv ind = inds[i];
      Gtype gt = ind.getGtype(gs);
      AllelePair ap1 = gt.getAllelePairAt(markerIndices[0]);
      AllelePair ap2 = gt.getAllelePairAt(markerIndices[1]);
      AllelePair[] aps = new AllelePair[]{ap1,ap2};
      if ( (ap1 != null) && (ap2 != null) )
      {
        int[] nAlleles = new int[2];
  	    for ( int j=0; j < aps.length; j++ )
  	    {
          int a1 = aps[j].getAlleleCode(true)-1;
          int a2 = aps[j].getAlleleCode(false)-1;
          if ( a1 == alleles[j] ) nAlleles[j]++;
          if ( a2 == alleles[j] ) nAlleles[j]++;
          nAlleles[j]--;
          aCounts[i] = nAlleles;
  	    }
      }
      else aCounts[i] = new int[]{-2,-2};
    }
    return aCounts;
  }
}
