import edu.princeton.cs.algs4.Picture;
import java.util.Arrays;

public class SeamCarver {

	private Picture picture;

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture) {
		if (picture == null)
			throw new IllegalArgumentException("Input picture is a null reference!");
		this.picture = new Picture(picture);
	}

	// current picture
   	public Picture picture() {
   		return new Picture(picture);
   	}

   	// width of current picture
   	public int width() {
   		return picture.width();
   	}

   	// height of current picture
   	public int height() {
   		return picture.height();
   	}

   	// energy of pixel at column x and row y
   	public  double energy(int x, int y) {
   		if (x < 0 || x >= width() || y < 0 || y>= height())
   			throw new IllegalArgumentException("Input coordinates are out of range of picture dimension!");
   		if (x == 0 || x == width()-1 || y == 0 || y == height()-1)
   			return 1000.0;
   		double dx = distanceSquared(picture.getRGB(x-1, y), picture.getRGB(x+1, y));
   		double dy = distanceSquared(picture.getRGB(x, y-1), picture.getRGB(x, y+1));
   		return Math.sqrt(dx+dy);
   	}

   	// sequence of indices for horizontal seam
   	public int[] findHorizontalSeam() {

   		// run findVerticalSeam() on transposed picture
   		Picture pictureOriginal = picture;
   		picture = new Picture(height(), width());
   		for (int row = 0; row < pictureOriginal.height(); row++)
   			for (int col = 0; col < pictureOriginal.width(); col++)
   				picture.setRGB(row, col, pictureOriginal.getRGB(col, row));
   		int[] seam = findVerticalSeam();
   		
   		// don't forget to restore
   		picture = pictureOriginal;

   		return seam;
   	}

   	// sequence of indices for vertical seam
   	public int[] findVerticalSeam() {
   		
   		// special case
   		if (width() == 1)
   			return new int[height()];

   		// offsetTo[y][x] stores the horizontal offset between pixel (x,y) and its precedessor in the shortest path
   		int[][] offsetTo = new int[height()][width()];
   		
   		// distanceTo[y][x] stores the shortest distance from pixel (x,y) to a virtual top node
		double[][] distanceTo = new double[height()][width()];
		for (int row = 0; row < height(); row++) {
			if (row == 0)	Arrays.fill(distanceTo[row], 1000.0);
			else 			Arrays.fill(distanceTo[row], Double.POSITIVE_INFINITY);
		}
		
		// precomputes energy for each pixel
		double[][] energies = new double[height()][width()];
		for (int row = 0; row < height(); row++)
			for (int col = 0; col < width(); col++)
				energies[row][col] = energy(col, row);
		
		// dynamic programming (shortest path via topological sort)
		for (int row = 0; row < height()-1; row++)
			for (int col = 0; col < width(); col++) {
				int[] offsets;
				if (col == 0)				offsets = new int[]{0, 1};
				else if (col == width()-1)	offsets = new int[]{-1, 0};
				else 						offsets = new int[]{-1, 0, 1};
				for (int s: offsets)
					if (distanceTo[row+1][col+s] > distanceTo[row][col]+energies[row+1][col+s]) {
						distanceTo[row+1][col+s] = distanceTo[row][col]+energies[row+1][col+s];
						offsetTo[row+1][col+s] = s;
					}
			}

		// find index of the last row with minimum distanceTo value
		int index = 0;
		for (int col = 0; col < width(); col++)
			if (distanceTo[height()-1][col] < distanceTo[height()-1][index])
				index = col;
		
		// construct seam array
		int[] seam = new int[height()];
		seam[height()-1] = index;
		for (int row = height()-1; row > 0; row--)
			seam[row-1] = seam[row] - offsetTo[row][seam[row]];

		return seam;
   	}

   	// remove horizontal seam from current picture
   	public void removeHorizontalSeam(int[] seam) {
   		if (seam == null)
   			throw new IllegalArgumentException("Input argument is a null reference!");
   		if (seam.length != width())
   			throw new IllegalArgumentException("Then length of the seam is not equal to the width of the image!");
   		if (height() <= 1)
   			throw new IllegalArgumentException("Height of image is less than 1!");
   		for (int i = 0; i < seam.length; i++) {
   			if (seam[i] < 0 || seam[i] >= height())
   				throw new IllegalArgumentException("Invalid seam: index out of vertical boundary!");
   			if (i > 0 && Math.abs(seam[i]-seam[i-1])>1)
   				throw new IllegalArgumentException("Invalid seam: difference between adjacent indices is larger than 1!");
   		}

   		Picture pictureOriginal = picture;
   		picture = new Picture(width(), height()-1);
   		for (int col = 0; col < width(); col++) {
   			int row = 0, rowOriginal = 0;
   			while (row < height()) {
   				if (rowOriginal == seam[col]) rowOriginal++;
   				picture.setRGB(col, row++, pictureOriginal.getRGB(col, rowOriginal++));
   			}
   		}
   	}

   	// remove vertical seam from current picture
   	public void removeVerticalSeam(int[] seam) {
   		if (seam == null)
   			throw new IllegalArgumentException("Input argument is a null reference!");
   		if (seam.length != height())
   			throw new IllegalArgumentException("Then length of the seam is not equal to the height of the image!");
   		if (width() <= 1)
   			throw new IllegalArgumentException("Width of image is less than 1!");
   		for (int i = 0; i < seam.length; i++) {
   			if (seam[i] < 0 || seam[i] >= width())
   				throw new IllegalArgumentException("Invalid seam: index out of horizontal boundary!");
   			if (i > 0 && Math.abs(seam[i]-seam[i-1])>1)
   				throw new IllegalArgumentException("Invalid seam: difference between adjacent indices is larger than 1!");
   		}

   		Picture pictureOriginal = picture;
   		picture = new Picture(width()-1, height());
   		for (int row = 0; row < height(); row++) {
   			int col = 0, colOriginal = 0;
   			while (col < width()) {
   				if (colOriginal == seam[row]) colOriginal++;
   				picture.setRGB(col++, row, pictureOriginal.getRGB(colOriginal++, row));
   			}
   		}   		
   	}

   	// client testing
   	public static void main(String[] args) {
   		Picture picture = new Picture(args[0]);
   		SeamCarver seamCarver = new SeamCarver(picture);
   		int[] seam = seamCarver.findVerticalSeam();
   		for (int ix: seam) System.out.println(ix);
   	}

   	// get the squared distance between two RGB values
   	private double distanceSquared(int rgb1, int rgb2) {
   		int r1 = (rgb1 >> 16) & 0xFF;
   		int g1 = (rgb1 >> 8) & 0xFF;
   		int b1 = (rgb1 >> 0) & 0xFF;
   		int r2 = (rgb2 >> 16) & 0xFF;
   		int g2 = (rgb2 >> 8) & 0xFF;
   		int b2 = (rgb2 >> 0) & 0xFF;
   		return Math.pow(r1-r2, 2)+Math.pow(g1-g2, 2)+Math.pow(b1-b2, 2);
   	}
}