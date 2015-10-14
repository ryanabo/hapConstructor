//******************************************************************************
//MCLDTop.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import alun.genepi.LinkageVariables;
import alun.genio.GeneticDataSource;
import alun.mcld.DecomposableSearch;
import alun.mcld.GraphMHScheme;
import alun.mcld.JointScheme;
import alun.mcld.LDSampler;
import edu.utah.med.genepi.gchapext.GeneticDataSourceImp;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeBuilder;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;
//==============================================================================
public class MCLDTop extends HapMCTopSim 
{

  //----------------------------------------------------------------------------
  public MCLDTop()
  {
  }

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
   PedQuery.Predicate[] querySample = new PedQuery.Predicate[] {
                                    PedQuery.IS_MCLD};
   setDataSource(querySample);
  }
  
  //----------------------------------------------------------------------------  
  public void setDataSource(PedQuery.Predicate[] querySample)
  {
    Indiv.GtSelector obs = Indiv.GtSelector.OBS;
	// only preprocess haplotype data
	int nLoci = gdef.getLocusCount();
	if ( nLoci > 1 )
	{
	  for ( int i = 0; i < study.length; i++ )
	  {
	    PedData peddata = study[i].getPedData();
	    Indiv[] sampleIndiv = peddata.getIndividuals(pInSample, 50);
	    GeneticDataSource gds = new GeneticDataSourceImp( study[i],
	                                                      sampleIndiv,
	                                                      obs,
	                                                      0,
	                                                      gdef );
	    // run McLinkLD
	    int a1 = gds.indAllele(2, 1, 0);
	    int a2 = gds.indAllele(2, 1, 1);
	    String s = mclinkld(gds);
	    
        for ( int j = 0 ; j < querySample.length; j++ )
	    {
          // Sample individuals specific to related data.
	      sampleIndiv = peddata.getIndividuals(querySample[j], 50);
	      GeneticDataSource sampleGds = new GeneticDataSourceImp( study[i], 
	                                                              sampleIndiv,
	                                                              obs,
	                                                              -1,
	                                                              gdef );
	      setPhasedData ( sampleGds, sampleIndiv, obs, -1, gdef,s);
	      study[i].setFreqDataSet( getGCHapFreq(sampleGds) );
	    }
      }
	} 
  }
  
  //----------------------------------------------------------
  public String mclinkld ( GeneticDataSource gds )
  {
	int n_samples = 1000;
	double errorprob = 0.001;
	int metropergibbs = 1000;
	
    LinkageVariables vars = new LinkageVariables(gds,errorprob);    
	LDSampler haps = new LDSampler(vars,0);
	GraphMHScheme sch = new DecomposableSearch(haps.getLoci());
	JointScheme mc = new JointScheme(haps,sch);
	
//	alun.genepi.Genotype gt = vars.getGenotype(2,1);
//	int p = gt.pat();
//	int m = gt.mat();
//    int a1 = gds.indAllele(2, 1, 0);
//    int a2 = gds.indAllele(2, 1, 1);
//    for ( int i = 0; i < vars.nIndividuals(); i++)
//    {
//      String ind_id = gds.id(i);    	
//  	  String paternal_hap = "";
//	  String maternal_hap = "";
//	  
//	  for (int j=0; j < vars.nLoci(); j++)
//	  {
//        alun.genepi.Genotype g = vars.getGenotype(i, j);
//	    paternal_hap = paternal_hap + " " + (((int) g.pat()) + 1);
//		maternal_hap = maternal_hap + " " + (((int) g.mat()) + 1);
//      }
//    }  
	
	//System.err.println("BIC parameter penalty = "+((Object) mc).getParameterPenalty());
    double t = 1.0;
    for (int i=1; i<=n_samples; i++)
	{
	  t *= 0.999;
	  for (int j=0; j<metropergibbs; j++)
	    mc.metropolisUpdate(t);
	  System.err.print("'");
	  mc.gibbsUpdate(t);
	  System.err.print("'");
	}
	// Maximize the likelihood for all ind haplotypes
	mc.reconstruct();
	
	StringBuffer s = new StringBuffer();
    for ( int i = 0; i < vars.nIndividuals(); i++)
    {
      gds.id(i);
	  
	  s.append("\n");
	  s.append( gds.id(i)+"\n");
	  s.append("Phenotype:\n");
	  
	  String paternal_hap = "";
	  String maternal_hap = "";
	  for (int j=0; j < vars.nLoci(); j++)
	  {
        alun.genepi.Genotype g = vars.getGenotype(i, j);
	    paternal_hap = paternal_hap + " " + (((int) g.pat()) + 1);
		maternal_hap = maternal_hap + " " + (((int) g.mat()) + 1);
      }
	  
	  s.append("Best explanation\n");
	  s.append("\t"+paternal_hap);
	  s.append("\n");
	  s.append("\t"+maternal_hap);
	  s.append("\n");
	  s.append("\t"+100+"%\n"); 
    }
    return s.toString();
  }
  
//----------------------------------------------------------------------------
  public void setPhasedData ( GeneticDataSource gds,
                              Indiv[] indiv,
                              Indiv.GtSelector selector,
                              int index,
                              GDef gdef, String s )
  {

    GtypeBuilder gt_builder = gdef.getGtypeBuilder();

    String newHaplotype = s;

    // need to setup a vector for pedid, indid, besta, bestb ? freq
    try
    {
      BufferedReader br = new BufferedReader( new StringReader(newHaplotype) );
      String firstline, line1, line2, line3;

      if ( (firstline = br.readLine()) != null )
      {
        while ( (line1 = br.readLine()) != null )
        {
          String pedid = null, indid = null,
                 besta = null, bestb = null,
                 freq = null;
          if ( line1.startsWith("Phenotype") )
          {
            StringTokenizer st = new StringTokenizer(firstline);
            pedid = st.nextToken();
            indid = st.nextToken();
            if ( ( line2 = br.readLine()) == null )
              System.out.println("failed to read Line 2 ");
            else if ( line2.startsWith("Best explanation") )
            {
              besta = br.readLine().trim();
              bestb = br.readLine().trim();
              freq  = br.readLine().trim().replace("%","");
            }
            if ( pedid != null && indid != null &&
                 besta != null && bestb != null )
            {
              for ( int j = 0; j < indiv.length; j++ )
              {
                String ped = indiv[j].getPedigree().getPed_id();
                String id = indiv[j].getID();
                if ( ped.equals(pedid) && id.equals(indid) )
                {
                  StringTokenizer a = new StringTokenizer(besta);
                  StringTokenizer b = new StringTokenizer(bestb);
                  while ( a.hasMoreTokens() && b.hasMoreTokens() )
                  {
                    String a1 = a.nextToken();
                    String a2 = b.nextToken();
                    gt_builder.addAllelePair( a1, a2 );
                  }
                  try
                  {
                    gt_builder.addHaploFrequency(
                               Double.valueOf(freq).doubleValue());
                    Gtype gt = gt_builder.buildNext();
                    if ( selector == Indiv.GtSelector.SIM )
                      indiv[j].setSimulatedGtype( gt, index );
                    else 
                      indiv[j].setGtype( gt, selector, true );
                  }
                  catch ( GEException g )
                  {
                    System.out.println("Failed to set Gtype for pedigree: " + ped + " indiv: " + id );
                  }
                  break;
                }
              } // end indiv for loop
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
  
  
}