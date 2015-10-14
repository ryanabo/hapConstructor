package alun.view;

import java.awt.JobAttributes;
import java.awt.PageAttributes;

public class PageType
{
	public static PageType[] list = 
	{
		new PageType(),
		new PageType(PageAttributes.MediaType.LETTER, PageAttributes.OrientationRequestedType.PORTRAIT),
		new PageType(PageAttributes.MediaType.LETTER, PageAttributes.OrientationRequestedType.LANDSCAPE),
		new PageType(PageAttributes.MediaType.A4, PageAttributes.OrientationRequestedType.PORTRAIT),
		new PageType(PageAttributes.MediaType.A4, PageAttributes.OrientationRequestedType.LANDSCAPE),
		new PageType(PageAttributes.MediaType.A3, PageAttributes.OrientationRequestedType.PORTRAIT),
		new PageType(PageAttributes.MediaType.A3, PageAttributes.OrientationRequestedType.LANDSCAPE),
		new PageType(PageAttributes.MediaType.C1, PageAttributes.OrientationRequestedType.PORTRAIT),
		new PageType(PageAttributes.MediaType.C1, PageAttributes.OrientationRequestedType.LANDSCAPE),
		new PageType(PageAttributes.MediaType.C0, PageAttributes.OrientationRequestedType.PORTRAIT),
		new PageType(PageAttributes.MediaType.C0, PageAttributes.OrientationRequestedType.LANDSCAPE)
	};

	public PageType()
	{
		name = "No Page";
	}

	public PageType(PageAttributes.MediaType paper, PageAttributes.OrientationRequestedType orient)
	{
		pa = new PageAttributes();
		pa.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
		pa.setColor(PageAttributes.ColorType.COLOR);
		pa.setOrigin(PageAttributes.OriginType.PHYSICAL);

		pa.setMedia(paper);
		pa.setOrientationRequested(orient);

		if (paper == PageAttributes.MediaType.A4)
		{
			name = "A4";
			height = (int)(72/2.54*29.7);
			width = (int)(72/2.54*21.0);
		}
		else if (paper == PageAttributes.MediaType.A3)
		{
			name = "A3";
			width = (int)(72/2.54*29.7);
			height = (int)(2*72/2.54*21.0);
		}
		else if (paper == PageAttributes.MediaType.LETTER)
		{
			name = "Letter";
			height = (int)(72*11);
			width = (int)(72*8.5);
		}
		else if (paper == PageAttributes.MediaType.C0)
		{
			name = "C0";
			height = (int)(72/2.54 * 129.7);
			width = (int)(72/2.54 * 91.7);
		}
		else if (paper == PageAttributes.MediaType.C1)
		{
			name = "C1";
			height = (int)(72/2.54 * 91.7);
			width = (int)(72/2.54 * 64.8);
		}

		if (orient == PageAttributes.OrientationRequestedType.LANDSCAPE)
		{
			int t = width;
			width = height;
			height = t;
			name = name + " " + "Landscape";
		}
		else
		{
			name = name + " " + "Portrait";
		}
	}

	public JobAttributes getJobAttributes()
	{
		return ja;
	}

	public PageAttributes getAttributes()
	{
		return pa;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public String toString()
	{
		return name;
	}

	private JobAttributes ja = null;
	private PageAttributes pa = null;
	private int width = 0;
	private int height = 0;
	private String name = null;
}
