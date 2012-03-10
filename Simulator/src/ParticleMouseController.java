import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.opensourcephysics.display.axes.CoordinateStringBuilder;


public class ParticleMouseController extends MouseInputAdapter {

	private OrbitalSimulation simulation;
	private CoordinateStringBuilder coordinateStrBuilder;
	
	public ParticleMouseController(OrbitalSimulation simulation) {
		this.simulation = simulation;
		coordinateStrBuilder = CoordinateStringBuilder.createCartesian();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("clicky!");
		String[] coordComponents = coordinateStrBuilder.getCoordinateString(simulation.frame.getDrawingPanel(), e).split("\\s+");
		double x = Double.parseDouble(coordComponents[0].substring(2));
		double y = Double.parseDouble(coordComponents[1].substring(2));
		addPlanet(x, y);
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("release!");
	}
	
	public void addPlanet(double x, double y) {
//		Scanner sc = new Scanner(System.in);
		double xVel, yVel, mass;
		int pixRadius;
		
		System.out.println("Please input the planet's information:");
		System.out.println("Format: [Name], [x], [y], [xVe], [yVel], [mass], [pixRadius]");
//		String[] data = sc.nextLine().split(", ");
		String[] data = {"NewPlanet", "-2", "-2", "0", "0", "10", "10"};
		
//		x = Double.parseDouble(data[1]);
//		y = Double.parseDouble(data[2]);
		xVel = Double.parseDouble(data[3]);
		yVel = Double.parseDouble(data[4]);
		mass = Double.parseDouble(data[5]);
		pixRadius = Integer.parseInt(data[6]);
		
		Particle p = new Particle(data[0], x, y, xVel, yVel, mass, pixRadius, Color.GREEN);
		simulation.frame.addDrawable(p);
		simulation.frame.addDrawable(p.getTrail());
		simulation.particles.add(p);
	}

}
