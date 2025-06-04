package com.oierbravo.create_mechanical_teleporter.content.logistics.manager;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportersNetwork;
import com.oierbravo.create_mechanical_teleporter.registrate.ModGuiTextures;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMenuTypes;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class TeleporterManagerMenu extends MenuBase<TeleporterManagerBlockEntity> {
    boolean isAdmin;
    boolean isLocked;

    TeleporterFrequency frequency;
    TeleportersNetwork network;

    public TeleporterManagerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    protected TeleporterManagerMenu(MenuType<?> type, int id, Inventory inv, TeleporterManagerBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    @Override
    protected TeleporterManagerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        isAdmin = extraData.readBoolean();
        isLocked = extraData.readBoolean();
        BlockPos blockPos = extraData.readBlockPos();

        if (Minecraft.getInstance().level
                .getBlockEntity(blockPos) instanceof TeleporterManagerBlockEntity tmbe)
            return tmbe;
        return null;
    }

    @Override
    protected void initAndReadInventory(TeleporterManagerBlockEntity contentHolder) {
        frequency = contentHolder.getFrequency();
        network = MechanicalTeleporter.TELEPORTERS.teleportersNetworks.get(frequency.freqId());
    }

    @Override
    protected void addSlots() {
        addPlayerSlots(7, ModGuiTextures.TELEPORTER_MANAGER.getHeight() - 37);
    }

    @Override
    protected void saveData(TeleporterManagerBlockEntity contentHolder) {

    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
    public static TeleporterManagerMenu create(int id, Inventory inv, TeleporterManagerBlockEntity be) {
        return new TeleporterManagerMenu(ModMenuTypes.TELEPORTER_MANAGER.get(), id, inv, be);
    }
}
