package demo;

public class Trigger extends GameObject {

	public Trigger(int ID, Bounds bounds) {
		super(ID, bounds);
	}

	public Entity trigger(Entity e) {
		return e;
	}
}
