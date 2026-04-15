/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class GCDensityFunctions {
    public static final ResourceKey<DensityFunction> NOODLES = createKey("caves/noodles");

    public static final class Moon {
        public static final ResourceKey<DensityFunction> EROSION = createKey("moon/erosion");
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("moon/final_density");
    }

    public static final class Mars {
        public static final ResourceKey<DensityFunction> TEMPERATURE = createKey("mars/temperature");           // controls placement of polar caps
        public static final ResourceKey<DensityFunction> CONTINENTALNESS = createKey("mars/continentalness");   // controls overall terrain elevation
        public static final ResourceKey<DensityFunction> EROSION = createKey("mars/erosion");                   // controls flatness of terrain
        public static final ResourceKey<DensityFunction> WEIRDNESS = createKey("mars/weirdness");               // controls special biomes
        public static final ResourceKey<DensityFunction> PV = createKey("mars/pv");
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("mars/final_density");
    }

    public static final class Venus {
        // Final Density handles overall terrain shape
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("venus/final_density");
    }

    public static final class Asteroid {
        // Final Density handles overall terrain shape
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("asteroid/final_density");
    }

    private static ResourceKey<DensityFunction> createKey(String id) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Constant.id(id));
    }

    public static void bootstrapRegistries(BootstrapContext<DensityFunction> context) {
        var vanillaRegistry = context.lookup(Registries.DENSITY_FUNCTION);
        var noiseRegistry = context.lookup(Registries.NOISE);
        DensityFunction shiftX = getFunction(vanillaRegistry, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(vanillaRegistry, NoiseRouterData.SHIFT_Z);
        DensityFunction y = getFunction(vanillaRegistry, NoiseRouterData.Y);

//        DensityFunction noodles = registerAndWrap(context, NOODLES, DensityFunctions.rangeChoice(
//                DensityFunctions.interpolated(
//                        DensityFunctions.rangeChoice(
//                                y, -25, 45,
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE), 1, 1),
//                                DensityFunctions.constant(-1)
//                        )
//                ),
//                -1000000, 0, DensityFunctions.constant(64),
//                DensityFunctions.add(
//                        DensityFunctions.interpolated(
//                                DensityFunctions.rangeChoice(
//                                        y, -25, 45,
//                                        DensityFunctions.add(
//                                                DensityFunctions.constant(-0.07500000000000001),
//                                                DensityFunctions.mul(
//                                                        DensityFunctions.constant(-0.025),
//                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE_THICKNESS), 1, 1)
//                                                )
//                                        ),
//                                        DensityFunctions.constant(0)
//                                )
//                        ),
//                        DensityFunctions.mul(
//                                DensityFunctions.constant(1.5),
//                                DensityFunctions.max(
//                                        DensityFunctions.interpolated(
//                                                DensityFunctions.rangeChoice(
//                                                        y, -25, 45,
//                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665),
//                                                        DensityFunctions.zero()
//                                                )
//                                        ).abs(),
//                                        DensityFunctions.interpolated(
//                                                DensityFunctions.rangeChoice(
//                                                        y, -25, 45,
//                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665),
//                                                        DensityFunctions.zero()
//                                                    )
//                                        ).abs()
//                                )
//                        )
//                )
//        ));
//        DensityFunction erosion = registerAndWrap(context, Moon.EROSION, DensityFunctions.flatCache(
//              DensityFunctions.shiftedNoise2d(
//                      shiftX, shiftZ, 1.0, noiseRegistry.getOrThrow(GCNoiseData.EROSION)
//              )
//        ));
//        context.register(Moon.FINAL_DENSITY, DensityFunctions.min(
//                DensityFunctions.add(
//                        DensityFunctions.interpolated(
//                                DensityFunctions.blendDensity(
//                                        DensityFunctions.rangeChoice(
//                                                erosion, 0.05, 2.000000000000001,
//                                                DensityFunctions.yClampedGradient(-64, 190, 1, -1),
//                                                DensityFunctions.yClampedGradient(-64, 200, 1, -1)
//                                        )
//                                )
//                        ),
//                        DensityFunctions.add(
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.BASALT_MARE), 0.00005, 0.0007),
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.BASALT_MARE_HEIGHT), 0, 0)
//                        )
//                ),
//                noodles
//            )
//        );

        MarsTerrainProvider.bootstrapRegistriesMars(context);

        context.register(Venus.FINAL_DENSITY, DensityFunctions.add(
                DensityFunctions.yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));

        context.register(Asteroid.FINAL_DENSITY, DensityFunctions.add(
                DensityFunctions.yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));
    }

    public static DensityFunction registerAndWrap(BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(context.register(key, densityFunction));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }

    public static DensityFunction peaksAndValleys(DensityFunction input) {
        /*
        See NoiseRouterData
         */
        return DensityFunctions.mul(
                DensityFunctions.add(DensityFunctions.add(input.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)),
                DensityFunctions.constant(-3.0)
        );
    }

    public static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> largeCraterSplineBuilder(Holder<DensityFunction> circularDensityFunction) {
        return CubicSpline.builder(new DensityFunctions.Spline.Coordinate(circularDensityFunction))
                .addPoint(0.0F, 0.0F)
                .addPoint(0.05F, 0.05F)
                .addPoint(0.2F, -0.5F)
                .addPoint(0.45F, -0.7F)
                .addPoint(0.8F, -0.75F)
                .addPoint(0.9F, -0.6F)
                .addPoint(1.1F, -0.5F)
                .build();
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> noiseParameters, double scaleX, double scaleY, double scaleZ) {
        return new DifferentScaledNoise(new DensityFunction.NoiseHolder(noiseParameters), scaleX, scaleY, scaleZ);
    }

    public record DifferentScaledNoise(DensityFunction.NoiseHolder noise, double xScale, double yScale, double zScale) implements DensityFunction {
        public static final MapCodec<DifferentScaledNoise> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DifferentScaledNoise::noise),
                                Codec.DOUBLE.fieldOf("x_scale").forGetter(DifferentScaledNoise::xScale),
                                Codec.DOUBLE.fieldOf("y_scale").forGetter(DifferentScaledNoise::yScale),
                                Codec.DOUBLE.fieldOf("z_scale").forGetter(DifferentScaledNoise::zScale)
                        )
                        .apply(instance, DifferentScaledNoise::new)
        );
        public static final KeyDispatchDataCodec<DifferentScaledNoise> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            return this.noise.getValue(context.blockX() * this.xScale, context.blockY() * this.yScale, context.blockZ() * this.zScale);
        }

        @Override
        public void fillArray(double[] densities, DensityFunction.ContextProvider applier) {
            applier.fillAllDirectly(densities, this);
        }

        @Override
        public @NotNull DensityFunction mapAll(DensityFunction.Visitor visitor) {
            return visitor.apply(new DifferentScaledNoise(visitor.visitNoise(this.noise), this.xScale, this.yScale, this.zScale));
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.noise.maxValue();
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    public static ShiftedNoise2dThreshold makeShiftedNoise2dThreshold(    // this exists because I need to pass the NoiseHolder to AccessibleShiftedNoise2d for it to see the NormalNoise
            Holder<NormalNoise.NoiseParameters> sourceNoise, double xzScale, Holder<NormalNoise.NoiseParameters> shiftNoise, double threshold
    ) {
        return new ShiftedNoise2dThreshold(new DensityFunction.NoiseHolder(sourceNoise), xzScale, new DensityFunction.NoiseHolder(shiftNoise), threshold);
    }

    public record ShiftedNoise2dThreshold(NoiseHolder source, double xzScale, NoiseHolder shift, double threshold) implements DensityFunction {
        /*
        See DensityFunctions.ShiftedNoise. This is a wrapper for ShiftedNoise (such as is used to store terrain noises like erosion or weirdness) that allows noise to be calculated
        at an (x,z) location as opposed to through a function context. It prevents DistributedCircularDensityFunction from needing to allocate a new FunctionContext for the center of
        the potential circle every loop.
        Note that while noises are defined with shiftX and shiftZ, these are just various shifts of Noises.Shift, and are taken into account in computeAt
         */
        private static final MapCodec<ShiftedNoise2dThreshold> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                NormalNoise.NoiseParameters.CODEC.fieldOf("source").forGetter(df -> df.source().noiseData()), // uses lambda function to get variable
                                Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise2dThreshold::xzScale),
                                NormalNoise.NoiseParameters.CODEC.fieldOf("shift").forGetter(df -> df.shift().noiseData()),
                                Codec.DOUBLE.fieldOf("value_shift").forGetter(df -> df.threshold)
                        )
                        .apply(instance, GCDensityFunctions::makeShiftedNoise2dThreshold)  // Google recommended storing the noiseData for the codec, so it has to be wrapped for the constructor
        );
        public static final KeyDispatchDataCodec<ShiftedNoise2dThreshold> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        public static final Codec<ShiftedNoise2dThreshold> SUBCLASS_CODEC = DensityFunction.HOLDER_HELPER_CODEC.xmap(
                df -> {
                    if (df instanceof DensityFunctions.HolderHolder holder) {   // Minecraft passes through a HolderHolder, which must be unwrapped first
                        df = holder.function().value();
                    }
                    return (ShiftedNoise2dThreshold) df;},
                df -> df
        );

        private double computeShift(double x, double y, double z) {
            /*
            See DensityFunctions.ShiftNoise. ShiftA and ShiftB both pass compute calls through this.
             */
            return this.shift.getValue(x * 0.25, y * 0.25, z * 0.25) * 4.0;
        }

        public double computeAt(int x, int z) {
            /*
            See DensityFunctions.ShiftedNoise and ShiftA and ShiftB. As defined for shiftedNoise2d, shiftY and yScale are both 0.
             */
            double xSample = x * this.xzScale + computeShift(x, 0, z);
            double zSample = z * this.xzScale + computeShift(z, x, 0);
            return this.source.getValue(xSample, 0, zSample) - this.threshold;
        }

        @Override
        public double compute(FunctionContext context) {
            return computeAt(context.blockX(), context.blockZ());
        }

        @Override
        public void fillArray(double[] densities, ContextProvider applier) {
            applier.fillAllDirectly(densities, this);
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new ShiftedNoise2dThreshold(visitor.visitNoise(this.source), this.xzScale, visitor.visitNoise(this.shift), this.threshold)
            );
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.source.maxValue();
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    public static class DistributedCircularDensityFunction implements DensityFunction {
        /*
        To ensure repeatability, CircularDensityFunction placement is determined by dividing the world into square cells. Each cell has a unique coordinate that
        is used to generate a seed. Placement of the CircularDensityFunction within the cell is randomized based on the seed. The buffer ensures a distance
        between the center and the cell boundary. Final placement is conditional on whether the value of thresholdFunction is positive at that point.
         */
        private static final MapCodec<DistributedCircularDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ShiftedNoise2dThreshold.SUBCLASS_CODEC.fieldOf("threshold_function_1").forGetter(df -> df.thresholdFunction1),
                                ShiftedNoise2dThreshold.SUBCLASS_CODEC.optionalFieldOf("threshold_function_2").forGetter(df -> Optional.ofNullable(df.thresholdFunction2)),
                                Codec.INT.fieldOf("cell_size_exp").forGetter(df -> df.cellSizeExp),
                                Codec.INT.fieldOf("buffer").forGetter(df -> df.buffer),
                                Codec.INT.fieldOf("radius_lower").forGetter(df -> df.radiusLower),
                                Codec.INT.fieldOf("radius_upper").forGetter(df -> df.radiusUpper),
                                Codec.INT.fieldOf("nom_radius").forGetter(df -> df.nomRadius)
                        )   // second thresholdFunction must be optional since it can be null
                        .apply(instance, (tf1, tf2, cse, b, rl, ru, nr) -> new DistributedCircularDensityFunction(tf1, tf2.orElse(null), cse, b, rl, ru, nr))
        );
        public static final KeyDispatchDataCodec<DistributedCircularDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        private final ShiftedNoise2dThreshold thresholdFunction1;
        private final @Nullable GCDensityFunctions.ShiftedNoise2dThreshold thresholdFunction2;
        private final int cellSizeExp;
        private final int buffer;
        private final int radiusLower;
        private final int radiusUpper;
        private final int nomRadius;
        private final float invNomRadiusSq;

        public DistributedCircularDensityFunction(
                ShiftedNoise2dThreshold thresholdFunction1,
                @Nullable ShiftedNoise2dThreshold thresholdFunction2,
                int cellSizeExp, int buffer, int radiusLower, int radiusUpper, int nomRadius
        ) {
            if (cellSizeExp < 0 || buffer < 0 || radiusUpper < 0 || radiusLower < 0 || nomRadius < 0) {
                throw new IllegalArgumentException("Integer inputs must be non-negative.");
            }
            if (buffer >= 1 << cellSizeExp - 1) {
                throw new IllegalArgumentException("Buffer must be smaller than cellSize/2");
            }
            if (radiusUpper > 1 << cellSizeExp) {
                throw new IllegalArgumentException("Upper bound on radius must be no larger than cell size.");
            }
            if (radiusUpper < radiusLower) {
                throw new IllegalArgumentException("Upper bounds cannot be smaller than lower bounds.");
            }
            this.thresholdFunction1 = thresholdFunction1;
            this.thresholdFunction2 = thresholdFunction2;
            this.cellSizeExp = cellSizeExp;
            this.buffer = buffer;
            this.radiusLower = radiusLower;
            this.radiusUpper = radiusUpper;
            this.nomRadius = nomRadius;
            this.invNomRadiusSq = 1F / nomRadius / nomRadius;
        }

        public DistributedCircularDensityFunction(
                ShiftedNoise2dThreshold thresholdFunction1,
                int cellSizeExp, int buffer, int radiusLower, int radiusUpper, int nomRadius
        ) {
            this(thresholdFunction1, null, cellSizeExp, buffer, radiusLower, radiusUpper, nomRadius);
        }

        private static final ThreadLocal<CacheContainer> THREAD_CACHE = ThreadLocal.withInitial(CacheContainer::new);

        private static class CacheContainer {
            public final long[] keys = new long[64];            // 64 cache slots for cell positions
            // circle position and radius is computed per cell
            public final int[] xCenters = new int[64];          // xCenter in each cell
            public final int[] zCenters = new int[64];          // zCenter in each cell
            public final int[] radiiSq = new int[64];           // radiusSq in each cell
            // multiplier for final density to avoid if statement
            public final double[] meetsThresholds = new double[64];     // are threshold conditions satisfied (1.0) or not (0.0)

            public CacheContainer() {
                java.util.Arrays.fill(keys, Long.MIN_VALUE);
            }

            public void cacheValues(int idx, long key, int xCenter, int zCenter, int radiusSq, boolean meetsThreshold) {
                keys[idx] = key;
                xCenters[idx] = xCenter;
                zCenters[idx] = zCenter;
                radiiSq[idx] = radiusSq;
                meetsThresholds[idx] = meetsThreshold ? 1.0 : 0.0;
            }
        }

        private static int getSeedAtPos(int x, int z) {
            return ChunkPos.hash(x, z);
        }

        private static int hash(int x) {
            /*
            See https://github.com/skeeto/hash-prospector
             */
            x ^= x >>> 16;
            x *= 0x7feb352d;
            x ^= x >>> 15;
            x *= 0x846ca68b;
            x ^= x >>> 16;
            return x;
        }

        private static int nextIntInRange(int hash, int min, int max) {
            int range = max - min;
            if (range <= 0) {
                throw new IllegalArgumentException("Max must be greater than min.");
            }
            long unsignedHash = Integer.toUnsignedLong(hash);   // treat this as a fraction from 0 to 2^32
            int offset = (int)((unsignedHash * range) >>> 32);  // multiply fraction by range and divide by 2^32
            return min + offset;
        }

        @Override
        public double compute(FunctionContext context) {
            CacheContainer cache = THREAD_CACHE.get();
            final int x = context.blockX();
            final int z = context.blockZ();
            final int distToCellThreshold = radiusUpper - buffer;
            final int minCellX = (x - distToCellThreshold) >> cellSizeExp;  // Bit shift performs floorDiv, which is desired. The loop statements auto-skip cells if out of range.
            final int maxCellX = (x + distToCellThreshold) >> cellSizeExp;
            final int minCellZ = (z - distToCellThreshold) >> cellSizeExp;
            final int maxCellZ = (z + distToCellThreshold) >> cellSizeExp;
            double maxDensity = 0;
            // Functions could sit on boundary of neighboring cell.
            // Within each cell in 3x3 grid, determine where the density function locations should be and then process contributions.
            for (int currCellX = minCellX; currCellX <= maxCellX; currCellX++) {
                for (int currCellZ = minCellZ; currCellZ <= maxCellZ; currCellZ++) {
                    long key = ((long) currCellX << 32) | (currCellZ & 0xFFFFFFFFL);    // cache key for cell position, want to guarantee uniqueness for cell (use long)
                    int seed = getSeedAtPos(currCellX, currCellZ);                      // each cell has unique seed to determine center placement
                    int idx = seed & 63;                                                // modulo 64 (size of cache) -- note seed is sufficiently scrambled to find in cache
                    if (cache.keys[idx] != key) {                                       // but keep long as key to verify two cells are really different
                        int xCenter = (currCellX << cellSizeExp) + nextIntInRange(hash(seed ^ 0x12345), buffer, (1 << cellSizeExp) - buffer);
                        int zCenter = (currCellZ << cellSizeExp) + nextIntInRange(hash(seed ^ 0x6789A), buffer, (1 << cellSizeExp) - buffer);
                        int radius = radiusLower == radiusUpper ? radiusLower : nextIntInRange(hash(seed ^ 0xEDCBA), radiusLower, radiusUpper);
                        int radiusSq = radius * radius;
                        boolean meetsThreshold = (thresholdFunction1.computeAt(xCenter, zCenter) > 0) &&    // look at thresholdFunction at proposed placement location
                                (thresholdFunction2 == null || thresholdFunction2.computeAt(xCenter, zCenter) > 0);
                        cache.cacheValues(idx, key, xCenter, zCenter, radiusSq, meetsThreshold);
                    }
                    int radiusSq = cache.radiiSq[idx];
                    int dx = x - cache.xCenters[idx];
                    int dz = z - cache.zCenters[idx];
                    int distFromCenterSq = dx * dx + dz * dz;
                    maxDensity = Math.max(maxDensity, Math.max(radiusSq - distFromCenterSq, 0) * invNomRadiusSq * cache.meetsThresholds[idx]);    // has roughly 1 - r^2 profile; 0 if below threshold or if dist >= radius
                }
            }
            return maxDensity;
        }

        @Override
        public void fillArray(double[] densities, ContextProvider applier) {
            applier.fillAllDirectly(densities, this);
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new DistributedCircularDensityFunction(
                            (ShiftedNoise2dThreshold) thresholdFunction1.mapAll(visitor),
                            (ShiftedNoise2dThreshold) (thresholdFunction2 != null ? thresholdFunction2.mapAll(visitor) : null),
                            cellSizeExp, buffer, radiusLower, radiusUpper, nomRadius)
            );
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return (double) radiusUpper * radiusUpper * invNomRadiusSq;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }
}
