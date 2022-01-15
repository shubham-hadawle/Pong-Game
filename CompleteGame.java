import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class CompleteGame extends JFrame implements ActionListener
{
   JLabel luser,lpass;
   JTextField tuser;
   JPasswordField p;
   JButton blogin,bsignup;
   
   Login()
   {
     	 luser= new JLabel("Username");
	 lpass= new JLabel ("Password");
	 
	 tuser= new JTextField();
	 p= new JPasswordField();
	 blogin= new JButton("Login");
	 bsignup= new JButton("Sign Up");
	 
	 luser.setBounds(100,200,200,35);
	 lpass.setBounds(100,300,200,35);
	 tuser.setBounds(350,200,200,35);
	 p.setBounds(350,300,200,35);
	 blogin.setBounds(150,400,100,30);
	 bsignup.setBounds(300,400,100,30);
	 
	 add(luser);
	 add(lpass);
	 add(tuser);
	 add(p);
	 add(blogin);
	 add(bsignup);
	 
	 blogin.addActionListener(this);
	 bsignup.addActionListener(this);
	 
   }
	 
	 public void actionPerformed(ActionEvent ae)
	 {
		 if(ae.getActionCommand().equals("Login"))
		 {
			 try
		    {
		  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();//this statement loads driver for the database
		  Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/pongGame","root","Shubham18*");
		  
		  Statement st= con.createStatement();
		  ResultSet rs=st.executeQuery("select password from players where username='"+tuser.getText()+"'");
		  
		 
		
		  if(rs.next())
		  {
			  
			  System.out.println("Password: "+rs.getString("password"));
			  System.out.println("Entered password: "+String.valueOf(p.getPassword()));
			 // String password = String.valueOf(jPasswordField.getPassword());
			  if(rs.getString("password").equals(String.valueOf(p.getPassword())))
			  {
				//System.out.println("Success");
				this.setVisible(false);
				PongGame p = new PongGame();
			  }

		          else
				   System.out.println("Wrong password");
			   
		  }
		  else
		  {
			  System.out.println("Wrong username");
		  }
		  con.close();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		  
		 }
		 else
		 {
			  try
		    {
		  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();//this statement loads driver for the database
		  Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/pongGame","root","Shubham18*");
		  
		  String query= "insert into players values (?,?)";
		  PreparedStatement st= con.prepareStatement(query);
		  st.setString(1,tuser.getText());
		  st.setString(2,String.valueOf(p.getPassword()));
		  
		  st.execute();
		  con.close();
		  }
		  
		  catch(Exception e)
		  {
			  System.out.println(e);
		  }
		 }
		 
		 }
   public static void main(String args[])
   {
     Login l= new Login();
	 l.setSize(1366,768);
	 l.setLayout(null);
	 l.setTitle("User");
	 l.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 l.setVisible(true);
   
   }
}

class PongGame {

	PongGame()
	{
		GameFrame frame = new GameFrame();
	}

}

class GameFrame extends JFrame
{
	GamePanel panel;
	
	GameFrame()
	{
		panel = new GamePanel();
		this.add(panel);
		this.setTitle("Pong Game");
		this.setResizable(false);
		this.setBackground(Color.black);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
}

class GamePanel extends JPanel implements Runnable
{
	static final int GAME_WIDTH = 1000;
	static final int GAME_HEIGHT = (int )(GAME_WIDTH * (0.5555));
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
	static final int BALL_DIAMETER = 20;
	static final int PADDLE_WIDTH = 25;
	static final int PADDLE_HEIGHT = 100;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle1;
	Paddle paddle2;
	Ball ball;
	Score score;
	
	GamePanel() {
		newPaddles();
		newBall();
		score = new Score(GAME_WIDTH, GAME_HEIGHT);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void newBall() {
		random = new Random();
		ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
	}

	public void newPaddles() {
		paddle1 = new Paddle(0, (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
		paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH, (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
	}

	public void paint(Graphics g) {
		image = createImage(getWidth(), getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image, 0, 0, this);
	}

	public void draw(Graphics g) {
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
	}

	public void move() {
		paddle1.move();
		paddle2.move();
		ball.move();
	}

	public void checkCollision()
	{
		//Bounce the Ball off the Top & Bottom WIndow Edges
		if(ball.y <= 0)
		{
			ball.setYDirection(-ball.yVelocity);
		}
		if(ball.y >= GAME_HEIGHT-BALL_DIAMETER)
		{
			ball.setYDirection(-ball.yVelocity);
		}
		
		//Bounce the Ball off the Paddles
		if(ball.intersects(paddle1))
		{
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++;	//Optional for creating more Difficulty
			if(ball.yVelocity > 0)
				ball.yVelocity++;	//Optional for creating more Difficulty
			else
				ball.yVelocity--;	//Optional for creating more Difficulty
			ball.setXDirection(ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		
		if(ball.intersects(paddle2))
		{
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++;	//Optional for creating more Difficulty
			if(ball.yVelocity > 0)
				ball.yVelocity++;	//Optional for creating more Difficulty
			else
				ball.yVelocity--;	//Optional for creating more Difficulty
			ball.setXDirection(-ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		
		//Stops Paddles at Window Edges
		if(paddle1.y <=  0)
			paddle1.y = 0;
		if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
		
		if(paddle2.y <= 0)
			paddle2.y = 0;
		if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
		
		//Gives a Player a Point and Creates a new Ball
		if(ball.x <= 0)
		{
			score.player2++;
			newPaddles();
			newBall();
			System.out.println("Player 2's score: " + score.player2);
		}
		
		if(ball.x >= GAME_WIDTH-BALL_DIAMETER)
		{
			score.player1++;
			newPaddles();
			newBall();
			System.out.println("Player 1's score: " + score.player2);
		}
	}

	public void run() {
		//Game Loop
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		
		while(true)
		{
			long now = System.nanoTime();
			delta += (now - lastTime)/ns;
			lastTime = now;
			if(delta >= 1)
			{
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}

	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle2.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
	}
}

class Paddle extends Rectangle
{
	int id;
	int yVelocity;
	int speed = 10;
	
	Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id)
	{
		super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		this.id=id;
	}
	
	public void keyPressed(KeyEvent e)
	{
		switch(id)
		{
			case 1:
				if(e.getKeyCode()==KeyEvent.VK_W)
				{
					setYDirection(-speed);
					move();
				}
				
				if(e.getKeyCode()==KeyEvent.VK_S)
				{
					setYDirection(speed);
					move();
				}
				break;
				
			case 2:
				if(e.getKeyCode()==KeyEvent.VK_UP)
				{
					setYDirection(-speed);
					move();
				}
				
				if(e.getKeyCode()==KeyEvent.VK_DOWN)
				{
					setYDirection(speed);
					move();
				}
				break;
		}
	}

	public void keyReleased(KeyEvent e)
	{
		switch(id)
		{
			case 1:
				if(e.getKeyCode()==KeyEvent.VK_W)
				{
					setYDirection(0);
					move();
				}
				
				if(e.getKeyCode()==KeyEvent.VK_S)
				{
					setYDirection(0);
					move();
				}
				break;
				
			case 2:
				if(e.getKeyCode()==KeyEvent.VK_UP)
				{
					setYDirection(0);
					move();
				}
				
				if(e.getKeyCode()==KeyEvent.VK_DOWN)
				{
					setYDirection(0);
					move();
				}
				break;
		}
	}
	
	public void setYDirection(int yDirection)
	{
		yVelocity = yDirection;
	}
	
	public void move()
	{
		y = y + yVelocity;
	}
	
	public void draw(Graphics g)
	{
		if(id==1)
			g.setColor(Color.blue);
		else
			g.setColor(Color.red);
		
		g.fillRect(x, y, width, height);
	}
}

class Ball extends Rectangle
{
	Random random;
	int xVelocity;
	int yVelocity;
	int initialSpeed = 2;
	
	Ball(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		random = new Random();
		
		int randomXDirection = random.nextInt(2);
		if(randomXDirection == 0)
			randomXDirection--;
		setXDirection(randomXDirection*initialSpeed);
		
		int randomYDirection = random.nextInt(2);
		if(randomYDirection == 0)
			randomYDirection--;
		setYDirection(randomYDirection*initialSpeed);
	}
	
	public void setXDirection(int randomXDirection)
	{
		xVelocity = randomXDirection;
	}
	
	public void setYDirection(int randomYDirection)
	{
		yVelocity = randomYDirection;
	}
	
	public void move()
	{
		x += xVelocity;
		y += yVelocity;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.white);
		g.fillOval(x, y, height, width);
	}
}

class Score extends Rectangle
{
	static int GAME_WIDTH;
	static int GAME_HEIGHT;
	int player1;
	int player2;
	
	Score(int GAME_WIDTH, int GAME_HEIGHT)
	{
		Score.GAME_WIDTH = GAME_WIDTH;
		Score.GAME_HEIGHT = GAME_HEIGHT;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.white);
		g.setFont(new Font("Consolas", Font.PLAIN, 60));
		
		g.drawLine(GAME_WIDTH/2, 0, GAME_WIDTH/2, GAME_HEIGHT);
		
		g.drawString(String.valueOf(player1/10) + String.valueOf(player1%10), (GAME_WIDTH/2)-85,  50);
		g.drawString(String.valueOf(player2/10) + String.valueOf(player2%10), (GAME_WIDTH/2)+20,  50);
	}
}
