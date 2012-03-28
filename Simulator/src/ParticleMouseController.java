import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.event.MouseInputAdapter;

import org.opensourcephysics.display.axes.CoordinateStringBuilder;


public class ParticleMouseController extends MouseInputAdapter {

	final static double DRAG_FOR_VELOCITY_CONSTANT = .1;
	
	private OrbitalSimulation simulation;
	private CoordinateStringBuilder coordinateStrBuilder;
	private Particle temp;
	private double[] mousePressedCoords;
	private long timeMousePressed;
	private boolean planetAddMode;
	final private static Color[] PLANET_SPAWN_COLORS = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.MAGENTA};
	
	public ParticleMouseController(OrbitalSimulation simulation) {
		this.simulation = simulation;
		coordinateStrBuilder = CoordinateStringBuilder.createCartesian();
		planetAddMode = false;
		temp = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("clicky!");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println("enter!");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		System.out.println("exit!");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("press!");
		if(e.isPopupTrigger()) planetAddMode = false;
		else {
			planetAddMode = true;
			mousePressedCoords = getCoords(e);
			timeMousePressed = System.currentTimeMillis();
			addPlanet(mousePressedCoords[0], mousePressedCoords[1]);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!planetAddMode) return;
		
		System.out.println("release!");
		double[] mouseRealeasedCoords = getCoords(e);
		double dragTimeMillis = System.currentTimeMillis() - timeMousePressed;
		
		//set initial velocities based on the direction dragged and the time spent dragging:
		temp.setXVel(DRAG_FOR_VELOCITY_CONSTANT * (mouseRealeasedCoords[0] - mousePressedCoords[0]) / (dragTimeMillis / 1000));
		temp.setYVel(DRAG_FOR_VELOCITY_CONSTANT * (mouseRealeasedCoords[1] - mousePressedCoords[1]) / (dragTimeMillis / 1000));
		
		simulation.particles.add(temp);
		temp = null;
	}
	
	public void addPlanet(double x, double y) {
		temp = new Particle("New Planet", x, y, 0, 0, 10, 10, PLANET_SPAWN_COLORS[new Random().nextInt(PLANET_SPAWN_COLORS.length)]);
		simulation.frame.addDrawable(temp);
		simulation.frame.addDrawable(temp.getTrail());
	}
	
	private double[] getCoords(MouseEvent e) {
		String[] coordComponents = coordinateStrBuilder.getCoordinateString(simulation.frame.getDrawingPanel(), e).split("\\s+");
		return new double[] {Double.parseDouble(coordComponents[0].substring(2)), Double.parseDouble(coordComponents[1].substring(2))};
	}

}
