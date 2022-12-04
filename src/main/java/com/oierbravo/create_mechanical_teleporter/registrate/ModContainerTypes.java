package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.simple.SimpleTeleportControllerContainer;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.simple.SimpleTeleportControllerScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ModContainerTypes {
    public static final MenuEntry<SimpleTeleportControllerContainer> SIMPLE_TELEPORT_CONTROLLER =
            register("simple_teleport_controller", SimpleTeleportControllerContainer::new, () -> SimpleTeleportControllerScreen::new);
    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return MechanicalTeleporter.registrate()
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {}
}
