package javax.vecmath;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Vector4d extends Tuple4d implements java.io.Serializable {

	static final long serialVersionUID = 3938123424117448700L;

	public Vector4d(double x, double y, double z, double w) {
		super(x, y, z, w);
	}

	public Vector4d(double[] v) {
		super(v);
	}

	public Vector4d(Vector4d v1) {
		super(v1);
	}

	public Vector4d(Tuple4f t1) {
		super(t1);
	}

	public Vector4d(Tuple4d t1) {
		super(t1);
	}

	public final double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
	}

	public final double lengthSquared() {
		return (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
	}

	public final double dot(Vector4d v1) {
		return (this.x * v1.x + this.y * v1.y + this.z * v1.z + this.w * v1.w);
	}

	public final void normalize(Vector4d v1) {
		double norm;

		norm = 1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z + v1.w * v1.w);
		this.x = v1.x * norm;
		this.y = v1.y * norm;
		this.z = v1.z * norm;
		this.w = v1.w * norm;
	}

	public final void normalize() {
		double norm;

		norm = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		this.x *= norm;
		this.y *= norm;
		this.z *= norm;
		this.w *= norm;
	}

	public final double angle(Vector4d v1) {
		double vDot = this.dot(v1) / (this.length() * v1.length());
		if (vDot < -1.0)
			vDot = -1.0;
		if (vDot > 1.0)
			vDot = 1.0;
		return ((double) (Math.acos(vDot)));
	}
}