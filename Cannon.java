public class Cannon extends Piece {
    private GamePanel game;
    
    public Cannon(GamePanel game,String ref,int x,int y, int points) {
        super(ref,x,y,points);
        
        this.game = game;
        int lives = 3;
    }

    public void move(long delta) {
        if ((dx < 0) && (x < 10)) {
            return;
        }
        if ((dx > 0) && (x > 750)) {
            return;
        }
        super.move(delta);
    }
    
    public void collidedWith(Piece other) {
        // if it collides with an alien, lose a life
        if (other instanceof Alien) {
            game.notifyDeath();
        }
    }
}
