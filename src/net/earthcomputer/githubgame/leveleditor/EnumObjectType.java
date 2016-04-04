package net.earthcomputer.githubgame.leveleditor;

import java.awt.Color;

public enum EnumObjectType
{
	
	PLAYER(0, "Player", EnumObjectRenderShape.PLAYER, Color.ORANGE), SOLID_WALL(1, "SolidWall",
		EnumObjectRenderShape.FULL_SQUARE, Color.WHITE), RED_WALL(2, "RedWall", EnumObjectRenderShape.FULL_SQUARE,
			Color.RED), GREEN_WALL(3, "GreenWall", EnumObjectRenderShape.FULL_SQUARE, Color.GREEN), BLUE_WALL(4,
				"BlueWall", EnumObjectRenderShape.FULL_SQUARE, Color.BLUE), YELLOW_WALL(5, "YellowWall",
					EnumObjectRenderShape.FULL_SQUARE, Color.YELLOW), STAR(6, "Star", EnumObjectRenderShape.STAR,
						Color.WHITE), COLOR_SWITCHER(7, "ColorSwitcher", EnumObjectRenderShape.COLOR_SWITCHER,
							Color.WHITE), FLYING_CROSS(8, "FlyingCross", EnumObjectRenderShape.CROSS,
								Color.WHITE), HOPPING_TRIANGLE(9, "HoppingTriangle", EnumObjectRenderShape.TRIANGLE_UP,
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
