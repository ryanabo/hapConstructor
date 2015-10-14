package edu.utah.med.genepi.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileParser 
{
  public static int nLocus;
  public static String[] locusName;

  public FileParser( File f ) throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(f));
    String line = in.readLine();
    String[] tokens = line.trim().split("\\s+");

    //if (line.matches("^\\s*\\d+\\s+\\d+.+$"))
    if (tokens[0].matches("[A-Z]+.+$"))
    {
      nLocus = tokens.length - 7;
      locusName = new String[nLocus];
      for ( int i = 0; i < nLocus; i++ )
        locusName[i] = tokens[i+7];
    }
    else
    {
      nLocus = (int) ((tokens.length - 7 ) / 2);
      locusName = new String[nLocus];
    }

    in.close();
  }

  public int getNumLocus()
  { return nLocus; }

  public String[] getLocusName()
  { return locusName; }

}
