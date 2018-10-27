package club.sk1er.mods.levelhead.renderer;

import club.sk1er.mods.levelhead.Levelhead;
import club.sk1er.mods.levelhead.utils.Sk1erMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/**
 * Created by mitchellkatz
 * <p>
 * Modified by boomboompower on 16/6/2017
 */
public class LevelHeadRender {

    private Levelhead levelHead;

    public LevelHeadRender(Levelhead levelHead) {
        this.levelHead = levelHead;
    }

    @SubscribeEvent
    public void render(RenderPlayerEvent.Pre event) {
        if ((event.getEntityPlayer().getUniqueID().equals(Levelhead.getInstance().userUuid) && !levelHead.getConfig().isShowSelf()) || !Sk1erMod.getInstance().isHypixel())
            return;

        EntityPlayer player = event.getEntityPlayer();
        // TODO - "entityPlayer" field is private for newer versions (PORTING REQUIRED) - Newer versions use a getter instead
        // TODO - https://github.com/MinecraftForge/MinecraftForge/blob/1.11.x/src/main/java/net/minecraftforge/event/entity/player/PlayerEvent.java#L51-L54

        if (levelHead.loadOrRender(player) && (Levelhead.getInstance().getLevelString(player.getUniqueID())) != null) {
            if (player.getDistanceSq(Minecraft.getMinecraft().player) < 64 * 64) {
                double offset = 0.3;
                Scoreboard scoreboard = player.getWorldScoreboard();
                ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(2);

                if (scoreObjective != null && event.getEntityPlayer().getDistanceSq(Minecraft.getMinecraft().player) < 10 * 10) {
                    offset *= 2;
                }
                if (event.getEntityPlayer().getUniqueID().equals(Levelhead.getInstance().userUuid))
                    offset = 0;
                renderName(event, (Levelhead.getInstance().getLevelString(player.getUniqueID())), player, event.getX(), event.getY() + offset, event.getZ());
            }
        }
    }

    public void renderName(RenderPlayerEvent event, LevelheadTag tag, EntityPlayer entityIn, double x, double y, double z) {
        FontRenderer fontrenderer = event.getRenderer().getFontRendererFromRenderManager();
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.0F, (float) y + entityIn.height + 0.5F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-event.getRenderer().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(event.getRenderer().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int verticalShift =0;
        int i = Minecraft.getMinecraft().fontRenderer.getStringWidth(tag.getString())/2;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        renderString(fontrenderer, tag);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderString(FontRenderer renderer, LevelheadTag tag) {

        int y = 0;

        int x = -renderer.getStringWidth(tag.getString()) / 2;
        //Render header
        LevelheadComponent header = tag.getHeader();
        render(renderer, header, x);
        x += renderer.getStringWidth(header.getValue());
        //render footer
        render(renderer, tag.getFooter(), x);

    }

    private void render(FontRenderer renderer, LevelheadComponent header, int x) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        int y = 0;
        if (header.isRgb()) {
//            GlStateManager.color(header.getRed()/2, header.getBlue()/2, header.getGreen()/2);
            renderer.drawString(header.getValue(), x, y, new Color((float) header.getRed() / 255F, (float) header.getGreen() / 255F, (float) header.getBlue() / 255F, .2F).getRGB());
        } else if (header.isChroma()) {
            renderer.drawString(header.getValue(), x, y, Levelhead.getRGBDarkColor());
        } else {
            GlStateManager.color(255, 255, 255, .5F);
            renderer.drawString(header.getColor() + header.getValue(), x, y, Color.WHITE.darker().darker().darker().darker().darker().getRGB() * 255);
        }
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        if (header.isRgb()) {
            GlStateManager.color(header.getRed(), header.getBlue(), header.getGreen(), header.getAlpha());
            renderer.drawString(header.getValue(), x, y, new Color(header.getRed(), header.getGreen(), header.getBlue()).getRGB());
        } else if (header.isChroma()) {
            renderer.drawString(header.getValue(), x, y, header.isChroma() ? Levelhead.getRGBColor() : 553648127);
        } else {
            GlStateManager.color(255, 255, 255, .5F);

            renderer.drawString(header.getColor() + header.getValue(), x, y, Color.WHITE.darker().getRGB());
        }


    }
}
