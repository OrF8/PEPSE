package pepse.util;

/**
 * This class generates a smooth-noise function
 */
public class NoiseGenerator {
	private double seed;
	private long default_size;
	private int[] p;
	private int[] permutation;
	private double startPoint;

	/**
	 * Constructs a new NoiseGenerator instance with the given seed and start point.
	 *
	 * @param seed The seed value to initialize the noise generator.
	 * @param startPoint The starting point used in generating noise.
	 */
	public NoiseGenerator(double seed,int startPoint) {
		this.seed = seed;
		this.startPoint = startPoint;
		init();
	}

	/**
	 * Initializes the permutation array used for generating noise.
	 * This method populates the internal permutation array `p` with a repeated set of predefined
	 * values to enable methods to use a consistent permutation table for noise calculations.
	 */
	private void init() {
		// Initialize the permutation array.
		this.p = new int[512];
		this.permutation = new int[] { 151, 160, 137, 91, 90, 15, 131, 13, 201,
				95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99,
				37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
				197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
				237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74,
				165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
				229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
				244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76,
				132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
				86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
				124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
				206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
				213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155,
				167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
				224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
				193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235,
				249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
				84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236,
				205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
				215, 61, 156, 180 };
		this.default_size = 35;

		// Populate it
		for (int i = 0; i < 256; i++) {
			p[256 + i] = p[i] = permutation[i];
		}

	}

	/**
	 * Generates a noise value based on the given parameters using layered smooth noise functions.
	 *
	 * @param x The input value for which the noise is generated.
	 * @param factor A scaling factor that modifies the resulting noise value.
	 * @return A double representing the calculated noise value, scaled by the given factor.
	 */
	public double noise(double x,double factor) {
		double value = 0.0;
		double currentPoint = startPoint;

		while (currentPoint >= 1) {
			value += smoothNoise((x / currentPoint),0,0) * currentPoint;
			currentPoint /= 2.0;
		}

		return value * factor / startPoint;
	}

	/**
	 * Computes smooth noise for three-dimensional input coordinates, which is used
	 * in generating procedural textures or terrains. This function blends noise values
	 * from the surrounding points in the grid using fade and interpolation functions.
	 *
	 * @param x The x-coordinate for the noise generation.
	 * @param y The y-coordinate for the noise generation.
	 * @param z The z-coordinate for the noise generation.
	 * @return A double representing the smooth noise value at the given (x, y, z) coordinates.
	 */
	private double smoothNoise(double x, double y, double z) {
		// Offset each coordinate by the seed value
		x += this.seed;
		y += this.seed;
		x += this.seed;

		int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
		int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
		int Z = (int) Math.floor(z) & 255;

		x -= Math.floor(x); // FIND RELATIVE X,Y,Z
		y -= Math.floor(y); // OF POINT IN CUBE.
		z -= Math.floor(z);

		double u = fade(x); // COMPUTE FADE CURVES
		double v = fade(y); // FOR EACH OF X,Y,Z.
		double w = fade(z);

		int A = p[X] + Y;
		int AA = p[A] + Z;
		int AB = p[A + 1] + Z; // HASH COORDINATES OF
		int B = p[X + 1] + Y;
		int BA = p[B] + Z;
		int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

		return lerp(w, lerp(v, lerp(u, grad(p[AA], 		x, 		y, 		z		), 	// AND ADD
										grad(p[BA],		x - 1, 	y, 		z		)), // BLENDED
								lerp(u, grad(p[AB], 	x, 		y - 1, 	z		), 	// RESULTS
										grad(p[BB], 	x - 1, 	y - 1, 	z		))),// FROM 8
						lerp(v, lerp(u, grad(p[AA + 1], x, 		y, 		z - 1	), 	// CORNERS
										grad(p[BA + 1], x - 1, 	y, 		z - 1	)), // OF CUBE
								lerp(u, grad(p[AB + 1], x, 		y - 1,	z - 1	),
										grad(p[BB + 1], x - 1, 	y - 1, 	z - 1	))));
	}

	/**
	 * Applies the fade function to smooth transitions for a given input value.
	 * This function is typically used in noise generation algorithms to create
	 * smoother interpolations between values.
	 *
	 * @param t The input value to be transformed, typically in the range [0, 1].
	 * @return A double representing the smoothed output value based on the input.
	 */
	private double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	/**
	 * Performs a linear interpolation between two values based on a given factor.
	 *
	 * @param t The interpolation factor, typically between 0 and 1. A value of 0 returns `a`,
	 *          and a value of 1 returns `b`.
	 * @param a The starting value of the interpolation.
	 * @param b The ending value of the interpolation.
	 * @return The interpolated value between `a` and `b` based on the factor `t`.
	 */
	private double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	/**
	 * Computes the gradient vector based on the provided hash value and input coordinates.
	 * The method uses the hash value to determine one of 12 gradient directions
	 * and computes the dot product of the vector with the input coordinates.
	 *
	 * @param hash The hash value used to determine the gradient direction.
	 * @param x The x-coordinate of the input point.
	 * @param y The y-coordinate of the input point.
	 * @param z The z-coordinate of the input point.
	 * @return A double representing the gradient value based on the hash and input coordinates.
	 */
	private double grad(int hash, double x, double y, double z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
		v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}
}
