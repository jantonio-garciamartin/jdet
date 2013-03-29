package interfaces;

/**
 * This interface depicts the observer who listens to some observed object. 
 * --> Gang-of-four behavioral design pattern
 * 
 * @author Thilo Muth
 *
 */
public interface Observer {
	public void update(Observable object);
}
