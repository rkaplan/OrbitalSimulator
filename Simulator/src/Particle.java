import java.awt.Color;

import org.opensourcephysics.display.Circle;
import org.opensourcephysics.display.Trail;
/**
 * Particle 
 * @author Russell Kaplan
 *
 */
public class Particle extends Circle {

	//for data in arrays, [0] stores the value for x, and [1] for y:
	private double[] vel;
	private double[] accel;
	private Trail trail;

	/**
	 * Creates a new Particle object with the specified initial values.
	 * @param x The initial X coordinate of the Particle
	 * @param y The initial Y coordinate of the Particle
	 */
	public Particle(double x, double y) {
		super(x, y);

		trail = new Trail();
		trail.addPoint(x, y);
	}
	
	/**
	 * Creates a new Particle object with the specified initial values.
	 * @param x The initial X coordinate of the Particle
	 * @param y The initial Y coordinate of the Particle
	 * @param xVel The initial X velocity of the Particle
	 * @param yVel The initial Y Velocity of the Particle
	 */
	public Particle(double x, double y, double xVel, double yVel) {
		this(x, y);
		
		vel = new double[] {xVel, yVel};
		accel = new double[] {0, 0};
	}
	
	/**
	 * Creates a new Particle object with the specified initial values.
	 * @param x The initial X coordinate of the Particle
	 * @param y The initial Y coordinate of the Particle
	 * @param xVel The initial velocity of the Particle in the X direction
	 * @param yVel The initial Velocity of the Particle in the Y direction
	 * @param xAccel The initial acceleration of the Particle in the X direction
	 * @param yAccel The initial acceleration of the Particle in the Y direction
	 */
	public Particle(double x, double y, double xVel, double yVel, double xAccel, double yAccel) {
		this(x, y, xVel, yVel);
		accel = new double[] {xAccel, yAccel};
	}
	
	public void moveStep(double timeInterval) {
		//update accelerations and velocities:
		for(int i = 0; i < vel.length; i++) {
			vel[i] = vel[i] + accel[i] * timeInterval; //update the velocity based on acceleration
		}
		
		//update x and y positions:
		x = x + vel[0] * timeInterval;
		y = y + vel[1] * timeInterval;
		
		//update the trail:
		trail.addPoint(x, y);
	}

	public double getXVel() {
		return vel[0];
	}

	public void setXVel(double xVel) {
		vel[0] = xVel;
	}

	public double getYVel() {
		return vel[1];
	}

	public void setYVel(double yVel) {
		vel[1] = yVel;
	}

	public double getXAccel() {
		return accel[0];
	}

	public void setXAccel(double xAccel) {
		accel[0] = xAccel;
	}

	public double getYAccel() {
		return accel[1];
	}

	public void setYAccel(double yAccel) {
		accel[1] = yAccel;
	}
	
	/**
	 * Get the moving Object's trail.
	 * @return
	 */
	public Trail getTrail() {
		return trail;
	}
	
	/**
	 * Set the color of the moving Object's trail.
	 * @param c The new trail color
	 */
	public void setTrailColor(Color c) {
		trail.color = c;
	}

//	//helpers:
//	private double radians(double degrees) {
//		return degrees * Math.PI / 180;
//	}
}
