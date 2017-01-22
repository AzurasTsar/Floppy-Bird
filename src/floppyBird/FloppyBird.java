package floppyBird;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;
//credit to Jaryt Bustard for the base code for this program
//the tutorial video can be found here:https://www.youtube.com/watch?v=I1qTZaUcFX0&lc=z013szx5pksikw1jmy22uvljayqyqj3iky
public class FloppyBird implements ActionListener, MouseListener, KeyListener {
	
	public static FloppyBird floppyBird;
	public final int WIDTH=2000, HEIGHT=1000;
	public Renderer renderer;
	public Rectangle bird;
	public Random rand;
	public int ticks,yMotion,highScore;
	public int score=0;
	public static Boolean gameOver=false;
	public Boolean started=false;
	public char pause='p';
	public char pause2='P';
	
	static AudioInputStream clipNameAIS;
	static Clip juan;
	static Clip soundEffect1;
	static Clip gameOverSound;
	static Clip flapEffect;
	static Clip silence;

	
	
	Timer timer=new Timer(20,this);
	
	public ArrayList<Rectangle> columns;
	public int pauseCount=1;
		
	public FloppyBird()
	{
		rand=new Random();
		renderer=new Renderer();
		JFrame frame=new JFrame();
		
		frame.add(renderer);
		frame.addMouseListener(this);
		frame.addKeyListener(this);
		frame.setSize(WIDTH,HEIGHT);
		frame.setTitle("Floppy Bird");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		bird=new Rectangle(WIDTH/2-10,HEIGHT/2-10,50,30);
		columns=new ArrayList<Rectangle>();

		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
		
		
	}
	public void addColumn(Boolean start){
		
		int space=325;
		int width=150;
		int height=50+rand.nextInt(300);
		
		if(start)
		{
			columns.add(new Rectangle(WIDTH+width+columns.size()*300,HEIGHT-height-120,width,height));
			columns.add(new Rectangle(WIDTH+width+(columns.size()-1)*300,0,width,HEIGHT-height-space));
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size()-1).x+600,HEIGHT-height-120,width,height));
			columns.add(new Rectangle(columns.get(columns.size()-1).x,0,width,HEIGHT-height-space));
		}
	}
	
	
	public void paintColumn(Graphics g, Rectangle column){
		g.setColor(Color.cyan);
		g.fillRect(column.x, column.y, column.width-20, column.height-10);
		g.setColor(Color.white);
		g.fillRect(column.x+column.width-20, column.y, 20, column.height-10);
		g.setColor(Color.cyan.darker());
		g.drawRect(column.x, column.y, column.width, column.height-10);
		if(column.y>=HEIGHT/3){
			g.drawRoundRect(column.x-15, column.y, column.width+30, 50, 20, 20);}
			else{
				g.drawRoundRect(column.x-15, column.y+column.height-10, column.width+30, 50,20,20);}
		g.setColor(Color.cyan);
		if(column.y>=HEIGHT/3){
		g.fillRoundRect(column.x+-15, column.y, column.width+30, 50, 20, 20);}
		else{
			g.fillRoundRect(column.x+-15, column.y+column.height-10, column.width+30, 50,20,20);}

		
		
	}
	
	public void flap()
	{
		if(!started)
		{
			started=true;
		}
	
		if(gameOver)
		{
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 50, 30);
			columns.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}
		else if(!gameOver)
		{
			if(yMotion>0)
			{
				yMotion=0;
			}
			else
			{
				yMotion-=15;
			}
		}
	}
	
	//death sound method
	public static void playGameOver()
	{
		//if(gameOver=true){
		try{
			clipNameAIS = AudioSystem.getAudioInputStream(new File("gameoversound.wav"));
			gameOverSound = AudioSystem.getClip();
			gameOverSound.open(clipNameAIS);}
		catch(Exception x){System.out.println("Failure to load sound");}
		
		gameOverSound.setFramePosition(0);
		gameOverSound.start();
		}//}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		int speed = 10;

		ticks++;

		if (started)
		{
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15)
			{
				yMotion += 2;
			}

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0)
				{
					columns.remove(column);

					if (column.y == 0)
					{
						addColumn(false);
					}
				}
			}

			bird.y += yMotion;

			for (Rectangle column : columns)
			{
				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10)
				{
					score++;
					try{
						clipNameAIS = AudioSystem.getAudioInputStream(new File("soundeffect1.wav"));
						soundEffect1 = AudioSystem.getClip();
						soundEffect1.open(clipNameAIS);}
					catch(Exception x){System.out.println("Failure to load sound");}
					soundEffect1.setFramePosition(0);
					soundEffect1.start();
				}

				if (column.intersects(bird))
				{
					gameOver = true;
					playGameOver();
					if (bird.x <= column.x)
					{
						bird.x = column.x - bird.width;
					}
					else
					{
						if (column.y != 0)
						{
							bird.y = column.y - bird.height;
						}
						else if (bird.y < column.height)
						{
							bird.y = column.height;
						}
					}
				}
			}

			if (bird.y > HEIGHT - 150 || bird.y <= 0)
			{
				gameOver = true;
				playGameOver();
			}

			if (bird.y + yMotion >= HEIGHT - 150)
			{
				bird.y = HEIGHT - 150 - bird.height;
				gameOver = true;
		
			}
	
		}

		renderer.repaint();
	}
	

	public void repaint(Graphics g) {
		//background
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//moon
		g.setColor(Color.yellow);
		g.fillOval(100,80,200,200);
		g.setColor(Color.black);
		g.fillOval(150,60,200,200);
		
		//stars
		g.setColor(Color.yellow);
		for(int i=1;i<=200;i++)
		{
			g.fillRect(0+rand.nextInt(WIDTH), 0+rand.nextInt(HEIGHT), 2, 2);
		}
		//grass
		g.setColor(Color.GREEN.darker());
		g.fillRect(0, HEIGHT-150, WIDTH, 150);
		g.setColor(Color.GREEN.brighter());
		g.fillRect(0, HEIGHT-150, WIDTH, 30);
		
		//bird
		g.setColor(Color.white);
		g.fillOval(bird.x, bird.y, bird.width, bird.height);
		
		//iterate over Rectangle ArrayList
		for(Rectangle rect:columns)
		{
			paintColumn(g,rect);
		}
		
		//text and score
		g.setColor(Color.orange);
		g.setFont(new Font("Brush Script MT",1,220));
		if(!started)
		{
			g.drawString("CLICK TO START", 30, HEIGHT/2-100);
		}
		if(started&&!gameOver)
		{
			g.drawString(Integer.toString(score), WIDTH/2-50, 200);
		}
		if(gameOver)
		{
			g.setColor(Color.orange);
			g.drawString("GAME OVER", 290, HEIGHT/2-100);
		}
		
	}
	


	public static void main(String[] args) throws InterruptedException {
		floppyBird=new FloppyBird();
		//play background music
		try{
			clipNameAIS = AudioSystem.getAudioInputStream(new File("Green Greens - Kirby's Dream Land.wav"));//music composed by Jun Ishikawa. All credit to Nintendo/HAL Laboratory and the original creators
			juan = AudioSystem.getClip();
			juan.open(clipNameAIS);}
		catch(Exception e){System.out.println("Failure to load sound");}
		juan.setFramePosition(0);
		juan.start();
		juan.loop(Clip.LOOP_CONTINUOUSLY);
		
		if(gameOver==true)
		{
			playGameOver();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		flap();
		flap();
		try{
			clipNameAIS = AudioSystem.getAudioInputStream(new File("flapeffect.wav"));
			flapEffect = AudioSystem.getClip();
			flapEffect.open(clipNameAIS);}
		catch(Exception x){System.out.println("Failure to load sound");}
		flapEffect.setFramePosition(0);
		flapEffect.start();
	
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		char k=e.getKeyChar();
		if(k==KeyEvent.VK_SPACE)
		{
			flap();
			flap();
			try{
				clipNameAIS = AudioSystem.getAudioInputStream(new File("flapeffect.wav"));
				flapEffect = AudioSystem.getClip();
				flapEffect.open(clipNameAIS);}
			catch(Exception x){System.out.println("Failure to load sound");}
			flapEffect.setFramePosition(0);
			flapEffect.start();
		}
		}
	
	@Override
	public void keyTyped(KeyEvent e) {
		char k=e.getKeyChar();
		if(k==pause||k==KeyEvent.VK_ESCAPE||k==pause2)
		{
			pauseCount++;
		}
		if(pauseCount%2==0)
		{
			juan.stop();
			timer.stop();
		}
		else
		{
			juan.start();
			juan.loop(Clip.LOOP_CONTINUOUSLY);
			timer.start();
		}
		
		
		
	}





}
