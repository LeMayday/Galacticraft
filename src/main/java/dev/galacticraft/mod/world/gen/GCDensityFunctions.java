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

    public static DensityFunction clamp(DensityFunction function, double min, double max) {
        return DensityFunctions.min(
                DensityFunctions.max(function, DensityFunctions.constant(min)),
                DensityFunctions.constant(max)
        );
    }

    public static DensityFunction mapFromToNormalized(DensityFunction function, double fromMin, double fromMax) {
        DensityFunction clampedShifted = DensityFunctions.add(GCDensityFunctions.clamp(function, fromMin, fromMax), DensityFunctions.constant(-fromMin));
        return DensityFunctions.mul(clampedShifted, DensityFunctions.constant(1/(fromMax - fromMin)));
    }

    public static DensityFunction mapFromToRange(DensityFunction function, double fromMin, double fromMax, double toMin, double toMax) {
        return DensityFunctions.lerp(mapFromToNormalized(function, fromMin, fromMax), DensityFunctions.constant(toMin), DensityFunctions.constant(toMax));
    }

    public static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> largeCraterSplineBuilder(Holder<DensityFunction> circularDensityFunction) {
        // see https://www.desmos.com/calculator/dxkoluhsvo for how I got these numbers
        // works best with r/R_nom in [0.95, 1.03]
        return CubicSpline.builder(new DensityFunctions.Spline.Coordinate(circularDensityFunction))
                .addPoint(0.00F, 0.00F)
                .addPoint(0.10F, 0.03F)
                .addPoint(0.15F, 0.02F)
                .addPoint(0.40F, -0.25F)
                .addPoint(0.60F, -0.45F)
                .addPoint(0.80F, -0.54F)
                .addPoint(1.00F, -0.57F)
                .addPoint(1.02F, -0.53F)
                .addPoint(1.04F, -0.46F)
                .addPoint(1.06F, -0.33F, 1.0F)
                .addPoint(1.10F, -0.25F)
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

    public static class DuneDensityFunction implements DensityFunction {
        /*
        Dune ridges form where there is a prevailing wind direction, or in this case, along contours of constant DuneWind noise.
        duneNoise1 is preferred, and duneNoise2 is selected when duneNoise1 is near an extremum. This also enables star dunes (formed by different prevailing wind directions).
         */
        private static final MapCodec<DuneDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                DuneWind.SUBCLASS_CODEC.fieldOf("dune_noise1").forGetter(df -> df.duneNoise1),
                                DuneWind.SUBCLASS_CODEC.fieldOf("dune_noise2").forGetter(df -> df.duneNoise2),
                                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("amplitude_control_df").forGetter(df -> df.amplitudeControlDF)
                        )
                        .apply(instance, DuneDensityFunction::new)  // Google recommended storing the noiseData for the codec, so it has to be wrapped for the constructor
        );
        public static final KeyDispatchDataCodec<DuneDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        private final DuneWind duneNoise1;
        private final DuneWind duneNoise2;
        private final DensityFunction amplitudeControlDF;
        private final double small = 0.001;     // prevent crease at 0 contour

        public DuneDensityFunction(
                DuneWind duneNoise1, DuneWind duneNoise2,   // two possible wind directions (these also encode scale)
                DensityFunction amplitudeControlDF          // amplitude controls dune height
        ) {
            this.duneNoise1 = duneNoise1;
            this.duneNoise2 = duneNoise2;
            this.amplitudeControlDF = amplitudeControlDF;
        }

        private double duneCurve(double s) {
            /*
            Dune profile on [0, 1] in terms of direction-wise coordinate "s"
             */
            if (s < 0 || s > 1) throw new IllegalArgumentException("s must be in [0, 1].");
            double l = 2 * s - 1;   // linear component, normalized to [0, 1] on [0.5, 1]
            if (s > 0.5) return l;  // softer curve in windward direction (increasing "s")
            return l * l;           // steeper curve on leeward side (quartic would be steeper)
        }

        private double amplitude(FunctionContext context) {
            return this.amplitudeControlDF.compute(context);
        }

        @Override
        public double compute(FunctionContext context) {
            double n1 = duneNoise1.compute(context);
            double n2 = duneNoise2.compute(context);
            double dunes1 = duneCurve(n1 - Mth.floor(n1)) * 0.5;    // 0.5 is contribution of dune1
            double dunes2 = duneCurve(n2 - Mth.floor(n2)) * 0.5;    // 1 - 0.5 is contribution of dune2
            return this.amplitude(context) * (dunes1 + dunes2) + small;
        }

        @Override
        public void fillArray(double[] densities, ContextProvider applier) {
            applier.fillAllDirectly(densities, this);
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new DuneDensityFunction((DuneWind) duneNoise1.mapAll(visitor), (DuneWind) duneNoise2.mapAll(visitor), amplitudeControlDF.mapAll(visitor))
            );
        }

        @Override
        public double minValue() {
            return small;
        }

        @Override
        public double maxValue() {
            return this.amplitudeControlDF.maxValue();
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    public static abstract class ShiftedNoise2dWrapper implements DensityFunction {
        /*
        DensityFunction equivalent to shiftedNoise2d, but wraps noise to allow sampling at specific coordinates or custom shifting instead of relying on FunctionContext.
        Needed for DistributedCircularDensityFunction to sample threshold noise at circle centers and for ShiftedDuneNoise to offset 2nd wind direction noise.
         */
        public static <T extends ShiftedNoise2dWrapper> Codec<T> createSubclassCodec(Class<T> subclass) {
            return DensityFunction.HOLDER_HELPER_CODEC.xmap(
                    df -> {     // maps base class (e.g. DensityFunction) to subclass
                        if (df instanceof DensityFunctions.HolderHolder holder) {   // Minecraft passes through a HolderHolder, which must be unwrapped first
                            df = holder.function().value();
                        }
                        return subclass.cast(df);},
                    df -> df    // maps subclass to base class
            );
        }

        protected final NoiseHolder source;
        protected final double xzScale;
        protected final NoiseHolder shift;

        protected ShiftedNoise2dWrapper(NoiseHolder source, double xzScale, NoiseHolder shift) {
            this.source = source;
            this.xzScale = xzScale;
            this.shift = shift;
        }

        protected double computeShift(double x, double y, double z) {
            /*
            See DensityFunctions.ShiftNoise. ShiftA and ShiftB both pass compute calls through this.
             */
            return this.shift.getValue(x * 0.25, y * 0.25, z * 0.25) * 4.0;
        }

        public double computeAt(int x, int z) {
            /*
            See DensityFunctions.ShiftedNoise and ShiftA and ShiftB. As defined for shiftedNoise2d, shiftY and yScale are both 0.
             */
            double xSample = x * this.xzScale + this.computeShift(x, 0, z);
            double zSample = z * this.xzScale + this.computeShift(z, x, 0); // this is correct
            return this.source.getValue(xSample, 0, zSample);
        }

        @Override
        public double compute(FunctionContext context) {
            return this.computeAt(context.blockX(), context.blockZ());
        }

        @Override
        public void fillArray(double[] densities, ContextProvider applier) {
            applier.fillAllDirectly(densities, this);
        }

        @Override
        public double minValue() {
            return -this.maxValue();
        }

        @Override
        public double maxValue() {
            return this.source.maxValue();
        }

    }

    public static class DuneWind extends ShiftedNoise2dWrapper {
        /*
        Implementation of ShiftedNoise2dWrapper for ShiftedDuneNoise. Wind direction is essentially the gradient of this noise function (perp to contours).
        Dune ridges form where there is a prevailing wind direction, or in this case, along contours of constant noise.
         */
        private static final MapCodec<DuneWind> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                NormalNoise.NoiseParameters.CODEC.fieldOf("source").forGetter(df -> df.source.noiseData()),
                                NormalNoise.NoiseParameters.CODEC.fieldOf("shift").forGetter(df -> df.shift.noiseData()),
                                Codec.INT.fieldOf("scale").forGetter(df -> df.scale),
                                Codec.INT.fieldOf("offset").forGetter(df -> df.offset)
                        )
                        .apply(instance, DuneWind::new)
        );
        public static final KeyDispatchDataCodec<DuneWind> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        public static final Codec<DuneWind> SUBCLASS_CODEC = createSubclassCodec(DuneWind.class);
        private final int scale;
        private final int offset;
        private final double amplitude;

        public DuneWind(Holder<NormalNoise.NoiseParameters> sourceNoise, Holder<NormalNoise.NoiseParameters> shiftNoise, int scale, int offset) {
            this(new NoiseHolder(sourceNoise), new NoiseHolder(shiftNoise), scale, offset);
        }

        private DuneWind(NoiseHolder source, NoiseHolder shift, int scale, int offset) {
            /*
            The pattern in the DuneNoise class relies on the function being monotonic, at least on the domain where it is sampled.
            Since this is the underlying Perlin noise, there will be extrema where pattern will break down. Let T be 2^octave (period) of Perlin noise,
            S be scale (blocks), N be # of dune ridges you'd want to see contiguously (more than a few, not too many), and P be some fuzziness factor (% of amplitude, as fraction of 1)
            that represents what fraction of the amplitude to capture to avoid extrema. Then T should be greater than PI * S / (2 * P) * ceil(N / 2).
            This is not enforced explicitly. See https://adrianb.io/2014/08/09/perlinnoise.html for how to calculate T
             */
            super(source, 1.0, shift);
            this.scale = scale;                                                 // scale is roughly the spacing between dune ridges (in blocks)
            this.offset = offset;                                               // fixed value to offset the (x,z) position of noise
            int octave = 10;                                                    // this MUST match octave in GCNoiseData for underlying noise (cannot grab noiseData().value() during initialization)
            this.amplitude = (2 << octave) / (Mth.PI * scale);                  // rescales noise to ensure approximately 1 ridge per scale (slope 1/scale)
        }

        @Override
        protected double computeShift(double x, double y, double z) {
            /*
            See DensityFunctions.ShiftNoise. ShiftA and ShiftB both pass compute calls through this.
            Redefine for dunes. Behaves like frequency and amplitude.
             */
            return this.shift.getValue(x * 0.125, y * 0.125, z * 0.125) * 16.0;
        }

        @Override
        public double computeAt(int x, int z) {
            return super.computeAt(x - offset, z - offset) * amplitude;
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new DuneWind(visitor.visitNoise(this.source), visitor.visitNoise(this.shift), this.scale, this.offset)
            );
        }

        @Override
        public double maxValue() {
            return super.maxValue() * this.amplitude;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

    }

    public static class DCDFThreshold extends ShiftedNoise2dWrapper {
        /*
        Implementation of ShiftedNoise2dWrapper for DistributedCircularDensityFunction
         */
        private static final MapCodec<DCDFThreshold> DATA_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                NormalNoise.NoiseParameters.CODEC.fieldOf("source").forGetter(df -> df.source.noiseData()),
                                Codec.DOUBLE.fieldOf("xz_scale").forGetter(df -> df.xzScale),
                                NormalNoise.NoiseParameters.CODEC.fieldOf("shift").forGetter(df -> df.shift.noiseData()),
                                Codec.DOUBLE.fieldOf("threshold").forGetter(df -> df.threshold)
                        )
                        .apply(instance, DCDFThreshold::new)
        );
        public static final KeyDispatchDataCodec<DCDFThreshold> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        public static final Codec<DCDFThreshold> SUBCLASS_CODEC = createSubclassCodec(DCDFThreshold.class);
        private final double threshold;

        public DCDFThreshold(Holder<NormalNoise.NoiseParameters> sourceNoise, double xzScale, Holder<NormalNoise.NoiseParameters> shiftNoise, double threshold) {
            this(new NoiseHolder(sourceNoise), xzScale, new NoiseHolder(shiftNoise), threshold);
        }

        private DCDFThreshold(NoiseHolder source, double xzScale, NoiseHolder shift, double threshold) {
            super(source, xzScale, shift);
            this.threshold = threshold;
        }

        @Override
        public double computeAt(int x, int z) {
            return super.computeAt(x, z) - this.threshold;
        }

        @Override
        public @NotNull DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new DCDFThreshold(visitor.visitNoise(this.source), this.xzScale, visitor.visitNoise(this.shift), this.threshold)
            );
        }

        @Override
        public double maxValue() {
            return super.maxValue() - this.threshold;
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
                                DCDFThreshold.SUBCLASS_CODEC.fieldOf("threshold_function_1").forGetter(df -> df.thresholdFunction1),
                                DCDFThreshold.SUBCLASS_CODEC.optionalFieldOf("threshold_function_2").forGetter(df -> Optional.ofNullable(df.thresholdFunction2)),
                                Codec.INT.fieldOf("cell_size_exp").forGetter(df -> df.cellSizeExp),
                                Codec.INT.fieldOf("buffer").forGetter(df -> df.buffer),
                                Codec.FLOAT.fieldOf("radius_lower").forGetter(df -> df.rFracLower),
                                Codec.FLOAT.fieldOf("radius_upper").forGetter(df -> df.rFracUpper),
                                Codec.INT.fieldOf("nom_radius_lower").forGetter(df -> df.nomRadiusLower),
                                Codec.INT.fieldOf("nom_radius_upper").forGetter(df -> df.nomRadiusUpper)
                        )   // second thresholdFunction must be optional since it can be null
                        .apply(instance, (tf1, tf2, cse, b, rfl, rfu, nrl, nru) -> new DistributedCircularDensityFunction(tf1, tf2.orElse(null), cse, b, rfl, rfu, nrl, nru))
        );
        public static final KeyDispatchDataCodec<DistributedCircularDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
        private final DCDFThreshold thresholdFunction1;
        private final @Nullable DCDFThreshold thresholdFunction2;
        private final int cellSizeExp;
        private final int buffer;
        private final float rFracLower;
        private final float rFracUpper;
        private final int nomRadiusLower;
        private final int nomRadiusUpper;
        private final float invNomRadiusUpper;
        private final float invNomRadiusLower;
        private final int distToCellThreshold;

        public DistributedCircularDensityFunction(
                DCDFThreshold thresholdFunction1,
                @Nullable DCDFThreshold thresholdFunction2,
                int cellSizeExp, int buffer, float rFracLower, float rFracUpper, int nomRadiusLower, int nomRadiusUpper
        ) {
            if (cellSizeExp < 0 || buffer < 0 || rFracUpper < 0 || rFracLower < 0 || nomRadiusLower < 0 || nomRadiusUpper < 0) {
                throw new IllegalArgumentException("Inputs must be non-negative.");
            }
            if (buffer >= 1 << (cellSizeExp - 1)) {
                throw new IllegalArgumentException("Buffer must be smaller than cellSize/2");
            }
            if (rFracUpper * nomRadiusUpper > 1 << cellSizeExp) {
                throw new IllegalArgumentException("Upper bound on radius must be no larger than cell size.");
            }
            if (rFracUpper < rFracLower || nomRadiusUpper < nomRadiusLower) {
                throw new IllegalArgumentException("Upper bounds cannot be smaller than lower bounds.");
            }
            this.thresholdFunction1 = thresholdFunction1;
            this.thresholdFunction2 = thresholdFunction2;
            this.cellSizeExp = cellSizeExp;
            this.buffer = buffer;
            this.rFracLower = rFracLower;
            this.rFracUpper = rFracUpper;
            this.nomRadiusLower = nomRadiusLower;   this.invNomRadiusUpper = 1F / nomRadiusLower;
            this.nomRadiusUpper = nomRadiusUpper;   this.invNomRadiusLower = 1F / nomRadiusUpper;
            // largest possible extent a circle in a neighboring cell could protrude into this one -- used for short-circuiting
            this.distToCellThreshold = (int)Math.ceil(rFracUpper * nomRadiusUpper) - buffer;
        }

        public DistributedCircularDensityFunction(
                DCDFThreshold thresholdFunction1,
                int cellSizeExp, int buffer, float rFracLower, float rFracUpper, int nomRadiusLower, int nomRadiusUpper
        ) {
            this(thresholdFunction1, null, cellSizeExp, buffer, rFracLower, rFracUpper, nomRadiusLower, nomRadiusUpper);
        }

        private static final ThreadLocal<CacheContainer> THREAD_CACHE = ThreadLocal.withInitial(CacheContainer::new);

        private static class CacheContainer {
            public final long[] keys = new long[64];            // 64 cache slots for cell positions
            // circle position and radius is computed per cell
            public final int[] xCenters = new int[64];          // xCenter in each cell
            public final int[] zCenters = new int[64];          // zCenter in each cell
            public final float[] rFracsSq = new float[64];      // (r / Rnom)^2 in each cell
            public final float[] invNomRadiiSq = new float[64]; // invNomRadiusSq in each cell
            // multiplier for final density to avoid if statement
            public final float[] meetsThresholds = new float[64];   // are threshold conditions satisfied (1.0) or not (0.0)

            public CacheContainer() {
                java.util.Arrays.fill(keys, Long.MIN_VALUE);
            }

            public void cacheValues(int idx, long key, int xCenter, int zCenter, float rFracSq, float invNomRadiusSq, boolean meetsThreshold) {
                keys[idx] = key;
                xCenters[idx] = xCenter;
                zCenters[idx] = zCenter;
                rFracsSq[idx] = rFracSq;
                invNomRadiiSq[idx] = invNomRadiusSq;
                meetsThresholds[idx] = meetsThreshold ? 1.0F : 0.0F;
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
            if (max < min) throw new IllegalArgumentException("Max must be greater than min.");
            int range = max - min;
            long unsignedHash = Integer.toUnsignedLong(hash);   // treat this as a fraction from 0 to 2^32
            int offset = (int)((unsignedHash * range) >>> 32);  // multiply fraction by range and divide by 2^32
            return min + offset;
        }

        private static float nextFloatInRange(int hash, float min, float max) {
            // https://en.wikipedia.org/wiki/Single-precision_floating-point_format
            int floatAsBits = 0x3F800000 | (hash & 0x7FFFFF);           // take first 23 bits of hash and set exponent to 127, which is 0
            float randFloat = Float.intBitsToFloat(floatAsBits) - 1.0F; // random float in [0,1)
            return min + randFloat * (max - min);
        }

        @Override
        public double compute(FunctionContext context) {
            CacheContainer cache = THREAD_CACHE.get();
            final int x = context.blockX();
            final int z = context.blockZ();
            final int minCellX = (x - distToCellThreshold) >> cellSizeExp;  // Bit shift performs floorDiv, which is desired. The loop statements auto-skip cells if out of range.
            final int maxCellX = (x + distToCellThreshold) >> cellSizeExp;
            final int minCellZ = (z - distToCellThreshold) >> cellSizeExp;
            final int maxCellZ = (z + distToCellThreshold) >> cellSizeExp;
            float maxDensity = 0.0F;
            // Functions could sit on boundary of neighboring cell.
            // Within each cell in 3x3 grid, determine where the density function locations should be and then process contributions.
            for (int currCellX = minCellX; currCellX <= maxCellX; currCellX++) {
                for (int currCellZ = minCellZ; currCellZ <= maxCellZ; currCellZ++) {
                    long key = ChunkPos.asLong(currCellX, currCellZ);                   // cache key for cell position, want to guarantee uniqueness for cell (use long)
                    int seed = getSeedAtPos(currCellX, currCellZ);                      // each cell has unique seed to determine center placement
                    int idx = hash(seed) & 63;                                          // modulo 64 (size of cache) -- seed must be sufficiently scrambled
                    if (cache.keys[idx] != key) {                                       // but keep long as key to verify two cells are really different
                        int xCenter = (currCellX << cellSizeExp) + nextIntInRange(hash(seed ^ 0x12345), buffer, (1 << cellSizeExp) - buffer);
                        int zCenter = (currCellZ << cellSizeExp) + nextIntInRange(hash(seed ^ 0x6789A), buffer, (1 << cellSizeExp) - buffer);
                        float rFrac = nextFloatInRange(hash(seed ^ 0xEDCBA), rFracLower, rFracUpper);
                        float invNomRadius = nextFloatInRange(hash(seed ^ 0x42069), invNomRadiusLower, invNomRadiusUpper);    // this is technically not uniform in nomRadius and biases toward larger nomRadii
                        boolean meetsThreshold = (thresholdFunction1.computeAt(xCenter, zCenter) > 0) &&    // look at thresholdFunction at proposed placement location
                                (thresholdFunction2 == null || thresholdFunction2.computeAt(xCenter, zCenter) > 0);
                        cache.cacheValues(idx, key, xCenter, zCenter, rFrac * rFrac, invNomRadius * invNomRadius, meetsThreshold);
                    }
                    float rFracSq = cache.rFracsSq[idx];
                    float invNomRadiusSq = cache.invNomRadiiSq[idx];
                    int dx = x - cache.xCenters[idx];
                    int dz = z - cache.zCenters[idx];
                    int distFromCenterSq = dx * dx + dz * dz;
                    // (r^2 - x^2)/R_nom^2 => rFrac^2 - x^2/R_nom^2; has roughly 1 - r^2 profile; 0 if below threshold or if dist >= radius
                    maxDensity = Math.max(maxDensity, (rFracSq - distFromCenterSq * invNomRadiusSq) * cache.meetsThresholds[idx]);
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
                            (DCDFThreshold) thresholdFunction1.mapAll(visitor),
                            (DCDFThreshold) (thresholdFunction2 != null ? thresholdFunction2.mapAll(visitor) : null),
                            cellSizeExp, buffer, rFracLower, rFracUpper, nomRadiusLower, nomRadiusUpper)
            );
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return (double) rFracUpper * rFracUpper;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }
}
