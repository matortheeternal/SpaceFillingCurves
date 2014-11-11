import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class HilbertCurve {
	private static boolean debug = false;
	private static int xi = 100; // initial x, for border space
	private static int yi = 100; // initial y, for border space

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out
				.println("Enter the iteration of the Hilbert Curve you want to generate (n >= 1)");
		if (scanner.hasNextInt()) {
			UserInterface ui = new UserInterface();
			while (scanner.hasNextInt()) {
				int iteration = scanner.nextInt();
				if (iteration < 0)
					break;
				DrawHilbertCurve(ui, iteration);
			}
		}

		scanner.close();

		System.exit(0);

	}

	public static void DrawHilbertCurve(UserInterface ui, int iteration) {
		// initialize canvas
		int width = ui.getWidth(); // canvas x size
		int height = ui.getHeight(); // canvas y size
		int hsl = getHilbertSize(iteration); // hilbert curve side length in line units
		
		// if iteration is larger than support canvas size
		if (iteration > 8) {
			width = hsl * 3 + 2 * xi;
			height = hsl * 3 + 2*yi;
			System.out.println("Generating image at size "+width+"x"+height);
			ui.setVisible(false);
		}
		
		// set up  image
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		
		// prepare for drawing
		int state = 0;
		int x = xi; // x coordinate
		int y = height - yi; // y coordinate
		int d = (width < height) ? (width - 2*xi) / hsl : (height - 2*yi) / hsl; // length of a hilbert line unit on our canvas
		int dx = x; // destination x for line
		int dy = y; // destination y for line
		
		// prepare curve
		String curve = generateHilbertCurve(iteration);
		for (int i = 0; i < curve.length(); i++) {
			switch (curve.charAt(i)) {
			case '+':
				// turn right
				state -= 90;
				if (state < 0) state += 360;
				break;
			case '-':
				// turn left
				state += 90;
				state %= 360;
				break;
			case 'F':
				// draw forward
				if (debug) System.out.println("state = "+state);
				switch(state) {
				case 0:
					dx = x + d;
					dy = y;
					break;
				case 90:
					dx = x;
					dy = y - d;
					break;
				case 180:
					dx = x - d;
					dy = y;
					break;
				case 270:
					dx = x;
					dy = y + d;
					break;
				}
				if (debug) System.out.println("drawing line from ("+x+","+y+") to ("+dx+","+dy+")");
				g.drawLine(x, y, dx, dy);
				x = dx;
				y = dy;
				break;
			}
		}
//		ui.setImage(img);
		File outputfile = new File("hilbert-"+iteration+".png");
		try {
			ImageIO.write(img, "png", outputfile);
			System.out.println("Finished writing image.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (iteration <= 8) {
			ui.setImage(img);
			ui.repaint();
			ui.setVisible(true);
		}
//		g.dispose();
//		g = null;
//		img = null;
//		curve = null;
//		System.gc();
	}

	public static String generateHilbertCurve(int iteration) {
		String curve = "A";
		String axiomA = "-DF+CFC+FD-";
		String axiomB = "+CF-DFD-FC+";
		for (int i = 0; i < iteration; i++) {
			curve = curve.replace("A", axiomA);
			curve = curve.replace("B", axiomB);
			curve = curve.replace("C", "A");
			curve = curve.replace("D", "B");
			if (debug)
				System.out.println("curve at iteration #" + (i + 1) + " = "
						+ curve);
		}
		System.out.println("Generated curve is "+curve.length()+" characters long.");
		return curve;
	}

	private static int getHilbertSize(int iteration) {
		int result = 1;
		for (int i = 1; i < iteration; i++) {
			result *= 2;
			result++;
		}
		return result;
	}

}
