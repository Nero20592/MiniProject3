package miniprojekt3;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import rwth.i2.ltl2ba4j.DottyWriter;
import rwth.i2.ltl2ba4j.LTL2BA4J;
import rwth.i2.ltl2ba4j.internal.jnibridge.BAJni;
import rwth.i2.ltl2ba4j.model.ITransition;


public class Gui extends JFrame {
	
	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();
	JPanel lts1panel = new JPanel();
	JPanel lts2panel = new JPanel();
	JPanel cpcpanel = new JPanel();
	JPanel checkpanel = new JPanel();
	JButton loadLts1 = new JButton("load LTS1");
	JButton LTLtoBA = new JButton("LTL to BA");
	JButton showBA1 = new JButton("show Büchi Automata 1");
	JButton showLTLtoBA = new JButton("show LTLtoBA Graph");
	JButton transLTS = new JButton("Transform LTS to BA");
	JButton saveBA = new JButton("save BA");
	JButton check = new JButton("check LTL");
	JTextField text = new JTextField();

	LTS lts1 = null;
	LTS lts2 = null;
	LTS parallelComposition = null;
	BA ba = null;

	JLabel led1 = new JLabel("    •");
	JLabel led2 = new JLabel("    •");
	JLabel led3 = new JLabel("    •");
	
	public Gui(){
		setTitle("Miniproject");
	    setResizable(false);
	    setLocationRelativeTo(null);
	    setVisible(true);
	    setDefaultCloseOperation(3);

	    panel.setLayout(new GridLayout(6,0));
	    lts1panel.setLayout(new GridLayout(0,2));
	    lts2panel.setLayout(new GridLayout(0,2));
	    cpcpanel.setLayout(new GridLayout(0,2));
	    checkpanel.setLayout(new GridLayout(0,6));
	    
	    panel.add(lts1panel);
	    lts1panel.add(loadLts1);
	    lts1panel.add(showBA1);
	    
	    panel.add(lts2panel);
	    lts2panel.add(LTLtoBA);
	    lts2panel.add(showLTLtoBA);

	    panel.add(cpcpanel);
	    cpcpanel.add(transLTS);
	    cpcpanel.add(saveBA);
	    
	    Font font = new Font(null, Font.BOLD, 14);
        text.setFont(font);
        
	    panel.add(text);
	    
	    panel.add(check);
	    
		led1.setForeground(Color.gray);
		led2.setForeground(Color.gray);
		led3.setForeground(Color.gray);

		checkpanel.add(new JLabel("LTS1", SwingConstants.RIGHT));
		checkpanel.add(led1);
		checkpanel.add(new JLabel("LTS2", SwingConstants.RIGHT));
		checkpanel.add(led2);
		checkpanel.add(new JLabel("LTS1 || LTS2", SwingConstants.RIGHT));
		checkpanel.add(led3);
	    
		panel.add(checkpanel);
		
	    add(panel);
	    
	    pack();

	    loadLts1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
		        int approve = chooser.showOpenDialog(null);

		        if(approve == JFileChooser.APPROVE_OPTION)
		        {
		        	lts1 = LTS.read(chooser.getSelectedFile().getPath());
		        }
			}
	    });

	    LTLtoBA.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
		        	//BAJni test = new BAJni(text.getText());     	
		       Collection<ITransition> automaton = LTL2BA4J.formulaToBA(text.getText());
		       System.out.println(DottyWriter.automatonToDot(automaton));

		        
			}
	    });
	    
	    showBA1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(ba == null){
					JOptionPane.showMessageDialog(null,"You need to load LTS1 to proceed!", "Error",JOptionPane.ERROR_MESSAGE);
				}else{

					LTS.createGraphBA(ba, "./lts1.png");
					
					openLTS1();
				}
			}
	    });
	    
	    showLTLtoBA.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(lts2 == null){
					JOptionPane.showMessageDialog(null,"You need to load LTS2 to proceed!", "Error",JOptionPane.ERROR_MESSAGE);
				}else{

					LTS.createGraphBA(ba, "./lts2.png");
					
					openLTS2();
				}
			}
	    });
	    
	    transLTS.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(lts1 == null){
					JOptionPane.showMessageDialog(null,"You need to load an LTS to proceed!", "Error",JOptionPane.ERROR_MESSAGE);
				}else{
//					List<LTS> list = new ArrayList<LTS>();
//					list.add(lts1);
					ba = BA.transformToBA(lts1);
					
					//parallelComposition = LTS.parallelComposition(list);

					LTS.createGraphBA(ba, "./graph.png");
					
					openPng();
				}
			}
	    });
	    
	    saveBA.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(ba == null){
					JOptionPane.showMessageDialog(null,"You need to create a Buechi Automata to proceed!", "Error",JOptionPane.ERROR_MESSAGE);
				}else{
					JFileChooser chooser = new JFileChooser();
			        int approve = chooser.showSaveDialog(null);

			        if(approve == JFileChooser.APPROVE_OPTION)
			        {
			        	LTS.write(chooser.getSelectedFile().getPath(), parallelComposition);
			        }
				}
			}
	    });
	    
	    check.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(lts1 == null && lts2 == null && parallelComposition == null){
					JOptionPane.showMessageDialog(null,"You need to load at least one lts to proceed!", "Error",JOptionPane.ERROR_MESSAGE);
				}else{					
					if(lts1 != null){
						CTLChecker c1 = new CTLChecker(lts1);
						
						if(c1.checkFormula(new CTLFormula(text.getText())))
							led1.setForeground(Color.green);
						else
							led1.setForeground(Color.red);
					}

					if(lts2 != null){
						CTLChecker c2 = new CTLChecker(lts2);

						if(c2.checkFormula(new CTLFormula(text.getText())))
							led2.setForeground(Color.green);
						else
							led2.setForeground(Color.red);
					}
					
					if(parallelComposition != null){
						CTLChecker c3 = new CTLChecker(parallelComposition);

						if(c3.checkFormula(new CTLFormula(text.getText())))
							led3.setForeground(Color.green);
						else
							led3.setForeground(Color.red);
					}
				}
			}
		});
	}
	
	static void openPng(){
		final JFrame f = new JFrame();
		
		f.setTitle("Parallel Composition");
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
	
	static void openLTS1(){
		final JFrame f = new JFrame();
		
		f.setTitle("LTS1");
		f.setResizable(false);
		f.setVisible(true);

		JPanel p = new JPanel();
		
		f.add(p);
		
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("./lts1.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			p.add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		f.pack();
	}
	
	static void openLTS2(){
		final JFrame f = new JFrame();
		
		f.setTitle("LTS2");
		f.setResizable(false);
		f.setVisible(true);

		JPanel p = new JPanel();
		
		f.add(p);
		
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("./lts2.png"));
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
