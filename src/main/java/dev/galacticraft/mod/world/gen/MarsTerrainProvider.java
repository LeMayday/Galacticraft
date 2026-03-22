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

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
// See net.minecraft.data.worldgen.TerrainProvider;

public class MarsTerrainProvider {

    public static void bootstrapRegistriesMars(BootstrapContext<DensityFunction> context) {
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        var noiseRegistry = context.lookup(Registries.NOISE);
        DensityFunction shiftX = GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.SHIFT_Z);
        DensityFunction y = GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.Y);

        // redefine overworld noises to have desired frequencies (overworld xzScale is 0.25)

        DensityFunction temperature = GCDensityFunctions.registerAndWrap(context, GCDensityFunctions.Mars.TEMPERATURE,
                DensityFunctions.min(DensityFunctions.zero(), DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(
                                shiftX, shiftZ, 0.25, noiseRegistry.getOrThrow(Noises.TEMPERATURE)
                        ))
                )
        ); // cap temp at 0

        DensityFunction continentalness = GCDensityFunctions.registerAndWrap(context, GCDensityFunctions.Mars.CONTINENTALNESS, DensityFunctions.flatCache(
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 0.5, noiseRegistry.getOrThrow(Noises.CONTINENTALNESS)
                )
        ));

        DensityFunction erosion = GCDensityFunctions.registerAndWrap(context, GCDensityFunctions.Mars.EROSION, DensityFunctions.flatCache(
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 0.5, noiseRegistry.getOrThrow(Noises.EROSION)
                )
        ));

        DensityFunction weirdness = GCDensityFunctions.registerAndWrap(context, GCDensityFunctions.Mars.WEIRDNESS, DensityFunctions.flatCache(
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 0.25, noiseRegistry.getOrThrow(Noises.RIDGE)
                )
        ));

        DensityFunction pv = GCDensityFunctions.registerAndWrap(context, GCDensityFunctions.Mars.PV, DensityFunctions.flatCache(GCDensityFunctions.peaksAndValleys(weirdness)));

        // see TerrainProvider.java and OverworldBiomeBuilder.java
        DensityFunctions.Spline.Coordinate c = new DensityFunctions.Spline.Coordinate(densityLookup.getOrThrow(GCDensityFunctions.Mars.CONTINENTALNESS));
        DensityFunctions.Spline.Coordinate e = new DensityFunctions.Spline.Coordinate(densityLookup.getOrThrow(GCDensityFunctions.Mars.EROSION));

        var erosionModifierSpline = CubicSpline.builder(e)
                .addPoint(-1.0F, 0.75F)
                .addPoint(-0.95F, 0.70F)
                .addPoint(-0.85F, 0.4F)
                .addPoint(-0.375F, 0.3F)
                .addPoint(0.375F, 0.15F)
                .addPoint(1.0F, 0.1F)
                .build();

        DensityFunction erosionModifier = DensityFunctions.spline(erosionModifierSpline);

        var hc = CubicSpline.builder(c)
                .addPoint(-1.02F, -0.40F)
                .addPoint(-0.51F, -0.3667F)
                .addPoint(-0.44F, -0.12F)
                .addPoint(-0.18F, -0.12F)
                .addPoint(0.0F, 0.0F, 0.5F)
                .addPoint(0.3F, 0.4F)
                .build();

        int maxTerrainHeight = 224;
        int normalTerrainHeight = 72;
        int lowestTerrainHeight = 48;

        DensityFunction fixed = DensityFunctions.add(
                DensityFunctions.yClampedGradient(normalTerrainHeight, maxTerrainHeight, 0.0, -1.0),
                DensityFunctions.yClampedGradient(lowestTerrainHeight, normalTerrainHeight, 1.0, 0.0)
        );


        GCDensityFunctions.ShiftedNoise2dThreshold cThreshold = GCDensityFunctions.makeShiftedNoise2dThreshold(noiseRegistry.getOrThrow(Noises.CONTINENTALNESS), 0.5, noiseRegistry.getOrThrow(Noises.SHIFT), 0.8);
        GCDensityFunctions.ShiftedNoise2dThreshold wThreshold = GCDensityFunctions.makeShiftedNoise2dThreshold(noiseRegistry.getOrThrow(Noises.RIDGE), 0.5, noiseRegistry.getOrThrow(Noises.SHIFT), 0.8);

        DensityFunction test = DensityFunctions.flatCache(new GCDensityFunctions.DistributedCircularDensityFunction(
                cThreshold, 8, 50, 100, 180, 200
        ));
//        test = DensityFunctions.spline(shieldVolcanoSplineBuilder(Holder.direct(test)));

//        DensityFunction valley = DensityFunctions.spline(valleySplineBuilder(densityLookup.getOrThrow(Mars.PV)));
//        test = noise(noiseRegistry.getOrThrow(GCNoiseData.MARS_DUNES), 1.5, 0, 0.75);
        //test = DensityFunctions.max(DensityFunctions.zero(), DensityFunctions.add(test, DensityFunctions.mul(continentalness, DensityFunctions.constant(-0.5))));
        //test = DensityFunctions.add(test, DensityFunctions.mul(continentalness, DensityFunctions.constant(-0.05)));
        DensityFunction craterSpline = DensityFunctions.spline(GCDensityFunctions.largeCraterSplineBuilder(Holder.direct(test)));
        test = DensityFunctions.interpolated(craterSpline);
        test = DensityFunctions.interpolated(DensityFunctions.add(test, continentalness));
        // test = DensityFunctions.interpolated(DensityFunctions.spline(marsOffset(densityLookup.getOrThrow(GCDensityFunctions.Mars.CONTINENTALNESS), densityLookup.getOrThrow(GCDensityFunctions.Mars.EROSION))));
//        test = DensityFunctions.spline(TerrainProvider.overworldOffset(
//                new DensityFunctions.Spline.Coordinate(densityLookup.getOrThrow(Mars.CONTINENTALNESS)),
//                new DensityFunctions.Spline.Coordinate(densityLookup.getOrThrow(Mars.EROSION)),
//                new DensityFunctions.Spline.Coordinate(densityLookup.getOrThrow(Mars.PV)),
//                false
//        ));

        context.register(GCDensityFunctions.Mars.FINAL_DENSITY, DensityFunctions.add(fixed, test));
    }

    private static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> shieldVolcanoSplineBuilder(Holder<DensityFunction> input) {
        return CubicSpline.builder(new DensityFunctions.Spline.Coordinate(input))
                .addPoint(0.0F, 0.0F)
                .addPoint(0.05F, 0.1F, 0.25F)
                .addPoint(0.7F, 0.5F, 0.25F)
                .addPoint(0.9F, 0.8F)
                .addPoint(1.0F, 1.0F)
                .build();
    }

    private static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> valleySplineBuilder(Holder<DensityFunction> input) {
        return CubicSpline.builder(new DensityFunctions.Spline.Coordinate(input))
                .addPoint(-1.0F, -0.95F)
                .addPoint(-0.93F, -0.95F)
                .addPoint(-0.70F, -0.85F)
                .addPoint(-0.60F, -0.75F)
                .addPoint(-0.50F, -0.25F)
                .addPoint(-0.40F, 0.0F)
                .build();
    }

    private static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> marsOffset(
            Holder<DensityFunction> continentalness, Holder<DensityFunction> erosion, Holder<DensityFunction> peaksValleys) {
        return CubicSpline.builder(new DensityFunctions.Spline.Coordinate(continentalness))
                .addPoint(0.0F, marsErosionOffset(-0.5F, erosion))
                .addPoint(0.5F, 0.0F)
                .addPoint(0.9F, marsErosionOffset(0.5F, erosion))
                .build();
    }

    private static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> marsErosionOffset(float c, Holder<DensityFunction> erosion) {
        return CubicSpline.builder(new DensityFunctions.Spline.Coordinate(erosion))
                .addPoint(0.0F, -0.4F + c)
                .addPoint(0.5F, 0.0F)
                .addPoint(0.9F, 0.4F + c)
                .build();
    }

}
