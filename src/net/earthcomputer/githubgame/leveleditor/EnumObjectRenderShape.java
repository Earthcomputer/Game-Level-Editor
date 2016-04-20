package net.earthcomputer.githubgame.leveleditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

import javax.imageio.ImageIO;

public enum EnumObjectRenderShape
{
	FULL_SQUARE, PLAYER, STAR, COLOR_SWITCHER, CROSS, TRIANGLE_UP, CIRCLE;
	
	private static final BufferedImage STAR_IMAGE;
	
	static
	{
		try
		{
			STAR_IMAGE = ImageIO
				.read(new BufferedInputStream(EnumObjectRenderShape.class.getResourceAsStream("/Star.png")));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void render(Graphics g, int x, int y, Color color)
	{
		g.setColor(color);
		switch(this)
		{
			case FULL_SQUARE:
				g.fillRect(x, y, 16, 16);
				break;
			case PLAYER:
				g.fillRect(x, y, 16, 16);
				g.setColor(Color.RED);
				g.fillRect(x + 8, y + 3, 2, 2);
				g.fillRect(x + 12, y + 3, 2, 2);
				g.fillRect(x + 8, y + 10, 8, 2);
				break;
			case STAR:
				g.fillRect(x, y, 16, 16);
				g.drawImage(STAR_IMAGE, x, y, null);
				break;
			case COLOR_SWITCHER:
				g.setColor(Color.RED);
				g.fillPolygon(new int[] { x, x + 16, x + 8 }, new int[] { y, y, y + 8 }, 3);
				g.setColor(Color.GREEN);
				g.fillPolygon(new int[] { x + 16, x + 16, x + 8 }, new int[] { y, y + 16, y + 8 }, 3);
				g.setColor(Color.BLUE);
				g.fillPolygon(new int[] { x + 16, x, x + 8 }, new int[] { y + 16, y + 16, y + 8 }, 3);
				g.setColor(Color.YELLOW);
				g.fillPolygon(new int[] { x, x, x + 8 }, new int[] { y + 16, y, y + 8 }, 3);
				break;
			case CROSS:
				g.drawLine(x + 3, y + 3, x + 13, y + 13);
				g.drawLine(x + 3, y + 13, x + 13, y + 3);
				break;
			case TRIANGLE_UP:
				g.fillPolygon(new int[] { x + 8, x, x + 16 }, new int[] { y + 8, y + 16, y + 16 }, 3);
				break;
			case CIRCLE:
				g.fillOval(x, y, 16, 16);
				break;
			default:
				throw new RuntimeException("Tharr be not maintainin' yer switch statements!");
		}
	}
}
