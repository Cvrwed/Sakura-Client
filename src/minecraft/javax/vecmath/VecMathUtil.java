package javax.vecmath;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class VecMathUtil {
	static final long hashLongBits(long hash, long l) {
		hash *= 31L;
		return hash + l;
	}

	static final long hashFloatBits(long hash, float f) {
		hash *= 31L;
		if (f == 0.0f)
			return hash;

		return hash + Float.floatToIntBits(f);
	}

	static final long hashDoubleBits(long hash, double d) {
		hash *= 31L;
		if (d == 0.0d)
			return hash;

		return hash + Double.doubleToLongBits(d);
	}

	static final int hashFinish(long hash) {
		return (int)(hash ^ (hash >> 32));
	}
}