package Snake;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.JPanel;

/**
 * Created by ying on 17/07/2017.
 */
public class Snake implements ActionListener, KeyListener {
    /* ========================== DECLARATIONS ======================== */

    //Graphics Declarations
    public JFrame jframe;
    public RenderPanel renderPanel;
    public Dimension dim, frameDim;
    public int bottomFrame, rightFrame;

    public Timer timer = new Timer(20, this);
    public int ticks = 0;

    //Snake body
    public static Snake snake;
    public ArrayList<Point> snakeParts = new ArrayList<Point>();
    public Point head, cherry;
    public int tailLength;

    //Constants
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    public static final int SCALE = 10;

    //Helper variables
    public int direction, score;
    public boolean over, paused;
    public Random random;

    /* ====================== FUNCTIONS & CLASSES ==================== */

    //Graphics
    public class RenderPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);
            g.fillRect(0, 0, frameDim.width, frameDim.height);
            g.setColor(Color.blue);
            for(Point point: snakeParts){
                g.fillRect(point.x*SCALE, point.y*SCALE, SCALE, SCALE);
            }
            g.fillOval(head.x*SCALE, head.y*SCALE, SCALE, SCALE);
            g.setColor(Color.red);
            g.fillOval(cherry.x*SCALE, cherry.y*SCALE, SCALE, SCALE);
        }
    }

    public Snake() {
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        frameDim = new Dimension(800,700);
        bottomFrame = frameDim.height/SCALE - SCALE/2;
        rightFrame = frameDim.width/SCALE - 2;

        jframe = new JFrame("Snake");
        jframe.setVisible(true);
        jframe.setSize(frameDim.width-4, frameDim.height-SCALE/2);
        jframe.setResizable(false);
        jframe.setLocation(dim.width / 2 - jframe.getWidth() / 2, dim.height / 2 - jframe.getHeight() / 2);
        jframe.add(renderPanel = new RenderPanel());
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.addKeyListener(this);
        startGame();
    }

    //Initialise
    public void startGame(){
        over = false;
        paused = false;
        score = 0;
        tailLength = 5;

        snakeParts.clear();
        random = new Random();
        head = new Point(random.nextInt(rightFrame+1), random.nextInt(bottomFrame+1));
        direction = random.nextInt(4);
        buildInitialTail();

        cherry = new Point(random.nextInt(rightFrame+1), random.nextInt(bottomFrame+1));
        while(CherryInSnake()){
            cherry = new Point(random.nextInt(frameDim.width / SCALE), random.nextInt(frameDim.height / SCALE));
        }
        timer.start();
    }

    //Helper func
    //To ensure cherry does not appear on the body of snake
    public boolean CherryInSnake(){
        if(cherry.equals(head))
            return true;
        for(int i=0; i < snakeParts.size(); i++){
            if(cherry.equals(snakeParts.get(i)))
                return true;
        }
        return false;
    }

    //Helper func
    //To check for game over
    public void headInTail(){
        for(int i=0; i < snakeParts.size(); i++){
            if(head.equals(snakeParts.get(i))) {
                over = true;
                break;
            }
        }
    }

    //Snake initial body builder
    public void buildInitialTail(){
        if(tailLength > 0){
            for(int j=tailLength-1; j >= 0; j--){
                if(direction == UP){
                    if(head.y+j+1 > bottomFrame){
                        snakeParts.add(new Point(head.x, head.y + j - bottomFrame));
//                        System.out.println(j+"y up 1 = " + (head.y + j - bottomFrame));
                    } else {
                        snakeParts.add(new Point(head.x, head.y + j + 1));
//                        System.out.println(j+"y up 2 = " + (head.y + j + 1));
                    }
                } else if (direction == DOWN) {
                    if(head.y-j-1 < 0){
                        snakeParts.add(new Point(head.x, bottomFrame+head.y-j));
//                        System.out.println(j+"y down 1 = " + (bottomFrame+head.y-j));
                    } else {
                        snakeParts.add(new Point(head.x, head.y-j-1));
//                        System.out.println(j+"y down 2 = " + (head.y-j-1));

                    }
                } else if (direction == LEFT){
                    if(head.x+j+1 > rightFrame){
                        snakeParts.add(new Point(head.x+j-rightFrame, head.y));
//                        System.out.println(j+"x left 1 = " + (head.x+j-rightFrame));
                    } else {
                        snakeParts.add(new Point(head.x+j+1, head.y));
//                        System.out.println(j+"x left 2 = " + (head.x+j+1));
                    }
                } else{
                    if(head.x-j-1 < 0){
                        snakeParts.add(new Point(rightFrame+head.x-j, head.y));
//                        System.out.println(j+"x right 1 = " + (rightFrame+head.x-j));
                    } else {
                        snakeParts.add(new Point(head.x-j-1, head.y));
//                        System.out.println(j+"x right 2 = " + (head.x-j-1));
                    }
                }
            }
        }
    }


    /* =========================== OVERRIDES ========================= */
    @Override
    public void actionPerformed(ActionEvent e) {
        renderPanel.repaint();

        ticks++;
        if (ticks % 5 == 0 && head != null && !over && !paused) {
            snakeParts.add(new Point(head.x, head.y));
            if(snakeParts.size()>tailLength)
                snakeParts.remove(0);
            if (cherry != null) {
                if (head.equals(cherry)) {

                    score += 10;
                    tailLength++;
                    cherry = new Point(random.nextInt(rightFrame+1), random.nextInt(bottomFrame+1));
                    while(CherryInSnake()){
                        cherry = new Point(random.nextInt(frameDim.width / SCALE), random.nextInt(frameDim.height / SCALE));
                    }
                }
            }
            if (direction == UP) {
                if(head.y - 1 >= 0)
                    head = new Point(head.x, head.y - 1);
                else
                    head = new Point(head.x, bottomFrame);
            }
            else if (direction == DOWN) {
                if(head.y + 1 <= bottomFrame)
                    head = new Point(head.x, head.y + 1);
                else
                    head = new Point(head.x, 0);
            }
            else if (direction == LEFT) {
                if(head.x - 1 >= 0)
                    head = new Point(head.x - 1, head.y);
                else
                    head = new Point(rightFrame, head.y);
            }
            else {//RIGHT
                if (head.x + 1 <= frameDim.width / SCALE - 2)
                    head = new Point(head.x + 1, head.y);
                else
                    head = new Point(0, head.y);
            }
            headInTail();

        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int i = e.getKeyCode();
        if((i == KeyEvent.VK_LEFT || i == KeyEvent.VK_A) && direction != RIGHT)
            direction = LEFT;
        if((i == KeyEvent.VK_RIGHT || i == KeyEvent.VK_D) && direction != LEFT)
            direction = RIGHT;
        if((i == KeyEvent.VK_UP || i == KeyEvent.VK_W) && direction != DOWN)
            direction = UP;
        if((i == KeyEvent.VK_DOWN || i == KeyEvent.VK_S) && direction != UP)
            direction = DOWN;
        if(i == KeyEvent.VK_SPACE) {
            if (over)
                startGame();
            else
                paused = !paused;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    /* =========================== MAIN ========================= */
    public static void main(String[] args) {
        snake = new Snake();
    }
}