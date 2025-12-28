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
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
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

    private static DensityFunction.FunctionContext centerFunctionContextAt(DensityFunction.FunctionContext context, int xCenter, int yCenter, int zCenter) {
        return moveFunctionContextTo(context, context.blockX() - xCenter, context.blockY() - yCenter, context.blockZ() - zCenter);
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
            float currR = Mth.sqrt(x*x + z*z);
            float noiseHeight = (radius - currR) / nominalRadius;
            return Math.max(noiseHeight, 0.0F);
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
            return (double) nominalRadius / radius;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }
}
