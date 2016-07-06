package net.earthcomputer.stepfish.leveleditor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum EnumObjectType {
	// @formatter:off
	PLAYER				(0	, "Player"			, "player"),
	SOLID_WALL			(1	, "SolidWall"		, "wall"),
	EARTH_WALL			(2	, "EarthWall"		, "wall_earth"),
	WATER_WALL			(3	, "WaterWall"		, "wall_water"),
	AIR_WALL			(4	, "AirWall"			, "wall_air"),
	FIRE_WALL			(5	, "FireWall"		, "wall_fire"),
	STAR1				(6	, "Star1"			, "star1"),
	STAR2				(11	, "Star2"			, "star2"),
	STAR3				(12	, "Star3"			, "star3"),
	ELEMENT_SWITCHER	(7	, "ElementSwitcher"	, "element_switcher"),
	FLYING_CROSS		(8	, "FlyingCross"		, "flying_cross"),
	SWITCHING_SPIKE		(9	, "SwitchingSpike"	, "spike_switching"),
	EARTH_SPIKE			(13	, "EarthSpike"		, "spike_earth"),
	WATER_SPIKE			(14	, "WaterSpike"		, "spike_water"),
	AIR_SPIKE			(15	, "AirSpike"		, "spike_air"),
	FIRE_SPIKE			(16	, "FireSpike"		, "spike_fire"),
	EXIT				(10	, "Exit"			, "exit"),
	ENEMY_BLOCKER		(17	, "EnemyBlocker"	, "enemy_blocker"),
	MUD					(18	, "Mud"				, "mud");
	// @formatter:on

	private static final String[] NAMES;

	private final int id;
	private final String name;
	private final String textureName;
	private BufferedImage texture;

	private EnumObjectType(int id, String name, String textureName) {
		this.id = id;
		this.name = name;
		this.textureName = textureName;
		loadTexture();
	}

	private void loadTexture() {
		try {
			texture = ImageIO.read(EnumObjectType.class.getResourceAsStream("/" + textureName + ".png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void render(int x, int y, Graphics g) {
		g.drawImage(texture, x, y, null);
	}

	public static EnumObjectType byID(int id) {
		for (EnumObjectType type : values()) {
			if (type.id == id)
				return type;
		}
		return null;
	}

	public static EnumObjectType byName(String name) {
		for (EnumObjectType type : values()) {
			if (type.name.equals(name))
				return type;
		}
		return null;
	}

	public static String[] getAllNames() {
		return NAMES;
	}

	static {
		EnumObjectType[] values = values();
		NAMES = new String[values.length];
		for (int i = 0; i < values.length; i++)
			NAMES[i] = values[i].name;
	}

}
