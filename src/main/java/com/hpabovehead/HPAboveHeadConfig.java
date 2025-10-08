package com.hpabovehead;

import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Config;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("settings")
public interface HPAboveHeadConfig extends Config
{
	@ConfigItem(
			keyName = "fontSize",
			name = "Font Size",
			description = "Size of the text above the HP bar",
			position = 0
	)
	@Range(min = 8, max = 40)
	default int fontSize()
	{
		return 14;
	}

	@ConfigItem(
			keyName = "font",
			name = "Font",
			description = "Font used for the text",
			position = 1
	)
	default FontOption font()
	{
		return FontOption.VERDANA;
	}
	
	@ConfigItem(
			keyName = "color",
			name = "Text Color",
			description = "Color of the text above the HP bar",
			position = 2
	)
	default Color color()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "textHeightOffset",
			name = "Text vertical offset",
			description = "How high above the HP bar to render the HP text",
			position = 3
	)
	default int textHeightOffset()
	{
		return 47;
	}

	@ConfigItem(
			keyName = "textXOffset",
			name = "Text horizontal offset",
			description = "Horizontal offset for the HP text relative to the player's center",
			position = 4
	)
	default int textXOffset()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "showBackground",
			name = "Show Background",
			description = "Draw a background behind the HP text",
			position = 5
	)
	default boolean showBackground()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "backgroundColor",
			name = "Background Color",
			description = "Color of the HP text background",
			position = 6
	)
	default Color backgroundColor()
	{
		return new Color(0, 0, 0, 125); // semi-transparent black
	}

	@ConfigItem(
			keyName = "alwaysShow",
			name = "Always Show",
			description = "Always show HP text, even when not in combat",
			position = 7
	)
	default boolean alwaysShow()
	{
		return false;
	}

	@ConfigSection(
			name = "Color Thresholds",
			description = "Settings for color thresholds based on HP percentage",
			position = 8,
			closedByDefault = true
	)
	String colorThresholds = "colorThresholds";


	@ConfigItem(
			keyName = "useColorThresholds",
			name = "Use Color Thresholds",
			description = "Change text color based on HP percentage",
			position = 9,
			section = colorThresholds
	)
	default boolean useColorThresholds()
	{
		return false;
	}

	@ConfigItem(
			keyName = "midThreshold",
			name = "Mid HP Threshold (%)",
			description = "HP percentage below which text color changes to mid HP color",
			position = 10,
			section = colorThresholds
	)
	@Range(min = 1, max = 100)
	default int midThreshold()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "midColor",
			name = "Mid HP color",
			description = "Color to use when HP is below mid threshold",
			position = 11,
			section = colorThresholds
	)
	default Color midColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
			keyName = "lowThreshold",
			name = "Low HP Threshold (%)",
			description = "HP percentage below which text color changes to low HP color",
			position = 12,
			section = colorThresholds
	)
	@Range(min = 1, max = 100)
	default int lowThreshold()
	{
		return 25;
	}

	@ConfigItem(
			keyName = "lowColor",
			name = "Low HP color",
			description = "Color to use when HP is below low threshold",
			position = 13,
			section = colorThresholds
	)
	default Color lowColor()
	{
		return Color.RED;
	}

	/* enums */

	enum FontOption
	{
		ARIAL,
		VERDANA,
		TIMES_NEW_ROMAN,
		MONOSPACED,
		UNICA_ONE,
		TAHOMA,
		CALIBRI,
		COURIER_NEW,
		GEORGIA,
		COMIC_SANS_MS,
		SEGOE_UI,
		LUCIDA_CONSOLE,
		ROBOTO

	}

}
