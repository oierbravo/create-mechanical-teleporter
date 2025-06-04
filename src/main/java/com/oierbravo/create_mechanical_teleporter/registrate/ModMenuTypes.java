package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.manager.TeleporterManagerMenu;
import com.oierbravo.create_mechanical_teleporter.content.logistics.manager.TeleporterManagerScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ModMenuTypes {
    public static final MenuEntry<TeleporterManagerMenu> TELEPORTER_MANAGER =
            register("teleporter_manager", TeleporterManagerMenu::new, () -> TeleporterManagerScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return MechanicalTeleporter.registrate()
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {}
}
