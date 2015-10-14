package edu.utah.med.genepi.gui2;
 
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class DisplayProcess extends JPanel
			   implements ActionListener
{
  long sec;
  JButton button;
  BufferedReader err = null;
  BufferedReader in = null;
  private static void createUI()
  {
    JFrame frame = new JFrame("Process Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel panel = new DisplayProcess();
    frame.setContentPane(panel);
    frame.pack();
    frame.setVisible(true);
    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  public DisplayProcess() 
  {
      super();
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(900,500));
      JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      JPanel top = new JPanel();
      button = new JButton("Press");
      button.addActionListener(this);
      top.add(button);
      JScrollPane bottom = new JScrollPane();
      JTextArea area = new JTextArea();
      area.setEditable(false);
      PrintStream ps = new PrintStream(new TextAreaOutputStream(area));
      System.setOut(ps);
      System.setErr(ps);
      bottom.setViewportView(area);
      splitpane.setTopComponent(top);
      splitpane.setBottomComponent(bottom);
      splitpane.setOneTouchExpandable(true);
      add(splitpane);
  }

  public class ProcessThread extends Thread
  {
    public void run()
    {
      System.out.println("inside a process");
      try 
      {
        String ss = "java -jar /export/home/jathine/classes/Genie2.6.3.jar pedgenie /export/home/jathine/data/metatemp.rgen";
        Process p = Runtime.getRuntime().exec(ss);
        err = new BufferedReader( new InputStreamReader(p.getErrorStream()));
        in = new BufferedReader( new InputStreamReader(p.getInputStream()));
      }
      catch ( Exception e )
      {
          System.out.println("Error executing Genie : " + e.getMessage());
      }
      if ( err != null )
        System.out.println("err is not null");
      if ( in != null )
        System.out.println("in is not null");
    }
  }  // end ProcessThread class

  public class DisplayThread extends Thread
  {
    public void run()
    { 
      while ( this.isAlive() )
      { System.out.println(".."); }
    }
  } // end DisplayThread
  public void actionPerformed ( ActionEvent ae )
  {
      Object source = ae.getSource();
      if ( source == button )
      {
        System.out.println("Executing Genie ..... ");
        DisplayThread dth = new DisplayThread();
        dth.run();
        ProcessThread pth = new ProcessThread();
        pth.yield();
        pth.run();
        String line;
        boolean display = true;
        try
        {
          pth.join();
          pth.interrupt();
          System.out.println("process completed");
          if ( err != null )
          {
            line = err.readLine();
            if ( (line = err.readLine()) != null )
            {
              display = false;
              System.out.println("Error executing Genie .....");
              System.out.println(line);
              while ( (line = err.readLine()) != null )
              {
                System.out.println(line);
              }       
            }
          }
          if ( display && in != null )
          {
            System.out.println("in has something");
            while ( ( line = in.readLine()) != null )
            {
              System.out.println(line);
            }
          }
        }
        catch ( Exception e)
        {
          System.out.println("Failed to read InputStream");
        }
      }
  }       

  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(
	new Runnable()
	{
	  public void run()
  	  {
	    createUI();
	  }
	} );
  }
}
