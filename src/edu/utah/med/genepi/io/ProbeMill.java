//******************************************************************************
// ProbeMill.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.CCStatRun;

//==============================================================================
public class ProbeMill {

  //----------------------------------------------------------------------------
  public static CCStatRun.Probe newStatProbe(final int analysis_index,
                                             final int stat_index)
  {
    try {
      return new CCStatRun.Probe() {
        private final PrintWriter pwOut = new PrintWriter(
          new FileWriter(
            File.createTempFile(
              "a" + (analysis_index + 1) + "s" + (stat_index + 1) + "-", ".log",
              new File(System.getProperty("user.dir"))
            )
          )
        );
        public void logTableContents(CCAnalysis.Table[] t) 
        {
          for ( int i = 0; i < t.length; i++ )
            logTableContents(t[i]);
        }

        public void logTableContents(CCAnalysis.Table t) 
        {
          for (int irow = 0; irow < 2; ++irow)
          {
            CCAnalysis.Table.Row row = t.getRowAt(irow);
            //int[]                counts = row.getCells();
            Number[]                counts = row.getCells();

            pwOut.print(row.getPtype() + ": ");
            for (int icol = 0; icol < counts.length; ++icol)
              pwOut.print(counts[icol].intValue() + " ");
            pwOut.println();
          }
        }

        public void logStatResult(CCStat.Result r) {
          pwOut.println("Statistic = " + r);
        }
        public void closeLog() { pwOut.close(); }
      };
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
