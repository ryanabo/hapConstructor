package edu.utah.med.genepi.hapconstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.analysis.SingleMarkerResult;

public class hapCResultsManager {

	private hapCParamManager params = null;
	private GenieDataSet gd = null;
	private hapCResultsBuffer[] resultsBuffer = null;
	private LocusResultStorage[] testedLoci = null;
	private HaplotypeResultStorage[] significantHaplotypes = null;
	private int nLoci = 0;
	private boolean hitMostSignificant = false;
	
	public hapCResultsManager( GenieDataSet genieDataSet, hapCParamManager hapconParamManager, int nloci )
	{
		params = hapconParamManager;
		nLoci = nloci;
		testedLoci = new LocusResultStorage[nLoci];
		significantHaplotypes = new HaplotypeResultStorage[nLoci];
		int nbuffers = params.useBackSets() ? 2 : 1;
		resultsBuffer = new hapCResultsBuffer[nbuffers];
		gd = genieDataSet;
	}
	
	//----------------------------------------------------------------------------
	public void processAnalysis( Analysis analysis, hapCAnalysisStep analysisStep,  int nSims )
	{
		int step = analysisStep.getStep();
		double sigThreshold = params.getThreshold(step-1);
		String analysisType = analysis.getAnalysisType();
		
		hapCResult result = null;
		if ( analysisType.equals("singlemarker") ) {
		  result = new SingleMarkerResult(analysis,params.getLogTableCounts()); 
		  //System.out.println("SingleMarkerResult " + result.getComboSet().getAlleleV());
		}
		else if ( analysisType.equals("haplotype") )
		{
			result = new HapResult(analysis,params.getLogTableCounts());
		  //System.out.println("HapResult " + result.getComboSet().getAlleleV());
			if ( result.metSignificance(sigThreshold) )
			{
				if (significantHaplotypes[analysisStep.getStep()-1] == null ) significantHaplotypes[analysisStep.getStep()-1]  = new HaplotypeResultStorage();
				significantHaplotypes[analysisStep.getStep()-1].storeHaplotype(result.getLocusAddress(),result.getHaplotype(false));
			}
		}
		else if ( analysisType.equals("interaction") ) result = new IntxResult(analysis,params.getLogTableCounts());
		else if ( analysisType.equals("compositetrend") ) result = new CTResult(analysis,params.getLogTableCounts());
		else if ( analysisType.equals("compositegenotype") ) result = new CGResult(analysis,params.getLogTableCounts());
		
                //System.out.println(" Check result HERE : " + result.getComboSet().getAlleleV());
		if ( analysisStep.getStep() > 1 )
		{
			if ( testedLoci[analysisStep.getStep()-1] == null ) testedLoci[analysisStep.getStep()-1] = new LocusResultStorage();
			testedLoci[analysisStep.getStep()-1].addAddress(result.getLocusAddress());
		}
	
		if ( result.metSignificance(sigThreshold) )
		{
			// Store results in buffer if met threshold. Always store forward results in buffer 0
			// and backward results in buffer 1.
			int bufferIndex = analysisStep.getDirection();
			//System.out.println("Storing result in buffer " + bufferIndex);
			if ( resultsBuffer[bufferIndex] == null ) resultsBuffer[bufferIndex] = new hapCResultsBuffer(analysisStep.getStep());
			resultsBuffer[bufferIndex].storeResult(result);
			// Write result out?
			try 
			{
				if ( params.getSigTestingIndex() == -1 )
				{
					String buildFileName = params.getBuildFileName(gd.getoutpathStem());
					writetoFile(result,buildFileName,false);
				}
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			if ( result.hitMostSignificant(nSims) ) hitMostSignificant = true;
		}
		try 
		{
			if ( !result.checkFailed() ) writeResult(result);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------------------------
	public void writeResult( hapCResult result ) throws IOException
	{
		if ( params.getSigTestingIndex() == -1 )
		{
			String allpvalsFileName = params.getAllPvalFileName();
			writetoFile(result,allpvalsFileName,true);
		}
		else
		{
			String allpvalsFileName = params.getAllPvalFileName();
			writetoFile(result,allpvalsFileName,true);
		}
	}
	
	//----------------------------------------------------------------------------
	//Write information from each analysis to a file.
	private void writetoFile( hapCResult result, String fileName, boolean appendSigIndex ) throws IOException
	{
		String output = result.getOutput(); 
		if ( appendSigIndex )
		{
			StringBuffer sb = new StringBuffer();
			sb.append(Integer.toString(params.getSigTestingIndex()));
			sb.append(":");
			sb.append(output);
			output = sb.toString();
		}
		
		File freport = new File(fileName);	  
		if ( freport.exists() )
		{
			FileWriter fw = new FileWriter(fileName,true);
			fw.write(output + "\n");
			fw.close();
		}
		else
		{
			PrintWriter pw  = new PrintWriter(new BufferedWriter(new FileWriter(freport)), true);
			pw.println(output);
			pw.close();
		}
	}
	
	//----------------------------------------------------------------------------
	public boolean processResults()
	{	
		boolean continueAnalysis = true;
		int stepDirection = params.getStepDirection();
		//System.out.println("start params.checkStopAtMostSignificant");
		if ( params.checkStopAtMostSignificant() && hitMostSignificant ) continueAnalysis = false;
		else if ( (resultsBuffer[stepDirection] != null) && ((resultsBuffer[stepDirection].getResults()).length > 0) )
		{
			//System.out.println("Step direction processing results " + stepDirection);
			hapCWrappedResult[] bufferedResults = resultsBuffer[stepDirection].getResults();
			for ( int i=0; i < bufferedResults.length; i++ ) {
				//System.out.println("processResult with bufferedResults["+i+"]");
				processResult(bufferedResults[i]);
				//System.out.println("processResult with bufferedResults["+i+"] END");
			}
			if ( stepDirection == 0 ) resultsBuffer[stepDirection] = null;
			if ( resultsBuffer.length > 1 && resultsBuffer[1] != null && stepDirection == 0 )
			{
				//System.out.println("Processing resuolts from buffer 1 - step " + resultsBuffer[1].getStoredStep() );
				if ( resultsBuffer[1].getStoredStep() == params.getStep() )
				{
					//System.out.println("Processing results from buffer 1");
					bufferedResults = resultsBuffer[1].getResults();
					for ( int i=0; i < bufferedResults.length; i++ )
					{
						//System.out.println("Previous result locus address " + bufferedResults[i].getLocusAddress());
						processResult(bufferedResults[i]);
					}
					resultsBuffer[1] = null;
				}
			}
		}
		else continueAnalysis = false;
		//System.out.println("end of params.checkStopAtMostSignificant");
		
		return continueAnalysis;
	}
	
	//----------------------------------------------------------------------------
	private void processResult( hapCWrappedResult result )
	{
		//System.out.println("processing results WRAPPED");
		// Create new fillers with result
		if ( params.getStepDirection() == 0 ) addMarkers(result);
		else
		{
			//System.out.println("Removing markers");
			removeMarkers(result);
		}
		//System.out.println("processing results WRAPPED END");
	}
	
	//----------------------------------------------------------------------------
	private void addMarkers( hapCWrappedResult wrappedResult )
	{
		hapCResult[] results = wrappedResult.getResults();
		long locusAddress = wrappedResult.getLocusAddress();
		long allLociMask = (long)(Math.pow(2,nLoci)-1);
		long bits2add = allLociMask ^ locusAddress;
		
		for ( int i = 0; i < nLoci; i++ )
		{
			if ( ((bits2add >> i) & 1) == 1 )
			{
				for ( int j=0; j < results.length; j++ ) {
 					//System.out.println("hapCResultsManager - addMarkers");
					results[j].newMarkerSet(i,gd,params,significantHaplotypes[params.getStep()-1]);
 					//System.out.println("hapCResultsManager - addMarkers END");
				}
			}
		}
	}
	
	//---------------------------------------------------------------------------	
	public void removeMarkers( hapCWrappedResult wrappedResult ) 
	{
		hapCResult[] results = wrappedResult.getResults();
		long locusAddress = wrappedResult.getLocusAddress();
		
		int step = params.getStep();
		Long[] previousStepLoci = testedLoci[params.getStep()-2].getLociAddresses();
		long[] tempStorage = new long[step];
		int matchIter = 0;
		for ( int i=0; i < previousStepLoci.length; i++ )
		{
			long lA = previousStepLoci[i];
			if ( (locusAddress & lA) == lA ) 
			{
				tempStorage[matchIter] = ~lA;
				matchIter++;
				if ( matchIter == step ) break;
			}
		}
		
		long orBits = 0;
		for ( int i=0; i < step; i++ ) orBits |= tempStorage[i];
		if ( orBits != -1 )
		{
			for ( int i=0; i < nLoci; i++ )
			{
				if ( (((locusAddress >> i) & 1) == 1) && (((orBits >> i) & 1) == 0) )
				{
					for ( int j=0; j < results.length; j++ ) results[j].reducedMarkerSet(i,gd,params);
				}
			}
		}
	}	
}
