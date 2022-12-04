package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractTreeGrower {
   /**
    * @return a {@link net.minecraft.world.level.levelgen.feature.ConfiguredFeature} of this tree
    */
   @Nullable
   protected abstract Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive);

   public boolean growTree(ServerLevel pLevle, ChunkGenerator pGenerator, BlockPos pPos, BlockState pState, RandomSource pRandom) {
      Holder<? extends ConfiguredFeature<?, ?>> holder = this.getConfiguredFeature(pRandom, this.hasFlowers(pLevle, pPos));
      if (holder == null) {
         return false;
      } else {
         ConfiguredFeature<?, ?> configuredfeature = holder.value();
         BlockState blockstate = pLevle.getFluidState(pPos).createLegacyBlock();
         pLevle.setBlock(pPos, blockstate, 4);
         if (configuredfeature.place(pLevle, pGenerator, pRandom, pPos)) {
            if (pLevle.getBlockState(pPos) == blockstate) {
               pLevle.sendBlockUpdated(pPos, pState, blockstate, 2);
            }

            return true;
         } else {
            pLevle.setBlock(pPos, pState, 4);
            return false;
         }
      }
   }

   private boolean hasFlowers(LevelAccessor pLevel, BlockPos pPos) {
      for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(pPos.below().north(2).west(2), pPos.above().south(2).east(2))) {
         if (pLevel.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }
}