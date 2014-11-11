import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class GosperCurve {
	private static boolean debug = false;
	private static int xi = 100; // initial x, for border space
	private static int yi = 100; // initial y, for border space
	private static Stroke stroke = new BasicStroke(1.0000001f);

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out
				.println("Enter the iteration of the Gosper Curve you want to generate (n >= 1)");
		if (scanner.hasNextInt()) {
			UserInterface ui = new UserInterface();
			while (scanner.hasNextInt()) {
				int iteration = scanner.nextInt();
				if (iteration < 0)
					break;
				DrawGosperCurve(ui, iteration);
			}
		}

		scanner.close();

		System.exit(0);

	}

	public static void DrawGosperCurve(UserInterface ui, int iteration) {
		// initialize canvas
		int width = ui.getWidth(); // canvas x size
		int height = ui.getHeight(); // canvas y size
		int hsl = getGosperLength(iteration); // gosper curve side length in line units
		
		// if iteration is larger than support canvas size
		if (iteration > 5) {
			width = hsl * 7 + 2*xi;
			height = hsl * 7 + 2*yi;
			System.out.println("Generating image at size "+width+"x"+height);
		}
		
		// set up  image
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g = (Graphics2D) img.getGraphics();
		// Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		
		// prepare for drawing
		int state = 90;
		int adjwidth = width - 2*xi;
		int d = adjwidth / hsl;
		double x = xi + d*hsl; // x coordinate
		double y = height - yi - d*hsl/3; // y coordinate
		double dx = x; // destination x for line
		double dy = y; // destination y for line
		
		// prepare curve
		String curve = generateGosperCurve(iteration);
		for (int i = 0; i < curve.length(); i++) {
			switch (curve.charAt(i)) {
			case '+':
				// turn right
				state -= 60;
				if (state < 0) state += 360;
				break;
			case '-':
				// turn left
				state += 60;
				state %= 360;
				break;
			case 'A':
			case 'B':
				// draw forward
				if (debug) System.out.println("state = "+state);
				dx = (x + d * Math.cos(Math.toRadians(state)));
				dy = (y + d * Math.sin(Math.toRadians(state)));
				if (debug) System.out.println("drawing line from ("+x+","+y+") to ("+dx+","+dy+")");
				g.setStroke(stroke);
				g.drawLine((int) x, (int) y, (int) dx, (int) dy);
				x = dx;
				y = dy;
				break;
			}
		}
//		ui.setImage(img);
		File outputfile = new File("gosper-"+iteration+".png");
		try {
			ImageIO.write(img, "png", outputfile);
			System.out.println("Finished writing image.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (iteration <= 5) {
			ui.setImage(img);
			ui.repaint();
			ui.setVisible(true);
		} else {
			ui.setVisible(false);
		}
	}

	public static String generateGosperCurve(int iteration) {
		String curve = "A";
		String axiomA = "C-D--D+C++CC+D-";
		String axiomB = "+C-DD--D-C++C+D";
		if (debug) if (debug)
			System.out.println("curve at iteration #" + 0 + " = "
					+ curve);
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

	private static int getGosperLength(int iteration) {
		int result = 4;
		for (int i = 1; i < iteration; i++) {
			result *= 3;
			result -= 4;
		}
		return result;
	}

}
