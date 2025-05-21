package com.oierbravo.create_mechanical_teleporter.content.items.wand;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.registrate.ModBlocks;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;

//public class TeleportWandItem extends Item  implements MenuProvider {
public class TeleportWandItem extends Item {
    public TeleportWandItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild()) {
            if (player.isShiftKeyDown()) {


            } else {
                if (ModBlocks.MECHANICAL_TELEPORTER.has(hitState)) {
                    //if (world.isClientSide)
                    //    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.activateBind(ctx.getClickedPos()));
                    player.getCooldowns()
                            .addCooldown(this, 2);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return use(world, player, ctx.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getActivationStatus(stack).isAir()) {
            if (tryPerformAction(level, player, stack)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
            return InteractionResultHolder.fail(stack);
        }
        return super.use(level, player, hand);
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player))
            return;

        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);

        if(level.isClientSide) {
            entity.getPersistentData()
                    .putInt("PlayerHasAir", Math.round(backtanks.stream()
                            .map(BacktankUtil::getAir)
                            .reduce(0, Integer::sum)));

        }
    }
    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new TeleportWandItemRenderer()));
    }
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, maxUses());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, maxUses());
    }
    private static int maxUses() {
        return AllConfigs.server().equipment.airInBacktank.get() / MConfigs.server().teleportWand.airAmount.get();
    }

    private boolean tryPerformAction(Level level, Player player, ItemStack stack) {
        boolean isCreative = player.isCreative();
        if (TeleportHandler.hasResources(player) || isCreative) {
            if (performAction(this, level, player)) {
                if (!level.isClientSide() && !isCreative) {
                    TeleportHandler.consumeResources(player);
                }

                return true;
            }

            return false;
        }

        return false;
    }

    public boolean performAction(Item item, Level level, Player player) {
        if (!player.isShiftKeyDown()) {
            if (TeleportHandler.shortTeleport(level, player)) {
                player.getCooldowns().addCooldown(item, MConfigs.server().teleportWand.cooldown.get());
                return true;
            }
        } else {
            if (TeleportHandler.blockTeleport(level, player)) {
                player.getCooldowns().addCooldown(item, MConfigs.server().teleportWand.cooldown.get());
                return true;
            }/* else if (TeleportHandler.interact(level, player)) {
                player.getCooldowns().addCooldown(this, MConfigs.server().teleportWand.cooldown.get());
                return true;
            }*/
        }
        return false;
    }

    protected ActivationStatus getActivationStatus(ItemStack stack) {
        return ActivationStatus.ALL;
    }
    protected enum ActivationStatus {
        BLOCK(true, false), AIR(false, true), ALL(true, true);

        private final boolean isBlock;
        private final boolean isAir;

        ActivationStatus(boolean isBlock, boolean isAir) {
            this.isBlock = isBlock;
            this.isAir = isAir;
        }

        public boolean isBlock() {
            return isBlock;
        }

        public boolean isAir() {
            return isAir;
        }
    }
}
