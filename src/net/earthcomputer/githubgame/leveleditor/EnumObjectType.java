package net.earthcomputer.githubgame.leveleditor;

import java.awt.Color;

public enum EnumObjectType
{
	
	PLAYER(0, "Player", EnumObjectRenderShape.PLAYER, Color.GREEN.darker()), SOLID_WALL(1, "SolidWall",
		EnumObjectRenderShape.FULL_SQUARE, Color.WHITE), EARTH_WALL(2, "EarthWall", EnumObjectRenderShape.FULL_SQUARE,
			Color.GREEN.darker()), WATER_WALL(3, "WaterWall", EnumObjectRenderShape.FULL_SQUARE, Color.BLUE), AIR_WALL(
				4, "AirWall", EnumObjectRenderShape.FULL_SQUARE,
				Color.CYAN.darker()), FIRE_WALL(5, "FireWall", EnumObjectRenderShape.FULL_SQUARE, Color.ORANGE), STAR1(
					6, "Star1", EnumObjectRenderShape.STAR, Color.RED), STAR2(11, "Star2", EnumObjectRenderShape.STAR,
						Color.GREEN), STAR3(12, "Star3", EnumObjectRenderShape.STAR, Color.BLUE), ELEMENT_SWITCHER(7,
							"ElementSwitcher", EnumObjectRenderShape.ELEMENT_SWITCHER, Color.WHITE), FLYING_CROSS(8,
								"FlyingCross", EnumObjectRenderShape.CROSS, Color.WHITE), SWITCHING_SPIKE(9,
									"SwitchingSpike", EnumObjectRenderShape.TRIANGLE_UP,
									Color.RED), EXIT(10, "Exit", EnumObjectRenderShape.CIRCLE, Color.PINK);
									
	private static final String[] NAMES;
	
	private final int id;
	private final String name;
	private final EnumObjectRenderShape shape;
	private final Color renderColor;
	
	private EnumObjectType(int id, String name, EnumObjectRenderShape shape, Color renderColor)
	{
		this.id = id;
		this.name = name;
		this.shape = shape;
		this.renderColor = renderColor;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public EnumObjectRenderShape getRenderShape()
	{
		return shape;
	}
	
	public Color getRenderColor()
	{
		return renderColor;
	}
	
	public static EnumObjectType byID(int id)
	{
		for(EnumObjectType type : values())
		{
			if(type.id == id) return type;
		}
		return null;
	}
	
	public static EnumObjectType byName(String name)
	{
		for(EnumObjectType type : values())
		{
			if(type.name.equals(name)) return type;
		}
		return null;
	}
	
	public static String[] getAllNames()
	{
		return NAMES;
	}
	
	static
	{
		EnumObjectType[] values = values();
		NAMES = new String[values.length];
		for(int i = 0; i < values.length; i++)
			NAMES[i] = values[i].name;
	}
	
}
