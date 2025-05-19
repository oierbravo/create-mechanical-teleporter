package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;

import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

//From EnderIO
//License CCO
public class TeleportHandler {

    public static final int MIN_TELEPORTATION_DISTANCE_SQUARED = 25;

    /*public static boolean canTeleport(Player player) {
        return canItemTeleport(player) || canBlockTeleport(player);
    }*/
    public static boolean canTeleport(Player player) {
        return canItemTeleport(player);
    }

    public static boolean canItemTeleport(Player player) {
        return canItemTeleport(player, InteractionHand.MAIN_HAND) || canItemTeleport(player, InteractionHand.OFF_HAND);
    }

    private static boolean canItemTeleport(Player player, InteractionHand hand) {
        return player.getItemInHand(hand).is(ModItems.TELEPORT_WAND.asItem());
    }

    public static boolean canBlockTeleport(Player player) {
        return player.getBlockStateOn().getBlock() instanceof TeleporterBlock;
    }

    public static boolean hasResources(Player player) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);
        if(backtanks.isEmpty())
            return false;
        if(!BacktankUtil.hasAirRemaining(backtanks.getFirst()))
            return false;
        return BacktankUtil.getAir(backtanks.getFirst()) >= MConfigs.server().teleportWand.airAmount.get();
    }

    public static void consumeResources(Player player) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);
        BacktankUtil.consumeAir(player, backtanks.getFirst(), MConfigs.server().teleportWand.airAmount.get());
    }

    public static boolean shortTeleport(Level level, Player player) {
        Optional<Vec3> pos = teleportPosition(level, player);
        if (pos.isPresent()) {
            if (player instanceof ServerPlayer serverPlayer) {
                Optional<Vec3> eventPos = teleportEvent(player, pos.get());
                if (eventPos.isPresent()) {
                    player.teleportTo(eventPos.get().x(), eventPos.get().y(), eventPos.get().z());
                    serverPlayer.connection.resetPosition();
                    player.fallDistance = 0;

                    if (player.isInWall()) {
                        // without this line the player takes 1 tick of damage before their pose changes
                        player.setPose(Pose.SWIMMING);
                    }

                    //player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                } else {
                    //player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
                }
            }
            return true;
        } else {
            return false;
        }
    }
    public static boolean teleportToTeleporter(Level level, Player pPlayer, BlockPos teleporterBlockPos){
        BlockPos destination = teleporterBlockPos.above();
        if(isTeleportPositionClear(level, teleporterBlockPos.above()).isPresent()){
            pPlayer.teleportTo(destination.getX() + 0.5,destination.getY()+ 0.5,destination.getZ()+ 0.5);
            pPlayer.getPersistentData().put("lastTeleportedPos", NbtUtils.writeBlockPos(pPlayer.getOnPos()));
            return true;
        }
        return false;
    }

    public static boolean blockTeleport(Level level, Player player) {
        return blockTeleport(level, player, false);
    }

    public static boolean blockTeleport(Level level, Player player, boolean sendToServer) {
        BlockEntity onBlockEntity = level.getBlockEntity(player.getOnPos());
        if(onBlockEntity instanceof TeleporterBlockEntity teleporterBlockEntity){
            ModMessages.sendToServer(new RequestTeleportPayload(player.getUUID(), teleporterBlockEntity.getTeleport().getNetworkKey()));
        }
        return true;
        /*return getTeleportTarget(player)
                .filter(iTravelTarget -> blockTeleportTo(level, player, iTravelTarget, sendToServer))
                .isPresent();*/
    }

    /*public static boolean interact(Level level, Player player) {
        return getInteractionTarget(player).filter(iTravelTarget -> interactWithTarget(level, player, iTravelTarget))
                .isPresent();
    }*/


    /*public static boolean blockTeleportTo(Level level, Player player, TravelTarget target, boolean sendToServer) {
        Optional<Double> height = isTeleportPositionClear(level, target.pos());
        if (height.isEmpty()) {
            return false;
        }
        BlockPos blockPos = target.pos();
        Vec3 teleportPosition = new Vec3(blockPos.getX() + 0.5f, blockPos.getY() + height.get() + 1,
                blockPos.getZ() + 0.5f);
        teleportPosition = teleportEvent(player, teleportPosition).orElse(null);
        if (teleportPosition != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                player.teleportTo(teleportPosition.x(), teleportPosition.y(), teleportPosition.z());
                // Stop "moved too quickly" warnings
                serverPlayer.connection.resetPosition();
                player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75F, 1F);
            } else if (sendToServer) {
                ModMessages.sendToServer(new RequestTravelPacket(target.pos()));
            }

            player.resetFallDistance();
            return true;
        }
        return false;
    }*/

    /*private static boolean interactWithTarget(Level level, Player player, TravelTarget target) {
        return target.interact(level, player);
    }*/

    public static Optional<Vec3> teleportPosition(Level level, Player player) {
        @Nullable
        BlockPos target = null;
        double floorHeight = 0;

        // inspired by Entity#pick
        Vec3 playerPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle().normalize();
        //int range = BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_RANGE.get();
        int range = 10;
        Vec3 toPos = playerPos.add(lookVec.scale(range));

        ClipContext clipCtx = new ClipContext(playerPos, toPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE,
                CollisionContext.empty());
        BlockHitResult bhr = level.clip(clipCtx);

        // process the result
        if (bhr.getType() == HitResult.Type.MISS) {
            target = bhr.getBlockPos();
        } else if (bhr.getType() == HitResult.Type.BLOCK) {
            Direction dir = bhr.getDirection();
            if (dir == Direction.UP) {
                // teleport the player *inside* the target block, then later push them up by the
                // block's height
                // warning: relies on the fact that isTeleportClear works with heights >= 1
                target = bhr.getBlockPos();
            } else if (dir == Direction.DOWN) {
                target = bhr.getBlockPos().below((int) Math.ceil(player.getBbHeight()));
            } else {
                target = bhr.getBlockPos().offset(dir.getStepX(), 0, dir.getStepZ());
                if (level.getBlockState(target).getCollisionShape(level, target).isEmpty()) {
                    target = target.below();
                }
            }
        }

        // if target block is close, also try to teleport through
        // eventually this distance should become configurable client-side
        if (playerPos.distanceToSqr(bhr.getLocation()) < 9) {
            // add small amount to make sure it starts at the correct block
            Vec3 traverseFrom = bhr.getLocation().add(lookVec.scale(0.01));

            // since we can't return null from the fail condition, instead use an invalid
            // position
            BlockPos failPosition = new BlockPos(0, Integer.MAX_VALUE, 0);

            boolean aimingUp = lookVec.y > 0.5;

            // can reuse same toPos and clipCtx because this traversal should be along the
            // same line
            BlockPos newTarget = BlockGetter.traverseBlocks(traverseFrom, toPos, clipCtx,
                    (traverseCtx, traversePos) -> {
                        if (!aimingUp) {
                            // check underneath first, since that's more likely to be where the player wants
                            // to teleport
                            BlockPos checkBelow = traversalCheck(level, traversePos.below());
                            if (checkBelow != null) {
                                return checkBelow;
                            }
                        }

                        return traversalCheck(level, traversePos);
                    }, (failCtx) -> failPosition);
            if (newTarget != failPosition) {
                target = newTarget.immutable();
            }
        }

        if (target != null) {
            Optional<Double> ground = isTeleportPositionClear(level, target.below());
            if (ground.isPresent()) { // to use the same check as the anchors use the position below
                floorHeight = ground.get();
            } else {
                target = null;
            }
        }

        if (target == null || player.blockPosition().distManhattan(target) < 2) {
            return Optional.empty();
        }
        return Optional.of(Vec3.atBottomCenterOf(target).add(0, floorHeight, 0));
    }

    @Nullable
    private static BlockPos traversalCheck(Level level, BlockPos traversePos) {
        BlockState blockState = level.getBlockState(traversePos);
        var collision = blockState.getCollisionShape(level, traversePos);
        if (collision.isEmpty() && isTeleportPositionClear(level, traversePos.below()).isPresent()) {
            return traversePos;
        }
        return null;
    }

    /*public static Optional<TravelTarget> getInteractionTarget(Player player) {
        Vec3 positionVec = player.position().add(0, player.getEyeHeight(), 0);

        return TravelTargetApi.INSTANCE.getInItemRange(player.level(), player.blockPosition())
                .filter(TravelTarget::canInteract)
                .filter(target -> target.pos().distToCenterSqr(player.position()) > MIN_TELEPORTATION_DISTANCE_SQUARED)
                .filter(target -> Math.abs(getAngleRadians(positionVec, target.pos(), player.getYRot(),
                        player.getXRot())) <= Math.toRadians(15))
                .min(Comparator.comparingDouble(target -> Math
                        .abs(getAngleRadians(positionVec, target.pos(), player.getYRot(), player.getXRot()))));
    }*/

    /*public static Optional<TravelTarget> getTeleportAnchorTarget(Player player) {
        Vec3 positionVec = player.position().add(0, player.getEyeHeight(), 0);

        return TravelTargetApi.INSTANCE.getInItemRange(player.level(), player.blockPosition())
                .filter(TravelTarget::canTeleportTo)
                .filter(target -> target.pos().distToCenterSqr(player.position()) > MIN_TELEPORTATION_DISTANCE_SQUARED)
                .filter(target -> Math.abs(getAngleRadians(positionVec, target.pos(), player.getYRot(),
                        player.getXRot())) <= Math.toRadians(15))
                .filter(target -> isTeleportPositionClear(player.level(), target.pos()).isPresent())
                .min(Comparator.comparingDouble(target -> Math
                        .abs(getAngleRadians(positionVec, target.pos(), player.getYRot(), player.getXRot()))));
    }*/

    private static double getAngleRadians(Vec3 positionVec, BlockPos anchor, float yRot, float xRot) {
        Vec3 blockVec = new Vec3(anchor.getX() + 0.5 - positionVec.x, anchor.getY() + 1.0 - positionVec.y,
                anchor.getZ() + 0.5 - positionVec.z).normalize();
        Vec3 lookVec = Vec3.directionFromRotation(xRot, yRot).normalize();
        return Math.acos(lookVec.dot(blockVec));
    }

    /**
     *
     * @return Optional.empty if it can't teleport and the height where to place the player. This is so you can tp on top of carpets up to a whole block
     */
    public static Optional<Double> isTeleportPositionClear(BlockGetter level, BlockPos target) {
        if (level.isOutsideBuildHeight(target)) {
            return Optional.empty();
        }

        BlockPos above = target.above();
        double height = level.getBlockState(above).getCollisionShape(level, above).max(Direction.Axis.Y);
        if (height <= 0.2d) {
            return Optional.of(Math.max(height, 0));
        }

        above = above.above();
        boolean noCollisionAbove = level.getBlockState(above).getCollisionShape(level, above).isEmpty();
        if (noCollisionAbove) {
            return Optional.of(Math.max(height, 0));
        }

        return Optional.empty();
    }

    private static Optional<Vec3> teleportEvent(Player player, Vec3 target) {
        EntityTeleportEvent event = new EntityTeleportEvent(player, target.x(), target.y(), target.z());
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return Optional.empty();
        }

        return Optional.of(new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
    }
}