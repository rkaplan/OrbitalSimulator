import java.io.Serializable;
import org.opensourcephysics.display.Trail;


public class SerializableTrail extends Trail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Override
	public int hashCode() { //sketchy temporary workaround needed to ensure SimulationStates can recognize other states that are exactly the same
		return new Integer((int)(numpts + xmin + xmax + ymin + ymax)).hashCode();
	}
	
}
