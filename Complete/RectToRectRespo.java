import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.EventQueue;

public class RectToRectRespo {
	PointObject ro1;
	Rectangle2D r1,r2,r3;
	boolean xCollide,yCollide;
	boolean prev_x_col,prev_y_col;
	
	public static void main(String[] args) {
		new RectToRectRespo();
	}
	
	public RectToRectRespo() {
		
		ro1 = new PointObject(0f,0f);
		//mouse-controlled rect
		r1 = new Rectangle2D.Float();
		//stationary rect
		r2 = new Rectangle2D.Float(150f,200f,80f,50f);
		//collision response rect
		r3 = new Rectangle2D.Float();
		
		EventQueue.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				JFrame jf = new JFrame("RectToRectRespo");
				Panel pnl = new Panel();
				pnl.addMouseMotionListener(new MouseMotion());
				jf.add(pnl);
				jf.pack();
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setLocationRelativeTo(null);
				jf.setVisible(true);
			}
			
		});
		
	}
	
	void checkCollision(){
		
		//If xCollide and yCollide are not both true then
		//it means that we don't have a complete collision
		//we need to get xCollide and yCollide values before
		//r1 gets a complete collision
		if(!xCollide || !yCollide){
			prev_x_col = xCollide;
			prev_y_col = yCollide;
		}
		
		if(r1.getX() < r2.getX() + r2.getWidth() && 
		   r1.getX() + r1.getWidth() > r2.getX()) xCollide = true;
		else xCollide = false;
		
		if(r1.getY() < r2.getY() + r2.getHeight() && 
		   r1.getY() + r1.getHeight() > r2.getY()) yCollide = true;
		else yCollide = false;
	}
	
	//Respond to collision if the subject rectangle is in collision
	//state
	void collisionRespo(){
		//get the centers of rects
		double r1_center_x = r1.getX() + r1.getWidth() * 0.5;
		double r1_center_y = r1.getY() + r1.getHeight() * 0.5;
		double r2_center_x = r2.getX() + r2.getWidth() * 0.5;
		double r2_center_y = r2.getY() + r2.getHeight() * 0.5;
		
		//if xCollide and yCollide are false then r1 had collided
		//with r2 at the start of the program or r1 emerged from 
		//no collision area(corners)
		if(!prev_x_col && !prev_y_col){
			//Basic solution
			//prev_x_col = true;
			//or
			//prev_y_col = true;
			
			//Sophisticated solution: Finding the shortest overlap
			//if the overlap in x axis is longer than the overlap in y
			//then r1 should probably be on top or bottom. Otherwise,
			//r1 should probably be on left or right.
			double avgWidth = ( r1.getWidth() + r2.getWidth() ) * 0.5;
			double avgHeight = ( r1.getHeight() + r2.getHeight() ) * 0.5;
			double dst_x = Math.abs(r1_center_x - r2_center_x);
			double dst_y = Math.abs(r1_center_y - r2_center_y);
			double overlap_x = avgWidth - dst_x;
			double overlap_y = avgHeight - dst_y;
			if(overlap_x > overlap_y) prev_x_col = true;
			else prev_y_col = true;
		}
		
		//if previous xCollide is true it means that r1 emerged from
		//the top or bottom of r2
		if(prev_x_col){
			//if r1_center_y is less than r2_center_y it means that r1
			//stays at top of r2. Otherwise, r1 stays at bottom of r2 
			if(r1_center_y < r2_center_y)
			  r3.setRect(r1.getX(), r2.getY() - r1.getHeight(), 40f, 40f);
		    else
			  r3.setRect(r1.getX(), r2.getY() + r2.getHeight(), 40f, 40f);
		}
		//if previous yCollide is true it means that r1 emerged from left
		//or right of r2
		else if(prev_y_col){
			//if r1_center_x is less than r2_center_x it means that r1
			//stays at left of r2. Otherwise, r1 stays at right of r2
			if(r1_center_x < r2_center_x)
			  r3.setRect(r2.getX() - r1.getWidth(), r1.getY(), 40f, 40f);
		    else
			  r3.setRect(r2.getX() + r2.getWidth(), r1.getY(), 40f, 40f);
		}
		
	}
	
	void updateData(){
		//System.out.println("updating...");
		checkCollision();
		if(xCollide && yCollide) collisionRespo();
	}
	
	void drawObjects(Graphics2D g2d){
		//System.out.println("drawing objects...");
		g2d.setPaint(Color.GREEN);
		//if(xCollide && yCollide) g2d.setPaint(Color.RED);
		g2d.fill(r2);
		r1.setRect(ro1.getX(), ro1.getY(), 40f, 40f);
		if(xCollide && yCollide){
			g2d.setPaint(Color.YELLOW);
			g2d.fill(r3);
			g2d.setPaint(Color.RED);
			g2d.draw(r1);
		}
		else { 
		    g2d.setPaint(Color.YELLOW);
			g2d.fill(r1);
		}
		
		
	}
	
	class Panel extends JPanel {
		
		Panel(){
			Timer timer = new Timer(16, new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e){
					updateData();
					repaint();
				}
			});
			timer.start();
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400,400);
		}
		
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setPaint(Color.BLACK);
			g2d.fillRect(0,0,getWidth(),getHeight());
			drawObjects(g2d);
			g2d.setPaint(Color.WHITE);
			g2d.drawString("Mouse-controlled square will turn red once there's a collision",40f,20f);
			g2d.drawString("xCollide: "+xCollide +", yCollide: " + yCollide,40f,40f);
			//g2d.drawString("prev_x_col: "+prev_x_col +", prev_y_col: " + prev_y_col,40f,40f);
			//g2d.drawString("Stationary square will turn red once there's a collision",60f,20f);
			g2d.dispose();
		}
	}
	
	class PointObject
	{
		private float x,y;
	
		PointObject(float x, float y)
		{
			this.x = x;
			this.y = y;
		
		}
	
		float getX(){return x;}
		float getY(){return y;}
	
		void setX(float x){this.x = x;}
		void setY(float y){this.y = y;}
	}
	
	class MouseMotion implements MouseMotionListener {
	
		@Override
		public void mouseDragged(MouseEvent e){}
	
		@Override
		public void mouseMoved(MouseEvent e){
			ro1.setX(e.getX());
			ro1.setY(e.getY());
		}
	}
	
}

