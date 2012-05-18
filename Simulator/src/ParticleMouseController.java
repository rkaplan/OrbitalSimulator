import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.event.MouseInputAdapter;

import org.opensourcephysics.display.axes.CoordinateStringBuilder;


public class ParticleMouseController extends MouseInputAdapter {

	final static double DRAG_FOR_VELOCITY_CONSTANT = .3;
	final static double NEW_PLANET_MASS = 5.9742E24;
	
	private OrbitalSimulation simulation;
	private CoordinateStringBuilder coordinateStrBuilder;
	private Particle tempParticle;
	private double[] mousePressedCoords;
	private long timeMousePressed;
	private boolean planetAddMode;
	final private static Color[] PLANET_SPAWN_COLORS = {Color.RED, Color.BLUE, Color.BLACK, Color.PINK, Color.GREEN, Color.MAGENTA};
	
	public ParticleMouseController(OrbitalSimulation simulation) {
		this.simulation = simulation;
		coordinateStrBuilder = CoordinateStringBuilder.createCartesian();
		planetAddMode = false;
		tempParticle = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
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
		
		double[] mouseRealeasedCoords = getCoords(e);
		double dragTimeMillis = System.currentTimeMillis() - timeMousePressed;
		
		//set initial velocities based on the direction dragged and the time spent dragging:
		tempParticle.setXVel(DRAG_FOR_VELOCITY_CONSTANT * (mouseRealeasedCoords[0] - mousePressedCoords[0]) / (dragTimeMillis / 1000));
		tempParticle.setYVel(DRAG_FOR_VELOCITY_CONSTANT * (mouseRealeasedCoords[1] - mousePressedCoords[1]) / (dragTimeMillis / 1000));
		
		simulation.particles.add(tempParticle);
		simulation.cacheCurrentState();
		tempParticle = null;
	}
	
	public void addPlanet(double x, double y) {
		tempParticle = new Particle("New Planet", x, y, 0, 0, NEW_PLANET_MASS, 10, PLANET_SPAWN_COLORS[new Random().nextInt(PLANET_SPAWN_COLORS.length)]);
		simulation.frame.addDrawable(tempParticle);
		simulation.frame.addDrawable(tempParticle.getTrail());
	}
	
	private double[] getCoords(MouseEvent e) {
		String[] coordComponents = coordinateStrBuilder.getCoordinateString(simulation.frame.getDrawingPanel(), e).split("\\s+");
		return new double[] {Double.parseDouble(coordComponents[0].substring(2)), Double.parseDouble(coordComponents[1].substring(2))};
	}

}
