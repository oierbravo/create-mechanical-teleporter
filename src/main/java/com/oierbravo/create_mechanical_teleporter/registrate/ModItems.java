package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.content.items.EnderPotatoItem;
import com.oierbravo.create_mechanical_teleporter.content.items.EnderSoupItem;
import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandItem;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.MechanicalCraftingRecipeBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModItems {


    private static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate();

    public static final ItemEntry<TeleportWandItem> TELEPORT_WAND =
            REGISTRATE.item("teleport_wand", TeleportWandItem::new)
                    .lang("Teleport Wand")
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .recipe((ctx, p) ->
                            MechanicalCraftingRecipeBuilder.shapedRecipe(ctx.get())
                                    .key('W', Ingredient.of(ItemTags.PLANKS))
                                    .key('C', Ingredient.of(ModItems.POLISHED_ENDER_QUARTZ))
                                    .key('B', Ingredient.of(AllTags.commonItemTag("ingots/brass")))
                                    .key('S', Ingredient.of(AllTags.commonItemTag("plates/brass")))
                                    .patternLine(  " C S")
                                    .patternLine( "  C ")
                                    .patternLine( " W C")
                                    .patternLine( "B   ")
                                    .build(p))
                    .register();

    /*public static final ItemEntry<HandTeleporterBlockItem> HAND_TELEPORTER =
            REGISTRATE.item("hand_teleporter", HandTeleporterBlockItem::new)
                    .lang("Hand teleporter")
                    .properties(p -> p.stacksTo(1).durability(200))
                    .model(AssetLookup.itemModelWithPartials())
                    .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get())
                            .define('T', AllItems.TRANSMITTER)
                            .define('C', AllBlocks.BRASS_CASING)
                            .define('B', ItemTags.BUTTONS)
                            .pattern("T ")
                            .pattern("CB")
                            .unlockedBy("has_transmitter", RegistrateRecipeProvider.has(AllBlocks.BRASS_CASING))
                            .save(p, ModConstants.asResource("crafting/" + c.getName())))
                    .register();*/

    public static final ItemEntry<Item> ENDER_QUARTZ =
            REGISTRATE.item("ender_quartz", Item::new)
                    .lang("Ender quartz")
                    .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get())
                            .define('Q', Items.QUARTZ)
                            .define('P', Items.ENDER_PEARL)
                            .pattern("PQQ")
                            .pattern("QQQ")
                            .pattern("QQQ")
                            .unlockedBy("has_ender_pearl", RegistrateRecipeProvider.has(Items.ENDER_PEARL))
                            .save(p, ModConstants.asResource("crafting/" + c.getName())))
                    .register();

    public static final ItemEntry<Item> POLISHED_ENDER_QUARTZ =
            REGISTRATE.item("polished_ender_quartz", Item::new)
                    .lang("Polished ender quartz")
                    .register();

    public static final ItemEntry<EnderSoupItem> ENDER_SOUP =
            REGISTRATE.item("ender_soup", EnderSoupItem::new)
                    .properties(properties -> properties.food(Foods.BEETROOT_SOUP))
                    .lang("Ender soup")
                    .register();

    public static final ItemEntry<EnderPotatoItem> ENDER_POTATO =
            REGISTRATE.item("ender_potato", EnderPotatoItem::new)
                    .properties(properties -> properties.food(Foods.POTATO))
                    .lang("Ender potato")
                    .register();


    public static void register() {}

}
