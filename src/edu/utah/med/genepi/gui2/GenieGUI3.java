package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
//import javax.swing.tree.DefaultMutableTreeNode;

public class GenieGUI extends JPanel
                      implements ActionListener
{
  MainPanel mainPanel;
  //private DynamicTree treePanel;

  public GenieGUI()
  { 
    super(new BorderLayout());
    //treePanel = new DynamicTree();
    //populateTree(treePanel);
    MainPanel mainPanel = new MainPanel();
    //add(treePanel, BorderLayout.WEST);
    add(mainPanel, BorderLayout.CENTER);
  }

  /*
  public void populateTree(DynamicTree treePanel)
  {
    String globalName = new String("Global Variables");
    String simProgName = new String("Simulation Programs");
    String datafileName = new String("Data File");
    JButton button = new JButton("aba");
    DefaultMutableTreeNode global, simProg, datafile, newitem;
    global = treePanel.addObject(null, globalName);
    simProg = treePanel.addObject(null, simProgName);
    datafile = treePanel.addObject(null, datafileName);
    newitem = treePanel.addObject(null, button);
  }

  public void mouseClicked (MouseEvent me)
  {
    mainPanel.displayPanel(new String("StatisticPanel"));
    repaint(); }

  public void mouseEntered (MouseEvent me)
  {}
  public void mouseExited (MouseEvent me)
  {}
  public void mousePressed (MouseEvent me)
  {}
  public void mouseReleased (MouseEvent me)
  {}
  private static void createAndShowGUI()
  {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("Genie .rgen parameter file setup");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GenieGUI contentPane = new GenieGUI();
    contentPane.setOpaque(true);
    frame.setContentPane(contentPane);
    frame.pack();
    frame.setVisible(true);
  }
  */
  
  public static void main(String[] args)
  {
    javax.swing.SwingUtilities.invokeLater( new Runnable()
    {
      public void run()
      { 
        createAndShowGUI();
      }
    });
  }
}
