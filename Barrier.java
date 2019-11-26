public class Barrier extends Piece {
	private GamePanel game;

	public Barrier(GamePanel game,String ref,int x,int y, int points) {
		super(ref,x,y,points);
		
		this.game = game;
	}

	public void collidedWith(Piece other) {}
}