package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum ModGuiTextures implements ScreenElement, TextureSheetSegment {
    SIMPLE_TELEPORT_CONTROLLER("teleport_controller", 179, 109),
    MECHANICAL_TELEPORTER("mechinical_teleporter", 179, 109);
    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;

    private ModGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private ModGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    ModGuiTextures(String location, int startX, int startY, int width, int height) {
        this(ModConstants.MODID, location, startX, startY, width, height);
    }

    ModGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    /*@OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }*/

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }


    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
