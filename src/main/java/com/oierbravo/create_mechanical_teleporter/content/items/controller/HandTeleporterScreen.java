package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.mojang.blaze3d.platform.InputConstants;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.foundation.ItemStackUtils;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.SetAddressToBlockEntityPayload;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.SetAddressToItemPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModBlocks;
import com.oierbravo.create_mechanical_teleporter.registrate.ModGuiTextures;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class HandTeleporterScreen  extends AbstractSimiScreen {
    private EditBox addressBox;
    public ItemStack item;

    private int targetSlot;

    private IconButton confirmButton;
    private IconButton resetButton;

    int bgHeight = ModGuiTextures.HAND_TELEPORTER.getHeight();
    int bgWidth = ModGuiTextures.HAND_TELEPORTER.getWidth();

    private ItemStack icon;

    private String address;
    private BlockPos blockPos;
    private HandTeleporterBlockEntity handTeleporterBlockEntity;

    public HandTeleporterScreen(int targetSlot, ItemStack item){
        this.item = item;
        this.targetSlot = targetSlot;
        this.address = TeleporterFrequency.from(this.item).address();
        icon = ModBlocks.HAND_TELEPORTERS.get(DyeColor.BROWN).asStack();
    }

    public HandTeleporterScreen(BlockPos blockPos, String address){
        this.blockPos = blockPos;
        this.address = address;
        icon = ModBlocks.HAND_TELEPORTERS.get(DyeColor.GREEN).asStack();
    }

    @Override
    protected void init() {
        setWindowSize(bgWidth, bgHeight);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        addressBox = new EditBox(new NoShadowFontWrapper(font), x + 24, y + 29, 140, 10,
               Component.empty());
        addressBox.setBordered(false);
        addressBox.setMaxLength(25);
        addressBox.setTextColor(0x3D3C48);
        addressBox.setValue(address);
        addressBox.setFocused(false);
        addressBox.mouseClicked(0, 0, 0);
        addressBox.setResponder(this::onAddressEdited);

        addRenderableWidget(addressBox);

        resetButton = new IconButton(x + 158, y + 55, AllIcons.I_TRASH);
        resetButton.withCallback(() -> {
            sendClearPacket();
        });
        addRenderableWidget(resetButton);

        confirmButton = new IconButton(x + 180, y + 55, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);
    }

    @Override
    public void removed() {
        /*CatnipServices.NETWORK.sendToServer(new PackagePortConfigurationPacket(menu.contentHolder.getBlockPos(), addressBox.getValue(),
                acceptPackages.green));*/
        super.removed();
    }

    @Override
    protected void renderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindowForeground(graphics, mouseX, mouseY, partialTicks);
        String itemName = "";
        if(item != null)
            itemName = ItemStackUtils.getName(item);
        else if(handTeleporterBlockEntity != null)
            itemName = handTeleporterBlockEntity.getCustomName().getString();

        graphics.drawString(font, itemName, guiLeft + 8, guiTop + 4, Color.BLACK.brighter().getRGB(), false);

        GuiGameElement.of(icon).scale(3).at(guiLeft + bgWidth - 5, guiTop + 33, 100)
                .render(graphics);

    }

    private void sendClearPacket() {
        addressBox.setValue("");
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hitEnter = getFocused() instanceof EditBox
                && (pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER);

        if (hitEnter && addressBox.isFocused()) {
            addressBox.setFocused(false);
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void onAddressEdited(String s) {
        this.address = s;
        if(item != null)
            ModMessages.sendToServer(new SetAddressToItemPayload(s,this.targetSlot));
        if(handTeleporterBlockEntity != null)
            ModMessages.sendToServer(new SetAddressToBlockEntityPayload(blockPos,address));

    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ModGuiTextures.HAND_TELEPORTER.render(graphics, guiLeft, guiTop);
    }

}