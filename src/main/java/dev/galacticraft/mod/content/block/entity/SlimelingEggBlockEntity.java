package dev.galacticraft.mod.content.block.entity;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SlimelingEggBlockEntity extends BlockEntity {
    private static final String OWNER_TAG = "Owner";

    @Nullable
    public UUID ownerUUID;

    public SlimelingEggBlockEntity(BlockPos pos, BlockState blockState) {
        super(GCBlockEntityTypes.SLIMELING_EGG, pos, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.hasUUID(OWNER_TAG)) {
            this.ownerUUID = tag.getUUID(OWNER_TAG);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (this.ownerUUID != null) {
            tag.putUUID(OWNER_TAG, this.ownerUUID);
        }
    }
}