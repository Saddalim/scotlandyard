package asset;

import java.awt.Color;

public enum PlayerColor
{
	Red, Green, Blue, Yellow, Purple, MrX;
	
	public static Color getAwtColor(PlayerColor pc)
	{
		switch (pc)
		{
		case Blue: return new Color(0, 141, 202);
		case Green: return new Color(34, 177, 76);
		case MrX: return new Color(187, 187, 187);
		case Red: return new Color(245, 78, 69);
		case Yellow: return new Color(255, 201, 14);
		case Purple: return new Color(230, 26, 209);
		default: return new Color(0, 0, 0);
		}
	}
}
