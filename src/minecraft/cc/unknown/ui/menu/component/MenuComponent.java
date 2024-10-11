package cc.unknown.ui.menu.component;

import cc.unknown.ui.menu.MenuColors;
import cc.unknown.util.Accessor;

public class MenuComponent implements Accessor, MenuColors {

    private double x;
    private double y;
    private double width;
    private double height;

    public MenuComponent(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public MenuComponent(double x, double y, double width, double height, Runnable runnable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}
