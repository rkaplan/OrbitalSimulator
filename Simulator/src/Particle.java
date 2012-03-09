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
	}

	public boolean hasCollidedWith(Particle p) {
		return Math.abs(x - p.getX()) < .5 && Math.abs(y - p.getY()) < .5;
	}

	public static Particle createParticleFromCollision(Particle p1, Particle p2) {
		//use conservation of momentum to find new velocities:
		double newXVel = (p1.getMass() * p1.getXVel() + p2.getMass() * p2.getXVel()) / (p1.getMass() + p2.getMass());
		double newYVel = (p1.getMass() * p1.getYVel() + p2.getMass() * p2.getYVel()) / (p1.getMass() + p2.getMass());
		
		//find the areas of each circle to determine the radius of the new Particle (whose area will be the sum of those two areas):
		double p1area = Math.PI * Math.pow(p1.getPixRadius(), 2);
		double p2area = Math.PI * Math.pow(p2.getPixRadius(), 2);
		int newPixRadius = (int)Math.ceil(Math.sqrt((p1area + p2area) / Math.PI));
		
		return new Particle("Planet", p1.getX(), p1.getY(), newXVel, newYVel, p1.getMass() + p2.getMass(), newPixRadius, Color.GRAY);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public Trail getTrail() {
		return trail;
	}

	public void setTrail(SerializableTrail trail) {
		this.trail = trail;
	}

	public Color getTrailColor() {
		return this.trail.color;
	}

	public void setTrailColor(Color c) {
		trail.color = c;
	}
	
	public int getPixRadius() {
		return pixRadius;
	}
	
	public void setPixRadius(int pixRadius) {
		this.pixRadius = pixRadius;
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

	//	//helpers:
	//	private double radians(double degrees) {
	//		return degrees * Math.PI / 180;
	//	}
}