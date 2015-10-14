package edu.utah.med.genepi.hc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Evalsigs {
  public Map ps = new HashMap();
  public Map ps2model = new HashMap();
  public double[] obs_cdf;
  public double[] sims_cdf;
  
  //------------------------------------------
  public void readfile(String filename)
  {
	String type = "obs";
	if (filename.equals("all_sims.final")) 
	{
	  type = "sims";
	}
    StringBuffer contents = new StringBuffer();

	//declared here only to make visible to finally clause
	BufferedReader input = null;
	try
	{
	  //use buffering, reading one line at a time
	  //FileReader always assumes default encoding is OK!
	  input = new BufferedReader( new FileReader(filename) );
	  String line = null; //not declared within while loop
	  while (( line = input.readLine()) != null)
	  {
	    //System.out.println(line);
	    process_line(line,type);
	  }
	}
	catch (FileNotFoundException ex) {
	  ex.printStackTrace();
	}
	catch (IOException ex){
	  ex.printStackTrace();
	}	  
  }
  
  //------------------------------------------------------
  private void store_models ( String model, Double p)
  {
    Set s = new HashSet();
    String key = p+"";
    if( ps2model.containsKey(key) )
    {
      s = (Set) ps2model.get(key);
      s.add(model);
    }
    else
    {
      s.add(model);
      ps2model.put(key, s);
    }	  
  }
  
  //------------------------------------------------------
  public String retrieve_models(Double p)
  {
	String models_out = "";
    Set s = (Set) ps2model.get(p+"");
    if (s.size() > 1)
    {
      Iterator it = s.iterator();
      models_out = (String) it.next();
      s.remove(models_out);
    }
    else
    {
      Iterator it = s.iterator();
      models_out = (String) it.next();
    }
    return models_out;
  }

  //-----------------------------------------------------
  public void process_line(String line, String type)
  {
    String[] split_line = line.split(":");
    String lociset = split_line[1];
    int step = lociset.split("-").length;
    List map_ps = new ArrayList();
    String simrun = split_line[0];
    
    if ( ps.containsKey(simrun) )
    {
      map_ps = (List) ps.get(simrun);
    }

    String model = split_line[2];
    String stat = split_line[3];
    //System.out.println(line);
    if(stat.indexOf("Odd") > -1)
    {
      //Get the number of columns 
      int numcols = (split_line.length - 4) / 3;
      for( int i = 0; i < numcols; i++ )
      {
        int lineloc = 3 + (3 * (i+1));
        double p = Double.parseDouble(split_line[lineloc]);
//        System.out.println("p-val:" + split_line[lineloc]);
//        System.out.println("p-val:" + p);
        map_ps.add(p);
        if ( simrun.equals("-1") )
        {
          String key = line.substring(3,line.lastIndexOf(":"));
//          System.out.println(key);
          store_models(key,p);
        }
      }
    }
    else if ( line.indexOf("Interaction") > -1 )
    {
    	double p = Double.parseDouble(split_line[7]);
    	map_ps.add(p);
    	if ( simrun.equals("-1") )
    	{
    		String key = line.substring(3,line.lastIndexOf(":"));
    		store_models(key,p);
    	}
    }
    else if ( line.indexOf("IntxLD") > -1 )
    {
    	double p = Double.parseDouble(split_line[5]);
    	map_ps.add(p);
    	if ( simrun.equals("-1") )
    	{
    		String key = line.substring(3,line.lastIndexOf(":"));
    		store_models(key,p);
    	}
    }
    else if ( stat.indexOf("Chi-squared") > -1 || stat.indexOf("Chi-Squared") > -1 )
    {
      int lineloc = split_line.length - 1;
      double p = Double.parseDouble(split_line[lineloc]);
      map_ps.add(p);
      if ( simrun.equals("-1") )
      {      
        String key = line.substring(3,line.lastIndexOf(":"));
//        System.out.println(key);
        store_models(key,p);    	  
        //store_models(model,p);
      }
    }
    ps.put(simrun, map_ps);
  }
   
  //------------------------------------------------
  private int[] rank( List<Double> l, String type)
  {
	Double[] ll = l.toArray(new Double[0]);
	int len = ll.length;
	double[] cdf = new double[len];
	int[] ranks = new int[len];
	
	// Put the first rank in, which is the last item. This takes the highest rank.
	double previous_pv = ll[len-1];
	// Start from the back and go forward. If the next item is the same as the previous
	// p-value, then set its rank to the same as the previous item.
    for ( int i =(len-1); i > -1; i-- )
    {
      if ( i == 12)
      {
        int p = 0;
      }
      double pv = ll[i];
      int pos = i;
      if(pv == previous_pv && i != (len-1))
      {
        ranks[i] = ranks[i+1];
      }
      else
      {
        ranks[i] = i+1;
        previous_pv = pv;
      }
      double r = ranks[i];
      double dl = len;
      double res = r / dl;
      cdf[i] = res;
    }
    
	if ( type.equals("obs") )
	{
	  obs_cdf = cdf;
	}
	else
	{
	  sims_cdf = cdf;
	}
    
    return ranks;
  }
  
  //--------------------------------------------------
  private List<Double> query_sims( int rank, int simrun )
  {
    List sim_ps = new ArrayList();
    String sr = simrun+"";
    if ( ps.containsKey(sr) )
    {
      List simp_set = (List) ps.get(sr);
      Collections.sort(simp_set);
      // If simp_set is small than the rank of the observed p-value, then use what is available.
      if ( simp_set.size() < rank )
      {
        rank = simp_set.size();
      }
      
      for ( int i = 0; i < rank; i++ )
      {
        sim_ps.add((Double) simp_set.get(i));	
      }      
    }
    return sim_ps;
  }
  
  private int query_rate( double obs_val, int rank, int simrun )
  {
    int t = 0;
    List sim_ps = new ArrayList();
    String sr = simrun+"";
    if ( ps.containsKey(sr) )
    {
      List simp_set = (List) ps.get(sr);
      Collections.sort(simp_set);
      
      for ( int i = 0; i < simp_set.size(); i++ )
      {
        if ( (Double) simp_set.get(i) <= obs_val && i <= rank )
        {
          t = t + 1;
        }
      }
    }
    return t;
  }
  
  //--------------------------------
  public double[][] efdr()
  {
	List obs = (List) ps.get("-1");
	Collections.sort(obs);
    int[] rank_obs = rank(obs,"obs");
    
    double[][] eFDRs = new double[obs.size()][4];
    double previous_q = 1.0;
    for ( int i = ( obs.size() - 1 ); i > -1; i-- )
    {
      System.out.println(i);
      if(i == 18){
        int bb = 0;
      }
      double obs_val = (Double) obs.get(i);
      int obs_rank = rank_obs[i];
      List sim_ps = new ArrayList();
      
      int rate = 0;
      
      for ( int j =0; j < ps.keySet().size() - 1; j++ )
      {
        sim_ps.addAll(query_sims(obs_rank,j));
        rate = rate + query_rate(obs_val,obs_rank, j);
      }
            
      int emp_count = 0;
      int emp_total = sim_ps.size();
      Collections.sort(sim_ps);
      
      int it =0;
      double sim_p = (Double) sim_ps.get(it);
      while ( obs_val >= sim_p && it < (sim_ps.size() - 1 ) )
      {
        emp_count++;
        it++;
        sim_p = (Double) sim_ps.get(it);
      }
      if ( emp_count == 0 )
      {
        emp_count = 1;
      }
      if ( rate == 0 )
      {
        rate = 1;
      }      
      double q = ((Double) (emp_count*1.00)) / ((Double) (emp_total*1.00));
      if ( q > 1.0 ) 
      {
        q = 1.0;
      }
      else if ( q == 0.00 )
      {
        q = 1.00 / ( (Double) (emp_total*1.00));
      }
      if ( q > previous_q)
      {
        q = previous_q;
      }
      else
      {
        previous_q = q;
      }
      eFDRs[i][0] = q;
      eFDRs[i][1] = (Double) obs.get(i);
      eFDRs[i][2] = (Double) (emp_count*1.00);
      eFDRs[i][3] = (Double) (emp_total*1.00);
    }
    return eFDRs;
  }
  
  //-------------------------------------------------------
  public void write_efdr(double[][] qs) throws IOException
  {
	BufferedWriter out = null;

	if(! new File("significance.out").exists())
	{
	  out = new BufferedWriter(new FileWriter("significance.out"));
      out.write("eFDR value\tObserved P-value\tModels\n");
	}
	else
	{
	  out = new BufferedWriter(new FileWriter("significance.out",true));
      out.write("eFDR value\tRate\tObserved P-value\tModels\n");
	}
	
	for (int j = 0; j < qs.length; j++ )
	{
	//System.out.println(eFDRs[j] + "\n");
	  Double qval = round(qs[j][0],6);
	  Double obsval = round(qs[j][1],6);
	  //Double rate = round(qs[j][2],6);
	  String models = retrieve_models(qs[j][1]);
	  //System.out.println(models);
	  out.write(qval + "\t\t" + round(qs[j][2],1) + "/" + round(qs[j][3],1) + "\t\t" + obsval + "\t\t\t" + models + "\n");
	}
	out.close();	  
  }
  
  //-------------------------------------------------------
  public static double round(double val, int places) {
	long factor = (long)Math.pow(10,places);

	// Shift the decimal the correct number of places
	// to the right.
	val = val * factor;

	// Round to the nearest integer.
	long tmp = Math.round(val);

	// Shift the decimal the correct number of places
	// back to the left.
	return (double)tmp / factor;
  }  
}
