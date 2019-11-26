public class CommandShip extends Piece {
	private double moveSpeed = 100;
	private GamePanel game;
	private int points;
	
	public CommandShip(GamePanel game,String ref,int x,int y, int points) {
		super(ref,x,y, points);
		
		this.game = game;
		dx = moveSpeed;
		this.points = points;
	}

	public void move(long delta) {
		if ((dx < 0) && (x < 10)) {
			game.removePiece(this);
		}

		if ((dx > 0) && (x > 750)) {
			game.removePiece(this);
		}

		super.move(delta);
	 }

	public void collidedWith(Piece other) {
		// collisions with aliens are handled elsewhere
	}
	
	public int addPoints(){
	    return points;
	}
}