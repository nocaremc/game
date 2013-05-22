package nocare;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameSettings
{
	
	private static final char KEY_A = 0x1e;
	private static final char KEY_D = 0x20;
	private static final char KEY_SPACE = 0x39;
	private static final char KEY_ESCAPE = 0x1;
	
	private int windowWidth;
	private int windowHeight;
	private int refreshRate;
	private int bitsPerPixel;
	private boolean isFullscreen;
	
	
	private int exitKey;
	private int leftDirectionKey;
	private int rightDirectionKey;
	private int jumpKey;
	
	private Map<String, Integer> keyMap = new HashMap<String, Integer>();
	
	public static GameSettings loadDefault()
	{
		GameSettings g = new GameSettings();
		g.refreshRate = 60;
		g.bitsPerPixel = 32;
		g.windowWidth = 1280;
		g.windowHeight = 768;
		g.leftDirectionKey = KEY_A;
		g.rightDirectionKey = KEY_D;
		g.jumpKey = KEY_SPACE;
		g.exitKey = KEY_ESCAPE;
		g.createKeyMap();
		return g;
	}

	public int getKey(String key)
	{
		return keyMap.get( key );
	}
	
	private void createKeyMap()
	{
		keyMap.put( "key.exit", exitKey );
		keyMap.put( "key.left", leftDirectionKey );
		keyMap.put( "key.right", rightDirectionKey );
		keyMap.put( "key.jump", jumpKey );
	}

	public static GameSettings loadSettingsFile()
	{
		//TODO should be obvious
		return loadDefault();
	}
	
	/*
	 * Getters
	 */
	public int getScreenWidth()
	{
		return windowWidth;
	}
	
	public int getScreenHeight()
	{
		return windowHeight;
	}
	
	public float getRefreshRate()
	{
		return refreshRate;
	}
	
	public int getBitsPerPixel()
	{
		return bitsPerPixel;
	}
	
	public boolean isFullscreen()
	{
		return isFullscreen;
	}
	
	/**
	 * Returns the display mode equivelant to current game settings
	 * Only returns Exact matches
	 * TODO: Support for best-match guessing
	 * @return
	 * @throws LWJGLException
	 */
	public DisplayMode getDisplayMode() throws LWJGLException
	{
		DisplayMode[] modes = Display.getAvailableDisplayModes();

		for ( int i = 0; i < modes.length; i++ )
		{
			DisplayMode current = modes[i];
			
			if (current.getWidth() == windowWidth && current.getHeight() == windowHeight)
			{
				//System.out.println(i+" has correct dimensions.");
				if (current.getBitsPerPixel() == bitsPerPixel)
				{
					//System.out.println(i+" has correct bpp");
					if (current.getFrequency() == refreshRate)
					{
						//System.out.println(i+" has correct refresh rate");
						return current;
					}
				}
			}
		}
		
		System.out.println("returning non-graphics card display mode");
		return new DisplayMode(windowWidth, windowHeight);
	}
}
