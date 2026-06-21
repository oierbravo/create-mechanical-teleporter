package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.ITeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToFrequencyPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Glob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.apache.commons.lang3.tuple.MutablePair;

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

    public static boolean canBlockTeleport(Player player) {
        TeleporterBehavior link = BlockEntityBehaviour.get(player.level(), player.getOnPos(), TeleporterBehavior.TYPE);
        return link != null && link.isTeleportable();
    }
    public static boolean blockPosTeleport(Level level, Player player, BlockPos blockPos) {

            if (player instanceof ServerPlayer serverPlayer) {
                Optional<Vec3> eventPos = teleportEvent(player, blockPos.getCenter());
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
                    if(livingEntity instanceof Player player)
                            player.playSound(SoundEvents.ENDERMAN_TELEPORT,  1F, 1F);
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

            if(!MechanicalTeleporter.TELEPORTERS.mayInteract(freqId,player))
                return false;

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

                if(succes && AllBlockTags.SEATS.matches(player.level().getBlockState(destinationGlobalPos.pos().above()))){
                    sitDown(player.level(),destinationGlobalPos.pos().above(), player);
                }
                if(succes){
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    return true;
                }
            }

            for(TeleportersNetwork.TrainLink trainLink : network.trainLinks){
                boolean success = teleportToTrain(trainLink.trainId(), trainLink.carriageId(), trainLink.address(), player);
                if(success) {
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    return true;
                }
            }

            for(TeleportersNetwork.ContraptionLink contraptionLink : network.contraptionLinks){
                boolean success = teleportToContraptionLink(contraptionLink, player);
                if(success) {
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    return true;
                }
            }
        }
        player.displayClientMessage(ModLang.ui_no_valide_teleporter.t().component(),true);
        player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
        return false;
    }
    public static boolean teleportToTrain(UUID trainId, int carriageId, String address, Player player){
        if(!(player instanceof ServerPlayer serverPlayer))
            return false;
        Train train = Create.RAILWAYS.trains.get(trainId);
        if(train == null)
            return false;
        if(carriageId < 0 || carriageId >= train.carriages.size())
            return false;
        Carriage carriage = train.carriages.get(carriageId);

        CarriageContraptionEntity carriageContraptionEntity = carriage.anyAvailableEntity();

        // Loaded path: the carriage entity exists (same dimension, or another loaded dimension).
        // Match the teleporter by address, then move + seat synchronously.
        if(carriageContraptionEntity != null){
            Contraption contraption = carriageContraptionEntity.getContraption();
            ResourceKey<Level> destinationDimension = player.level().dimension();
            List<ResourceKey<Level>> trainDimensions = train.getPresentDimensions();
            if(!trainDimensions.contains(player.level().dimension()))
                destinationDimension = trainDimensions.getFirst();

            for(MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor : contraption.getActors()){
                if(actor.getLeft().state().getBlock() instanceof ITeleporterBlock
                        && actor.getRight().blockEntityData.getString("SignAddress").equals(address)){
                    return teleportToContraption(contraption, player, destinationDimension);
                }
            }
            return false;
        }

        // Unloaded path: the carriage lives in an unloaded (typically another) dimension, so there is
        // no entity and no contraption to inspect. The link was already validated by address upstream.
        // We do NOT teleport the player yet: the carriage is an entity, not solid blocks, so dropping
        // the player onto the anchor before the entity exists means a lethal mid-air fall (lava/void in
        // the nether). Instead PendingTrainSeats force-loads the carriage chunk so Create respawns the
        // entity, and only then teleports the player straight onto a free seat.
        for(ResourceKey<Level> dimension : carriage.getPresentDimensions()){
            Optional<BlockPos> anchor = carriage.getPositionInDimension(dimension);
            if(anchor.isEmpty())
                continue;
            PendingTrainSeats.enqueue(serverPlayer.serverLevel().getServer(), serverPlayer.getUUID(), trainId, carriageId, dimension, anchor.get());
            return true;
        }
        return false;
    }


    public static boolean teleportToContraptionLink(TeleportersNetwork.ContraptionLink link, Player player){
        if(!(player instanceof ServerPlayer serverPlayer))
            return false;
        if(link.dimension != player.level().dimension())
            return false;
        ServerLevel level = serverPlayer.serverLevel().getServer().getLevel(link.dimension);
        if(level == null)
            return false;

        Entity entity = level.getEntity(link.entityId);

        if(entity instanceof OrientedContraptionEntity contraptionEntity){
            Contraption contraption = contraptionEntity.getContraption();
            for(MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor : contraption.getActors()){
                if(actor.getLeft().state().getBlock() instanceof ITeleporterBlock
                        && actor.getRight().blockEntityData.getString("SignAddress").equals(link.address)){
                    return trySeat(contraption, serverPlayer);
                }
            }
            return false;
        }

        PendingContraptionSeats.enqueue(serverPlayer.serverLevel().getServer(), serverPlayer.getUUID(), link.entityId, link.dimension, link.lastKnownPos);
        return true;
    }

    private static boolean teleportToContraption(Contraption contraption, Player player, ResourceKey<Level> destinationDimension){
        if(player instanceof ServerPlayer serverPlayer){
            if(player.level().dimension() != destinationDimension){
                ServerLevel target = serverPlayer.serverLevel().getServer().getLevel(destinationDimension);
                if(target == null)
                    return false;
                serverPlayer.teleportTo(target, contraption.anchor.getX() + 0.5, contraption.anchor.getY() + 1, contraption.anchor.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
            }
            return trySeat(contraption, serverPlayer);
        }
        return false;
    }

    static boolean trySeat(Contraption contraption, ServerPlayer player){
        int seatIndex = findFreeSeatIndex(contraption);
        if(seatIndex < 0)
            return false;
        seat(contraption, player, seatIndex);
        return true;
    }

    static int findFreeSeatIndex(Contraption contraption){
        if(contraption.entity == null)
            return -1;
        List<BlockPos> seats = contraption.getSeats();
        for(int i = 0; i < seats.size(); i++){
            if(!contraption.getSeatMapping().containsValue(i))
                return i;
        }
        return -1;
    }

    static void seat(Contraption contraption, ServerPlayer player, int seatIndex){
        contraption.entity.addSittingPassenger(player, seatIndex);
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
    public static void teleportToSpawn(ServerPlayer serverPlayer) {
        if (serverPlayer.getRespawnPosition() == null)
            return;
        GlobalPos spawnPos = new GlobalPos(serverPlayer.getRespawnDimension(), serverPlayer.getRespawnPosition());
        teleportToGlobalPosSimple(spawnPos, serverPlayer);

    }
    public static void teleportToGlobalPosSimple(BlockPos destinationBlockPos, ServerPlayer serverPlayer){
        GlobalPos destinationGlobalPos = new GlobalPos(serverPlayer.getRespawnDimension(), destinationBlockPos);
        teleportToGlobalPosSimple(destinationGlobalPos, serverPlayer);
    }

        public static void teleportToGlobalPosSimple(GlobalPos destinationGlobalPos, ServerPlayer serverPlayer){
        BlockPos teleportDestination = destinationGlobalPos.pos().above();
        ServerLevel targetDimension = (ServerLevel) serverPlayer.level();
        boolean sameDimension = serverPlayer.level().dimension() == destinationGlobalPos.dimension();
        if(!sameDimension) {
            targetDimension = serverPlayer.level().getServer().getLevel(destinationGlobalPos.dimension());
        }
        if(targetDimension == null)
            return;

        if(!sameDimension) {
            DimensionTransition transition = new DimensionTransition(targetDimension, new Vec3(teleportDestination.getX() + (double) 0.5F, teleportDestination.getY(), teleportDestination.getZ() + (double) 0.5F), Vec3.ZERO, serverPlayer.getYRot(), serverPlayer.getXRot(), false, DimensionTransition.DO_NOTHING);
            serverPlayer.changeDimension(transition);
            return;
        }
        serverPlayer.teleportTo(teleportDestination.getX() + 0.5,teleportDestination.getY()+ 0.5,teleportDestination.getZ()+ 0.5);

    }

    public static boolean tryTeleportToGlobalPosAndSit(GlobalPos destinationGlobalPos,String address, ServerPlayer serverPlayer) {
        return tryTeleportToGlobalPosAndSit(destinationGlobalPos, address, serverPlayer);
    }
    public static boolean tryTeleportToGlobalPosAndSit(GlobalPos destinationGlobalPos,String address, ServerPlayer serverPlayer, boolean checkPlayerOnTeleporter) {
        if(checkPlayerOnTeleporter){
            TeleporterBehavior teleporterBehavior = BlockEntityBehaviour.get(serverPlayer.level(), serverPlayer.getOnPos(), TeleporterBehavior.TYPE);
            if(teleporterBehavior == null & !teleporterBehavior.checkRequerimentsForTeleport())
                return false;
        }

        boolean succes = TeleportHandler.tryTeleportToGlobalPos(destinationGlobalPos, address, serverPlayer, false);

        if (succes && AllBlockTags.SEATS.matches(serverPlayer.level().getBlockState(destinationGlobalPos.pos().above()))) {
            sitDown(serverPlayer.level(), destinationGlobalPos.pos().above(), serverPlayer);
        }
        if (succes) {
            serverPlayer.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
        }
        return succes;
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