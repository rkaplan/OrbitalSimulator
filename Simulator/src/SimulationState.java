import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimulationState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Particle[] particles;
	private double timeElapsed;
	private double timeInterval;
	private double gravConstant;
	private boolean elasticCollisions;
	
	public SimulationState(List<Particle> particles, double timeElapsed, double timeInterval, double gravConstant, boolean elasticCollisions) {
		this.particles = new Particle[particles.size()];
		for(int i = 0; i < particles.size(); i++) {
			this.particles[i] = particles.get(i).deepCopy();
		}
		this.timeElapsed = timeElapsed;
		this.timeInterval = timeInterval;
		this.gravConstant = gravConstant;
		this.elasticCollisions = elasticCollisions;
	}
	
	public void save(String filename) {
		save(new File(filename));
	}
	
	public void save(File file) {
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
			fos.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Particle> getParticles() {
		List<Particle> res = new ArrayList<Particle>();
		for(Particle p : particles) {
			res.add(p.deepCopy());
		}
		return res;
	}
	
	public double getTimeElapsed() {
		return timeElapsed;
	}

	public double getTimeInterval() {
		return timeInterval;
	}

	public double getGravConstant() {
		return gravConstant;
	}

	public boolean isElasticCollisions() {
		return elasticCollisions;
	}
	
}
