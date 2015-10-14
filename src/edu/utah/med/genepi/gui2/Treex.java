//package edu.utah.med.genepi.gui2;

import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class Treex extends JPanel 
                   implements TreeSelectionListener
{
  int openPaneCount = 0;
  String title = null;
  JPanel treepanel;
  private static String ADD_COMMAND = "add";
  private static String REMOVE_COMMAND = "remove";
  protected JTree tree;
  protected DefaultMutableTreeNode rootNode;
  protected DefaultTreeModel treeModel;
  DefaultMutableTreeNode newNode = null;
  private Toolkit toolkit = Toolkit.getDefaultToolkit();
 
  public Treex(String inTitle)
  {
    super(new GridLayout(1,0));
    title = inTitle;

    rootNode = new DefaultMutableTreeNode(title);
    treeModel = new DefaultTreeModel(rootNode);
    tree = new JTree(treeModel);
    tree.setUI(new BasicTreeUI());
    tree.getSelectionModel().setSelectionMode(
         TreeSelectionModel.SINGLE_TREE_SELECTION );
    tree.addTreeSelectionListener(this);
    tree.setShowsRootHandles(true);
    JScrollPane scrollPane = new JScrollPane(tree);
    tree.scrollPathToVisible(new TreePath(rootNode.getPath()));

    JScrollPane scroll = new JScrollPane(tree);
    
    add(scroll);
  }

  public DefaultMutableTreeNode getCurrentNode()
  {
    DefaultMutableTreeNode currentNode = rootNode;
    TreePath currentPath = tree.getSelectionPath();
    if ( currentPath != null )
      currentNode = (DefaultMutableTreeNode) 
                    tree.getLastSelectedPathComponent();
    System.out.println("Print currentNode " + currentNode);
    return currentNode;
  }

  public void valueChanged(TreeSelectionEvent tse)
  {
    //tree.setUI(new BasicTreeUI());
    //DefaultMutableTreeNode node = getCurrentNode();
    //if ( node == null )
    //  System.out.println("What happened?");
    //else 
    //  System.out.println("Current node is okay ");
    //tree.updateUI();
    //System.out.println("node : " + node);
  }



    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("DynamicTreeMenu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        //DynamicTreeDemo newContentPane = new DynamicTreeDemo();
        //DynamicTreeMenu newContentPane = new DynamicTreeMenu("root node");
        Treex newContentPane = new Treex("new Tree");
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
