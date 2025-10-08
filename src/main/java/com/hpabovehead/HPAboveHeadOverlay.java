package com.hpabovehead;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class HPAboveHeadOverlay extends Overlay
{
    private final Client client;
    private final HPAboveHeadConfig config;
    
    // Animation state
    private Color currentColor;
    private Color targetColor;
    private long transitionStartTime;
    private static final long TRANSITION_DURATION_MS = 200;

    @Inject
    public HPAboveHeadOverlay(Client client, HPAboveHeadConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.MED);
        this.currentColor = null;
        this.targetColor = null;
        this.transitionStartTime = 0;
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
        int height = localPlayer.getLogicalHeight() + config.textHeightOffset();
        LocalPoint localLocation = localPlayer.getLocalLocation();
        Point canvasPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);

        if (canvasPoint != null)
        {
            int x = canvasPoint.getX() - (textWidth / 2) + config.textXOffset();
            int y = canvasPoint.getY();

            // Draw background if enabled
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

            // Get color based on HP percentage and thresholds
            Color textColor = getTextColor(currentHp, maxHp);

            // Draw shadow
            g.setColor(Color.BLACK);
            g.drawString(text, x + 1, y + 1);

            // Draw main text
            g.setColor(textColor);
            g.drawString(text, x, y);

        }
        
        return null;
    }

    /**
     * Determines the text color based on HP percentage and threshold settings.
     * Smoothly fades between colors when crossing thresholds.
     */
    private Color getTextColor(int currentHp, int maxHp)
    {
        if (!config.useColorThresholds())
        {
            // Reset animation state if thresholds are disabled
            currentColor = null;
            return config.color();
        }

        double hpPercent = (double) currentHp / maxHp * 100;
        int lowThreshold = config.lowThreshold();
        int midThreshold = config.midThreshold();

        // Determine what the target color should be based on HP percentage
        Color newTargetColor;
        if (hpPercent <= lowThreshold)
        {
            newTargetColor = config.lowColor();
        }
        else if (hpPercent <= midThreshold)
        {
            newTargetColor = config.midColor();
        }
        else
        {
            newTargetColor = config.color();
        }

        // Initialize or detect color change
        if (currentColor == null)
        {
            // First render, no animation needed
            currentColor = newTargetColor;
            targetColor = newTargetColor;
            return currentColor;
        }

        // Check if target color has changed (threshold crossed)
        if (!newTargetColor.equals(targetColor))
        {
            // Start a new transition
            currentColor = getAnimatedColor(); // Get the current animated color
            targetColor = newTargetColor;
            transitionStartTime = System.currentTimeMillis();
        }

        return getAnimatedColor();
    }

    /**
     * Calculates the current color during a transition animation
     * @return The interpolated color based on elapsed time
     */
    private Color getAnimatedColor()
    {
        if (currentColor == null || targetColor == null || currentColor.equals(targetColor))
        {
            return targetColor != null ? targetColor : config.color();
        }

        long elapsedTime = System.currentTimeMillis() - transitionStartTime;
        
        if (elapsedTime >= TRANSITION_DURATION_MS)
        {
            // Transition complete
            currentColor = targetColor;
            return currentColor;
        }

        // Calculate interpolation position (0.0 to 1.0)
        double progress = (double) elapsedTime / TRANSITION_DURATION_MS;
        return interpolateColor(currentColor, targetColor, progress);
    }

    /**
     * Interpolates between two colors based on a position value (0.0 to 1.0)
     * @param color1 Starting color
     * @param color2 Ending color
     * @param position Position between colors (0.0 = color1, 1.0 = color2)
     * @return Interpolated color
     */
    private Color interpolateColor(Color color1, Color color2, double position)
    {
        // Clamp position between 0 and 1
        position = Math.max(0.0, Math.min(1.0, position));

        int r = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * position);
        int g = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * position);
        int b = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * position);
        int a = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * position);

        return new Color(r, g, b, a);
    }
}