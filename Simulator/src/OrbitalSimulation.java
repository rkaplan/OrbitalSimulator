import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JFileChooser;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.display.Drawable;
import org.opensourcephysics.frames.DisplayFrame;


public class OrbitalSimulation extends AbstractSimulation {

	private static final boolean DEBUG = false;
	
	private static final int[] FRAME_LOCATION = {0, 0};
	private static final int[] FRAME_DIMENSIONS = {800, 500};
	
	protected SimulationControl control;
	
	protected DisplayFrame frame;
	protected Stack<SimulationState> states;
	protected List<Particle> particles;
	protected ParticleMouseController pmc; //for detecting MouseEvents and triggering appropriate OrbitalSimulation responses
	
	protected JFileChooser fileChooser;
	
	protected double timeElapsed;
	protected double timeInterval;
	protected double gravConstant;
	
	protected boolean elasticCollisions;
	
	@Override
	protected void doStep() {
		updateAccelerations();
		moveParticles();
		
		timeElapsed += timeInterval;
	}
	
	private void updateAccelerations() {
		Particle cur;
		double sumXForces;
		double sumYForces;
		if(DEBUG) System.out.println("Sum of forces of each particle: ");
		for(int i = 0; i < particles.size(); i++) {
			cur = particles.get(i);
			sumXForces = sumYForces = 0;
			for(int j = 0; j < particles.size(); j++) {
				if(j==i) continue;
				sumXForces += forceOfGravityX(cur.getMass(), cur.getX(), cur.getY(), particles.get(j).getMass(), particles.get(j).getX(), particles.get(j).getY());
				sumYForces += forceOfGravityY(cur.getMass(), cur.getX(), cur.getY(), particles.get(j).getMass(), particles.get(j).getX(), particles.get(j).getY());
			}
			if(DEBUG) System.out.println("Particle " + i + ": X = " + sumXForces + ", Y = " + sumYForces);
			cur.setXAccel(sumXForces / cur.getMass());
			cur.setYAccel(sumYForces / cur.getMass());
		}
	}
	
	private void moveParticles() {
		for(int i = 0; i < particles.size(); i++) {
			particles.get(i).moveStep(timeInterval);
		}
		
		for(int i = 0; i < particles.size(); i++) {
			for(int j = i+1; j < particles.size(); j++) {
				if(haveCollided(particles.get(i), particles.get(j))) {
					computeCollision(particles.get(i), particles.get(j));
				}
			}
		}
	}

	private void computeCollision(Particle p1, Particle p2) {
		p1.setLatestCollision(p2);
		p2.setLatestCollision(p1);
		
		if(elasticCollisions) {
			//2 * (m1v1 + m2v2 / m1 + m2) - v0
			double m1, vx1, vy1, m2, vx2, vy2;
			m1 = p1.getMass();
			vx1 = p1.getXVel();
			vy1 = p1.getYVel();
			m2 = p2.getMass();
			vx2 = p2.getXVel();
			vy2 = p2.getYVel();
			
			if(DEBUG) {
				System.out.println("m1: " + m1);
				System.out.println("vx1: " + vx1);
				System.out.println("vy1: " + vy1);
				System.out.println("m2: " + m2);
				System.out.println("vx2: " + vx2);
				System.out.println("vy2: " + vy2);
			}
			
			p1.setXVel(2 * (m1*vx1 + m2*vx2) / (m1 + m2) - vx1);
			p2.setXVel(2 * (m1*vx1 + m2*vx2) / (m1 + m2) - vx2);
			
			if(DEBUG) {
				System.out.println("new vx1: " + p1.getXVel());
				System.out.println("new vx2: " + p2.getXVel());
				System.out.println("Collision happened!");
			}
			
			p1.setYVel(2 * (m1*vy1 + m2*vy2) / (m1 + m2) - vy1);
			p2.setYVel(2 * (m1*vy1 + m2*vy2) / (m1 + m2) - vy2);
		}
		else { //inelastic collisions
			Particle merged = Particle.createParticleFromCollision(p1, p2);
			
			frame.removeDrawable(p1);
			frame.removeDrawable(p1.getTrail());
			frame.removeDrawable(p2);
			frame.removeDrawable(p2.getTrail());
			frame.addDrawable(merged);
			frame.addDrawable(merged.getTrail());
			
			particles.remove(p1);
			particles.remove(p2);
			particles.add(merged);
		}
	}
	
	private boolean haveCollided(Particle p1, Particle p2) {
		if(!(Math.abs(p1.getX() - p2.getX()) < .5 && Math.abs(p1.getY() - p2.getY()) < .5)) //too far away
			return false;

		else if(p1.getLatestCollision() == null || p2.getLatestCollision() == null) //their collision definitely hasn't already been computed
			return true;
		
		else if(p1.getLatestCollision().equals(p2) || p2.getLatestCollision().equals(p1))
			return false; //don't count this time as a collision because it's already been computed
		
		return true;
	}
	
	/**
	 * Sets available fields and their default values in the controller.
	 */
	@Override
	public void reset() {
		this.control = (SimulationControl)super.control; //enables access to JFrame methods like repaint that are needed for some features
		
		control.setValue("Name", "Planet");
		control.setValue("X", 5);
		control.setValue("Y", 0);
		control.setValue("X Velocity", 0);
		control.setValue("Y Velocity", 0);
		control.setValue("Radius", 10);
		control.setValue("Time Interval", .01);
	}
	
	@Override
	public void initialize() {
		frame = new DisplayFrame("X", "Y", "Orbital Simulation");
		frame.addButton("loadState", "Load", 
				"Load a simulation from a .orbital file", this);
		frame.addButton("saveState", "Save", 
				"Save the current simulation to a file", this);
		frame.addButton("toggleCollisionType", "Toggle Elastic / Inelastic Collisions", 
				"Change whether particles bounce off of each other or merge when they collide", this);
		frame.setLocation(FRAME_LOCATION[0], FRAME_LOCATION[1]);
		frame.setSize(new Dimension(FRAME_DIMENSIONS[0], FRAME_DIMENSIONS[1]));
		frame.setVisible(true);
		
		pmc = new ParticleMouseController(this);
		frame.getDrawingPanel().addMouseListener(pmc);
		frame.getDrawingPanel().addMouseMotionListener(pmc);
		
		fileChooser = new JFileChooser(System.getProperty("user.dir"));
		
		this.setDelayTime(10);
		
		Particle initial = new Particle(control.getString("Name"), control.getDouble("X"), control.getDouble("Y"),
				control.getDouble("X Velocity"), control.getDouble("Y Velocity"), 10, control.getInt("Radius"), Color.RED);
		
		Particle second = new Particle("Test", 0, 0, 0, 0, 100, 10, Color.BLUE);
		
		particles = new ArrayList<Particle>();
		particles.add(initial);
		particles.add(second);
		
		for(Particle particle : particles) {
			frame.addDrawable(particle.getTrail());
			frame.addDrawable(particle);
		}
		
		timeElapsed = 0;
		timeInterval = control.getDouble("Time Interval");
		gravConstant = 6.67;
		elasticCollisions = false;
		
		states = new Stack<SimulationState>();
		states.add(currentState());
	}
	
	public void loadState() {
		SimulationState loaded;
		
		try {
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if(file.getName().endsWith(".orbital")) {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
					loaded = (SimulationState)in.readObject();
					in.close();
					states.push(loaded);
					pushCurrentStateAndRevertToState(loaded);
				}
				else control.println("Error: invalid file type");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void saveState() {
		int returnValue = fileChooser.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = this.fileChooser.getSelectedFile();
			states.push(currentState());
			states.peek().save(file);
		}
	}
	
	public void toggleCollisionType() {
		elasticCollisions = !elasticCollisions;
		if(elasticCollisions) control.println("Collision type changed to elastic.");
		else control.println("Collision type changed to inelastic.");
	}
	
	private void pushCurrentStateAndRevertToState(SimulationState state) {
		states.push(currentState());
		
		particles = state.getParticles();
		timeElapsed = state.getTimeElapsed();
		timeInterval = state.getTimeInterval();
		gravConstant = state.getGravConstant();
		elasticCollisions = state.isElasticCollisions();
		
		for(Drawable d : frame.getDrawables()) {
			frame.removeDrawable(d);
		}
		
		for(Particle p : particles) {
			frame.addDrawable(p);
			frame.addDrawable(p.getTrail());
		}
		
		frame.repaint();
	}
	
	private SimulationState currentState() {
		return new SimulationState(particles, timeElapsed, timeInterval, gravConstant, elasticCollisions);
	}
	
	private double forceOfGravityX(double m1, double x1, double y1, double m2, double x2, double y2) {
		return gravConstant * m1 * m2 * (x2 - x1) / Math.pow(distance(x1, y1, x2, y2), 3);
	}
	
	private double forceOfGravityY(double m1, double x1, double y1, double m2, double x2, double y2) {
		return gravConstant * m1 * m2 * (y2 - y1) / Math.pow(distance(x1, y1, x2, y2), 3);
	}
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
	}

	public static void main(String[] args) {
		SimulationControl.createApp(new OrbitalSimulation());
	}
	
}