public class Alien extends Piece {
	private double moveSpeed = 75;
	private GamePanel game;
	private int points;
	
	public Alien(GamePanel game,String ref,int x,int y, int points) {
		super(ref,x,y,points);
		
		this.game = game;
		dx = -moveSpeed;
		this.points = points;
	}
	
	public void move(long delta) {
		// if we have reached the left hand side of the screen and
		// are moving left then request a logic update 
		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
		}
		// and vice vesa, if we have reached the right hand side of 
		// the screen and are moving right, request a logic update
		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
		}
		
		// proceed with normal move
		super.move(delta);
	}
	
	public void doLogic() {
		// swap over horizontal movement and move down the
		// screen a bit
		dx = -dx;
		y += 10;
		
		// if we've reached the bottom of the screen then the player
		// dies
		if (y > 570) {
			game.notifyDeath();
		}
	}
	
	// Notification that this alien has collided with another entity
	public void collidedWith(Piece other) {
		// collisions with aliens are handled elsewhere
	}
	
	public int addPoints(){
	    return points;
	}
	public static boolean isAlien(Piece me) {
	    if (me.points < 40){
	        return true;
	    }
	    return false;
	}
}
