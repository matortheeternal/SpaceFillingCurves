import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class KochCurve {
	private static boolean debug = false;
	private static int xi = 100; // initial x, for border space
	private static int yi = 100; // initial y, for border space
	private static Stroke stroke = new BasicStroke(1.0000001f);

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out
				.println("Enter the iteration of the Koch Curve you want to generate (n >= 1)");
		if (scanner.hasNextInt()) {
			UserInterface ui = new UserInterface();
			while (scanner.hasNextInt()) {
				int iteration = scanner.nextInt();
				if (iteration < 0)
					break;
				DrawKochCurve(ui, iteration);
			}
		}

		scanner.close();

		System.exit(0);

	}

	public static void DrawKochCurve(UserInterface ui, int iteration) {
		// initialize canvas
		int width = ui.getWidth(); // canvas x size
		int height = ui.getHeight(); // canvas y size
		int hsl = getKochLength(iteration); // koch curve side length in line units
		
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
		int state = 60;
		int adjwidth = width - 2*yi;
		int d = (int) Math.sqrt((1.333) * (adjwidth * adjwidth/16.0)); // length of a koch line unit on our canvas
		double offset = Math.sqrt((0.75)*d*d);
		d = (int) ((3.0 * d)/hsl);
		double x = xi; // x coordinate
		double y = (int) (yi + offset); // y coordinate
		double dx = x; // destination x for line
		double dy = y; // destination y for line
		
		// prepare curve
		String curve = generateKochCurve(iteration);
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
			case 'F':
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
		File outputfile = new File("koch-"+iteration+".png");
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

	public static String generateKochCurve(int iteration) {
		String curve = "F++F++F";
		String axiom = "E-E++E-E";
		if (debug) if (debug)
			System.out.println("curve at iteration #" + 0 + " = "
					+ curve);
		for (int i = 0; i < iteration; i++) {
			curve = curve.replace("F", axiom);
			curve = curve.replace("E", "F");
			if (debug)
				System.out.println("curve at iteration #" + (i + 1) + " = "
						+ curve);
		}
		System.out.println("Generated curve is "+curve.length()+" characters long.");
		return curve;
	}

	private static int getKochLength(int iteration) {
		int result = 1;
		for (int i = 0; i < iteration; i++) {
			result *= 3;
		}
		return result;
	}

}
