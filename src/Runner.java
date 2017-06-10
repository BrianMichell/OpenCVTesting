
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

@SuppressWarnings("unused")
public class Runner {

	private static Mat image; // A saved version of the picture.
	private static Mat tmp;
	private static int hueMin;
	private static int hueMax;
	private static int satMin;
	private static int satMax;
	private static int valMin;
	private static int valMax;

	public void graphics() {

		System.out.println("This is the HSV file");

		SwingUtilities.invokeLater(() -> { // Lambda expression to start the
											// Swing program
			JFrame frame = new JFrame("OpenCV image testing"); // Creates the
																// framework and
																// names it.

			GridLayout layout = new GridLayout(0, 3);
			frame.setLayout(layout);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Allows the
																	// program
																	// to stop
																	// when the
																	// "X"
																	// button is
																	// pressed
			BufferedImage img = null; // A usable picture for Swing

			try {
				img = Mat2BufferedImage(tmp); // Sets the picture to whatever's
												// in the matrix
			} catch (Exception e) {
				e.printStackTrace();
			}

			JLabel lbl = new JLabel(); // Where the picture will be placed

			lbl.setIcon(new ImageIcon(img));

			frame.add(lbl);

			frame.setLocationRelativeTo(null); // The program will open
												// wherever.
			frame.pack(); // Puts all the frame together
			frame.setVisible(true); // Shows the frame

			new Thread(() -> {
				for (int i = 0; i < 10; i++) {
					updateDisplay(frame, lbl);
				}
				try{
					Thread.sleep(3000);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}).start();

		});

	}

	public Runner() {

		System.loadLibrary("opencv_java310");
		image = Imgcodecs.imread("C:\\Users\\Brian\\Pictures\\tower.jpg");
		tmp = new Mat(image.width(), image.cols(), image.type());
		Imgproc.cvtColor(image, tmp, Imgproc.COLOR_BGR2HSV);
		Core.inRange(tmp, new Scalar(0, 100, 0), new Scalar(50, 255, 255), tmp);

	}

	/**
	 * Uses a clone of the original image and then blurs it by whatever the
	 * current blur is.
	 * 
	 * @return the appropriately blurred version of the original picture
	 * @author Brian
	 */
	public static Mat hsvChange() {

		// Mat tmp = vid.grab();
		// Imgproc.cvtColor(image, tmp, Imgproc.COLOR_BGR2HSV);
		// Core.inRange(tmp, new Scalar(hueMin,satMin,valMin), new
		// Scalar(hueMax,satMax,valMax), tmp);
		return null;
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
	 * Update the image being displayed.
	 * 
	 * @param frm
	 *            The canvas that needs updating.
	 * @param label
	 *            The holder for the picture.
	 */
	private static void updateDisplay(JFrame frm, JLabel label) {
		VideoCapture vid = new VideoCapture(0);
		vid.open(0);
		if (!vid.isOpened()) {
			System.out.println("This has failed!");
		} else {
			System.out.println("It's all good!");

			Mat tmp = new Mat();
			if (vid.read(tmp))
				try {
					label.setIcon(new ImageIcon((Mat2BufferedImage(tmp))));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			frm.repaint();
		}
	}

}
