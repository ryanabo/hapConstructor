package alun.viewgraph;

import java.awt.Button;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditor extends MapViewer 
{
	static { className = "alun.graph.MapEditor" ; }

	public void init()
	{
		super.init();
		viewer = new MovingMouser(this);
		editor = new EditingMouser(this);
		setCoordinator(new EditingCoordinator(this));
	}

        protected Panel makeButtonPanel()
        {
                Panel b = super.makeButtonPanel();
		GridLayout gl = (GridLayout)b.getLayout();
		gl.setRows(gl.getRows()+2);
                Button but = new Button("View mode");
                but.addActionListener
                (
                        new ActionListener()
                        {
                                public void actionPerformed(ActionEvent e)
                                {
					if (getMouser() == viewer)
					{
						setMouser(editor);
						getCanvas().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
						((Button)e.getSource()).setLabel("Edit mode");
					}
					else
					{
						setMouser(viewer);
						getCanvas().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						((Button)e.getSource()).setLabel("View mode");
					}
                                }
                        }
                );
                b.add(but);

		TextField tx = new TextField();
		tx.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					((EditingCoordinator)getCoordinator()).find(((TextField)e.getSource()).getText());
					setChanged();
				}
			}
		);
		Panel tp = new Panel();
		tp.setLayout(new GridLayout(1,2));
		tp.add(new Label("Find"));
		tp.add(tx);
		b.add(tp);
                return b;
        }

// Private data

	private MapMouser viewer = null;
	private MapMouser editor = null;
}
