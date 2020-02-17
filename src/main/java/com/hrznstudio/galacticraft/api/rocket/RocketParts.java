package com.hrznstudio.galacticraft.api.rocket;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RocketParts {
    public static final RocketPart DEFAULT_CONE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_cone"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.CONE;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_CONE_BASIC_RENDER_BLOCK;
        }

    });

    public static final RocketPart DEFAULT_BODY = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_body"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.BODY;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_BODY_RENDER_BLOCK;
        }
    });

    public static final RocketPart DEFAULT_FIN = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_fin"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.FIN;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_FINS_RENDER_BLOCK;
        }
    });

    public static final RocketPart NO_BOOSTER = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_booster"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.BOOSTER;
        }

        @Override
        public Block getBlockToRender() {
            return Blocks.AIR;
        }
    });

    public static final RocketPart DEFAULT_BOTTOM = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_bottom"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.BOTTOM;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_BOTTOM_RENDER_BLOCK;
        }
    });

    public static final RocketPart ADVANCED_CONE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "advanced_cone"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.CONE;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_CONE_ADVANCED_RENDER_BLOCK;
        }
    });

    public static final RocketPart SLOPED_CONE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "sloped_cone"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.CONE;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_CONE_SLOPED_RENDER_BLOCK;
        }
    });

    public static final RocketPart BOOSTER_TIER_1 = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "booster_tier_1"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.BOOSTER;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_THRUSTER_TIER_1_RENDER_BLOCK;
        }
    });

    public static final RocketPart BOOSTER_TIER_2 = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "booster_tier_2"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.BOOSTER;
        }

        @Override
        public Block getBlockToRender() {
            return GalacticraftBlocks.ROCKET_THRUSTER_TIER_2_RENDER_BLOCK;
        }
    });

    public static final RocketPart STORAGE_UPGRADE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "storage_upgrade"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.UPGRADE;
        }

        @Override
        public Block getBlockToRender() {
            return Blocks.AIR;
        }

        @Override
        public Item getDesignerItem() {
            return Blocks.CHEST.asItem();
        }
    });

    public static final RocketPart NO_UPGRADE = Registry.register(Galacticraft.ROCKET_PARTS, new Identifier(Constants.MOD_ID, "default_upgrade"), new RocketPart() {
        @Override
        public RocketPartType getType() {
            return RocketPartType.UPGRADE;
        }

        @Override
        public Block getBlockToRender() {
            return Blocks.AIR;
        }

    });

    public static void register() {
    }

    public static RocketPart getPartForType(RocketPartType type) {
        switch (type) {
            case BODY:
                return DEFAULT_BODY;
            case CONE:
                return DEFAULT_CONE;
            case FIN:
                return DEFAULT_FIN;
            case BOTTOM:
                return DEFAULT_BOTTOM;
            case BOOSTER:
                return NO_BOOSTER;
            case UPGRADE:
                return NO_UPGRADE;
            default:
                throw new IllegalArgumentException("invalid part type");
        }
    }

    public static RocketPart getPartToRenderForType(RocketPartType type) {
        switch (type) {
            case BODY:
                return DEFAULT_BODY;
            case CONE:
                return DEFAULT_CONE;
            case FIN:
                return DEFAULT_FIN;
            case BOTTOM:
                return DEFAULT_BOTTOM;
            case BOOSTER:
                return BOOSTER_TIER_1;
            case UPGRADE:
                return STORAGE_UPGRADE;
            default:
                throw new IllegalArgumentException("invalid part type");
        }
    }
}
