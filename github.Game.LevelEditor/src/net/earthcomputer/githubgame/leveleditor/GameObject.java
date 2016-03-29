package net.earthcomputer.githubgame.leveleditor;

import java.awt.Graphics;

public class GameObject {

	public double x;
	public double y;
	public int id;
	
	public GameObject(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public void render(int scrollX, int scrollY, Graphics g) {
		
	}
	
}
