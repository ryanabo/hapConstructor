package alun.viewgraph;

import java.awt.event.MouseEvent;

public class EditingMouser extends MapMouser
{
	public EditingMouser(MapViewer g)
	{
		super(g);
	}

	public void mouseClicked(MouseEvent e)
	{
		EditingCoordinator ed = (EditingCoordinator)co();
		Mappable v = co().get(e.getX(),e.getY());

		if (v == null)
		{
			if (e.getClickCount() == 2)
			{
				switch(button(e))
				{
				case 3:
					break;
				case 2:
					ed.removeAll();
					break;
				case 1:
					ed.addAll();
					break;
				}
			}
		}
		else
		{
			switch(button(e))
			{
			case 3:
				if (e.isShiftDown())
					ed.peelAll();
				else if (e.isControlDown())
					ed.peel(v,true);
				else
					ed.peel(v,false);
				ed.flash();
				break;
			case 2:
				if (e.isShiftDown())
					ed.removeComponent(v);
				else
					ed.remove(v);
				ed.flash();
				break;
			case 1:
				if (e.isShiftDown())
					ed.expandComponent(v);
				else
					ed.expand(v);
				ed.flash();
				break;
			}
		}
 	}
}
