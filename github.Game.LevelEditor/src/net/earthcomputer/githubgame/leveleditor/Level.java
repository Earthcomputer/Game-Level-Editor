package net.earthcomputer.githubgame.leveleditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {

	public String name;
	public int levelWidth;
	public int levelHeight;
	public List<GameObject> objects = Collections.synchronizedList(new ArrayList<GameObject>());
	
	public Level(String name, int levelWidth, int levelHeight, List<GameObject> objects) {
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		this.objects = objects;
	}
	
}
