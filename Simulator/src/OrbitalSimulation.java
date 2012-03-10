import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.DisplayFrame;


public class OrbitalSimulation extends AbstractSimulation {

	private static final boolean DEBUG = false;
	
	private static final int[] FRAME_LOCATION = {0, 0};
	
	protected DisplayFrame frame;
	protected Stack<SimulationState> states;
	protected List<Particle> particles;
	protected ParticleMouseController pmc;
	
	private double timeElapsed;
	private double timeInterval;
	private double gravConstant;
	
	@Override
	protected void doStep() {
		updateAccelerations();
		moveParticles();
		
		timeElapsed += timeInterval;
//		for(Particle p : particles) System.out.print(p.getName() + " ");
//		System.out.println();
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
				if(particles.get(i).hasCollidedWith(particles.get(j))) {
					Particle p1 = particles.get(i);
					Particle p2 = particles.get(j);
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
		}
	}
	
	/**
	 * Sets available fields and their default values in the controller.
	 */
	@Override
	public void reset() {
		control.setValue("Name", "Planet");
		control.setValue("X", 5);
		control.setValue("Y", 0);
		control.setValue("X Velocity", 0);
		control.setValue("Y Velocity", 5);
		control.setValue("Radius", 10);
		control.setValue("Time Interval", .01);
	}
	
	@Override
	public void initialize() {
		frame = new DisplayFrame("X", "Y", "Orbital Simulation");
		frame.setLocation(FRAME_LOCATION[0], FRAME_LOCATION[1]);
		frame.setVisible(true);
		frame.setSize(new Dimension(800, 600));
		
		pmc = new ParticleMouseController(this);
		frame.getDrawingPanel().addMouseListener(pmc);
		frame.getDrawingPanel().addMouseMotionListener(pmc);
		
		this.setDelayTime(10);
		
		Particle initial = new Particle(control.getString("Name"), control.getDouble("X"), control.getDouble("Y"),
				control.getDouble("X Velocity"), control.getDouble("Y Velocity"), 10, control.getInt("Radius"), Color.RED);
		
		Particle second = new Particle("Test", 0, 0, 0, 2, 100, 10, Color.BLUE);
		
		particles = new ArrayList<Particle>();
		particles.add(initial);
		particles.add(second);
		
		for(Particle particle : particles) {
			frame.addDrawable(particle.getTrail());
			frame.addDrawable(particle);
		}
		
		states = new Stack<SimulationState>();
		states.add(new SimulationState(particles));
		
		timeElapsed = 0;
		timeInterval = control.getDouble("Time Interval");
		gravConstant = 6.67;
	}
	
	private double forceOfGravityX(double m1, double x1, double y1, double m2, double x2, double y2) {
		return gravConstant * m1 * m2 * (x2 - x1) / Math.pow(distance(x1, y1, x2, y2), 3);
	}
	
	private double forceOfGravityY(double m1, double x1, double y1, double m2, double x2, double y2) {
		return gravConstant * m1 * m2 * (y2 - y1) / Math.pow(distance(x1, y1, x2, y2), 3);
	}
	
	private static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
	}

	public static void main(String[] args) {
		SimulationControl.createApp(new OrbitalSimulation());
	}
	
}