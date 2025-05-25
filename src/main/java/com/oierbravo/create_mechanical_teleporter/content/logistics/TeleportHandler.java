package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToFrequencyPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Glob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.simibubi.create.content.contraptions.actors.seat.SeatBlock.sitDown;


public class TeleportHandler {

    public static final int MIN_TELEPORTATION_DISTANCE_SQUARED = 25;

    public static boolean canTeleport(Player player) {
        return canItemTeleport(player);
    }

    public static boolean canItemTeleport(Player player) {
        return canItemTeleport(player, InteractionHand.MAIN_HAND) || canItemTeleport(player, InteractionHand.OFF_HAND);
    }

    private static boolean canItemTeleport(Player player, InteractionHand hand) {
        return player.getItemInHand(hand).is(ModItems.TELEPORT_WAND.asItem());
    }

    public static boolean  canBlockTeleport(Player player) {
        TeleporterBehavior link = BlockEntityBehaviour.get(player.level(), player.getOnPos(), TeleporterBehavior.TYPE);
        return link != null;
    }

    //From EnderIO:
    //License CCO
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

                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                } else {
                    player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
                }
            }
            return true;
        } else {
            return false;
        }
    }
    public static boolean shortRandomTeleport(Level level, Entity entity, int range) {
        Optional<Vec3> pos = teleportRandomPosition(level, entity);
        if (pos.isPresent()) {
            if (entity instanceof LivingEntity livingEntity) {
                Optional<Vec3> eventPos = teleportEvent(entity, pos.get());
                if (eventPos.isPresent()) {
                    livingEntity.teleportTo(eventPos.get().x(), eventPos.get().y(), eventPos.get().z());
                    if(entity instanceof ServerPlayer serverPlayer)
                        serverPlayer.connection.resetPosition();
                    livingEntity.fallDistance = 0;

                    if (livingEntity.isInWall()) {
                        // without this line the player takes 1 tick of damage before their pose changes
                        livingEntity.setPose(Pose.SWIMMING);
                    }
                    livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT,  1F, 1F);
                } else {
                    entity.playSound(SoundEvents.DISPENSER_FAIL, 1F, 1F);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean teleportToFrequency(TeleporterFrequency frequency, ServerPlayer player){

        UUID freqId = frequency.freqId();
        String address = frequency.address();

        if(MechanicalTeleporter.TELEPORTERS.teleportersNetworks.containsKey(freqId)){
            TeleportersNetwork network = MechanicalTeleporter.TELEPORTERS.teleportersNetworks.get(freqId);
            boolean foundCurrent = false;
            GlobalPos destinationGlobalPos = null;

            Set<TeleportersNetwork.TrainLink> trainLinks = network.trainLinks;

            for(GlobalPos globalPos : network.loadedLinks) {
                if(!globalPos.equals(new GlobalPos(player.level().dimension(),player.getOnPos()))) {
                    if(foundCurrent && TeleportHandler.tryTeleportToGlobalPos(globalPos, address, player, true)) {
                        destinationGlobalPos = globalPos;
                        break;
                    }
                } else {
                    foundCurrent = true;
                }


            }
            if(destinationGlobalPos == null) {
                for(GlobalPos globalPos : network.loadedLinks) {
                    if(!globalPos.equals(new GlobalPos(player.level().dimension(),player.getOnPos()))) {
                        if(TeleportHandler.tryTeleportToGlobalPos(globalPos, address, player, true)){
                            destinationGlobalPos = globalPos;
                            break;
                        }
                    }
                }
            }
            if(destinationGlobalPos != null){
                boolean succes = TeleportHandler.tryTeleportToGlobalPos(destinationGlobalPos, address, player, false);

                if(succes && player.level().getBlockState(destinationGlobalPos.pos().above()).getBlock() instanceof SeatBlock){
                    sitDown(player.level(),destinationGlobalPos.pos().above(), player);
                }
                if(succes){
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    return true;
                }

            } else {
                player.displayClientMessage(ModLang.translate("ui.no_valid_teleporter").component(),true);
                player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
            }

            for(TeleportersNetwork.TrainLink trainLink : network.trainLinks){
                Train train = Create.RAILWAYS.trains.get(trainLink.trainId());
                int carriageIndex = trainLink.carriageId();
                Carriage carriage = train.carriages.get(carriageIndex);
                CarriageContraptionEntity carriageContraptionEntity = carriage.anyAvailableEntity();
                Contraption contraption = carriageContraptionEntity.getContraption();
                List<BlockPos> seats = contraption.getSeats();
                for(BlockPos seatPos :  contraption.getSeats()){
                    int seatIndex = contraption.getSeats().indexOf(seatPos);
                    if(!contraption.getSeatMapping().containsValue(seatIndex)){
                        carriageContraptionEntity.addSittingPassenger(player,seatIndex);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean teleportToTeleporter(Level level, Player pPlayer, BlockPos teleporterBlockPos){
        BlockPos destination = teleporterBlockPos.above();
        if(isTeleportPositionClear(level, teleporterBlockPos.above()).isPresent()){
            pPlayer.teleportTo(destination.getX() + 0.5,destination.getY()+ 0.5,destination.getZ()+ 0.5);
            return true;
        }
        return false;
    }

    public static boolean blockTeleport(Level level, Player player) {
        return blockTeleport(level, player, false);
    }

    public static boolean blockTeleport(Level level, Player player, boolean sendToServer) {
        TeleporterBehavior link = BlockEntityBehaviour.get(level, player.getOnPos(), TeleporterBehavior.TYPE);
        if (sendToServer && link != null){
            ModMessages.sendToServer(new RequestTeleportToFrequencyPayload(TeleporterFrequency.from(link)));
            return true;
        }
        return false;
    }

    //From EnderIO:
    //License CCO
    public static Optional<Vec3> teleportPosition(Level level, Player player) {
        @Nullable
        BlockPos target = null;
        double floorHeight = 0;

        // inspired by Entity#pick
        Vec3 playerPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle().normalize();
        int range = MConfigs.server().wand.range.get();
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

    public static Optional<Vec3> teleportRandomPosition(Level level, Entity entity) {
        @Nullable
        BlockPos target = null;
        double floorHeight = 0;

        // inspired by Entity#pick
        Vec3 playerPos = entity.getEyePosition();
        Vec3 lookVec = entity.getLookAngle().normalize();
        int range = 5;
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
                target = bhr.getBlockPos().below((int) Math.ceil(entity.getBbHeight()));
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

        if (target == null || entity.blockPosition().distManhattan(target) < 2) {
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

    private static Optional<Vec3> teleportEvent(Entity entity, Vec3 target) {
        EntityTeleportEvent event = new EntityTeleportEvent(entity, target.x(), target.y(), target.z());
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return Optional.empty();
        }

        return Optional.of(new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
    }

    public static boolean tryTeleportToGlobalPos(GlobalPos destinationGlobalPos,String address, ServerPlayer serverPlayer, boolean simulate) {
        BlockPos teleportDestination = destinationGlobalPos.pos().above();
        ServerLevel targetDimension = (ServerLevel) serverPlayer.level();
        boolean sameDimension = serverPlayer.level().dimension() == destinationGlobalPos.dimension();
        if(!sameDimension) {
            targetDimension = serverPlayer.level().getServer().getLevel(destinationGlobalPos.dimension());
        }

        if(targetDimension == null)
            return false;

        if(!targetDimension.isLoaded(destinationGlobalPos.pos()))
            return false;

        if(isTeleportPositionClear(targetDimension,teleportDestination).isEmpty())
            return false;

        TeleporterBehavior link = BlockEntityBehaviour.get(targetDimension, destinationGlobalPos.pos(), TeleporterBehavior.TYPE);

        if(!link.checkRequerimentsForTeleport())
            return false;

        if(!matchAddress(link, address))
            return false;

        if(simulate)
            return true;

        if(!sameDimension) {
            DimensionTransition transition = new DimensionTransition(targetDimension, new Vec3(teleportDestination.getX() + (double) 0.5F, teleportDestination.getY(), teleportDestination.getZ() + (double) 0.5F), Vec3.ZERO, serverPlayer.getYRot(), serverPlayer.getXRot(), false, DimensionTransition.DO_NOTHING);
            serverPlayer.changeDimension(transition);
            consumeTeleporterResources(serverPlayer.level(), destinationGlobalPos.pos());
            return true;
        }
        serverPlayer.dismountTo(0.5,0.5,0.5);
        serverPlayer.teleportTo(teleportDestination.getX() + 0.5,teleportDestination.getY()+ 0.5,teleportDestination.getZ()+ 0.5);
        consumeTeleporterResources(serverPlayer.level(), destinationGlobalPos.pos());
        return true;
    }

    public static void consumeTeleporterResources(Level level, BlockPos pos){
        TeleporterBehavior link = BlockEntityBehaviour.get(level, pos, TeleporterBehavior.TYPE);
        if(link != null)
            link.consumeResources();
    }

    public static boolean matchAddress(TeleporterBehavior teleporterBehavior, String address){
        return matchAddress(teleporterBehavior.signBasedAddress, address);
    }
    public static boolean matchAddress(String teleporterAddress, String address) {
        if (address.isBlank())
            return teleporterAddress.isBlank();
        if (address.equals("*") || teleporterAddress.equals("*"))
            return true;
        String matcher = Glob.toRegexPattern(address, "");
        String boxMatcher = Glob.toRegexPattern(teleporterAddress, "");
        return address.matches(boxMatcher) || teleporterAddress.matches(matcher);
    }
}