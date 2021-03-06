import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.opensourcephysics.display.Circle;
import org.opensourcephysics.display.Trail;
/**
 * Particle 
 * @author Russell Kaplan
 *
 */
public class Particle extends Circle implements Serializable {

	private static final long serialVersionUID = 1L;

	//for data in arrays, [0] stores the value for x, and [1] for y:
	private String name;
	private double[] vel;
	private double[] accel;
	private double mass;
	private SerializableTrail trail;
	private Particle latestCollision; //for ensuring the same collision is not processed multiple times

	public Particle(String name, double x, double y, double xVel, double yVel, double mass, int pixRadius, Color color) {
		super(x, y, pixRadius);
		this.name = name;
		this.color = color;
		this.pixRadius = pixRadius;

		this.vel = new double[] {xVel, yVel};
		this.accel = new double[] {0, 0};
		this.mass = mass;

		this.trail = new SerializableTrail();
		this.trail.color = color;
		this.trail.addPoint(x, y);

		this.latestCollision = null; //hasn't collided with any particles yet
	}

	public void moveStep(double timeInterval) {

		//update velocities:
		for(int i = 0; i < vel.length; i++) {
			vel[i] = vel[i] + accel[i] * timeInterval; //update the velocity based on acceleration
		}

		//update x and y positions:
		x = x + vel[0] * timeInterval;
		y = y + vel[1] * timeInterval;

		//update the trail:
		trail.addPoint(x, y);

		//		System.out.println("\tAfter stepping:");
		//		System.out.println("\tCoords: " + x + ", " + y);
		//		System.out.println("\tLatest collision is null? " + Boolean.toString(latestCollision == null));

		//check to see if the particle from the previous collision is outside of the collision detection range,
		//indicating that future collisions with that particle should be handled normally and no longer ignored:
		if(latestCollision != null) {
			if(!(Math.abs(x - latestCollision.getX()) < .5 && Math.abs(y - latestCollision.getY()) < .5))
				latestCollision = null;
			//TODO: handle when latestCollision is removed from the simulation (e.g. from inelastic collision)
		}

	}

	/**
	 * Creates a <code>Particle</code> that represents the outcome of a
	 * perfectly inelastic collision between <code>p1</code> and
	 * <code>p2</code>.
	 * @param p1
	 * @param p2
	 * @return The <code>Particle</code> created from the collision
	 */
	public static Particle createParticleFromCollision(Particle p1, Particle p2) {
		//use conservation of momentum to find new velocities:
		double newXVel = (p1.getMass() * p1.getXVel() + p2.getMass() * p2.getXVel()) / (p1.getMass() + p2.getMass());
		double newYVel = (p1.getMass() * p1.getYVel() + p2.getMass() * p2.getYVel()) / (p1.getMass() + p2.getMass());

		//find the areas of each circle to determine the radius of the new Particle (whose area will be the sum of those two areas):
		double p1area = Math.PI * Math.pow(p1.getPixRadius(), 2);
		double p2area = Math.PI * Math.pow(p2.getPixRadius(), 2);
		int newPixRadius = (int)Math.ceil(Math.sqrt((p1area + p2area) / Math.PI));

		//determine new color by mixing the colors of the two particles:
		Color newColor = new Color((p1.getColor().getRed() + p2.getColor().getRed()) / 2, 
				(p1.getColor().getGreen() + p2.getColor().getGreen()) / 2, 
				(p1.getColor().getGreen() + p2.getColor().getGreen()) / 2);
		
		double newX;
		double newY;
		if(p1.getPixRadius() > p2.getPixRadius()) {
			newX = p1.getX();
			newY = p1.getY();
		}
		else if(p2.getPixRadius() > p1.getPixRadius()) {
			newX = p2.getX();
			newY = p2.getY();
		}
		else {
			newX = (p1.getX() + p2.getX())/2;
			newY = (p1.getY() + p2.getY())/2;
		}

		return new Particle("Planet", newX, newY, newXVel, newYVel, p1.getMass() + p2.getMass(), newPixRadius, newColor);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public double getX() {
		return x;
	}
	
	
	public void setX(double x) {
		this.x = x;
	}

	public double getXVel() {
		return vel[0];
	}

	public void setXVel(double xVel) {
		vel[0] = xVel;
	}

	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
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

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		this.trail.color = color;
	}

	public Trail getTrail() {
		return trail;
	}

	public void setTrail(SerializableTrail trail) {
		this.trail = trail;
	}

	public int getPixRadius() {
		return pixRadius;
	}

	public void setPixRadius(int pixRadius) {
		this.pixRadius = pixRadius;
	}

	public Particle getLatestCollision() {
		return latestCollision;
	}

	public void setLatestCollision (Particle p) {
		this.latestCollision = p;
	}

	public Particle deepCopy() {
		Particle copy = null;

		//serialize the Particle and unpack the bytecode to create a deep copy:
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.flush();
			out.close();

			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			copy = (Particle)in.readObject();
			in.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return copy;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(x);
		sb.append(y);
		sb.append(pixRadius);
		sb.append(name);
		for(int i = 0; i < vel.length; i++) {
			sb.append(vel[i]);
			sb.append(accel[i]);
		}
		sb.append(mass);
		sb.append(trail.hashCode());
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	//	//helpers:
	//	private double radians(double degrees) {
	//		return degrees * Math.PI / 180;
	//	}
}