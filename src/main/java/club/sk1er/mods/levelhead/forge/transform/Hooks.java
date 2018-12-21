package club.sk1er.mods.levelhead.forge.transform;

import java.awt.Color;

import club.sk1er.mods.levelhead.Levelhead;
import club.sk1er.mods.levelhead.display.DisplayConfig;
import club.sk1er.mods.levelhead.display.LevelheadDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;

@SuppressWarnings("unused")
public final class Hooks {

	private Levelhead levelhead;

	public Hooks(Levelhead levelhead) {
		this.levelhead = levelhead;
	}

	/**
	 * java.lang.IncompatibleClassChangeError
	 */
    public void drawPingHook(int i, int x, int y, NetworkPlayerInfo playerInfo) {
        if (!levelhead.getDisplayManager().getMasterConfig().isEnabled()) {
            return;
        }
        LevelheadDisplay tab = levelhead.getDisplayManager().getTab();
        if (tab != null) {

            if (!tab.getConfig().isEnabled()) {
                return;
            }

            if (levelhead.getLevelheadPurchaseStates().isTab()) {
                String s = tab.getTrueValueCache().get(playerInfo.getGameProfile().getId());
                if (s != null) {
                    FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
                    int x1 = i + x - 12 - fontRendererObj.getStringWidth(s);
                    DisplayConfig config = tab.getConfig();
                    if (config.isFooterChroma()) {
                        fontRendererObj.drawString(s, x1, y, Levelhead.getRGBColor());
                    } else if (config.isFooterRgb()) {
                        fontRendererObj.drawString(s, x1, y, new Color(config.getFooterRed(), config.getFooterGreen(), config.getFooterBlue()).getRGB());
                    } else {
                        fontRendererObj.drawString(config.getFooterColor() + s, x1, y, Color.WHITE.getRGB());
                    }
                }
            }
        }
    }

    /**
	 * java.lang.IncompatibleClassChangeError
	 */
    public int getLevelheadWith(NetworkPlayerInfo playerInfo) {
        if (!levelhead.getDisplayManager().getMasterConfig().isEnabled()) {
            return 0;
        }
        LevelheadDisplay tab = levelhead.getDisplayManager().getTab();
        if (tab != null) {
            if (!tab.getConfig().isEnabled())
                return 0;
            if (levelhead.getLevelheadPurchaseStates().isTab()) {
                String s = tab.getTrueValueCache().get(playerInfo.getGameProfile().getId());
                if (s != null) {
                    return Minecraft.getMinecraft().fontRendererObj.getStringWidth(s) + 2;
                }
            }
        }
        return 0;
    }

}
