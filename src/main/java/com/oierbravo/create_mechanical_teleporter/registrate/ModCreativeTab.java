package com.oierbravo.create_mechanical_teleporter.registrate;


import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class ModCreativeTab extends CreativeModeTab {
	public static ModCreativeTab MAIN;

	public ModCreativeTab(String name) {
		super(MechanicalTeleporter.MODID+":"+name);
		MAIN = this;
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(Items.ACACIA_BOAT);
	}
}
