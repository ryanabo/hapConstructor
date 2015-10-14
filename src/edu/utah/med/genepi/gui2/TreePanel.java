//package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TreePanel extends JPanel 
                       implements ActionListener
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
  JButton addButton, removeButton;
  Treex treeX;
 
  public TreePanel()
  {
    super(new BorderLayout());

    treeX = new Treex("treeX");

    JPanel buttonPanel = new JPanel();
    addButton = new JButton(ADD_COMMAND);
    removeButton = new JButton(REMOVE_COMMAND);
    addButton.setActionCommand(ADD_COMMAND);
    removeButton.setActionCommand(REMOVE_COMMAND);
    addButton.addActionListener(this);
    removeButton.addActionListener(this);
    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);

    add(treeX, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }


  public void actionPerformed(ActionEvent e)
  {
  }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Tree Panel X");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        //DynamicTreeDemo newContentPane = new DynamicTreeDemo();
        //DynamicTreeMenu newContentPane = new DynamicTreeMenu("root node");
        TreePanel newContentPane = new TreePanel();
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
