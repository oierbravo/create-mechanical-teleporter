package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.registrate.ModContainerTypes;
import com.simibubi.create.foundation.gui.container.GhostItemContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SimpleTeleportControllerContainer extends GhostItemContainer<ItemStack> {

	public SimpleTeleportControllerContainer(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public SimpleTeleportControllerContainer(MenuType<?> type, int id, Inventory inv, ItemStack filterItem) {
		super(type, id, inv, filterItem);
	}

	public static SimpleTeleportControllerContainer create(int id, Inventory inv, ItemStack filterItem) {
		return new SimpleTeleportControllerContainer(ModContainerTypes.SIMPLE_TELEPORT_CONTROLLER.get(), id, inv, filterItem);
	}

	@Override
	protected ItemStack createOnClient(FriendlyByteBuf extraData) {
		return extraData.readItem();
	}

	@Override
	protected ItemStackHandler createGhostInventory() {
		return SimpleTeleportControllerItem.getFrequencyItems(contentHolder);
	}

	@Override
	protected void addSlots() {
		addPlayerSlots(8, 131);

		int x = 81;
		int y = 33;
		int slot = 0;

		for (int column = 0; column < 1; column++) {
			for (int row = 0; row < 2; ++row)
				addSlot(new SlotItemHandler(ghostInventory, slot++, x, y + row * 18));
			x += 24;
			if (column == 3)
				x += 11;
		}
	}

	@Override
	protected void saveData(ItemStack contentHolder) {
		contentHolder.getOrCreateTag()
			.put("Items", ghostInventory.serializeNBT());
	}

	@Override
	protected boolean allowRepeats() {
		return true;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (slotId == playerInventory.selected && clickTypeIn != ClickType.THROW)
			return;
		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return playerInventory.getSelected() == contentHolder;
	}

}
