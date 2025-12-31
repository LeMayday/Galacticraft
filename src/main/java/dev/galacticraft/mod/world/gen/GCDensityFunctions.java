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
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

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

        bootstrapRegistriesMars(context);

        context.register(Venus.FINAL_DENSITY, DensityFunctions.add(
                DensityFunctions.yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));

        context.register(Asteroid.FINAL_DENSITY, DensityFunctions.add(
                DensityFunctions.yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));
    }

    private static void bootstrapRegistriesMars(BootstrapContext<DensityFunction> context) {
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        var noiseRegistry = context.lookup(Registries.NOISE);
        DensityFunction shiftX = getFunction(densityLookup, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(densityLookup, NoiseRouterData.SHIFT_Z);
        DensityFunction y = getFunction(densityLookup, NoiseRouterData.Y);

        // redefine overworld noises to have 4x frequency

        DensityFunction temperature = registerAndWrap(context, Mars.TEMPERATURE,
                DensityFunctions.min(DensityFunctions.zero(), DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(
                                shiftX, shiftZ, 1.0, noiseRegistry.getOrThrow(Noises.TEMPERATURE)
                        ))
                )
        ); // cap temp at 0

        DensityFunction continentalness = registerAndWrap(context, Mars.CONTINENTALNESS, DensityFunctions.flatCache(
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 1.0, noiseRegistry.getOrThrow(Noises.CONTINENTALNESS)
                )
        ));

        DensityFunction erosion = registerAndWrap(context, Mars.EROSION, DensityFunctions.flatCache(
              DensityFunctions.shiftedNoise2d(
                      shiftX, shiftZ, 1.0, noiseRegistry.getOrThrow(Noises.EROSION)
              )
        ));

        DensityFunction weirdness = registerAndWrap(context, Mars.WEIRDNESS, DensityFunctions.flatCache(
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 1.0, noiseRegistry.getOrThrow(Noises.RIDGE)
                )
        ));

        context.register(Mars.FINAL_DENSITY, DensityFunctions.add(
                DensityFunctions.yClampedGradient(32, 160, 1, -1),
                DensityFunctions.blendDensity(continentalness)
        ));


//        context.register(Mars.FINAL_DENSITY, DensityFunctions.add(
//                DensityFunctions.yClampedGradient(32, 160, 1, -1),
//                DensityFunctions.blendDensity(
//                        DensityFunctions.rangeChoice(GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.CONTINENTS), 0, 1,
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.MARS_HIGHLAND), 1, 1),
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.MARS_LOWLAND), 1, 1)
//                        )
//                )
////                DensityFunctions.add(
////                        DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.MARS_HIGHLAND), 1, 1),
////                        DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.MARS_LOWLAND), 1, 1)
////                )
//        ));
    }

    private static DensityFunction registerAndWrap(BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(context.register(key, densityFunction));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }


    private static DensityFunction.FunctionContext moveFunctionContextTo(DensityFunction.FunctionContext context, int x, int y, int z) {
        return new DensityFunction.FunctionContext() {
            @Override
            public int blockX() {
                return x;
            }

            @Override
            public int blockY() {
                return y;
            }

            @Override
            public int blockZ() {
                return z;
            }

            @Override
            public @NotNull Blender getBlender() {
                return context.getBlender();
            }
        };
    }

    public record PlacedDensityFunction(DensityFunction source, int xCenter, int zCenter) implements DensityFunction {

        private static final MapCodec<PlacedDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(PlacedDensityFunction::source),
                                Codec.INT.fieldOf("x_center").forGetter(PlacedDensityFunction::xCenter),
                                Codec.INT.fieldOf("z_center").forGetter(PlacedDensityFunction::zCenter)
                        )
                        .apply(instance, PlacedDensityFunction::new)
        );
        public static final KeyDispatchDataCodec<PlacedDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        private static DensityFunction.FunctionContext centerFunctionContextAt(DensityFunction.FunctionContext context, int xCenter, int yCenter, int zCenter) {
            return moveFunctionContextTo(context, context.blockX() - xCenter, context.blockY() - yCenter, context.blockZ() - zCenter);
        }

        @Override
        public double compute(FunctionContext context) {
            return source.compute(centerFunctionContextAt(context, xCenter, 0, zCenter));
        }

        @Override
        public void fillArray(double[] densities, ContextProvider applier) {
            for (int i = 0; i < densities.length; i++) {
                densities[i] = this.compute(applier.forIndex(i));
            }
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new GCDensityFunctions.PlacedDensityFunction(this.source.mapAll(visitor), this.xCenter, this.zCenter)
            );
        }

        @Override
        public double minValue() {
            return this.source.minValue();
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

    public record CircularDensityFunction(int radius, int nominalRadius) implements DensityFunction.SimpleFunction {
        /*
        DensityFunction that is a perfect circle with noise starting at 0 at the edge and reaching 1 at nominalRadius
        Nominal radius can be used to set where value exceeds 1 to add things like central peaks for craters (perhaps 150 is a good value?)
         */
        private static final MapCodec<CircularDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.INT.fieldOf("radius").forGetter(CircularDensityFunction::radius),
                                Codec.INT.fieldOf("nominal_radius").forGetter(CircularDensityFunction::nominalRadius)
                        )
                        .apply(instance, CircularDensityFunction::new)
        );
        public static final KeyDispatchDataCodec<CircularDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        public static double computeDensity(FunctionContext context, int radius, int nominalRadius) {
            if (radius < 0 || nominalRadius < 0) {
                throw new IllegalArgumentException("Radius inputs must be non-negative.");
            }
            int x = context.blockX();
            int z = context.blockZ();
            if (x*x + z*z >= radius*radius) {
                return 0.0;
            }
            float currR = Mth.sqrt(x*x + z*z);
            return (radius - currR) / nominalRadius;
        }

        @Override
        public double compute(FunctionContext context) {
            return computeDensity(context, this.radius, this.nominalRadius);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return (double) radius / nominalRadius;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    public static AccessibleShiftedNoise2d makeAccessibleShiftedNoise2d(    // this exists because I need to pass the NoiseHolder to AccessibleShiftedNoise2d for it to see the NormalNoise
            Holder<NormalNoise.NoiseParameters> sourceNoise, double xzScale, Holder<NormalNoise.NoiseParameters> shiftNoise
    ) {
        return new AccessibleShiftedNoise2d(new DensityFunction.NoiseHolder(sourceNoise), xzScale, new DensityFunction.NoiseHolder(shiftNoise));
    }

    public record AccessibleShiftedNoise2d(DensityFunction.NoiseHolder source, double xzScale, DensityFunction.NoiseHolder shift) implements DensityFunction {
        /*
        See DensityFunctions.ShiftedNoise
         */
        private static final MapCodec<AccessibleShiftedNoise2d> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                NormalNoise.NoiseParameters.CODEC.fieldOf("source").forGetter(ths -> ths.source().noiseData()), // uses lambda function to get variable
                                Codec.DOUBLE.fieldOf("xz_scale").forGetter(AccessibleShiftedNoise2d::xzScale),
                                NormalNoise.NoiseParameters.CODEC.fieldOf("shift").forGetter(ths -> ths.shift().noiseData())
                        )
                        .apply(instance, GCDensityFunctions::makeAccessibleShiftedNoise2d)  // Google recommended storing the noiseData for the codec, so it has to be wrapped for the constructor
        );
        public static final KeyDispatchDataCodec<AccessibleShiftedNoise2d> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        public static final Codec<AccessibleShiftedNoise2d> SUBCLASS_CODEC = DensityFunction.HOLDER_HELPER_CODEC.xmap(
                df -> {
                    if (df instanceof DensityFunctions.HolderHolder holder) {   // Minecraft passes through a HolderHolder, which must be unwrapped first
                        df = holder.function().value();
                    }
                    return (AccessibleShiftedNoise2d) df;},
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
            return this.source.getValue(xSample, 0, zSample);
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
                    new AccessibleShiftedNoise2d(visitor.visitNoise(this.source), this.xzScale, visitor.visitNoise(this.shift))
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
        is used to generate a seed for a RandomSource. Placement of the CircularDensityFunction within the cell is randomized based on the seed. The buffer ensures
        a distance between the center and the cell boundary. Final placement is conditional on whether the value of thresholdFunction is positive at that point.
         */
        private static final MapCodec<DistributedCircularDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                AccessibleShiftedNoise2d.SUBCLASS_CODEC.fieldOf("threshold_function").forGetter(df -> df.thresholdFunction),
                                Codec.DOUBLE.fieldOf("threshold").forGetter(df -> df.threshold),
                                Codec.INT.fieldOf("cell_size_exp").forGetter(df -> df.cellSizeExp),
                                Codec.INT.fieldOf("buffer").forGetter(df -> df.buffer),
                                Codec.INT.fieldOf("radius_lower").forGetter(df -> df.radiusLower),
                                Codec.INT.fieldOf("radius_upper").forGetter(df -> df.radiusUpper),
                                Codec.INT.fieldOf("nom_radius_lower").forGetter(df -> df.nomRadius)
                        )
                        .apply(instance, DistributedCircularDensityFunction::new)
        );
        public static final KeyDispatchDataCodec<DistributedCircularDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        private final AccessibleShiftedNoise2d thresholdFunction;
        private final double threshold;
        private final int cellSizeExp;
        private final int buffer;
        private final int radiusLower;
        private final int radiusUpper;
        private final int nomRadius;
        private final float invNomRadius;

        public DistributedCircularDensityFunction(
                AccessibleShiftedNoise2d thresholdFunction, double threshold, int cellSizeExp, int buffer, int radiusLower, int radiusUpper, int nomRadius
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
            this.thresholdFunction = thresholdFunction;
            this.threshold = threshold;
            this.cellSizeExp = cellSizeExp;
            this.buffer = buffer;
            this.radiusLower = radiusLower;
            this.radiusUpper = radiusUpper;
            this.nomRadius = nomRadius;
            this.invNomRadius = 1F / nomRadius;
        }

        private static long getSeedAtPos(int x, int z) {
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
            final int x = context.blockX();
            final int z = context.blockZ();
            final int distToCellThreshold = radiusUpper - buffer;
            final int minCellX = (x - distToCellThreshold) >> cellSizeExp;  // Bit shift performs floorDiv, which is desired. The loop statements auto-skip cells if out of range.
            final int maxCellX = (x + distToCellThreshold) >> cellSizeExp;
            final int minCellZ = (z - distToCellThreshold) >> cellSizeExp;
            final int maxCellZ = (z + distToCellThreshold) >> cellSizeExp;
            double result = 0;
            // Within each cell in 3x3 grid, determine where the density function locations should be and then process contributions.
            int seed, xCenter, dx, zCenter, dz, distFromCenterSq, radius;
            for (int currCellX = minCellX; currCellX <= maxCellX; currCellX++) {
                for (int currCellZ = minCellZ; currCellZ <= maxCellZ; currCellZ++) {
                    seed = (int) getSeedAtPos(currCellX, currCellZ);
                    xCenter = (currCellX << cellSizeExp) + nextIntInRange(hash(seed ^ 0x12345), buffer, (1 << cellSizeExp) - buffer);
                    dx = x - xCenter;
                    zCenter = (currCellZ << cellSizeExp) + nextIntInRange(hash(seed ^ 0x6789A), buffer, (1 << cellSizeExp) - buffer);
                    dz = z - zCenter;
                    distFromCenterSq = dx * dx + dz * dz;
                    radius = radiusLower == radiusUpper ? radiusLower : nextIntInRange(hash(seed ^ 0xEDCBA), radiusLower, radiusUpper);
                    if (distFromCenterSq >= radius * radius) continue;  // calculate radius for this cell, short circuit if current pos is farther
                    if (this.thresholdFunction.computeAt(xCenter, zCenter) > threshold) {   // look at thresholdFunction at proposed placement location
                        result = Math.max(result, (radius - Mth.sqrt(distFromCenterSq)) * invNomRadius);
                    }
                }
            }
            return result;
        }

        @Override
        public void fillArray(double[] densities, ContextProvider applier) {
            applier.fillAllDirectly(densities, this);
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new DistributedCircularDensityFunction(
                            (AccessibleShiftedNoise2d)thresholdFunction.mapAll(visitor),
                            threshold, cellSizeExp, buffer, radiusLower, radiusUpper, nomRadius)
            );
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return (double) radiusUpper * invNomRadius;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }
}
