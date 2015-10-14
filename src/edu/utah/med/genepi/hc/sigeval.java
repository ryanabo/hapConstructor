package edu.utah.med.genepi.hc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class sigeval { 

  public sigeval()
  {
    // Stream to read file
	FileInputStream fin;		

	try
	{
	  // Open an input stream
	  fin = new FileInputStream ("myfile.txt");

	  // Read a line of text
	  BufferedReader d = new BufferedReader(new InputStreamReader(fin));

	  System.out.println( d.readLine());

	  // Close our input stream
	  fin.close();		
	}
	// Catches any error conditions
	catch (IOException e)
	{
	  System.err.println ("Unable to read from file");
	  System.exit(-1);
	}  
  }
  
}
