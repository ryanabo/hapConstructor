package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.utah.med.genepi.util.Ut;


public class TreexDetailPanel extends JPanel 
                              implements ActionListener, 
                                         TreeSelectionListener
{
  int childPaneCount = 0;
  String title;
  String detailpanelname;
  JPanel treepanel, buttonPanel;
  JLayeredPane layeredPane;
  private static String ADD_COMMAND = "add";
  private static String REMOVE_COMMAND = "remove";
  private static String COMMIT_COMMAND = "commit";
  protected JTree tree;
  protected DefaultMutableTreeNode rootNode;
  protected DefaultTreeModel treeModel;
  DefaultMutableTreeNode newNode = null;
  private Toolkit toolkit = Toolkit.getDefaultToolkit();
  JButton addButton, removeButton, commitButton;
  private static final String SPKG_GUI = Ut.pkgOf(DetailPanel.class);
 
  public TreexDetailPanel(String inTitle)
  {
    super(new GridLayout(1,0));
    title = inTitle;
    /*
    MouseListener ml = new MouseAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow != -1)
        {
          if(e.getClickCount() == 1)
            mySingleClick(selRow, selPath);
          else if(e.getClickCount() == 2)
            myDoubleClick(selRow, selPath);
        }
        //tree.updateUI();
        System.out.println("mouse event");
      }
   
      public void mouseReleased(MouseEvent e)
      { System.out.println("mouse released "); }
    };
    */

    rootNode = new DefaultMutableTreeNode(title);
    treeModel = new DefaultTreeModel(rootNode);
    //treeModel.addTreeModelListener(new MyTreeModelListener());
    tree = new JTree(treeModel);
    //tree.setUI(new BasicTreeUI());
    tree.getSelectionModel().setSelectionMode(
         TreeSelectionModel.SINGLE_TREE_SELECTION );
    tree.addTreeSelectionListener(this);
    tree.addMouseListener(ml);
    tree.setShowsRootHandles(true);
    tree.setExpandsSelectedPaths(true);
    JScrollPane scrollPane = new JScrollPane(tree);

    // File List Panel
    treepanel = new JPanel();
    treepanel.setLayout(new BorderLayout());
    TitledEtched bordertitle = new TitledEtched(title + " Listing");
    treepanel.setBorder(bordertitle.title);

    buttonPanel = new JPanel();
    addButton = new JButton(ADD_COMMAND);
    removeButton = new JButton(REMOVE_COMMAND);
    commitButton = new JButton(COMMIT_COMMAND);
    addButton.setActionCommand(ADD_COMMAND);
    removeButton.setActionCommand(REMOVE_COMMAND);
    commitButton.setActionCommand(COMMIT_COMMAND);
    addButton.addActionListener(this);
    removeButton.addActionListener(this);
    commitButton.addActionListener(this);
    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);
    buttonPanel.add(commitButton);

    treepanel.add(scrollPane, BorderLayout.CENTER);
    treepanel.add(buttonPanel, BorderLayout.SOUTH);
    
    // Detail Pane
    layeredPane = new JLayeredPane();
    layeredPane.setLayout(new BorderLayout());

    // add treepanel and layeredPane to splitpane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(treepanel);
    splitPane.setRightComponent(layeredPane);

    add(splitPane);
  }

  public DetailPanelImp createDetailPanel()
                        throws Exception 
  {
    childPaneCount++;
    Object newPanel = Ut.newModule(SPKG_GUI, detailpanelname);
    DetailPanelImp detail = (DetailPanelImp) newPanel;
    detail.setParentNode(rootNode);
    detail.setTreexDetailPanel(this);
    detail.buildDetail(childPaneCount);
    DefaultMutableTreeNode detailNode = new DefaultMutableTreeNode
                                       (detail,
                                        detail.getAllowsChildren() );
  
    detail.setMyNode(detailNode);
    DefaultMutableTreeNode node = addNode(rootNode, detailNode);
    return detail;
  }

  public int getChildPaneCount()
  {
    return childPaneCount;
  }

  /** add new node to tree */
  public DefaultMutableTreeNode addNode ( Object obj, boolean allowsChildren )
  {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(obj,
                                                             allowsChildren);
    return addNode(node);
  }

  public DefaultMutableTreeNode addNode ( DefaultMutableTreeNode node ) 
  { 
    DefaultMutableTreeNode parentNode = null;
    TreePath parentPath = tree.getSelectionPath();
    if ( parentPath == null )
      parentNode = rootNode;
    else
      parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
    // check whether parentNode allow children
    if ( parentNode.getAllowsChildren() )
      return addNode( parentNode, node );
    else 
      return null;
    //return addNode(rootNode, node);
  }

  public DefaultMutableTreeNode addNode ( DefaultMutableTreeNode parent,
                                          DefaultMutableTreeNode child )
  {
    if ( parent == null )
      parent = rootNode;
    try
    {
      treeModel.insertNodeInto(child, parent, parent.getChildCount()); 
    } 
    catch (Exception ee) 
    { System.err.println("Error : " + ee.getMessage());}
    tree.setModel(treeModel);
    tree.scrollPathToVisible(new TreePath(child.getPath()));
    DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) 
                                      tree.getLastSelectedPathComponent();
    //if ( lastNode != null )
      tree.updateUI();
    return child;
  }

  public DefaultMutableTreeNode getFirstNode()
  {
    return (DefaultMutableTreeNode) rootNode.getFirstChild();
  }
    
  /** return current selected node. */
  public DefaultMutableTreeNode getCurrentNode()
  {
    DefaultMutableTreeNode currentNode = null;
    TreePath currentPath = tree.getSelectionPath(); 
    if ( currentPath != null )
    {
      currentNode =(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    }
    else
      currentNode = rootNode;
    return currentNode;
  }
 
  public void removeCurrentNode()
  {
    DefaultMutableTreeNode currentNode = getCurrentNode();
    MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
    if (parent != null)
    {
      treeModel.removeNodeFromParent(currentNode);
      return;
    }
    // Either there was no selection, or the root was selected.
    toolkit.beep();
  }

  public void displaySelectedScreen()
  {
    DefaultMutableTreeNode selectedNode = getCurrentNode();
    displaySelectedScreen(selectedNode);
  }

  public void displaySelectedScreen(DefaultMutableTreeNode selectedNode)
  {
    if ( selectedNode != rootNode )
      displaySelectedScreen( selectedNode,
                             (DetailPanelImp) selectedNode.getUserObject());
  }
  
  public void displaySelectedScreen(DetailPanelImp screen)
  {
    displaySelectedScreen(null, screen);
  }

  public void displaySelectedScreen(DefaultMutableTreeNode selectedNode,
                                    DetailPanelImp inScreen)
  {
    JPanel screen = (JPanel) inScreen;
    screen.setVisible(true);
    screen.setOpaque(true);
    TreeNode parentNode = rootNode;
    if ( selectedNode != null && selectedNode != rootNode)
      parentNode = selectedNode.getParent();
    //if ( parentNode == rootNode )
    //{
      layeredPane.moveToFront(screen);
      layeredPane.updateUI();
      layeredPane.repaint();
    //}
    //else
    //{
    //  inScreen.displayPanel();
    //}
    //tree.updateUI();
    this.updateUI();
    this.repaint();
  }

  public DetailPanelImp[] getDetailPanel()
  {
    DetailPanelImp[] list = new DetailPanelImp[rootNode.getChildCount()];
    int i = 0 ; 
    for ( Enumeration e = rootNode.children(); e.hasMoreElements(); )
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
      list[i] = (DetailPanelImp) node.getUserObject();
      i++;
    }
    return list;
  }

  public void commitedAction()
  {}

  public void actionPerformed(ActionEvent e)
  {
    String cmd = e.getActionCommand();
    if ( ADD_COMMAND.equals(cmd) )
    {
      DetailPanelImp screen = null;
      DefaultMutableTreeNode selectedNode = getCurrentNode();      
      if ( selectedNode == rootNode )
      {
        try 
        {
          screen = createDetailPanel();
          screen.setOpaque(true);
        }
        catch ( Exception ee )
        {
          System.out.println(
             "Failed to create Detail Panel, exception : " + ee.getMessage());
        }
        if ( screen != null )
        {
          //DefaultMutableTreeNode node = addNode(screen, false );
          layeredPane.add(screen, childPaneCount);
          displaySelectedScreen(rootNode, screen);
        }
      }
      else if ( selectedNode.getAllowsChildren() )
      {
        DetailPanelImp panel = (DetailPanelImp) selectedNode.getUserObject();
        if ( !panel.getFlag() )
        {
          //each detailpanel has childname setup as a public global variable
          //String name = panel.getChildName();
          try 
          {
            screen = panel.createChildPanel();
          }
          catch ( Exception ex )
          {
            System.out.println("Error creating Child Detail Panel : " + e);
          }
          layeredPane.add(screen, screen.childPaneCount);
          displaySelectedScreen(screen);
          //screen.displayPanel();
        }
      }
    }
    else if ( REMOVE_COMMAND.equals(cmd) )
    {
      DefaultMutableTreeNode selectedNode = getCurrentNode();      
      if ( selectedNode != rootNode)
      {
        removeCurrentNode();
        DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)
                               rootNode.getFirstChild();
        displaySelectedScreen(nextNode);
      }
      else 
        JOptionPane.showMessageDialog(this, "Can not remove this last Component!");
    }
    else if ( COMMIT_COMMAND.equals(cmd) )
    {
      commitedAction();
    }
  }

  MouseListener ml = new MouseAdapter() 
  {
    /*
    public void mousePressed(MouseEvent e) 
    {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if(selRow != -1) 
      {
        if(e.getClickCount() == 1) 
          mySingleClick(selRow, selPath);
        else if(e.getClickCount() == 2) 
          myDoubleClick(selRow, selPath);
      }
    }
    */
  };  

  public void valueChanged(TreeSelectionEvent tse)
  {
    DefaultMutableTreeNode node = getCurrentNode();
    if ( node != rootNode )
    {
      DetailPanelImp panel = (DetailPanelImp) node.getUserObject();
      TreeNode parentNode = node.getParent();
      //if ( parentNode != rootNode )
      //  panel.displayPanel();
      //else
        displaySelectedScreen(node, panel);
      /*
      System.out.println("in valueChanged");
      if ( node.getAllowsChildren() )
      {
        System.out.println("after panel.displayPanel  and allows children : " + panel);
      }
      else 
      {
        System.out.println("in else after displaySelectedScreen");
      }
      */
    }
  }

  /*
  public void mousePressed(MouseEvent me)
  {}

  public void mouseExited(MouseEvent me)
  {}

  public void mouseEntered(MouseEvent me)
  {}

  public void mouseReleased(MouseEvent me)
  {System.out.println("mouse release");}

  public void mouseClicked(MouseEvent me)
  {}


  class MyTreeModelListener implements TreeModelListener 
  {
    public void treeNodesChanged(TreeModelEvent e) 
    {
      DefaultMutableTreeNode node;
      node = (DefaultMutableTreeNode)
               (e.getTreePath().getLastPathComponent());

       // If the event lists children, then the changed
       // node is the child of the node we've already
       // gotten.  Otherwise, the changed node and the
       // specified node are the same.

      try 
      {
        int index = e.getChildIndices()[0];
        node = (DefaultMutableTreeNode)
                 (node.getChildAt(index));
      } 
      catch (NullPointerException npx) 
      {}
    }

    public void treeNodesInserted(TreeModelEvent e) {}
    public void treeNodesRemoved(TreeModelEvent e) {}
    public void treeStructureChanged(TreeModelEvent e) {}
  }
  */

}
