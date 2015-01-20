package miniprojekt3;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import rwth.i2.ltl2ba4j.LTL2BA4J;
import rwth.i2.ltl2ba4j.model.ITransition;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();
	JPanel lts1panel = new JPanel();
	JPanel lts2panel = new JPanel();
	JPanel cpcpanel = new JPanel();
	JPanel checkpanel = new JPanel();
	JButton loadKS1 = new JButton("load KS");
	JButton lTLtoBAButton = new JButton("LTL to BA");
	JButton showBA1 = new JButton("show KStoBA Graph ");
	JButton showLTLtoBA = new JButton("show LTLtoBA Graph");
	JButton transKStoBA = new JButton("Transform KS to BA");
	JButton constructProduct = new JButton("Construct Product");
	JButton check = new JButton("Check LTL");
	JTextField text = new JTextField();

	KS ks1 = null;
	KS parallelComposition = null;
	BA ba = null;
	BA ba1 = null;
	BA ba2 = null;
	BA ba12 = null;
	BA ltlToBA = null;
	Collection<ITransition> automaton = null;

	JLabel led1 = new JLabel("    •");
	JLabel led2 = new JLabel("    •");
	JLabel led3 = new JLabel("    •");
	

	public Gui() {
		setTitle("Miniproject");
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(3);

		panel.setLayout(new GridLayout(6, 0));
		lts1panel.setLayout(new GridLayout(0, 3));
		lts2panel.setLayout(new GridLayout(0, 2));
		cpcpanel.setLayout(new GridLayout(0, 1));
		checkpanel.setLayout(new GridLayout(0, 6));

		panel.add(lts1panel);
		lts1panel.add(loadKS1);
		lts1panel.add(transKStoBA);
		lts1panel.add(showBA1);
		panel.add(text);
		panel.add(lts2panel);
		lts2panel.add(lTLtoBAButton);
		lts2panel.add(showLTLtoBA);

		panel.add(cpcpanel);
		
		cpcpanel.add(constructProduct);

		Font font = new Font(null, Font.BOLD, 14);
		text.setFont(font);

		

		panel.add(check);

		led1.setForeground(Color.gray);
		led2.setForeground(Color.gray);
		led3.setForeground(Color.gray);

		checkpanel.add(new JLabel("KS1", SwingConstants.RIGHT));
		checkpanel.add(led1);
		checkpanel.add(new JLabel("LTLtoBA", SwingConstants.RIGHT));
		checkpanel.add(led2);
		checkpanel.add(new JLabel("LTL satisfied", SwingConstants.RIGHT));
		checkpanel.add(led3);

		panel.add(checkpanel);

		add(panel);

		pack();

		loadKS1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int approve = chooser.showOpenDialog(null);

				if (approve == JFileChooser.APPROVE_OPTION) {
					ks1 = KS.read(chooser.getSelectedFile().getPath());
				}
			}
		});

		lTLtoBAButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
				automaton = LTL2BA4J.formulaToBA(text.getText());
				ltlToBA = LTLtoBA.ltl2BA(automaton);
				ltlToBA.createGraph("./LTLtoBA.png");
				led2.setForeground(Color.green);
				}catch(IllegalArgumentException exep){
				JOptionPane.showMessageDialog(null, "You need to enter a valid LTLFormula! Please use these operators: \r\n"
						+ "        true, false\r\n" + 
	                    "        any lowercase string\r\n" + 
	                    "\r\n" + 
	                    "Boolean operators:\r\n" + 
	                    "        !   (negation)\r\n" + 
	                    "        ->  (implication)\r\n" + 
	                    "        <-> (equivalence)\r\n" + 
	                    "        &&  (and)\r\n" + 
	                    "        ||  (or)\r\n" + 
	                    "\r\n" + 
	                    "Temporal operators:\r\n" + 
	                    "        []  (always)\r\n" + 
	                    "        <>  (eventually)\r\n" + 
	                    "        U   (until)\r\n" + 
	                    "        V   (release)\r\n" + 
	                    "        X   (next)"
	                    					, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		transKStoBA.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (ks1 == null) {
					JOptionPane.showMessageDialog(null, "You need to load a Kripke Structure to proceed!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					ba = ks1.transformToBA();
					ba.createGraph("./KS1.png");
					led1.setForeground(Color.green);
				}
			}
		});

		showBA1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ks1 == null && ba == null) {
					JOptionPane.showMessageDialog(null, "You need to load a Kripke Structur and transform it to BA to proceed!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					openKS1();
				}
			}
		});
	

		showLTLtoBA.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (automaton == null) {
					JOptionPane.showMessageDialog(null, "You need to enter a valid  LTLFormula  to proceed!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					openLTLtoBA();
				}
			}
		});



		constructProduct.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ks1 == null && automaton == null){
					JOptionPane.showMessageDialog(null, "You need transform the Kripke Strucure und LTLFormula to proceed!", "Error", JOptionPane.ERROR_MESSAGE);
				}else{

				ba1 = ks1.transformToBA();
				ba12 = ba1.constructProduct(ltlToBA);

				ba12.createGraph("./graph.png");

				openPng();
				}
			
			}
		});

		check.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (ba12 == null) {
					JOptionPane.showMessageDialog(null, "You need to construct the Product of both BA's to proceed!", "Error", JOptionPane.ERROR_MESSAGE);
					
				}
				else if(ba12.isAcceptedLanguageEmpty()){
					led3.setForeground(Color.red);
				}
				else{
				
					led3.setForeground(Color.green);
				}
			}
		});
	}

	static void openPng() {
		final JFrame f = new JFrame();

		f.setTitle("constructed Product");
		f.setResizable(false);
		f.setVisible(true);

		JPanel p = new JPanel();

		f.add(p);

		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("./graph.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			p.add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		f.pack();
	}

	static void openKS1() {
		final JFrame f = new JFrame();

		f.setTitle("KS1");
		f.setResizable(false);
		f.setVisible(true);

		JPanel p = new JPanel();

		f.add(p);

		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("./KS1.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			p.add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		f.pack();
	}

	static void openLTLtoBA() {
		final JFrame f = new JFrame();

		f.setTitle("LTLtoBA");
		f.setResizable(false);
		f.setVisible(true);

		JPanel p = new JPanel();

		f.add(p);

		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("./LTLtoBA.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			p.add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		f.pack();
	}

	public static void main(String... args) {

		new Gui();
	}
}
