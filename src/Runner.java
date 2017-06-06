
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

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Runner {
	
	private static int blur; // The amount that is being blurred.
	private static Mat image; // A saved version of the picture.
	private static Mat tmp;
	private static final int MIN_BLUR = 1; // This is the lowest number the blur can go before it throws errors.
	private static final int MIN_THRESH=1;
	private static final int MAX_THRESH=255;
	
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
				img = Mat2BufferedImage(image); // Sets the picture to whatever's in the matrix
			} catch (Exception e) {
				e.printStackTrace();
			}

			JLabel lbl = new JLabel(); // Where the picture will be placed

			// The buttons used to manipulate the picture
			JButton blurPlus = new JButton("+");
			JButton blurMinus = new JButton("-");
			JButton threshPlus=new JButton("+");
			JButton threshMinus=new JButton("-");

			// Slider to make quick adjustments based on the range
			JSlider blurSlider = new JSlider(MIN_BLUR, max_blur, MIN_BLUR);
			JSlider thresh = new JSlider(MIN_THRESH,MAX_THRESH,50); // Default set to 50 is arbitrary.

			// Sets up a menu bar which will be used to select the OpenCV function at a later time.
			JMenuBar menu = new JMenuBar();
			JMenu selector = new JMenu("Help");
			selector.add(new JButton("Help"));
			menu.add(selector);

			// Code that is run every time the slider is moved or a button is pushed.
			// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
			blurSlider.addChangeListener((e) -> { 
				blur = blurSlider.getValue();
				threshold=thresh.getValue();
				updateDisplay(frame,lbl);
			});
			
			thresh.addChangeListener((e)->{
				blur = blurSlider.getValue();
				threshold=thresh.getValue();
				updateDisplay(frame,lbl);
			});

			blurPlus.addActionListener((e) -> {
				changeBlur(1);
				blurSlider.setValue(blur);
				updateDisplay(frame,lbl);
			});

			blurMinus.addActionListener((e) -> {
				changeBlur(-1);
				blurSlider.setValue(blur);
				updateDisplay(frame,lbl);
			});
			
			threshPlus.addActionListener((e)->{
				changeThresh(1);
				thresh.setValue(threshold);
				updateDisplay(frame,lbl);
			});
			
			threshMinus.addActionListener((e)->{
				changeThresh(-1);
				thresh.setValue(threshold);
				updateDisplay(frame,lbl);
			});
			// ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

			lbl.setIcon(new ImageIcon(img));

			frame.add(blurMinus);
			frame.add(blurSlider);
			frame.add(blurPlus);
			frame.add(new JLabel("▲▲▲Blur tool▲▲▲"));
			frame.add(lbl);
			frame.add(new JLabel("▼▼▼Threshold tool▼▼▼"));
			frame.add(threshMinus);
			frame.add(thresh);
			frame.add(threshPlus);
			
			frame.setJMenuBar(menu);
			frame.setLocationRelativeTo(null); // The program will open wherever.
			frame.pack(); // Puts all the frame together
			frame.setVisible(true); // Shows the frame

		});

	}

	public Runner() {
		blur = MIN_BLUR;

		System.loadLibrary("opencv_java310");
		image = Imgcodecs.imread("C:\\Users\\Brian\\Pictures\\tower.jpg");
		tmp = new Mat(image.width(), image.cols(), image.type());
		Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(tmp, tmp, threshold, 255, Imgproc.THRESH_BINARY);
		Imgproc.equalizeHist(tmp, tmp);

	}

	/**
	 * Uses a clone of the original image and then blurs it by whatever the
	 * current blur is.
	 * 
	 * @return the appropriately blurred version of the original picture
	 * @author Brian
	 */
	public static Mat blurIt() {
		Mat tmp = image.clone();
		Imgproc.cvtColor(image, tmp, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(tmp, tmp, threshold, 255, Imgproc.THRESH_BINARY);
		Imgproc.equalizeHist(tmp, tmp);
		Imgproc.blur(tmp, tmp, new Size(blur, blur));
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
	 * A little utility to streamline altering the blur. It will catch any
	 * potential problems with too much or too little blur.
	 * 
	 * @param change
	 *            The amount that you want the blur to change by.
	 * @author Brian
	 */
	public static void changeBlur(int change) {
		if(blur+change>=MIN_BLUR && blur+change<=max_blur){
			blur+=change;
		}
	}
	
	/**
	 * Changes the threshold value and keeps it within the correct range.
	 * @param change The amount that you want to change the threshold by.
	 */
	public static void changeThresh(int change){
		if(threshold+change>=MIN_THRESH && threshold+change<=MAX_THRESH){
			threshold+=change;
		}
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
		Mat tmp = blurIt().clone();
		try {
			label.setIcon(new ImageIcon(Mat2BufferedImage(tmp)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		frm.repaint();
	}

}
