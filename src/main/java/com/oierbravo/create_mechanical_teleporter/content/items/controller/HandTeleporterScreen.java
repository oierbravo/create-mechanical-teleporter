package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.mojang.blaze3d.platform.InputConstants;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.foundation.ItemStackUtils;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.SetAddressToItemPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModGuiTextures;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
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

    public HandTeleporterScreen(int targetSlot, ItemStack item){
        this.item = item;
        this.targetSlot = targetSlot;
        this.address = TeleporterFrequency.from(this.item).address();
        icon = ModItems.HAND_TELEPORTER.asStack();
    }

    @Override
    protected void init() {
        super.init();


        int x = guiLeft/2;
        int y = guiTop/2;

        addressBox = new EditBox(new NoShadowFontWrapper(font), bgWidth / 2 + 23, y + bgHeight - 50, 110, 10,
               Component.empty());
        addressBox.setBordered(false);
        addressBox.setMaxLength(25);
        addressBox.setTextColor(0x3D3C48);
        addressBox.setValue(address);
        addressBox.setFocused(false);
        addressBox.mouseClicked(0, 0, 0);
        addressBox.setResponder(this::onAddressEdited);

        addRenderableWidget(addressBox);

        confirmButton = new IconButton(x + bgWidth - 33, y + bgHeight - 23, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirmButton);

        resetButton = new IconButton(x + bgWidth - 55, y + bgHeight - 23, AllIcons.I_TRASH);
        resetButton.withCallback(() -> {
            sendClearPacket();
        });
        addRenderableWidget(resetButton);

    }

    @Override
    public void removed() {
        /*CatnipServices.NETWORK.sendToServer(new PackagePortConfigurationPacket(menu.contentHolder.getBlockPos(), addressBox.getValue(),
                acceptPackages.green));*/
        super.removed();
    }

    @Override
    protected void renderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindowBackground(graphics, mouseX, mouseY, partialTicks);

        String itemName = ItemStackUtils.getName(item);
        graphics.drawString(font, itemName, guiLeft - bgWidth/2 + 6, guiTop - bgHeight + 21, Color.BLACK.brighter().getRGB());

        GuiGameElement.of(icon).scale(4).at(guiLeft + (float) bgWidth /2, guiTop - (float) bgHeight /2, -200)
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
        ModMessages.sendToServer(new SetAddressToItemPayload(s,this.targetSlot));
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft/2;
        int y = guiTop/2;

        ModGuiTextures.HAND_TELEPORTER.render(graphics, x, y);
    }

}