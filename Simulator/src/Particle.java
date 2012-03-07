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
	private double[] vel;
	private double[] accel;
	private Trail trail;

	public Particle(double x, double y, int radius) {
		super(x, y, radius);

		trail = new Trail();
		trail.addPoint(x, y);
	}
	
	public Particle(double x, double y, int radius, double xVel, double yVel) {
		this(x, y, radius);
		
		vel = new double[] {xVel, yVel};
		accel = new double[] {0, 0};
	}
	
	public Particle(double x, double y, int radius, double xVel, double yVel, double xAccel, double yAccel) {
		this(x, y, radius, xVel, yVel);
		accel = new double[] {xAccel, yAccel};
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
	
	public Trail getTrail() {
		return trail;
	}
	
	public void setTrail(Trail trail) {
		this.trail = trail;
	}
	
	public void setTrailColor(Color c) {
		trail.color = c;
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