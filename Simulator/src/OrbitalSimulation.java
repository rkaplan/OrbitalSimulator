import java.util.List;
import java.util.Stack;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.DisplayFrame;


public class OrbitalSimulation extends AbstractSimulation {

	DisplayFrame frame;
	Stack<SimulationState> states;
	List<Particle> particles;
	
	@Override
	protected void doStep() {
		control.println("yay!");
	}

	/**
	 * Sets available fields and their default values in the controller.
	 */
	@Override
	public void reset() {
		control.setValue("X", 0);
		control.setValue("Y", 0);
		control.setValue("Radius", 5);
		
	}
	
	@Override
	public void initialize() {
		frame = new DisplayFrame("X", "Y", "Orbital Simulation");
		Particle p = new Particle(control.getDouble("X"), control.getDouble("Y"), control.getInt("Radius"));
		
		frame.setVisible(true);
		frame.addDrawable(p);
	}

	public static void main(String[] args) {
		SimulationControl.createApp(new OrbitalSimulation());
	}
	
}
