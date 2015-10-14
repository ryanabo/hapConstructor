package edu.utah.med.genepi.hc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class resultManager {
  
  public resultManager ()
  {
  }
	
  public static void readResults () throws IOException 
  {
    Scanner s = null;
    double sum = 0;
    try 
    {
      s = new Scanner( new BufferedReader(new FileReader("usnumbers.txt")) );
      while ( s.hasNext() ) 
      {
        if ( s.hasNextDouble() )
        {
          sum += s.nextDouble();
        } 
        else
        {
          s.next();
        }   
      }
    } finally { s.close(); }
    System.out.println(sum);
  }
  
}
