import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * A class to represent an enemy plane
 * @author Blake
 *
 */
public class BasicPlane implements Enemy {
	private static final double VELOCITY = 9;
	private final static int SHOOT_DELAY = 30;
	private static final double TURN_SPEED = 2;
	private static final int MAX_HEALTH = 50;
	private static final int POINT_VALUE = 25;
	private boolean alive;
	
	private double xPos;
	private double yPos;
	private double direction;
	private Player player;
	
	private int shootTimer;
	private int health;
	
	/**
	 * Creates a new basic plane
	 * @param xPos the x position
	 * @param yPos the y position
	 * @param player the player
	 */
	public BasicPlane(double xPos, double yPos, Player player) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.player = player;
		alive = true;
		direction = 0;
		health = MAX_HEALTH;
	}
	
	/**
	 * Draws the BasicPlane to a given Graphics object
	 * @param g the given Graphics object
	 */
	public void draw(Graphics g){
		if(alive && (Math.abs(xPos-player.getPosition()[0])<Game.FRAME_WIDTH || Math.abs(xPos-player.getPosition()[1])<Game.FRAME_HEIGHT)) {
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform trans = AffineTransform.getRotateInstance(Math.toRadians(direction+180),xPos,yPos);
			trans.translate(xPos,yPos);
			trans.scale(1.2,  1.2);
			double spriteDirection = direction>0? direction : (360+direction);
			if(spriteDirection>=75 && spriteDirection<=105) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite1"), trans, null);
			}else if(spriteDirection>=105 && spriteDirection<=135) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite2"), trans, null);
			}else if(spriteDirection>=135 && spriteDirection<=165) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite3"), trans, null);
			}else if(spriteDirection>=165 && spriteDirection<=195) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite4"), trans, null);
			}else if(spriteDirection>=195 &&spriteDirection<=225) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite3"), trans, null);
			}else if(spriteDirection>=225 && spriteDirection<=255) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite2"), trans, null);
			}else if(spriteDirection>=255 && spriteDirection<=285) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite5"), trans, null);
			}else if(spriteDirection>=285 && spriteDirection<=315) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite6"), trans, null);
			}else if(spriteDirection>=315 && spriteDirection<=345) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite7"), trans, null);
			}else if((spriteDirection>=345 && spriteDirection<=360) || (spriteDirection>=0 && spriteDirection<=15)) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite8"), trans, null);
			}else if(spriteDirection>=15 && spriteDirection<=45) {
				g2.drawImage(ImageAtlas.getImage("EnemySprite7"), trans, null);
			}else{
				g2.drawImage(ImageAtlas.getImage("EnemySprite6"), trans, null);
			}
			//g.setColor(Color.yellow);
			//g.fillOval((int)(xPos-25*Math.cos(Math.toRadians(direction))+10*Math.sin((Math.toRadians(direction)))-5), (int)(yPos-25*Math.sin(Math.toRadians(direction))-10*Math.cos(Math.toRadians(direction))-5), 10, 10);
			if(yPos>Sky.SKY_HEIGHT)
				alive = false;
		}
		
	}
	
	/**
	 * Simulates the next step of the simulation
	 */
	public void step() {
		double planeXVelocity = Math.cos(Math.toRadians(direction))*VELOCITY;
		double planeYVelocity = Math.sin(Math.toRadians(direction))*VELOCITY;
		double relativeX = player.getPosition()[0]-xPos;
		double relativeY = player.getPosition()[1]-yPos;
		
		double cross = planeXVelocity*relativeY - planeYVelocity*relativeX;
		if(Math.abs(cross)>600) {
			if(cross>0){
				direction -= TURN_SPEED;
			}else {
				direction += TURN_SPEED;
			}
		}else {
			if(shootTimer == 0) {
				Hazards.addHazard(new PlaneBullet(xPos-100*Math.cos(Math.toRadians(direction))+25*Math.sin((Math.toRadians(direction))), yPos-100*Math.sin(Math.toRadians(direction))-25*Math.cos(Math.toRadians(direction)), direction));
				shootTimer = 1;
			}
		}
		
		if(shootTimer == SHOOT_DELAY+1) {
				shootTimer = 0;
		}else {
			shootTimer++;
		}
		xPos -= Math.cos(Math.toRadians(direction))*(VELOCITY);//-directionDifference/180*3);
		yPos -= Math.sin(Math.toRadians(direction))*(VELOCITY);//-directionDifference/180*3);

	}
	
	/**
	 * Returns the hitbox of the BasicPlane
	 * @return the hitbox of the BasicPlane
	 */
	public Area getCollision() {
		Area a = new Area(new Rectangle2D.Double(0,0,50,20));
		AffineTransform trans = AffineTransform.getRotateInstance(Math.toRadians(direction+180),xPos,yPos);
		trans.translate(xPos,yPos);
		trans.scale(1.2,  1.2);
		a.transform(trans);
		return a;
	}
	
	/**
	 * Reacts to a collision with a given bullet
	 * @param bullet the given bullet
	 */
	public void notifyCollision(Bullet bullet) {
		if(bullet.getBulletGroup()==Bullet.BulletGroup.CONTACT) {
			health -= 15;
		}else {
			health -= 50;
		}
		if(health < 1) {
			if(bullet.getBulletGroup() == Bullet.BulletGroup.CONTACT) {
				Camera.shakeScreen(15);
				ScoreManager.addKill(this);
			}else if(bullet.getBulletGroup() == Bullet.BulletGroup.PLAYER){
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ScoreManager.addKill(this);
				Camera.shakeScreen(8);
			}
			Sky.addDetail(new DeadPlane(xPos-25*Math.cos(Math.toRadians(direction))+10*Math.sin((Math.toRadians(direction))), yPos-25*Math.sin(Math.toRadians(direction))-10*Math.cos(Math.toRadians(direction)), direction, VELOCITY*0.75));
			Sky.addDetail(new Explosion(xPos-25*Math.cos(Math.toRadians(direction))+10*Math.sin((Math.toRadians(direction))), yPos-25*Math.sin(Math.toRadians(direction))-10*Math.cos(Math.toRadians(direction)),2));
			alive = false;
		}
		
	}
		
	/**
	 * Returns whether the BasicPlane is alive
	 * @return whether the BasicPlane is alive
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/**
	 * Returns the position of the BasicPlane
	 * @return the position of the BasicPlane
	 */
	public double[] getPosition(){
		double[] position = {xPos, yPos};
		return position;
	}

	/**
	 * Returns the score value of the BasicPlane
	 * @return the score value of the BasicPlane
	 */
	@Override
	public int getPointValue() {
		// TODO Auto-generated method stub
		return POINT_VALUE;
	}

}
