import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Setup {
	
	/**
	 * Setup user defined variables
	 * @param args N/A
	 */
	public static void main(String[] args) {
		
		Runner r = new Runner();
		

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Setup");
			JTextField txt = new JTextField();
			JButton okay = new JButton("Okay");
			JLabel label = new JLabel("Please input the max blur value you want and press Okay");

			okay.addActionListener((e) -> {
				if (txt.getText() != null) { // Just makes sure that there is
												// something in the field
					if (Integer.parseInt(txt.getText()) >= 2) {
						r.setMaxVal(Integer.parseInt(txt.getText()));
					} else {
						r.setMaxVal(100);
					}
					frame.dispose();
					r.graphics();
				}
			});

			frame.getContentPane().add(txt, BorderLayout.NORTH);
			frame.getContentPane().add(okay, BorderLayout.SOUTH);
			frame.getContentPane().add(label, BorderLayout.CENTER);

			frame.setLocationRelativeTo(null); // The program will just open
												// wherever. You can change this
												// if you want it to be
												// somewhere specific.
			frame.pack(); // Puts all the frame together
			frame.setVisible(true); // Shows the frame

		});

	}

}
