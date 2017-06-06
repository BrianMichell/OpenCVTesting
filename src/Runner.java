
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("unused")
public class Runner {
	
	private static int blur; // The amount that is being blurred.
	private static Mat image; // A saved version of the picture.
	private static Mat tmp;
	private static int hueMin;
	private static int hueMax;
	private static int satMin;
	private static int satMax;
	private static int valMin;
	private static int valMax;
	
	private static int max_blur = 2;
	private static int threshold=25;

	public void graphics() {
		
		System.out.println("This is the HSV file");

		SwingUtilities.invokeLater(() -> { // Lambda expression to start the
											// Swing program
			JFrame frame = new JFrame("OpenCV image testing"); // Creates the framework and names it.
			
			GridLayout layout = new GridLayout(0,3);
			frame.setLayout(layout);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Allows the program to stop when the "X" button is pressed
			BufferedImage img = null; // A usable picture for Swing

			try {
				img = Mat2BufferedImage(tmp); // Sets the picture to whatever's in the matrix
			} catch (Exception e) {
				e.printStackTrace();
			}

			JLabel lbl = new JLabel(); // Where the picture will be placed
			JLabel picture = new JLabel(); // Unmodified picture

			// Slider to make quick adjustments based on the range
			JSlider hueMinSlider = new JSlider(0, 255, 0);
			JSlider hueMaxSlider = new JSlider(0,255,255);
			JSlider satMinSlider = new JSlider(0,255,0);
			JSlider satMaxSlider = new JSlider(0,255,255);
			JSlider valMinSlider = new JSlider(0,255,0);
			JSlider valMaxSlider = new JSlider(0,255,255);

			// Code that is run every time the slider is moved or a button is pushed.
			// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
			
			hueMinSlider.addChangeListener((e)->{
				hueMin=hueMinSlider.getValue();
				updateDisplay(frame,lbl);
			});
			hueMaxSlider.addChangeListener((e)->{
				hueMax=hueMaxSlider.getValue();
				updateDisplay(frame,lbl);
			});
			satMinSlider.addChangeListener((e)->{
				satMin=satMinSlider.getValue();
				updateDisplay(frame,lbl);
			});
			satMaxSlider.addChangeListener((e)->{
				satMax=satMaxSlider.getValue();
				updateDisplay(frame,lbl);
			});
			valMinSlider.addChangeListener((e)->{
				valMin=valMinSlider.getValue();
				updateDisplay(frame,lbl);
			});
			valMaxSlider.addChangeListener((e)->{
				valMax=valMaxSlider.getValue();
				updateDisplay(frame,lbl);
			});
			

			lbl.setIcon(new ImageIcon(img));
			
			
			frame.add(hueMaxSlider);
			frame.add(new JLabel("<--- Max Hue Min --->"));
			frame.add(hueMinSlider);
			frame.add(satMaxSlider);
			frame.add(new JLabel("<--- Max Sat Min --->"));
			frame.add(satMinSlider);
			frame.add(valMaxSlider);
			frame.add(new JLabel("<--- Max Val Min --->"));
			frame.add(valMinSlider);
			frame.add(lbl);
			frame.add(new JLabel(""));
			JLabel original = new JLabel();
			original.setIcon(new ImageIcon(Mat2BufferedImage(image)));
			frame.add(original);
	
			frame.setLocationRelativeTo(null); // The program will open wherever.
			frame.pack(); // Puts all the frame together
			frame.setVisible(true); // Shows the frame

		});

	}

	public Runner() {

		System.loadLibrary("opencv_java310");
		image = Imgcodecs.imread("C:\\Users\\Brian\\Pictures\\tower.jpg");
		tmp = new Mat(image.width(), image.cols(), image.type());
		//tmp=image.clone();
		Imgproc.cvtColor(image, tmp, Imgproc.COLOR_BGR2HSV);
		Core.inRange(tmp, new Scalar(0,100,0), new Scalar(50,255,255), tmp);

	}

	/**
	 * Uses a clone of the original image and then blurs it by whatever the
	 * current blur is.
	 * 
	 * @return the appropriately blurred version of the original picture
	 * @author Brian
	 */
	public static Mat hsvChange() {
		Mat tmp = image.clone();
		Imgproc.cvtColor(image, tmp, Imgproc.COLOR_BGR2HSV);
		Core.inRange(tmp, new Scalar(hueMin,satMin,valMin), new Scalar(hueMax,satMax,valMax), tmp);
		return tmp;
	}

	/**
	 * Converts an OpenCV matrix into a buffered image usable by Swing
	 * 
	 * @param m
	 *            OpenCV matrix containing desired picture
	 * @return A picture usable by Swing
	 * @author The internet. All credit given in the method.
	 */
	public static BufferedImage Mat2BufferedImage(Mat m) {
		// source:
		// http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		// Fastest code
		// The output can be assigned either to a BufferedImage or to an Image

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;

	}


	/**
	 * Used to set the maximum blur value by other classes.
	 * @param val The amount that the max blur is.
	 */
	public void setMaxVal(int val) {
		max_blur = val;
	}
	
	/**
	 * Update the image being displayed.
	 * @param frm The canvas that needs updating.
	 * @param label The holder for the picture.
	 */
	private static void updateDisplay(JFrame frm, JLabel label){
		Mat tmp = hsvChange().clone();
		try {
			label.setIcon(new ImageIcon(Mat2BufferedImage(tmp)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		frm.repaint();
	}

}
