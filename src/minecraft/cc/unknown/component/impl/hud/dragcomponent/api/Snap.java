package cc.unknown.component.impl.hud.dragcomponent.api;

public class Snap {
    public double position, distance;
    public Orientation orientation;
    public boolean center, right, left;
	public Snap(double position, double distance, Orientation orientation, boolean center, boolean right,
			boolean left) {
		this.position = position;
		this.distance = distance;
		this.orientation = orientation;
		this.center = center;
		this.right = right;
		this.left = left;
	}
	public double getPosition() {
		return position;
	}
	public double getDistance() {
		return distance;
	}
	public Orientation getOrientation() {
		return orientation;
	}
	public boolean isCenter() {
		return center;
	}
	public boolean isRight() {
		return right;
	}
	public boolean isLeft() {
		return left;
	}
	public void setPosition(double position) {
		this.position = position;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	public void setCenter(boolean center) {
		this.center = center;
	}
	public void setRight(boolean right) {
		this.right = right;
	}
	public void setLeft(boolean left) {
		this.left = left;
	}
}
