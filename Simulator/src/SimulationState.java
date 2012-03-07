import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class SimulationState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Particle[] particles;
	
	public SimulationState(List<Particle> particles) {
		this.particles = new Particle[particles.size()];
		for(int i = 0; i < particles.size(); i++) {
			this.particles[i] = particles.get(i).deepCopy();
		}
	}
	
	public void save(String filename) {
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(filename);
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
	
}
