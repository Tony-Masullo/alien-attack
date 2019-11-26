import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GamePanel extends Canvas {
    private BufferStrategy strategy;
    private boolean gameRunning = true;
    private ArrayList pieces = new ArrayList();
    private ArrayList removeList = new ArrayList();
    private Piece alien;
    private Piece cannon;
    private Piece commandShip;
    private Piece barrier;
    private double moveSpeed = 300;
    private long lastFire = 0;
    private long firingInterval = 500;
    private int alienCount;
    private String message = "";
    private boolean waitingForKeyPress = true;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean firePressed = false;
    private boolean logicRequiredThisLoop = false;
    private int score = 0;
    private int lives = 3;
    private boolean hasReached1K = false;
    private boolean waitingForPPress = false;
    

    public GamePanel() {
        // create a frame to contain our game
        JFrame container = new JFrame("Alien Attack");
        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(800,600));
        panel.setLayout(null);
        
        // size of panel
        setBounds(0,0,800,600);
        panel.add(this);
        
        setIgnoreRepaint(true);
 
        container.pack();
        container.setResizable(false);
        container.setVisible(true);
        container.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        addKeyListener(new KeyInputHandler());
        
        requestFocus();

        createBufferStrategy(2);
        strategy = getBufferStrategy();
        
        initEntities();
    }
    

    private void startGame() {
        // clear out any existing entities
        pieces.clear();
        initEntities();
        lives = 3;
        // clear keyboard
        leftPressed = false;
        rightPressed = false;
        firePressed = false;
    }
    
    
    private void createCommandShip(){
        // create a random point value for the command ship
        int[] items = new int[]{50,100,150,300};
        Random rand = new Random();
        
        int points;
        points = items[rand.nextInt(items.length)];
        commandShip = new CommandShip(this, "sprites/alien.png", 10, 10, points);
        pieces.add(commandShip);
    }
    

    private void initEntities() {
        // create player cannon
        cannon = new Cannon(this,"sprites/ship.gif",370,550, 0);
        pieces.add(cannon);
        
        // create barrier layers
        barrier = new Barrier(this, "sprites/barrier3.gif", 170, 400, 0);
        pieces.add(barrier);
        barrier = new Barrier(this, "sprites/barrier2.gif", 170, 405, 0);
        pieces.add(barrier);
        barrier = new Barrier(this, "sprites/barrier1.gif", 170, 410, 0);
        pieces.add(barrier);
        
        barrier = new Barrier(this, "sprites/barrier3.gif", 370, 400, 0);
        pieces.add(barrier);
        barrier = new Barrier(this, "sprites/barrier2.gif", 370, 405, 0);
        pieces.add(barrier);
        barrier = new Barrier(this, "sprites/barrier1.gif", 370, 410, 0);
        pieces.add(barrier);
        
        barrier = new Barrier(this, "sprites/barrier3.gif", 570, 400, 0);
        pieces.add(barrier);
        barrier = new Barrier(this, "sprites/barrier2.gif", 570, 405, 0);
        pieces.add(barrier);
        barrier = new Barrier(this, "sprites/barrier1.gif", 570, 410, 0);
        pieces.add(barrier);
        
        // create a block of aliens 5x11
        alienCount = 0;
        int points = 10;
        for (int row=0;row<5;row++) {
            for (int x=0;x<11;x++) {
                if (row < 1){
                    Piece alien = new Alien(this,"sprites/alien.png",100+(x*50),(50)+row*30, points+20);
                    pieces.add(alien);
                    alienCount++;
                }
                else if (row < 3) {
                    Piece alien = new Alien(this,"sprites/alien.png",100+(x*50),(50)+row*30, points+10);
                    pieces.add(alien);
                    alienCount++;
                }
                else if (row < 5) {
                    Piece alien = new Alien(this,"sprites/alien.png",100+(x*50),(50)+row*30, points);
                    pieces.add(alien);
                    alienCount++;
                }
                
            }
        }
    }
    

    public void updateLogic() {
        logicRequiredThisLoop = true;
    }
    

    public void removePiece(Piece piece) {
        removeList.add(piece);
    }
    

    public void notifyDeath() {
        lives -= 1;
        if (lives == 0){
            message = "Game Over! Try Again";
            waitingForKeyPress = true;
            score = 0;
        }
    }
    

    public void notifyWin() {
        message = "You Win!";
        waitingForKeyPress = true;
    }
    

    public void notifyAlienKilled(Piece me, Piece other) {
        if (Alien.isAlien(other)){
            alienCount--;
        }
        
        score += other.addPoints();
        
        if (alienCount == 0) {
            notifyWin();
        }
        
        // speed up aliens as they die
        for (int i=0;i<pieces.size();i++) {
            Piece piece = (Piece) pieces.get(i);
            
            if (piece instanceof Alien) {
                // speed up by 2%
                piece.setHorizontalMovement(piece.getHorizontalMovement() * 1.02);
            }
        }
    }
    

    public void tryToFire() {
        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        
        // if we waited long enough, create the shot entity
        lastFire = System.currentTimeMillis();
        Projectile shot1 = new Projectile(this,"sprites/shot.gif",cannon.getX()+10,cannon.getY()-30, 0);
        pieces.add(shot1);
        
    }
    
    
    public void drawScore(Graphics2D g){
        g.setColor(Color.white);
        g.drawString("Score: " + score, 20, 20);
    }
    
    
    public void drawLives(Graphics2D g){
        g.setColor(Color.white);
        g.drawString("Lives: " + lives, 20, 580);
    }
    
    
    public void gameLoop() {
        long lastLoopTime = System.currentTimeMillis();
        //create random arrays to randomize shot and alien spawns
        int[] spawnChance = new int[1000];
        int[] spawnShotChance = new int[100];
        int[] randX = new int[600];
        int[] randY = new int[300];
        Arrays.setAll(spawnChance, (index) -> 1 + index);
        Arrays.setAll(spawnShotChance, (index) -> 1 + index);
        Arrays.setAll(randX, (index) -> 1 + index);
        Arrays.setAll(randY, (index) -> 1 + index);
        Random rand = new Random();
        
        int spawnCommandShipChance;
        int spawnShot;
        int randXint;
        int randYint;

        while (gameRunning) {
            // calculate time for entities to move
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();
            
            // create background
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.black);
            g.fillRect(0,0,800,600);
            
            drawScore(g);
            drawLives(g);
            
            
            if (!waitingForPPress){
                // entities move
                if (!waitingForKeyPress) {
                    for (int i=0;i<pieces.size();i++) {
                        Piece piece = (Piece) pieces.get(i);
                        
                        piece.move(delta);
                    }
                    //try to spawn command ship
                    spawnCommandShipChance = spawnChance[rand.nextInt(spawnChance.length)];
                    if (spawnCommandShipChance == 50){
                        createCommandShip();
                    }
                    spawnShot = spawnShotChance[rand.nextInt(spawnShotChance.length)];
                    randXint = randX[rand.nextInt(randX.length)];
                    randYint = randY[rand.nextInt(randY.length)];
                    if (spawnShot == 50){
                        Projectile shot2 = new Projectile(this,"sprites/shot.gif",randXint+10,randYint-30, -1);
                        pieces.add(shot2);
                    }
                }
                
                // draw all the entities we have in the game
                for (int i=0;i<pieces.size();i++) {
                    Piece piece = (Piece) pieces.get(i);
                    
                    piece.draw(g);
                }
                
                // check all collisions
                for (int p=0;p<pieces.size();p++) {
                    for (int s=p+1;s<pieces.size();s++) {
                        Piece me = (Piece) pieces.get(p);
                        Piece him = (Piece) pieces.get(s);
                        
                        if (me.collidesWith(him)) {
                            me.collidedWith(him);
                            him.collidedWith(me);
                            
                        }
                    }
                }
                
                // remove entities that died
                pieces.removeAll(removeList);
                removeList.clear();
    
                // do logic
                if (logicRequiredThisLoop) {
                    for (int i=0;i<pieces.size();i++) {
                        Piece piece = (Piece) pieces.get(i);
                        piece.doLogic();
                    }
                    
                    logicRequiredThisLoop = false;
                }
                
                // display waiting message
                if (waitingForKeyPress) {
                    g.setColor(Color.white);
                    g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
                    g.drawString("Press any key",(800-g.getFontMetrics().stringWidth("Press any key"))/2,300);
                }
                
                g.dispose();
                strategy.show();
                
                // movement
                cannon.setHorizontalMovement(0);
                
                if ((leftPressed) && (!rightPressed)) {
                    cannon.setHorizontalMovement(-moveSpeed);
                } else if ((rightPressed) && (!leftPressed)) {
                    cannon.setHorizontalMovement(moveSpeed);
                }
                
                // attempt fire
                if (firePressed) {
                    tryToFire();
                }
                
                // add a life at score = 1000
                if (score > 1000 && hasReached1K == false){
                    lives += 1;
                    hasReached1K = true;
                }
            
            // pause bracket
            }
            
            // pause briefly
            try { Thread.sleep(10); } catch (Exception e) {}
        }
    }
    

    private class KeyInputHandler extends KeyAdapter {
        private int pressCount = 1;
        
        public void keyPressed(KeyEvent e) {
            if (waitingForKeyPress) {
                return;
            }
               
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                firePressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_P) {
                if (waitingForPPress == false){
                    waitingForPPress = true;
                }
                else {
                    waitingForPPress = false;
                }
            }
        }


        public void keyReleased(KeyEvent e) {
            if (waitingForKeyPress) {
                return;
            }
            
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                firePressed = false;
            }
        }

        
        public void keyTyped(KeyEvent e) {
            // if key is pressed, start game/level
            if (waitingForKeyPress) {
                if (pressCount == 1) {
                    waitingForKeyPress = false;
                    startGame();
                    pressCount = 0;
                } else {
                    pressCount++;
                }
            }
            
            if (e.getKeyCode() == KeyEvent.VK_P) {
                waitingForPPress ^= true;
            }
            
            // escape to quit
            if (e.getKeyChar() == 27) {
                System.exit(0);
            }
        }
    }
    
    
    public static void main(String argv[]) {
        GamePanel g =new GamePanel();
        g.gameLoop();
    }
}
