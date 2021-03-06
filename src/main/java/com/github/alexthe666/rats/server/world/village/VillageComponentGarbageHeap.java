package com.github.alexthe666.rats.server.world.village;

public class VillageComponentGarbageHeap {}
    /*extends StructureVillagePieces.Village {

    int villagerCount = 0;
    private int averageGroundLevel = -1;

    public VillageComponentGarbageHeap() {
        super();
    }

    public VillageComponentGarbageHeap(StructureVillagePieces.Start startPiece, int p2, Random random, StructureBoundingBox structureBox, Direction facing) {
        super();
        this.villagerCount = 0;
        this.setCoordBaseMode(facing);
        this.boundingBox = structureBox;
    }

    public static VillageComponentGarbageHeap buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, Direction facing, int p5) {
        if (!RatConfig.villageGarbageHeaps) {
            return null;
        }
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 8, 4, 8, facing);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new VillageComponentGarbageHeap(startPiece, p5, random, structureboundingbox, facing) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);
            if (this.averageGroundLevel < 0) {
                return false;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
        }

        BlockPos blockpos = new BlockPos(this.boundingBox.minX + 1, this.boundingBox.minY + 1, this.boundingBox.minZ + 1);
        Direction facing = Direction.SOUTH;
        BlockPos genPos = blockpos;
        /*for(int i = this.boundingBox.minX; i < this.boundingBox.maxX; i++){
            for(int k = this.boundingBox.minZ; k < this.boundingBox.maxZ; k++){
                world.setBlockState(new BlockPos(i, boundingBox.maxY, k), Blocks.GOLD_BLOCK.getDefaultState());
            }
        }*/
//world.setBlockState(new BlockPos(this.boundingBox.minX, this.boundingBox.maxY, this.boundingBox.minZ), Blocks.PUMPKIN.getDefaultState().with(BlockHorizontal.FACING, facing));
      /*  return new WorldGenGarbageHeap(this, facing).generate(world, random, genPos);
    }

    public BlockState getBiomeBlock(BlockState state) {
        return getBiomeSpecificBlockState(state);
    }
}

     */