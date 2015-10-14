package edu.utah.med.genepi.gui2;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.utah.med.genepi.app.rgen.MainManager;
import edu.utah.med.genepi.util.Ut;

public class GenieGUI extends JPanel 
{
  JTabbedPane tabbedPane;
  GlobalTab globaltab;
  String pkg, title;
  HashMap<String, TabImp> tabmap;

  public GenieGUI() 
  {
    title = "Genie " + MainManager.application_version;
    tabmap = new HashMap<String, TabImp>();
    pkg = Ut.pkgOf(GlobalTab.class);
    tabbedPane = new JTabbedPane();
    addTab("Global");

    //Add the tabbed pane to this panel.
    add(tabbedPane);
     
    //The following line enables to use scrolling tabs.
    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
  }
  
  public String toString()
  { 
    return title;
  }

  public void addTab(String tabname)
  {
    try
    {
      String suffix = "Tab";
      Object newtab = Ut.newModule(pkg, tabname, suffix);
      TabImp tab = (TabImp) newtab;
      tab.build(this);
      tabmap.put(tabname, tab);
      tabbedPane.addTab(tabname, tab);
      tabbedPane.setSelectedComponent(tab);
      tabbedPane.updateUI();
      updateUI();
    }
    catch ( Exception e )
    {
      System.out.println("Error creating new Tab for the Panel : "+ e.getMessage());
      System.exit(0);
    }
  }

  public Tab getTab(String tabname)
  {
    return tabmap.get(tabname);
  }
    
  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event dispatch thread.
   */
  private static void createAndShowGUI() 
  {
    //Create and set up the window.
    JPanel newContentPane = new GenieGUI();
    newContentPane.setOpaque(true); //content panes must be opaque
    String title = newContentPane.toString();
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    //Create and set up the content pane.
    frame.setContentPane(newContentPane);
      
    //Display the window.
    frame.pack();
    frame.setSize(new Dimension(950, 800));
    //frame.setResizable(false);
    frame.setVisible(true);
  }
    
  public static void main(String[] args)
  {
    //Turn off metal's use of bold fonts
    //UIManager.put("swing.boldMetal", Boolean.FALSE);
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    SwingUtilities.invokeLater(
        new Runnable()
        {
          public void run()
          {
            createAndShowGUI();
          }
        });
  }
}
