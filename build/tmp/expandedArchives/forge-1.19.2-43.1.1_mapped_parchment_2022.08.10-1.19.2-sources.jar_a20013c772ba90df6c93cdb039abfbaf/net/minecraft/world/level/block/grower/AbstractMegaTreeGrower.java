package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public boolean growTree(ServerLevel pLevle, ChunkGenerator pGenerator, BlockPos pPos, BlockState pState, RandomSource pRandom) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (isTwoByTwoSapling(pState, pLevle, pPos, i, j)) {
               return this.placeMega(pLevle, pGenerator, pPos, pState, pRandom, i, j);
            }
         }
      }

      return super.growTree(pLevle, pGenerator, pPos, pState, pRandom);
   }

   /**
    * @return a {@link net.minecraft.world.level.levelgen.feature.ConfiguredFeature} of the huge variant of this tree
    */
   @Nullable
   protected abstract Holder<? extends ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource pRandom);

   public boolean placeMega(ServerLevel pLevle, ChunkGenerator pGenerator, BlockPos pPos, BlockState pState, RandomSource pRandom, int pBranchX, int pBranchY) {
      Holder<? extends ConfiguredFeature<?, ?>> holder = this.getConfiguredMegaFeature(pRandom);
      if (holder == null) {
         return false;
      } else {
         ConfiguredFeature<?, ?> configuredfeature = holder.value();
         BlockState blockstate = Blocks.AIR.defaultBlockState();
         pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY), blockstate, 4);
         pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY), blockstate, 4);
         pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY + 1), blockstate, 4);
         pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY + 1), blockstate, 4);
         if (configuredfeature.place(pLevle, pGenerator, pRandom, pPos.offset(pBranchX, 0, pBranchY))) {
            return true;
         } else {
            pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY), pState, 4);
            pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY), pState, 4);
            pLevle.setBlock(pPos.offset(pBranchX, 0, pBranchY + 1), pState, 4);
            pLevle.setBlock(pPos.offset(pBranchX + 1, 0, pBranchY + 1), pState, 4);
            return false;
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState pBlockUnder, BlockGetter pLevel, BlockPos pPos, int pXOffset, int pZOffset) {
      Block block = pBlockUnder.getBlock();
      return pLevel.getBlockState(pPos.offset(pXOffset, 0, pZOffset)).is(block) && pLevel.getBlockState(pPos.offset(pXOffset + 1, 0, pZOffset)).is(block) && pLevel.getBlockState(pPos.offset(pXOffset, 0, pZOffset + 1)).is(block) && pLevel.getBlockState(pPos.offset(pXOffset + 1, 0, pZOffset + 1)).is(block);
   }
}