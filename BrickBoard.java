import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class BrickBoard  extends JPanel implements Runnable, MouseListener
{
    boolean ingame = false;//you can set to false to control start of game
    private Dimension d;
    int BOARD_WIDTH=500;
    int BOARD_HEIGHT=500;
    int x = 0;//vars for starting coordinates and size of sprites
    int bx = 20;
    int by = 200;
    int xSpeed = 4;
    int ySpeed = 5;
    int ballW = 20;
    Brick[] bricks = new Brick[24];
    boolean[] showBrick = new boolean[24];
    int paddleW = 100;
    int paddleH = 25;
    int paddleX = (BOARD_WIDTH/2)-50;//starting coordinates for the paddle
    int paddleY = 400;
    boolean padRight = false;
    boolean padLeft = false;
    int points = 0;//vars for points (earned when bricks are broken) and lives (lost when ball hits rock bottom)
    int lives = 3;
    BufferedImage img;
    String message = "Click the Board to Start";
    private Thread animator;
    
    public BrickBoard()
    {
        addKeyListener(new TAdapter());
        addMouseListener(this);
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.black);
        //remove comments below if you'd like to add an image to your game
                
        try {
        img = ImageIO.read(this.getClass().getResource("breakoutBoard.png"));//img of fdti breakout boards for bricks
        } catch (IOException e) {
        System.out.println("Image could not be read");
        // System.exit(1);
        }
         
        if (animator == null || !ingame) {
            animator = new Thread(this);
            animator.start();
        }

        setDoubleBuffered(true);
        int xx = 20;
        int yy = 20;
        int count = 1;
        
        for (int i = 0; i < bricks.length; i++)
        {
            bricks[i] = new Brick(xx, yy, 50, 25);
            showBrick[i] = true;
            xx += 55;
            if (count%8==0)
            {
                xx = 20;
                yy += 25;
            }
            count++;
        }
    }

    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);//makes a black background

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);
        g.setColor(Color.blue);
        g.setFont(small);
        //g.drawString(message, 10, d.height-60);

        g.drawString(message, 10, d.height-60);//prints message var
        g.drawString("Lives: " + lives, 10, d.height-45);//prints lives and lives var
        g.drawString("Points: " + points, 75, d.height-45);//prints points and points var
        g.fillRect (paddleX, paddleY, paddleW, paddleH);//draws the paddle
        g.fillOval(bx, by, ballW, ballW);//and the ball
        for (int i = 0; i < bricks.length; i++)//creates the bricks
        {
            if(showBrick[i] == true)//and if they should be shown
            {
                //g.fillRect(bricks[i].x, bricks[i].y, bricks[i].w, bricks[i].h);
                g.drawImage(img, bricks[i].x, bricks[i].y, bricks[i].w, bricks[i].h, null);//draws the bricks using the img
            }
        }
        if (lives < 1)//if lives is 0 or lower
        {
            message = "You have Lost";//prints 
            ingame = false;//and stops the game
        }
        if (points == 24)//if all bricks have been broken
        {
            message = "You have Won";//prints
            ingame = false;//and stops the game
        }
        if (ingame) {//if the game is active
            bounceBall();//the ball will move
            if(padRight == true)//and the paddle can be controlled
            {
                paddleX = paddleX + 5;
            }
            if(padLeft == true)
            {
                paddleX = paddleX - 5;
            }
            // g.drawImage(img,0,0,200,200 ,null);//if you want to display an image
        }
        
        Toolkit.getDefaultToolkit().sync();
        g.dispose();

    }
    public void bounceBall()
    {
        if (bx > BOARD_WIDTH || bx < 0)//if the ball hits the sides
        {
            xSpeed = xSpeed * -1;//sleeping in reverse and everybody's bouncin off the wall
        }
        if (by < 0)//or the top of the screen
        {
            ySpeed = ySpeed * -1;//sleeping in reverse and everybody's bouncin off the wall
        }
        if (by > BOARD_HEIGHT)//if the ball hits rock bottom
        {
            lives -=1;//lives var decreases by one
            bx = 20;//ball is reset to original coordinates
            by = 200;
        }
        if (bx + ballW > paddleX && bx < paddleX + paddleW && by+ballW > paddleY && by < paddleY + paddleH)//if the ball hits the paddle
        {
            ySpeed *= -1;//the ball moves in the opposite y direction
        }
        for (int i = 0; i < bricks.length; i++)
        {
            if (showBrick[i] == true && bx+ballW > bricks[i].x && bx < bricks[i].x + bricks[i].w && by+ballW > bricks[i].y && by< bricks[i].y + bricks[i].h)//if the ball hits a brick
            {
                ySpeed *= -1;//the ball moves in the opposite y direction
                points += 1;//points var increases by one
                showBrick[i] = false;//and that brick is no longer visible
            }
        }
        bx += xSpeed;//also the ball x and y vals should change
        by += ySpeed;
        
    }
    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            padRight = false;
            padLeft = false;

        }

        public void keyPressed(KeyEvent e) {
            //System.out.println( e.getKeyCode());
            // message = "Key Pressed: " + e.getKeyCode();
            int key = e.getKeyCode();
            //message = "key pressed: " + key;//leave for troubleshooting

            if(key==39){//if the right key is pressed
                padRight = true;//the paddle moves to the right
            }
            if(key==37)//same with left
            {
                padLeft = true;
            }

        }
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        message = "Brick Breaker demo (1985)";//totally correct dev year
        ingame = true;//the game actually starts once the mouse is pressed
        
        

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();
        int animationDelay = 20;//control FPS of board
        long time = 
            System.currentTimeMillis();
        while (true) {//infinite loop
            // spriteManager.update();
            repaint();
            try {
                time += animationDelay;
                Thread.sleep(Math.max(0,time - 
                        System.currentTimeMillis()));
            }catch (InterruptedException e) {
                System.out.println(e);
            }//end catch
        }//end while loop

    }//end of run
}//end of class

