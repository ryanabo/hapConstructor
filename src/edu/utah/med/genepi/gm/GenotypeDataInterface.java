package edu.utah.med.genepi.gm;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import edu.utah.med.genepi.genie.GenieParameterData;
import edu.utah.med.genepi.genie.IndividualManager;
import edu.utah.med.genepi.hapconstructor.analysis.MarkerUnit;
import edu.utah.med.genepi.hapconstructor.analysis.ValueUnit;
import edu.utah.med.genepi.stat.TallierImp;

public class GenotypeDataInterface {

	// 3-dimensional byte array with bits of allele data
	private byte[][][] gtData = null;
	private byte[][] missingData = null;
	private boolean storeSims = false;
	// col 0 = gt data, col 1 = missing data
	private int[] nExtraInds = new int[]{0,0};
	private int nIndividuals = 0;
	
	/** Initialize GenotypeDataInterface by establishing the dimensions for the genotype
	 * data storage and missing data storage. Note that based on whether there are related
	 * subjects and what application is running the genotype data array dimension changes.
	 * If there are independent subjects then only store 1 dimension since the phenotypes are
	 * permuted. Otherwise, if the application is hapConstructor then store all simulations, and 
	 * if the application is pedgenie or hapmc store 2 dimensions for observed and simulated
	 * data. The simulated data is rotated into the second position as it is simulated and analyzed.
	 * 
	 * @param nInds The number of individuals that have the required amount of genotype data specified
	 * by the user to use in the analysis. This is by default 0.0%. 
	 * @param peds Boolean value indicating whether this study data has related subjects in it or not.
	 * @param gp GenieParameterData object with all the input parameters.
	 */
	public GenotypeDataInterface( int nInds, boolean storeSimulations, GenieParameterData gp )
	{
		storeSims = storeSimulations;	
		nIndividuals = nInds;
		int nLoci = gp.getLociCount(-1);
		int nSimDimensions = 2;
		
		if ( storeSims )
		{
			if (gp.getAppId().equals("hapconstructor")) nSimDimensions = gp.getNSims()+1;
			//else if (gp.getAppId().equals("pedgenie") || gp.getAppId().equals("hapmc") ) nSimDimensions = 2;
		}
		
		gtData = new byte[nSimDimensions][nLoci][getNCols(nInds,0)];
		missingData = new byte[nLoci][getNCols(nInds,1)];
	}
	
	//----------------------------------------------------------------------------
	public void loadNullData( DataInputStream input ) throws NumberFormatException, IOException
	{
		String line = null;
		int lines_persim = gtData[0].length + 1;
		int line_counter = 0;
		int simindex = 0;
		byte[][] m = null;
		int simIter = 1;
		int rowIter = 0;
		int colIter = 0;
		try {
		    while (true) {
		        byte value = input.readByte();
		        //System.out.println("Value for sim " + simIter + " row " + rowIter + " col " + colIter + " value " + value);
		        gtData[simIter][rowIter][colIter] = value;
		        colIter++;
		        if ( colIter == gtData[0][0].length )
		        {
		        	rowIter++;
		        	colIter = 0;
		        }
		        if ( rowIter == gtData[0].length )
		        {
		        	simIter++;
		        	colIter = 0;
		        	rowIter = 0;
		        }
		    }
		} catch (EOFException e) {
		}
		
//		while (( line = input.readLine()) != null )
//		{
//			if( line_counter == 0)
//			{
//				simindex = (int) Integer.parseInt(line.split(":")[1]);
//				System.out.println("Loading sim: " + simindex);
//			}
//
//			if( line_counter == 1 ) m = gtData[simindex];
//
//			if( line_counter > 0)
//			{
//				String[] values = line.split(",");
//				byte[] gts = new byte[values.length];
//				for( int i = 0; i < values.length; i++ ) gts[i] = Byte.parseByte(values[i]);
//				m[line_counter-1] = gts;
//			}
//
//			if( line_counter == (lines_persim - 1)) line_counter = 0;
//			else line_counter++;
//		}
	}
	
	//----------------------------------------------------------------------------
	public byte[][] getSim( int simIndex ){ return gtData[simIndex]; }
	
	//----------------------------------------------------------------------------
	public void tallyData( TallierImp tallier, int dataIndex, IndividualManager indivManager, int[] indIndices )
	{
		// Multiple dataQueries that correspond to column gtMatchers
		GenotypeDataQuery[] dataQuery = tallier.getDataQuery();
//		Individual[] allInds = indivManager.getAllInds();
		int[] analysisInds = indivManager.getAnalysisIndices();
		if ( indIndices != null )  
		{
			analysisInds = indIndices;
		}
		for ( int i = 0; i < analysisInds.length; i++ )
		{
//			if ( tallier.screenMissing(i) )
//			{   
			 	//System.out.println("indiv " + allInds[gtInds[i]].getIndId());
				int iter = 0;
				while ( iter < dataQuery.length )
				{
					queryIndData(dataQuery[iter],analysisInds[i],dataIndex);
					// If there are multiple dataQueries and the individuals gt data satisfies the
					// values in the dataQuery, then stop matching and move to next individual.
					// This is the same as gtMatchers function.
					//System.out.println("Ninds " + nIndividuals);
					if ( tallier.processQueryResult(dataQuery[iter].getQueryResult(),analysisInds[i],iter) ) break; 
					else iter++;
				}
//			}
		}
	}
	
	//----------------------------------------------------------------------------
	private void queryIndData( GenotypeDataQuery dq, int indIndex, int dataIndex )
	{
		DataQuery[] queries = dq.getQueries();
		int[] indGtAddress = globalIndex2localAddress(indIndex,"gt"); 
		int[] indMissingAddress = globalIndex2localAddress(indIndex,"missing");
		for ( int j=0; j < queries.length; j++ )
		{
			ValueUnit vu = queries[j].getValueUnit();
			MarkerUnit mu = queries[j].getMarkerUnit();
			int[] alleleMatches = new int[]{1,1};
			int[] lociIndices = mu.getLoci();
			//System.out.println("indices " + Ut.array2str(lociIndices," "));
			for ( int k=0; k < lociIndices.length; k++)
			{
				if ( indMissing(indMissingAddress,lociIndices[k]) )
				{
					alleleMatches = null;
					dq.setMissing();
					break;
				}
				else if ( alleleMatches[0] == 0 && alleleMatches[1] == 0 ) break;
				else
				{
					byte[] allele2match = vu.getAllele(k);
					int patternIter = 0;
					int nPatterns = allele2match.length;
					
					byte a1 = (byte) getData(dataIndex,lociIndices[k],indGtAddress[0],indGtAddress[1]);
					byte a2 = (byte) getData(dataIndex,lociIndices[k],indGtAddress[0],indGtAddress[1]+1);
					//System.out.println(a1 + " " + a2);
					while ( patternIter < nPatterns )
					{
						if ( nPatterns == 1 )
						{
							int match1 = (allele2match[0] == 0 || a1 == allele2match[0]) ? 1 : 0;
							int match2 = (allele2match[0] == 0 || a2 == allele2match[0]) ? 1 : 0;
							alleleMatches[0] = match1 & alleleMatches[0];
							alleleMatches[1] = match2 & alleleMatches[1];
							patternIter++;
						}
						else
						{
							alleleMatches[0] = (allele2match[patternIter] == 0 || a1 == allele2match[patternIter]) ? 1 : 0;
							alleleMatches[1] = (allele2match[patternIter+1] == 0 || a2 == allele2match[patternIter+1]) ? 1 : 0;
							if ( (alleleMatches[0] == 1) && (alleleMatches[1] == 1) )
							{
								patternIter = nPatterns;
								break;
							}
							else
							{
								alleleMatches[0] = 0;
								alleleMatches[1] = 0;
								patternIter+=2;
							}
						}
					}
				}
			}
			if ( alleleMatches == null ) break;
			else if ( !dq.filter(queries[j].filter(alleleMatches),j) || dq.checkFullMatch() ) break;
		}
	}
	
	//----------------------------------------------------------------------------
	private byte getData( int dataIndex, int locusIndex, int indAddress, int shift)
	{
		int a1 = ((gtData[dataIndex][locusIndex][indAddress] >> shift) & 1)+1;
		return (byte) a1;
	}
	
	//----------------------------------------------------------------------------
	private boolean indMissing( int[] indMissingAddress, int locusIndex )
	{
		//int v = missingData[locusIndex][indMissingAddress[0]]  >> indMissingAddress[1];
		int missingValue = (missingData[locusIndex][indMissingAddress[0]] >> indMissingAddress[1]) & 1;
		boolean missing = missingValue == 1? true : false;
		return missing;
	}
	
	//----------------------------------------------------------------------------
	public int[] getGt( int indIndex, int locusIndex, int dataSourceIndex )
	{
		int[] alleles = new int[]{0,0};
		int[] indGtAddress = globalIndex2localAddress(indIndex,"gt"); 
		int[] indMissingAddress = globalIndex2localAddress(indIndex,"missing");
		if ( !indMissing(indMissingAddress,locusIndex) )
		{
			alleles[0] = getData(dataSourceIndex,locusIndex,indGtAddress[0],indGtAddress[1]);
			alleles[1] = getData(dataSourceIndex,locusIndex,indGtAddress[0],indGtAddress[1]+1);
		}
		return alleles;
	}
	
	//----------------------------------------------------------------------------
	public int getNInds()
	{
//		int nFullCells = gtData[0].length;
//		if ( nExtraInds[0] > 0 ){ nFullCells--; }
//		int nInds = 4*nFullCells + nExtraInds[0];
		return nIndividuals;
	}
	
	/** Converts individual global index to local address composed of 
	 * cell index and within cell location.
	 * 
	 * @param indIndex Individual storage index value. 
	 * @param type String to denote which data structure to provide address:
	 * "missing" data or "gt" data.  
	 * @return integer array with 0 cell containing the cell index of individual
	 * and 1 cell containing the number of bits individual is shifted left in that
	 * cell.
	 */
	//----------------------------------------------------------------------------
	public int[] globalIndex2localAddress( int indIndex, String type )
	{
		int[] address = new int[2];
		
		int nbits = 4;
		int scale = 2;
		if ( type.equals("missing") )
		{ 
			nbits = 8;
			scale = 1;
		}
		
		int cellIndex = indIndex / nbits;
		int shiftRight = indIndex - (cellIndex*nbits);
		shiftRight = (nbits-(shiftRight+1))*scale;
		address[0] = cellIndex;
		address[1] = shiftRight;
		return address;
	}
	
	/** Sets the raw genotype input data into compressed byte array with nlocus rows
	 * and a column for 4 individuals genotype data represented as bits. The missing data
	 * is stored in a byte array with nlocus rows and a column for each person's missing status
	 * at that locus.
	 * 
	 * @param studyIndex Integer study index for obtaining the allelecode from locus storage.
	 * @param indIndex Integer index of individual.
	 * @param rawGtData String array with nlocus rows with 2 columns for each
	 * allele at each locus.
	 * @param gp GenieParameter data to access locus allele codes.
	 */
	//----------------------------------------------------------------------------
	public void setGtData( int studyIndex, int indIndex, String[][] rawGtData, GenieParameterData gp )
	{
		int[] gtAddress = globalIndex2localAddress(indIndex,"gt");
		int[] missingAddress = globalIndex2localAddress(indIndex,"missing");
		//rawGtData has nLoci rows and 2 columns for each allele.
		for ( int i = 0; i < rawGtData.length; i++ )
		{
			String[] locusData = rawGtData[i];
			String a1 = locusData[0];
			String a2 = locusData[1];
			
			byte compressedGt = gtData[0][i][gtAddress[0]];
			byte compressedMissing = missingData[i][missingAddress[0]];
			
			if ( a1.equals("0") || a2.equals("0") )
			{
				compressedMissing = (byte) (compressedMissing | (1 << missingAddress[1]));
				missingData[i][missingAddress[0]] = compressedMissing;
			}
			else
			{
				int[] alleleCodes = new int[]{gp.getAlleleCode(studyIndex,i,a1),gp.getAlleleCode(studyIndex,i,a2)};
				compressedGt = (byte) (compressedGt | (alleleCodes[0] << gtAddress[1]));
				compressedGt = (byte) (compressedGt | (alleleCodes[1] << (gtAddress[1]+1)));
				gtData[0][i][gtAddress[0]] = compressedGt;
			}
		}
	}
	
	//----------------------------------------------------------------------------
	public void storeData( int dataSourceIndex, int indIndex, int locusIndex, int allele1, int allele2 )
	{
		int a1 = allele1 - 1;
		int a2 = allele2 - 1;
		
		int[] gtAddress = globalIndex2localAddress(indIndex,"gt");
		byte compressedGt = gtData[dataSourceIndex][locusIndex][gtAddress[0]];
		byte gtMask = (byte)((1 << gtAddress[1]) | (1 << (gtAddress[1]+1)));
		// Clear what is in it.
		compressedGt &= ~gtMask;
		byte newValue = (byte) ((a2 << gtAddress[1]) | (a1 << (gtAddress[1]+1)));
		gtData[dataSourceIndex][locusIndex][gtAddress[0]] = (byte) (compressedGt | newValue);
		
		if ( dataSourceIndex == 0 )
		{
			// Overwrite existing data
			int[] missingAddress = globalIndex2localAddress(indIndex,"missing");
			byte missingValue = missingData[locusIndex][missingAddress[0]];
			byte missingMask = (byte) (1 << missingAddress[1]);
			if ( a1 == -1 && a2 == -1 )
			{
				// Insert 1 into position
				missingData[locusIndex][missingAddress[0]] = (byte) (missingValue | missingMask);
			}
			else
			{
				// Insert 0 into position
				missingData[locusIndex][missingAddress[0]] = (byte) (missingValue & (~missingMask));
			}
		}
	}
	
	//----------------------------------------------------------------------------
	public int getNCols( int nInds, int type )
	{
		int nGtPerByte = 4;
		if ( type == 1 ) nGtPerByte = 8;

		int nCols = (nInds / nGtPerByte);
		nExtraInds[type] = nInds - (nGtPerByte*nCols); 
		if ( nExtraInds[type] > 0 ) nCols++;
		return nCols;
	}
}
