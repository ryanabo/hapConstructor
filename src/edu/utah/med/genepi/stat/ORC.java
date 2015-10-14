//*****************************************************************************
// ORCompTrackerExt.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import java.util.Vector;
//=============================================================================
public class ORCompTrackerExt extends ORComparisonTracker 
{
  public Vector<Vector<Double>> vRatio;
  //---------------------------------------------------------------------------
  public void setStartingResult(CCStat.Result r0)
  {
    super.setStartingResult(r0);
    vRatio = new Vector<Vector<Double>>();
    for ( int i = 0; i < r0Vals.length; i++ )
    {
      vRatio.add(new Vector<Double>());
    }
  }

  //-------------------------------------------------------------------------
  public void compareResultAtX(CCStat.Result rx)
  {
    double[] rxVals = rx.doubleValues();
    for (int i = 0, n = rxVals.length; i < n; ++i)
    {
      double obs  = r0Vals[i];
      double sim  = rxVals[i];
      double obs1 = 1 / obs;
      Vector<Double> vCI = (Vector<Double>) vRatio.get(i); 
      if ( Double.isNaN(sim) )
        notval[i]++;
      else
      {
        System.out.println("add sim : " + rxVals[i]);
        if ( obs > 1.0 && sim >= obs )
          hitCounts[i]++;
        else if ( obs < 1.0 && sim <= obs )
          hitCounts[i]++;
        comparisonCount[i]++;

        if ( vCI.size() != 0 )
        {
          int a = vCI.size();
          for (int j = 0; j < a; j++ )
          {
            if ( rxVals[i] > ((Double) vCI.get(j)).doubleValue() )
            {
              if ( j == a - 1 )
              {
                vCI.add(new Double(rxVals[i]));
                break;
              }
            }
            else
            {
              vCI.add( j, new Double(rxVals[i]) );
              break;
            }
          }
        }
        else 
        {
          vCI.add(new Double(rxVals[i]));
        }
      }
      vRatio.setElementAt(vCI, i);
    }
  }

  //-------------------------------------------------------------------------
  public CCStat.Result getConfidenceIntervals()
  {
    String warning = "Warning : 95% CI is uninformative, likely due to sparse data or insufficient simulations";
    int nRatio = 1;
    if ( vRatio == null )
    {
      String[][]  ciPair = new String[1][2];
      ciPair[0][0] = "*";
      ciPair[0][1] = "*";
      return new ResultImp.PairSeries(ciPair);
    }
    if ( vRatio.size() > 0 )
    {
      nRatio = vRatio.size();
    }

    String[][]  ciPair = new String[nRatio][2];
    for ( int i = 0; i < nRatio; i++ )
    {
      ciPair[i][0] = "-";
      ciPair[i][1] = "-";
    }

    System.out.println("nRatio : " + nRatio  );
    if ( nRatio != 0 )
    {
      for ( int i = 0; i < nRatio; i++ )
      {
        System.out.println("r0Vals[i] " + r0Vals[i] );
        double firstCI, secondCI;
        if ( Double.isNaN(r0Vals[i]) )
        {
          messages = warning;
        }
        else 
        {
          firstCI = secondCI = 0.0;
          Vector<Double> vCI = (Vector<Double>) vRatio.get(i);
          System.out.println("vCI.size " + vCI.size() );
          if ( vCI.size() > 1 )
          {
            int ci1st = (new Double(comparisonCount[i] * 0.025)).intValue();
            for ( int m = 0 ; m < vCI.size(); m++) 
            System.out.println(" vCI["+m+"] " + vCI.get(m));
            System.out.println("vCI finished " );
            System.out.println("ci1st : " + ci1st);
            if ( ci1st > 0 )
              ci1st -= 1;
            int ci2ndNext = (new Double(comparisonCount[i] * 0.975)).intValue();
            int ci2nd = ci2ndNext - 1;
             System.out.println("ci2nd : " + ci2nd);

            double valFloat = (comparisonCount[i] * 0.025) - ci1st;
            double val2nd    = ((Double) vCI.get(ci1st)).doubleValue();
            double val3rd    = ((Double) vCI.get(ci1st + 1)).doubleValue();
            double val97th   = ((Double) vCI.get(ci2nd)).doubleValue();
            double val98th   = ((Double) vCI.get(ci2ndNext)).doubleValue();
            double first     = val2nd + valFloat * (val3rd - val2nd) ;
            double second    = val97th + valFloat * (val98th - val97th);
            System.out.println("val2nd - vCI.get(ci1st) : " + val2nd);
            System.out.println("val3rd - vCI.get(ci1st+ 1) : " + val3rd);
            System.out.println("val97 - vCI.get(ci2nd) : " + val97th);
            System.out.println("val97 - vCI.get(ci2ndNext) : " + val98th);
            System.out.println("float : " + valFloat);
            System.out.println("first : " + first + " second : " + second);
            
            if ( r0Vals[i] < 1.0 )
            {
              firstCI = r0Vals[i] / first;
              secondCI = r0Vals[i] * first;
            }
            else if ( r0Vals[i] > 1.0 )
            {
              firstCI = r0Vals[i] / second;
              secondCI = r0Vals[i] * second;
            }
            else if ( r0Vals[i] == 1.0 )
            {
              firstCI = first;
              secondCI = second;
            }
            System.out.println("firstCI : " + firstCI + " secondCI : " + secondCI);
            if ( Double.isNaN(firstCI) )
            {  
              messages = warning;
              if ( Double.isNaN(secondCI) )
              {
                ciPair[i][0] = "0";
                ciPair[i][1] = "infinity";
              }
              else
              {
                ciPair[i][0] = (new ResultImp.Real(secondCI)).toString();
                ciPair[i][1] = "infinity";
              }
            }
            else if ( Double.isNaN(secondCI) )
            {
              messages = warning;
              ciPair[i][0] = (new ResultImp.Real(firstCI)).toString();
              ciPair[i][1] = "infinity";
            }  
            else if ( firstCI > secondCI )
            {
              System.out.println("both are result!");
              if ( Double.isInfinite(firstCI) )
                ciPair[i][1] =  "infinity";
              else 
                ciPair[i][1] = (new ResultImp.Real(firstCI)).toString(); 
              if ( Double.isInfinite(secondCI))
                ciPair[i][0] = "infinity";
              else
                ciPair[i][0] = (new ResultImp.Real(secondCI)).toString();
            }
            else 
            {
              if ( Double.isInfinite(firstCI) )
                ciPair[i][0] = "infinity";
              else
                ciPair[i][0] = (new ResultImp.Real(firstCI)).toString();
              if ( Double.isInfinite(secondCI))
                ciPair[i][1] = "infinity";
              else
                ciPair[i][1] = (new ResultImp.Real(secondCI)).toString(); 
            }
          }
        }
      }
    }
    return new ResultImp.PairSeries(ciPair);
  }


  public Vector<Vector<Double>> getSimulatedResults()
  {
    return vRatio;
  }
  
  public int getDF()
  { return 4;}
}
