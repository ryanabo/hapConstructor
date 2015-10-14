package edu.utah.med.genepi.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Main 
{
  public static int nLocus;
  public static String[] locusName;

  public Main () throws IOException
  {
    parsefile();
  }

  private static void parsefile ()
          throws IOException
  {
    File file;
    JFileChooser filechooser = new JFileChooser();
    int returnVal = filechooser.showOpenDialog(null);
    //if ( filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
    if ( returnVal == JFileChooser.APPROVE_OPTION)
    {
      file = filechooser.getSelectedFile();

    BufferedReader in = new BufferedReader(new FileReader(file));
    String line = in.readLine();
    String[] tokens = line.trim().split("\\s+");

    if (line.matches("^\\s*\\d+\\s+\\d+.+$"))
    {
      nLocus = tokens.length - 6;
      locusName = new String[nLocus];
      for ( int i = 0; i < nLocus; i++ )
        locusName[i] = tokens[i+7];
    }
    else 
    {
      nLocus = (int) ((tokens.length - 6 ) / 2);
      locusName = new String[nLocus];
    }

    in.close();
    }
    filechooser.setVisible(false);
  }

  private static void createAndShowGUI()
  {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    PedGenieFrame pedgenieFrame = new PedGenieFrame(nLocus, locusName);
    pedgenieFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Display the window.
    pedgenieFrame.setVisible(true);
  }

  public static void main(String[] args) 
  {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater 
    ( new Runnable() 
      {
        public void run() 
        {
          createAndShowGUI();
        }
      }
    );
  }
}
