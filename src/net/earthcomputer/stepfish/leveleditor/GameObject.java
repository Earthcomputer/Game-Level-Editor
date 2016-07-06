package net.earthcomputer.stepfish.leveleditor;

import java.awt.Graphics;

public class GameObject {

	public int x;
	public int y;
	public EnumObjectType objectType;

	public GameObject(int x, int y, EnumObjectType objectType) {
		this.x = x;
		this.y = y;
		this.objectType = objectType;
	}

	public void render(int scrollX, int scrollY, Graphics g) {
		objectType.getRenderShape().render(g, x - scrollX, y - scrollY, objectType.getRenderColor());
	}

}
