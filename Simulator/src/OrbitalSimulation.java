import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.DisplayFrame;


public class OrbitalSimulation extends AbstractSimulation {

	private DisplayFrame frame;
	private Stack<SimulationState> states;
	private List<Particle> particles;
	
	private double timeElapsed;
	private double timeInterval;
	
	@Override
	protected void doStep() {
		updateAccelerations();
		updateVelocities();
		moveParticles();
	}
	
	private void updateAccelerations() {
		
	}
	
	private void updateVelocities() {
		
	}
	
	private void moveParticles() {
		
	}
	
	

	/**
	 * Sets available fields and their default values in the controller.
	 */
	@Override
	public void reset() {
		control.setValue("Name", "Planet");
		control.setValue("X", 0);
		control.setValue("Y", 0);
		control.setValue("X Velocity", 0);
		control.setValue("Y Velocity", 0);
		control.setValue("Radius", 5);
		control.setValue("Time Interval", .01);
		
	}
	
	@Override
	public void initialize() {
		frame = new DisplayFrame("X", "Y", "Orbital Simulation");
		frame.setVisible(true);
		
		Particle initial = new Particle(control.getString("Name"), control.getDouble("X"), control.getDouble("Y"),
				control.getDouble("X Velocity"), control.getDouble("Y Velocity"), control.getInt("Radius"), Color.RED);
		
		particles = new ArrayList<Particle>();
		particles.add(initial);
		
		for(Particle particle : particles) {
			frame.addDrawable(particle);
		}
		
		states = new Stack<SimulationState>();
		states.add(new SimulationState(particles));
		
		timeElapsed = 0;
		timeInterval = control.getDouble("Time Interval");
	}

	public static void main(String[] args) {
		SimulationControl.createApp(new OrbitalSimulation());
	}
	
}
