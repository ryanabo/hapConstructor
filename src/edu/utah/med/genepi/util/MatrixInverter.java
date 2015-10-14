package edu.utah.med.genepi.util;

public class MatrixInverter
{
	public MatrixInverter() 
        {}

	public static double[][] inverse(double[][] matrix)
	{
		return invert(matrix).inverse;
	}

	public static double determinant(double[][] matrix)
	{
		return invert(matrix).determinant;
	}

	private static InverseData invert(double[][] matrix)
	{
		double[][] x = new double[matrix.length][2*matrix.length];

		for (int i=0; i<x.length; i++)
		{
			for (int j=0; j<x.length; j++)
				x[i][j] = matrix[i][j];
			x[i][i+matrix.length] = 1;
		}

		for (int i=0; i<x.length; i++)
		{
			if (x[i][i] == 0)
				throw new RuntimeException("Matrix is not invertible");

			for (int j=0; j<x.length; j++)
				if (j != i)
				{
					double z = x[j][i]/x[i][i];
					for (int k=0; k<x[j].length; k++)
						x[j][k] -= x[i][k]*z;
				}
		}

		InverseData d = new InverseData();

		d.determinant = 1;

		for (int i=0; i<x.length; i++)
		{
			double z = x[i][i];
			for (int j=0; j<x[i].length; j++)
				x[i][j] /= z;
			d.determinant *= z;
		}

		d.inverse = new double[matrix.length][matrix.length];
		for (int i=0; i<d.inverse.length; i++)
			for (int j=0; j<d.inverse[i].length; j++)
				d.inverse[i][j] = x[i][j+d.inverse.length];

		return d;
	}

	public static void main(String[] args)
	{
		try
		{
/*
			double[][] x = new double[4][4];
			x[0][0] = 2;
			x[1][1] = 1;
			x[2][2] = 3;
			x[3][3] = 0.5;
*/

			double[][] x = 
			{
				{ 1, 2, 3, 0 },
				{ 1, 4, 3, 0 },
				{ 0, 2, 1, 4 },
				{ 0, 2, 0, 8 },
			};


			System.out.println("Matrix");
			for (int i=0; i<x.length; i++)
			{
				for (int j=0; j<x[i].length; j++)
					System.out.print(" "+x[i][j]);
				System.out.println();
			}

			System.out.println("Determinant");
			System.out.println(MatrixInverter.determinant(x));

			System.out.println("Inverse");
			double[][] y = MatrixInverter.inverse(x);
			for (int i=0; i<y.length; i++)
			{
				for (int j=0; j<y[i].length; j++)
					System.out.print(" "+y[i][j]);
				System.out.println();
			}

			System.out.println("Determinant of inverse");
			System.out.println(MatrixInverter.determinant(y));

			x = MatrixInverter.inverse(y);
			System.out.println("Inverse of the inverse");
			for (int i=0; i<x.length; i++)
			{
				for (int j=0; j<x[i].length; j++)
					System.out.print(" "+x[i][j]);
				System.out.println();
			}

		}
		catch (Exception e)
		{
			System.err.println("Caught in MatrixInverter.main()");
			e.printStackTrace();
		}
	}

}

class InverseData
{
	double determinant = 0;
	double[][] inverse = null;
}
