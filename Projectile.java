public class Projectile extends Piece {
    private double moveSpeed = -300;
    private GamePanel game;
    private boolean used = false;
    
    public Projectile(GamePanel game,String sprite,int x,int y, int points) {
        super(sprite,x,y,points);
        this.game = game;
        
        if (points < 0){
            // if it's an alien shot, shoot downwards
            dy = 300;   
        }
        else {
            dy = moveSpeed;
        }      
    }


    public void move(long delta) {
        super.move(delta);
        
        // shot offscreen
        if (y < -100) {
            game.removePiece(this);
        }
    }
    
    public void collidedWith(Piece other) {
        // stop double collisions
        if (used) {
            return;
        }
               
        // if we've hit an alien
        if (other instanceof Alien || other instanceof CommandShip) {
            if (points != -1){
                // remove the affected entities if it is not an alien shot
                game.removePiece(this);
                game.removePiece(other);
                
                // notify the game that the alien has been killed
                game.notifyAlienKilled(this, other);
                used = true;
            }
        }
        else if (other instanceof Barrier){
            // remove the barrier
            game.removePiece(this);
            game.removePiece(other);
                
        }
        else if (other instanceof Cannon){
            game.notifyDeath();
            game.removePiece(this);
        }
    }
}
