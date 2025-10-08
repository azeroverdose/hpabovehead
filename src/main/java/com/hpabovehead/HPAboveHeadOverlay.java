package com.hpabovehead;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.api.Skill;

import javax.inject.Inject;
import java.awt.*;

public class HPAboveHeadOverlay extends Overlay
{
    private final Client client;
    private final HPAboveHeadConfig config;

    @Inject
    public HPAboveHeadOverlay(Client client, HPAboveHeadConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null)
        {
            return null;
        }

        // Check if we should show HP - either always show is enabled, or player is in combat (health scale > 0)
        if (!config.alwaysShow() && localPlayer.getHealthScale() <= 0)
        {
            return null;
        }

        int maxHp = client.getRealSkillLevel(Skill.HITPOINTS);
        int currentHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        String text = currentHp + "/" + maxHp;

        // set font
        g.setFont(new Font(config.font().name(), Font.PLAIN, config.fontSize()));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        // calc height (player's logical height + offset)
        int height = localPlayer.getLogicalHeight() + config.textHeightOffset();
        net.runelite.api.coords.LocalPoint localLocation = localPlayer.getLocalLocation();
        net.runelite.api.Point canvasPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);

        if (canvasPoint != null)
        {
            // centre the text and apply horizontal offset
            int x = canvasPoint.getX() - (textWidth / 2) + config.textXOffset();
            int y = canvasPoint.getY();

            // optional background box
            if (config.showBackground())
            {
                int padding = 2;
                int boxX = x - padding;
                int boxY = y - textHeight + 4;
                int boxWidth = textWidth + padding * 2;
                int boxHeight = textHeight;

                g.setColor(config.backgroundColor());
                g.fillRect(boxX, boxY, boxWidth, boxHeight);
            }

            // optional shadow
            g.setColor(Color.BLACK);
            g.drawString(text, x + 1, y + 1);

            // HP text with color thresholds
            Color textColor = getTextColor(currentHp, maxHp);
            g.setColor(textColor);
            g.drawString(text, x, y);
        }

        return null;
    }

    /**
     * Determines the text color based on HP percentage and threshold settings
     */
    private Color getTextColor(int currentHp, int maxHp)
    {
        if (!config.useColorThresholds())
        {
            return config.color();
        }

        // Calculate HP percentage
        double hpPercentage = (double) currentHp / maxHp * 100;

        // Apply color thresholds (low has priority over mid)
        if (hpPercentage <= config.lowThreshold())
        {
            return config.lowColor();
        }
        else if (hpPercentage <= config.midThreshold())
        {
            return config.midColor();
        }
        else
        {
            return config.color();
        }
    }
}
