package edu.utah.med.genepi.genie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.ped.Individual;
import edu.utah.med.genepi.util.Ut;

public class DataFileProcessor {

	private LinkedHashMap<String,IndividualBuffer> indBuffer = new LinkedHashMap<String,IndividualBuffer>();
	private HashMap<String,String[]> pedDict = null;
	private int[] markerCols = null;
	private int stdyIndex = -1;
	private HashMap<String,Integer>[] alleleMap = null;
	String topSample = null;
	// Percent of genotypes required to be considered non-missing
	private double percentGt;
	private int nPhenoGenotypedInds = 0;
	private int nGenotypedInds = 0;
	private int nGenotypedCases = 0;
	private int nGenotypedControls = 0;
	
	//--------------------------------------------------------------------------------	
	DataFileProcessor( File genotypeFile, File quantFile, GenieParameterData gp, int studyIndex ) throws IOException
	{
		stdyIndex = studyIndex;
		percentGt = gp.getPercentGt();
		markerCols = new int[gp.getLociCount(-1)];
		topSample = gp.getTopSample();
		for ( int i=0; i < markerCols.length; i++ )
		{
			markerCols[i] = Col.MARKER1 + (gp.getLocus(-1,i).getIndex()) * 2;
		}
		alleleMap = new HashMap[markerCols.length];
		parseDataFile(genotypeFile);
		if ( quantFile != null ) parseQuantFile(quantFile,gp.getCovarIDs());
		gp.storeLocusMap(alleleMap,studyIndex);
	}
	
	//--------------------------------------------------------------------------------
	public HashMap<String,String[]> getPedDict(){ return pedDict; }
	
	//--------------------------------------------------------------------------------
	public LinkedHashMap<String,IndividualBuffer> getIndividualBuffer(){ return indBuffer; }
	
	//--------------------------------------------------------------------------------
	private void parseQuantFile( File f, int[] quantIndices ) throws IOException
	{
	    BufferedReader br = new BufferedReader(new FileReader(f));
	    String s;
	    int numtokens;
	    
	    if ( br.readLine().matches("^\\s*\\d+\\s+\\d+.+$")) br.reset();

	    while (( s = br.readLine()) != null )
	    {
	    	String[] tokens = s.trim().split("\\s+");
	    	StringTokenizer st = new StringTokenizer(s);
	    	numtokens = st.countTokens(); 
	    	double[] quants = new double[quantIndices.length];
	    	for ( int i = 0; i < quants.length; i++)
	    	{
	    		for ( int j = 2; j < numtokens; j++ )
	    		{ 
	    			if ( quantIndices[i] == (j-1) ) quants[i] = Double.parseDouble(tokens[ j ]);
	    		}
	    	}
	    	Qdata qd = new Qdata(quants);
	    	String pedid = tokens[Col.PED_ID];
	    	String indid = tokens[Col.IND_ID]; 
            String key = Ut.strAppend(pedid,indid,":");
	    	IndividualBuffer indBuff = indBuffer.get(key);
	    	indBuff.addQuantData(qd);
	    	indBuffer.put(key,indBuff);
	    }
	    br.close();
	}
	
	//--------------------------------------------------------------------------------
	private void parseDataFile( File f ) throws IOException
	{
	    BufferedReader in = new BufferedReader(new FileReader(f));
		for ( int i=0; i < markerCols.length; i++ ) alleleMap[i] = new HashMap<String,Integer>(); 
		boolean hasHeader = true;
		String line;
		if ( hasHeader ) line = in.readLine();
	    while ((line = in.readLine()) != null) processLine(line);
	    in.close();
	}
	
	//----------------------------------------------------------------------------
	private void processLine( String line )
	{
		String[] tokens = line.trim().split("\\s+");
		IndividualBuffer indBuff = processMeta(tokens);
		processMarkers(indBuff,tokens);
	}
	
	//----------------------------------------------------------------------------
	public int getStudyIndex(){ return stdyIndex; }
	
	//----------------------------------------------------------------------------
	private IndividualBuffer processMeta( String[] tokens )
	{
		int indStorageIndex = indBuffer.size();
		String pedid = tokens[Col.PED_ID];
		String indid = tokens[Col.IND_ID];		
		String paid = tokens[Col.DAD_ID];
		String maid = tokens[Col.MOM_ID];
		int sex = Integer.parseInt(tokens[Col.SEX_ID]);
		int liab = Integer.parseInt(tokens[Col.LIAB]);
		Individual ind = new Individual(pedid,indid,paid,maid,sex,liab,indBuffer.size());
		
		String key = Ut.strAppend(pedid,indid,":");
		
		int phen = Integer.parseInt(tokens[Col.PHEN]);
		Phenotype phenObj = new Phenotype(phen,indStorageIndex);
		
		IndividualBuffer indBuff = new IndividualBuffer(ind,phenObj);
		if ( indBuffer.containsKey(key) )
		{
			System.err.println("Warning: Multiple input lines for "+key);
			System.err.println("Ignoring all but the first.");
		}
		else indBuffer.put(key,indBuff);
		
		if ( !paid.equals("0") && !maid.equals("0") )
		{
			if ( pedDict == null ) pedDict = new HashMap<String,String[]>();
			pedDict.put(key,new String[]{paid,maid});
		}
		return indBuff;
	}
	
	//----------------------------------------------------------------------------
	private void processMarkers( IndividualBuffer indBuff, String[] tokens )
	{
		int nmarkers = markerCols.length;
		String[][] indMarkers = new String[nmarkers][2];
		int nGt = 0;
		for (int i = 0; i < nmarkers; ++i)
		{	        
			int col1 = markerCols[i];
			String a1 = tokens[col1];
			String a2 = tokens[col1+1];
			//System.out.println("Ind " + indBuff.indObj.getIndId() + " alleles for locus " + i + " " + a1 + " " + a2);
			indMarkers[i] = new String[]{a1,a2};
			// Do not count marker in allele frequencies if one allele is missing. 
			// Must have both alleles to count in allele frequency.
			if ( !a1.equals("0") && !a2.equals("0") ) 
			{
				checkAlleles(indBuff,alleleMap[i],indMarkers[i]);
				nGt++;
			}
		}
		double percentGenotyped = ((double) nGt / (double) nmarkers) * 100.00;
		if ( percentGenotyped >= percentGt )
		{
			indBuff.addGtData(indMarkers);
			nGenotypedInds++;
			if ( indBuff.isPhenoGenotyped() ) nPhenoGenotypedInds++;
			if ( indBuff.getPhenotype().getPhenotype() == 2 ) nGenotypedCases++;
			else if ( indBuff.getPhenotype().getPhenotype() == 1 ) nGenotypedControls++;
		}
	}
	
	//----------------------------------------------------------------------------
	private void checkAlleles( IndividualBuffer indBuff, HashMap<String,Integer> lMap, String[] alleles )
	{
		for ( int i=0; i < alleles.length; i++ )
		{
			if ( ! lMap.containsKey(alleles[i]) ) lMap.put(alleles[i],0);
			int count = lMap.get(alleles[i]);
			if ( topSample.equals("all") ) 
			{
				count++;
			}
			else if ( topSample.equals("founders") && indBuff.isFounder() )
			{
				count++;
			}
			lMap.put(alleles[i],count);
		}
	}
	
	//----------------------------------------------------------------------------
	public int getNGenotyped(){ return nGenotypedInds; }
	
	//----------------------------------------------------------------------------
	public int getNGenotypedCases(){ return nGenotypedCases; }
	
	//----------------------------------------------------------------------------
	public int getNGenotypedControls(){ return nGenotypedControls; }
	
	//----------------------------------------------------------------------------
	public int getNPhenotypedGenotyped(){ return nPhenoGenotypedInds; }
}
