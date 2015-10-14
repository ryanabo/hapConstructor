package edu.utah.med.genepi.phase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import alun.gchap.HapFormatter;
import alun.gchap.MarkerObservation;
import alun.gchap.MultiLocusObservation;
import alun.gchap.Observation;
import alun.genio.GeneticDataSource;
import edu.utah.med.genepi.genie.DataInterface;
import edu.utah.med.genepi.genie.GenieParameterData;
import edu.utah.med.genepi.gm.Gene;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.linkageext.GCHapDataSource;
import edu.utah.med.genepi.ped.Individual;

public class GCHapExt implements Phase {
	
	private double thresh = 10E-10;
	private GeneticDataSource[] gds = null;
	private Observation[] phasedData = null;

	public GCHapExt( GenieParameterData gp, GCHapDataSource ds )
	{
		if ( gp.getCaseOnlyIntx() || !gp.getPhaseTogether() )
		{
			Gene[] genes = gp.getGenes();
			gds = new GeneticDataSource[genes.length];
			for ( int i=0; i < gds.length; i++ )
			{
				Locus[] l = genes[i].getLoci();
				int[] indices = new int[l.length];
				for ( int j=0; j < l.length; j++ ) indices[j] = l[j].getIndex();
				GCHapDataSource gchapdata = new GCHapDataSource(ds,indices);
				gds[i] = gchapdata;
			}
			phasedData = new Observation[gds.length];
		}
		else
		{
			gds = new GeneticDataSource[]{ds};
			phasedData = new Observation[1];
		}
	}
	
	//----------------------------------------------------------------------------
	public void phase()
	{
		for ( int i=0; i < gds.length; i++ )
		{
			//System.out.println("Phasing gene " + i);
			phaseGene(gds[i],i);
		}
		//System.out.println(HapFormatter.formatHaplotypes(y));
		//System.out.println(HapFormatter.formatGuesses(y,gds));
	}
	
	//----------------------------------------------------------------------------	
	public void phaseGene( GeneticDataSource gds,int index )
	{
		MarkerObservation[] x = new MarkerObservation[gds.nLoci()];
		for (int i=0; i<x.length; i++)
			x[i] = new MarkerObservation(gds,i);

		Observation y = x[0];
		for (int i=1; i<x.length; i++)
		{
			y = new MultiLocusObservation(y,x[i]);
			y.getTrait().geneCount(1000);

			y.getTrait().downCode(thresh);
			y.getTrait().parsDownCode();
		}

	    y.getTrait().geneCount(1000);
		//y.geneCountToConvergence();
		y.getTrait().downCode(thresh);

		phasedData[index] = y;	
	}
	
	//----------------------------------------------------------------------------	
	public void output()
	{
		for ( int i=0; i < phasedData.length; i++ )
		{
			double[] freq = phasedData[i].getTrait().getLocus().alleleFrequencies();
			String[] code = phasedData[i].getTrait().getLocus().alleleNames();

			for ( int j = 0; j < freq.length; j++ )
			{
				System.out.println("Freq : " + freq[j] + " Haplotype : " + code[j]);
			}
		}
	}

	//----------------------------------------------------------------------------	
	public void storeData( GenieParameterData gp, DataInterface di, int simIndex )
	{
		for ( int i=0; i < phasedData.length; i++ ) storeGeneData(gp,di,simIndex,i);
	}
	
	//----------------------------------------------------------------------------	
	public void storeGeneData( GenieParameterData gp, DataInterface di, int simIndex, int geneIndex )
	{
		//System.out.println("Storing phased data for gene "+ geneIndex);
		int[] markerIndices = gp.getMarkerIndices(geneIndex);
		if( gp.getPhaseTogether() )
		{
			markerIndices = new int[gp.getLociCount(-1)];
			for ( int i=0; i < gp.getLociCount(-1); i++ ) markerIndices[i] = i;
		}
		String newHaplotype = HapFormatter.formatGuesses(phasedData[geneIndex],gds[geneIndex]);
		try
		{
			BufferedReader br = new BufferedReader(new StringReader(newHaplotype));
			String firstline, line1, line2, line3;
			if ( (firstline = br.readLine()) != null )
			{
				while ( (line1 = br.readLine()) != null )
				{
					String pedid = null, indid = null, besta = null, bestb = null, freq = null;
					if ( line1.startsWith("Phenotype") )
					{
						StringTokenizer st = new StringTokenizer(firstline);
						pedid = st.nextToken();
						indid = st.nextToken();
						
						if ( ( line2 = br.readLine()) == null ) System.out.println("failed to read Line 2 ");
						else if ( line2.startsWith("Best explanation") )
						{
							besta = br.readLine().trim();
							bestb = br.readLine().trim();
							freq  = br.readLine().trim().replace("%","");
						}
						
						if ( pedid != null && indid != null && besta != null && bestb != null )
						{
							parseInds(di,simIndex,pedid,indid,besta,bestb,markerIndices);
						}
					} // end if startsWith Phenotype
					else
					{
						firstline = line1;
					}
				} // end of while loop
			}
		}
		catch ( IOException e )
		{
			System.out.println ( "Failed to build new Gtype for individual" );
			System.out.println( e + "\n" + e.getMessage() );
			e.printStackTrace();
		}	
	}
	
	//----------------------------------------------------------------------------	
	public void parseInds( DataInterface di, int simIndex, String ipedid, String iindid, String besta, String bestb, int[] markerIndices )
	{
		int pedid = Integer.parseInt(ipedid);
		int indid = Integer.parseInt(iindid);
		Individual[] allInds = di.getAllInds();
		int[] dataInds = di.getDataInds();
		for ( int i = 0; i < dataInds.length; i++ )
		{
			Individual ind = allInds[dataInds[i]];
			int ped = ind.getPedIndex();
			int id = ind.getIndIndex();
			if ( ped == pedid && id == indid )
			{
				StringTokenizer a = new StringTokenizer(besta);
				StringTokenizer b = new StringTokenizer(bestb);
				int gtIter = 0;
				while ( a.hasMoreTokens() && b.hasMoreTokens() )
				{
					int a1 = Integer.parseInt(a.nextToken());
					int a2 = Integer.parseInt(b.nextToken());
					di.setPhasedData(simIndex,i,markerIndices[gtIter],a1,a2);
					gtIter++;
				}
				break;
			}
		}	
	}
}
