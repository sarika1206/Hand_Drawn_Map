package hello;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Tooldraw extends JPanel{
	private static final int BI_WIDTH =900;
	private static final int BI_HEIGHT =900;
	private static final Color LABEL_DRAW_COLOR =new Color(0,0,0);
	private static final Stroke LABEL_DRAW_STROKE =new BasicStroke(1);
	private static final Stroke BIMAGE_DRAW_STROKE =new BasicStroke(4);
	private static final int COLOR_DIV =5;
	private BufferedImage bImage =new BufferedImage(BI_WIDTH,BI_HEIGHT,BufferedImage.TYPE_INT_RGB);
	//private BuffersImage b=new Buffer
	private List<Point>pointList =new ArrayList<Point>();
	private JLabel imageLabel;

	private Random random = new Random();
	private JPanel btnPanel;
	private JLabel image;
	
	
	private List<Color>colorListR =new ArrayList<Color>();
	private Color currentColor;
	JFileChooser filechooser;
	
	private List<String> texts=new ArrayList<String>();
	JComboBox comboBox;
	JComboBox comboColor;
    //String msg;
	
	private List<Shape> shapes=new ArrayList<Shape>();
	private Shape currentShape=null;
	private List<Point> rects1=new ArrayList<Point>();
	private List<Point> rects2=new ArrayList<Point>();
	private Point currentRect1 =null;
	private Point currentRect2 =null;
	private Boolean started = false;
	private Boolean startedRect = false;
	private Image img;
	private Point p;
	private char mode = 'd'; 
	
	
	public Tooldraw(){
		Graphics2D g2d = bImage.createGraphics();
		g2d.setBackground(Color.white);
		g2d.clearRect(0, 0, BI_WIDTH, BI_HEIGHT);
		g2d.dispose();
		imageLabel=new JLabel( new ImageIcon(bImage)){
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
//				Graphics2D g2d = bImage.createGraphics();				
				super.paintComponent(g);
				//paintInLabel(g);
				for(Shape s:shapes){
					g2d.draw(s);
					//System.out.println(s.getBounds());						
				}
				for(int i=0;i<rects1.size();i++)
				{
					Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.1f);
					g2d.setComposite(comp);
					g2d.setPaint(colorListR.get(i));
					g2d.fillRect(rects1.get(i).x,rects1.get(i).y, rects2.get(i).x-rects1.get(i).x,  rects2.get(i).y-rects1.get(i).y);
					comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
					g2d.setComposite(comp);
					if(!startedRect)
						g2d.drawString(texts.get(i), (rects1.get(i).x+rects2.get(i).x)/2, (rects1.get(i).y+rects2.get(i).y)/2);
				}
				
				
			}
		};
		
		MyMouseAdapter  myMouseAdapter = new MyMouseAdapter();
		imageLabel.addMouseListener(myMouseAdapter);
		imageLabel.addMouseMotionListener(myMouseAdapter);
		imageLabel.setBorder(BorderFactory.createEtchedBorder());
		
		JButton saveImageBtn=new JButton("SaveImage");
		saveImageBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				Graphics2D g2d = (Graphics2D) g;
				Graphics2D g2d = bImage.createGraphics();	
				g2d.setColor(new Color(0,0,0));
				g2d.setStroke(new BasicStroke(1));
//				super.paintComponent(g);
				//paintInLabel(g);
				for(Shape s:shapes){
					g2d.draw(s);
					//System.out.println(s.getBounds());						
				}
				for(int i=0;i<rects1.size();i++)
				{
					Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.1f);
					g2d.setComposite(comp);
					g2d.setPaint(colorListR.get(i));
					g2d.fillRect(rects1.get(i).x,rects1.get(i).y, rects2.get(i).x-rects1.get(i).x,  rects2.get(i).y-rects1.get(i).y);
					comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
					g2d.setComposite(comp);
					if(!startedRect)
						g2d.drawString(texts.get(i), (rects1.get(i).x+rects2.get(i).x)/2, (rects1.get(i).y+rects2.get(i).y)/2);
				}
				saveImageActionPerformed();
			}
		});
		JButton clearImageBtn = new JButton("ClearImage");
		clearImageBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
								
				shapes.clear();
				rects1.clear();
				rects2.clear();
				colorListR.clear();
				colorListR.add(Color.BLACK);
				texts.clear();
				Graphics2D g2d = bImage.createGraphics();
				g2d.setBackground(Color.white);
				g2d.clearRect(0, 0, BI_WIDTH, BI_HEIGHT);
				g2d.dispose();
				repaint();
			}
		});
		JButton loadImageBtn=new JButton("LoadImage");
		loadImageBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadImageActionPerformed();
			}
		});
		JButton drawImageBtn=new JButton("DrawLine");
		drawImageBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawImageActionPerformed();
			}
		});
		JButton rectImageBtn=new JButton("DrawRectangle");
		rectImageBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rectImageActionPerformed();
			}
		});
		JButton  SaveVertexBtn=new JButton("SaveVertexFile");
		SaveVertexBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				wrtietofile();
			}
		});
		JButton HelpBtn=new JButton("Help");
		HelpBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null,"This program has two modes:\n\n1. Polygon    2. Rectangle\n\n\n1. Polygon:\n    Left Click    =  Draw a Polygon\n    Right Click  = Stop Drawing \n\n\n2. Rectangle:\n    Select from or type into the dropdown list to add labels to the rectangles.\n    Rectangle colors can be picked from the dropdown list. ");
				//HelpActionPerformed();
			}
		});
		setLayout(new BorderLayout());
		//ComboBOx For LIST OF ITEM IN Av BUILDING
		comboBox = new JComboBox();                                     
		GridBagConstraints gbc_comboBox =  new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		comboBox.setEditable(true);
		comboBox.addItem("");
		comboBox.addItem("Room");
		comboBox.addItem("Bathroom");
		comboBox.addItem("Hall");
		

		
		//SELECT COLOR 
		comboColor = new JComboBox();
		//comboBox.setEditable(true);
		comboColor.addItem("BLACK");
		comboColor.addItem("BLUE");
		comboColor.addItem("CYAN");
		colorListR.add(Color.BLACK);
		comboColor.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e)
			{
				colorListR.set(colorListR.size() - 1, strToClr(comboColor.getSelectedItem().toString()));
			}
		});
		
		
		
		btnPanel = new JPanel();
		btnPanel.add(saveImageBtn);
		btnPanel.add(loadImageBtn);
		btnPanel.add(rectImageBtn);
		btnPanel.add(drawImageBtn);
		btnPanel.add(SaveVertexBtn);
		btnPanel.add(comboBox);
		btnPanel.add(comboColor);
		btnPanel.add(HelpBtn);
		btnPanel.add(clearImageBtn);
		add(btnPanel,BorderLayout.SOUTH);
		add(imageLabel,BorderLayout.CENTER);
		
		image = new JLabel(" ");
		image.setOpaque(false);
		add(image,BorderLayout.NORTH);

	}
	
	private Color strToClr(String s)
	{
		if(s == "BLACK")
		{
			return Color.BLACK ;
		}
		else if(s  == "BLUE")
		{
			return Color.BLUE;
		}
		else if(s  == "CYAN")
		{
			return Color.CYAN;
		}
		else
				return Color.BLACK;
		
	}
	
	public void wrtietofile(){
		filechooser = new JFileChooser();
		int result = filechooser.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION){
			File ff = filechooser.getSelectedFile();
							
			try{
			OutputStream f = new FileOutputStream(ff); 
			for(Shape s:shapes){
				Line2D.Double l = (Line2D.Double)s;
				f.write(("" + (int)l.getP1().getX()).getBytes());
				f.write(" ".getBytes());
				f.write(("" + (int)l.getP1().getY()).getBytes());
				f.write(" ".getBytes());
				f.write(("" + (int)l.getP2().getX()).getBytes());
				f.write(" ".getBytes());
				f.write(("" + (int)l.getP2().getY()).getBytes());
				f.write("\n".getBytes());
			}
			System.out.println("Written " +  shapes.size() + " Lines Successfully!");
			f.write("\n".getBytes());
			for(int i=0;i<rects1.size();i++){
				Point l = rects1.get(i);
				Point l2 =rects2.get(i);
				f.write(("" + (int)l.getX()).getBytes());
				f.write(" ".getBytes());
				f.write(("" + (int)l.getY()).getBytes());
				f.write(" ".getBytes());
				f.write(("" + (int)l2.getX()).getBytes());
				f.write(" ".getBytes());
				f.write(("" + (int)l2.getY()).getBytes());

				f.write((" "+texts.get(i)).getBytes());
				f.write("\n".getBytes());
			}
			System.out.println("Written " +  rects1.size() + " Rectangles Successfully!");
			f.close();
			
			
			
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
		}
		}
	}
	
	private void rectImageActionPerformed()
	{
		mode = 'r';
		if (started)
		{
			started = false;
			shapes.remove(shapes.size()-1);
			//shapes.remove(shapes.size()-1);
		}
		
		repaint();
		
	}
	private void drawImageActionPerformed()
	{
		mode = 'd';
		if(startedRect)
		{
			startedRect = false;
			rects1.remove(rects1.size()-1);
			rects2.remove(rects2.size()-1);
			
			
		}
		repaint();
	}
	private void loadImageActionPerformed() {
		
	    filechooser = new JFileChooser();
		int returnVal = filechooser.showOpenDialog(this);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			
			try{
				 imageLabel.setIcon(new ImageIcon(ImageIO.read(file)));
				 repaint();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}		
	}

	private void saveImageActionPerformed() {
		
		JFileChooser filechooser = new JFileChooser();
		FileNameExtensionFilter filter =new FileNameExtensionFilter("JPG Imaged","jpg","bmp","png");
		filechooser.setFileFilter(filter);
		int result = filechooser.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION){
			File saveFile = filechooser.getSelectedFile();
			try{
				ImageIO.write(bImage,"png",saveFile);
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private void paintInLabel(Graphics g) {
		Graphics2D g2d= (Graphics2D) g;
		g2d.setColor(LABEL_DRAW_COLOR);
		g2d.setStroke(LABEL_DRAW_STROKE);
		if(pointList.size()<2){
			return;
		}
		for(int i=1;i<pointList.size();i++)
		{
			int x1 = pointList.get(i-1).x;
			int y1 = pointList.get(i-1).y;
			int x2 = pointList.get(i).x;
			int y2 = pointList.get(i).y;
			g2d.drawLine(x1, y1, x2, y2);;
		}
	}
	
	private class MyMouseAdapter extends MouseAdapter{
		public void mousePressed(MouseEvent e){

			if (mode == 'd')
			{
				if (e.getButton() == 2)
				{
					//writeToFile();
					return;
				}
				if (!started && e.getButton() == 1)
				{
					p = e.getPoint();
					//System.out.println("In here");
					currentShape = new Line2D.Double(p, p);
					started = true;
					shapes.add(currentShape);
				}
				else if (started && e.getButton() == 1)
				{
					currentShape = new Line2D.Double(p, e.getPoint());
					
					p = e.getPoint();
					shapes.add(currentShape);
				}
				
				if (e.getButton() == 3 && started == true)
				{
					started = false;
					shapes.remove(shapes.size()-1);
					//shapes.remove(shapes.size()-1);
				}
				
				repaint();
			}
			
			else if (mode == 'r' )
			{
				if (!startedRect)
				{
					currentRect1 = e.getPoint();
					currentRect2= new Point(0,0);
					startedRect=true;							
					
				}
				else
				{
					currentRect2 = e.getPoint();
					startedRect=false;
					rects1.remove(rects1.size()-1);
					rects2.remove(rects2.size()-1);
					texts.add(comboBox.getSelectedItem().toString());
					colorListR.add(strToClr(comboColor.getSelectedItem().toString()));
					
				}
				rects1.add(currentRect1);
				rects2.add(currentRect2);
				repaint();
			}
		}
		

		public void mouseReleased(MouseEvent e){
			
		}

	public void mouseDragged(MouseEvent e){
		
	}
	
	public void mouseMoved(MouseEvent e){
		if (started){
			Line2D shape = (Line2D)currentShape;
			shape.setLine(p, e.getPoint());
			repaint();
		}
		
		if(startedRect)
		{
			
			rects2.set(rects2.size()-1, e.getPoint());
			repaint();
		}
		
	}
	}
	


	private static void createAndShowUI()
	{
		JFrame frame = new JFrame("DrawAndSaveImage");
		frame.getContentPane().add(new Tooldraw());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable(){
		public void run(){
			createAndShowUI();
		}
	});
	
	}
}
