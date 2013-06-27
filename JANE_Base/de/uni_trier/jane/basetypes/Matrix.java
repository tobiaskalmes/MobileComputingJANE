/*
 * Created on 23.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.basetypes;

/**
 * @author Klaus Sausen
 * 4x4 transformation matrix for graphical objects
 */
public class Matrix {
	public Vector4D[] v = new Vector4D[4];

	public Matrix() 
	{
		v[0]=new Vector4D(0.0);
		v[1]=new Vector4D(0.0);
		v[2]=new Vector4D(0.0);
		v[3]=new Vector4D(0.0);
	}

	public Matrix(Vector4D v0, Vector4D v1, Vector4D v2, Vector4D v3)
	{
		v[0]=v0;
		v[1]=v1;
		v[2]=v2;
		v[3]=v3;
	}

	Matrix(double d)
	{
		v[0]=new Vector4D(d);
		v[1]=new Vector4D(d);
		v[2]=new Vector4D(d);
		v[3]=new Vector4D(d);
	}

	public Matrix(Matrix m)
	{
		v[0]=m.v[0].copy();
		v[1]=m.v[1].copy();
		v[2]=m.v[2].copy();
		v[3]=m.v[3].copy();
	}

	public double transform(double d) {
		return v[0].x*d;
	}
	
	public Matrix transpose() 
	{
		return new Matrix(	
			new Vector4D(v[0].x, v[1].x, v[2].x, v[3].x),
			new Vector4D(v[0].y, v[1].y, v[2].y, v[3].y),
			new Vector4D(v[0].z, v[1].z, v[2].z, v[3].z),
			new Vector4D(v[0].w, v[1].w, v[2].w, v[3].w)
		);
	}

	/**
	 * return a new matrix composed from the two given as a parameter 
	 * @param a
	 * @param b
	 * @return new matrix
	 */
	public static Matrix mul (Matrix a, Matrix b)
	{
	    return new Matrix(
	    new Vector4D(
	    a.v[0].x*b.v[0].x + a.v[0].y*b.v[1].x + a.v[0].z*b.v[2].x + a.v[0].w*b.v[3].x,
	    a.v[0].x*b.v[0].y + a.v[0].y*b.v[1].y + a.v[0].z*b.v[2].y + a.v[0].w*b.v[3].y,
	    a.v[0].x*b.v[0].z + a.v[0].y*b.v[1].z + a.v[0].z*b.v[2].z + a.v[0].w*b.v[3].z,
	    a.v[0].x*b.v[0].w + a.v[0].y*b.v[1].w + a.v[0].z*b.v[2].w + a.v[0].w*b.v[3].w),
	    new Vector4D(
	    a.v[1].x*b.v[0].x + a.v[1].y*b.v[1].x + a.v[1].z*b.v[2].x + a.v[1].w*b.v[3].x,
	    a.v[1].x*b.v[0].y + a.v[1].y*b.v[1].y + a.v[1].z*b.v[2].y + a.v[1].w*b.v[3].y,
	    a.v[1].x*b.v[0].z + a.v[1].y*b.v[1].z + a.v[1].z*b.v[2].z + a.v[1].w*b.v[3].z,
	    a.v[1].x*b.v[0].w + a.v[1].y*b.v[1].w + a.v[1].z*b.v[2].w + a.v[1].w*b.v[3].w),
	    new Vector4D(
	    a.v[2].x*b.v[0].x + a.v[2].y*b.v[1].x + a.v[2].z*b.v[2].x + a.v[2].w*b.v[3].x,
	    a.v[2].x*b.v[0].y + a.v[2].y*b.v[1].y + a.v[2].z*b.v[2].y + a.v[2].w*b.v[3].y,
	    a.v[2].x*b.v[0].z + a.v[2].y*b.v[1].z + a.v[2].z*b.v[2].z + a.v[2].w*b.v[3].z,
	    a.v[2].x*b.v[0].w + a.v[2].y*b.v[1].w + a.v[2].z*b.v[2].w + a.v[2].w*b.v[3].w),
	    new Vector4D(
	    a.v[3].x*b.v[0].x + a.v[3].y*b.v[1].x + a.v[3].z*b.v[2].x + a.v[3].w*b.v[3].x,
	    a.v[3].x*b.v[0].y + a.v[3].y*b.v[1].y + a.v[3].z*b.v[2].y + a.v[3].w*b.v[3].y,
	    a.v[3].x*b.v[0].z + a.v[3].y*b.v[1].z + a.v[3].z*b.v[2].z + a.v[3].w*b.v[3].z,
	    a.v[3].x*b.v[0].w + a.v[3].y*b.v[1].w + a.v[3].z*b.v[2].w + a.v[3].w*b.v[3].w)
	    );
	}

	/**
	 * multiply this matrix with the one given as a parameter, return it and discard 
	 * the old
	 * @param b the matrix that is multiplied
	 * @return result of composition
	 */
	public Matrix mul (Matrix b)
	{
	    Vector4D[] w = new Vector4D[4];
		w[0] = new Vector4D(
	    v[0].x*b.v[0].x + v[0].y*b.v[1].x + v[0].z*b.v[2].x + v[0].w*b.v[3].x,
	    v[0].x*b.v[0].y + v[0].y*b.v[1].y + v[0].z*b.v[2].y + v[0].w*b.v[3].y,
	    v[0].x*b.v[0].z + v[0].y*b.v[1].z + v[0].z*b.v[2].z + v[0].w*b.v[3].z,
	    v[0].x*b.v[0].w + v[0].y*b.v[1].w + v[0].z*b.v[2].w + v[0].w*b.v[3].w);
	    w[1] = new Vector4D(
	    v[1].x*b.v[0].x + v[1].y*b.v[1].x + v[1].z*b.v[2].x + v[1].w*b.v[3].x,
	    v[1].x*b.v[0].y + v[1].y*b.v[1].y + v[1].z*b.v[2].y + v[1].w*b.v[3].y,
	    v[1].x*b.v[0].z + v[1].y*b.v[1].z + v[1].z*b.v[2].z + v[1].w*b.v[3].z,
	    v[1].x*b.v[0].w + v[1].y*b.v[1].w + v[1].z*b.v[2].w + v[1].w*b.v[3].w);
	    w[2] = new Vector4D(
	    v[2].x*b.v[0].x + v[2].y*b.v[1].x + v[2].z*b.v[2].x + v[2].w*b.v[3].x,
	    v[2].x*b.v[0].y + v[2].y*b.v[1].y + v[2].z*b.v[2].y + v[2].w*b.v[3].y,
	    v[2].x*b.v[0].z + v[2].y*b.v[1].z + v[2].z*b.v[2].z + v[2].w*b.v[3].z,
	    v[2].x*b.v[0].w + v[2].y*b.v[1].w + v[2].z*b.v[2].w + v[2].w*b.v[3].w);
	    w[3] = new Vector4D(
	    v[3].x*b.v[0].x + v[3].y*b.v[1].x + v[3].z*b.v[2].x + v[3].w*b.v[3].x,
	    v[3].x*b.v[0].y + v[3].y*b.v[1].y + v[3].z*b.v[2].y + v[3].w*b.v[3].y,
	    v[3].x*b.v[0].z + v[3].y*b.v[1].z + v[3].z*b.v[2].z + v[3].w*b.v[3].z,
	    v[3].x*b.v[0].w + v[3].y*b.v[1].w + v[3].z*b.v[2].w + v[3].w*b.v[3].w);
	    v=w;
	    return this;
	}

	/**
	 * load identity into the space transformator part in the matrix
	 * @return this
	 */
	public static Matrix dropSpace(Matrix a) {
		Matrix m = new Matrix(a);
		m.v[0].x=1.0; m.v[0].y=0.0; m.v[0].z=0.0;
		m.v[1].x=0.0; m.v[1].y=1.0; m.v[1].z=0.0;
		m.v[2].x=0.0; m.v[1].y=0.0; m.v[2].z=1.0;
		return m;
	}

	/**
	 * load identity into the scale transformation
	 * @return new matrix m
	 */
	public static Matrix dropScale(Matrix a) {
		Matrix m = new Matrix(a);
		m.v[0].x=
		m.v[1].y=
		m.v[2].z=1.0; 
		return m;
	}

	/**
	 * drop translation
	 * @param a
	 * @return new matrix;
	 */
	public static Matrix dropTranslation(Matrix a) {
		Matrix m = new Matrix(a);
		m.v[0].w =
		m.v[1].w =
		m.v[2].w = 0.0;
		return m;
	}
	
	
	public static Matrix identity3d()
	{
		return new Matrix(
		new Vector4D(1.0, 0.0, 0.0, 0.0),
		new Vector4D(0.0, 1.0, 0.0, 0.0),
		new Vector4D(0.0, 0.0, 1.0, 0.0),
		new Vector4D(0.0, 0.0, 0.0, 1.0)
		);
	}


	public static Matrix translation3d(Vector4D v)
	{
	  return new Matrix(
		new Vector4D(1.0, 0.0, 0.0, v.x),
		new Vector4D(0.0, 1.0, 0.0, v.y),
		new Vector4D(0.0, 0.0, 1.0, v.z),
		new Vector4D(0.0, 0.0, 0.0, 1.0));
	}

	public static Matrix rotation3d(Vector4D axis, double angleDeg)
	{
		double	angleRad = angleDeg * Math.PI / 180.0,
		c = Math.cos(angleRad),
		s = Math.sin(angleRad),
		t = 1.0 - c;
		Vector4D Axis = new Vector4D(axis);
		Axis.normalize();
		return new Matrix(
			new Vector4D(t * Axis.x * Axis.x + c,
			     t * Axis.x * Axis.y - s * Axis.z,
			     t * Axis.x * Axis.z + s * Axis.y,
			     0.0),
			new Vector4D(t * Axis.x * Axis.y + s * Axis.z,
			     t * Axis.y * Axis.y + c,
			     t * Axis.y * Axis.z - s * Axis.x,
			     0.0),
			new Vector4D(t * Axis.x * Axis.z - s * Axis.y,
			     t * Axis.y * Axis.z + s * Axis.x,
			     t * Axis.z * Axis.z + c,
			     0.0),
			new Vector4D(0.0, 0.0, 0.0, 1.0));
	}

	public static Matrix scaling3d(Vector4D scaleVector)
	{
	  return new Matrix(
			new Vector4D(scaleVector.x, 0.0, 0.0, 0.0),
			new Vector4D(0.0, scaleVector.y, 0.0, 0.0),
			new Vector4D(0.0, 0.0, scaleVector.z, 0.0),
			new Vector4D(0.0, 0.0, 0.0, 1.0)
	               );
	}
	public void print() {
		for (int a=0;a<4;a++)
			v[a].print();
	}
}
//ASSIGNMENT OPERATORS
/*	template <class T>
	matrix<T>& matrix<T>::operator = ( const matrix<T>& m )
	{
		v[0]=m.v[0];
		v[1]=m.v[1];
		v[2]=m.v[2];
		v[3]=m.v[3];
		return *this;
	}

	template <class T>
	matrix<T>& matrix<T>::operator += ( const matrix<T>& m )
	{
		v[0]+=m.v[0];
		v[1]+=m.v[1];
		v[2]+=m.v[2];
		v[3]+=m.v[3];
		return *this;
	}

	template <class T>
	matrix<T>& matrix<T>::operator -= ( const matrix<T>& m )
	{
		v[0]-=m.v[0];
		v[1]-=m.v[1];
		v[2]-=m.v[2];
		v[3]-=m.v[3];
		return *this;
	}

	template <class T>
	matrix<T>& matrix<T>::operator *= ( const T d )
	{
		v[0]*=d;
		v[1]*=d;
		v[2]*=d;
		v[3]*=d;
		return *this;
	}

	template <class T>
	vec4<T>& matrix<T>::operator [] (int i) {
	    #ifdef DEBUG
	    if (i < VX || i > VW)
		v_err("matrix [] operator: illegal access; index = " << i << '\n');
	    #endif
	    return v[i];
	}
*/


/*
	template <class T>
	matrix<T> perspective3d(const T d)
	{//fixme ,..
		return 
		matrix<T>(
			vec4<T>(T(1.0), T(0.0), T(0.0), T(0.0)),
			vec4<T>(T(0.0), T(1.0), T(0.0), T(0.0)),
			vec4<T>(T(0.0), T(0.0), T(1.0), T(0.0)),
			vec4<T>(T(0.0), T(0.0), T(0.0), T(1.0)/d)
	               );
	}

	
	
	template <class T>//TODO: math ,..
					//Francois Doue...
	matrix<T> matrix<T>::inverse()	// Gauss-Jordan elimination with partial pivoting
	{
	    matrix<T> a(*this),		// As a evolves from original mat into identity
		 b(identity3d());	// b evolves from identity into inverse(a)
	    int i, j, i1;

	    // Loop over cols of a from left to right, eliminating above and below diag
	    for (j=0; j<4; j++) {   // Find largest pivot in column j among rows j..3
	    i1 = j;		    // Row with largest pivot candidate
	    for (i=j+1; i<4; i++)
		if (fabs(a.v[i].n[j]) > fabs(a.v[i1].n[j]))
		i1 = i;

	    // Swap rows i1 and j in a and b to put pivot on diagonal
	    swap(a.v[i1], a.v[j]);
	    swap(b.v[i1], b.v[j]);

	    // Scale row j to have a unit diagonal
	    #ifdef DEBUG
	    if (a.v[j].n[j]==0.)
		v_err("matrix::inverse: singular matrix; can't invert\n");
	    #endif
	    b.v[j] *= T(1.0)/a.v[j].n[j];
	    a.v[j] /= T(1.0)/a.v[j].n[j];

	    // Eliminate off-diagonal elems in col j of a, doing identical ops to b
	    for (i=0; i<4; i++)
		if (i!=j) {
		b.v[i] -= a.v[i].n[j]*b.v[j];
		a.v[i] -= a.v[i].n[j]*a.v[j];
		}
	    }
	    return b;
	}*/
/*
	template <class T>
	void matrix<T>::print(ostream& os) const {
	os 	<<"[\n"
		<< a.v[0] << "\n"
		<< a.v[1] << "\n"
		<< a.v[2] << "\n"
		<< a.v[3] 
		<< "\n]\n";
	}


	template <class T>
	ostream& operator << (ostream& os, const matrix<T>& m) {
		m.print(os);
		return os;
	}



//	 FRIENDS
	template <class T>
	matrix<T> operator - (const matrix<T>& a)
	{
		return 
		matrix<T>(-a.v[0], -a.v[1], -a.v[2], -a.v[3]);
	}

	template <class T>
	matrix<T> operator + (const matrix<T>& a, const matrix<T>& b)
	{
		return matrix<T>(	a.v[0] + b.v[0],
					a.v[1] + b.v[1],
					a.v[2] + b.v[2],
					a.v[3] + b.v[3]
				);
	}

	template <class T>
	matrix<T> operator - (const matrix<T>& a, const matrix<T>& b)
	{
		return matrix<T>(
				a.v[0] - b.v[0],
				a.v[1] - b.v[1],
				a.v[2] - b.v[2],
				a.v[3] - b.v[3]
			);
	}


	template <class T>
	matrix<T> operator * (const matrix<T>& a, const T d)
	{
		return matrix<T>(a.v[0] * d, a.v[1] * d, a.v[2] * d, a.v[3] * d);
	}

	template <class T>
	matrix<T> operator * (const T d, const matrix<T>& a)
	{
		return a*d;
	}


	template <class T>
	int operator == (const matrix<T>& a, const matrix<T>& b)
	{
		return ((a.v[0]==b.v[0]) 
		&& 	(a.v[1]==b.v[1])
		&&	(a.v[2]==b.v[2])
		&&	(a.v[3]==b.v[3])); 
	}

	template <class T>
	int operator != (const matrix<T>& a, const matrix<T>& b)
	{
		return !(a==b);
	}


	template <class T>
	void swap(matrix<T>& a, matrix<T>& b)
	{
		matrix<T> tmp(a); 
		a=b;
		b=tmp;
	}


	template <class T>
	matrix<T> glFrustum(	T left, T right,
				T bottom, T top,
				T nearval, T farval )
	{//fixme ,..
	   T x, y, a, b, c, d;
	   matrix<T> m;

	   if (nearval<=T(0.0) || farval<=T(0.0)) {
	      return m;
	   }

	   x = (T(2.0)*nearval) / (right-left);
	   y = (T(2.0)*nearval) / (top-bottom);
	   a = (right+left) / (right-left);
	   b = (top+bottom) / (top-bottom);
	   c = -(farval+nearval) / ( farval-nearval);
	   d = -(T(2.0)*farval*nearval) / (farval-nearval);  

		m=matrix<T>(
			vec4<T>(    x , T(0.0),      a , T(0.0)),
			vec4<T>(T(0.0),     y ,      b , T(0.0)),
			vec4<T>(T(0.0), T(0.0),      c ,     d ),
			vec4<T>(T(0.0), T(0.0), T(-1.0), T(0.0)));
	//   M(0,0) = x;     M(0,1) = 0.0F;  M(0,2) = a;      M(0,3) = 0.0F;
	//   M(1,0) = 0.0F;  M(1,1) = y;     M(1,2) = b;      M(1,3) = 0.0F;
	//   M(2,0) = 0.0F;  M(2,1) = 0.0F;  M(2,2) = c;      M(2,3) = d;
	//   M(3,0) = 0.0F;  M(3,1) = 0.0F;  M(3,2) = -1.0F;  M(3,3) = 0.0F;
		return m;
	}

	template <class T>
	matrix<T> gluPerspective( T fovy, T aspect, T zNear, T zFar )
	{//fixme ,..
	   T xmin, xmax, ymin, ymax;

	   ymax = zNear * tan( fovy * M_PI / 360.0 );
	   ymin = -ymax;

	   xmin = ymin * aspect;
	   xmax = ymax * aspect;

	   return glFrustum( xmin, xmax, ymin, ymax, zNear, zFar );
	}


	template <class T>
	matrix<T> gluLookAt( vec3<T> eye, vec3<T> center, vec3<T> up)
	{
	   matrix<T> m;
	   //float x[3], y[3], z[3];
	   vec3<T> x,y,z;
	   T mag;

	   // Make rotation matrix 

		z = eye - center;
		z.normalize();
	   //mag = sqrt( z[0]*z[0] + z[1]*z[1] + z[2]*z[2] );
	   if (mag) {  // mpichler, 19950515 
	      z[0] /= mag;
	      z[1] /= mag;
	      z[2] /= mag;
	   }

	   // Y vector 
		y = up;
	   // X vector = Y cross Z 
		x = y^z;
	   //x[0] =  y[1]*z[2] - y[2]*z[1];
	   //x[1] = -y[0]*z[2] + y[2]*z[0];
	   //x[2] =  y[0]*z[1] - y[1]*z[0];

	   // Recompute Y = Z cross X 
		y = z^x;
	   //y[0] =  z[1]*x[2] - z[2]*x[1];
	   //y[1] = -z[0]*x[2] + z[2]*x[0];
	   //y[2] =  z[0]*x[1] - z[1]*x[0];

	   // mpichler, 19950515 
	   // cross product gives area of parallelogram, which is < 1.0 for
	   // non-perpendicular unit-length vectors; so normalize x, y here
	   //

		x.normalize();
	   //mag = sqrt( x[0]*x[0] + x[1]*x[1] + x[2]*x[2] );
	   //if (mag) {
	    //  x[0] /= mag;
	     // x[1] /= mag;
	     // x[2] /= mag;
	   //}

	   	y.normalize();
	   //mag = sqrt( y[0]*y[0] + y[1]*y[1] + y[2]*y[2] );
	   //if (mag) {
	   //   y[0] /= mag;
	   //   y[1] /= mag;
	   //   y[2] /= mag;
	   //}

		m=matrix<T>(
			vec4<T>(x,T(0.0)),
			vec4<T>(y,T(0.0)),
			vec4<T>(z,T(0.0)),
			vec4<T>(T(0.0), T(0.0), T(0.0), T(1.0))
			);
			
	//*#define M(row,col)  m[col*4+row]
	//   M(0,0) = x[0];  M(0,1) = x[1];  M(0,2) = x[2];  M(0,3) = 0.0;
	//   M(1,0) = y[0];  M(1,1) = y[1];  M(1,2) = y[2];  M(1,3) = 0.0;
	//   M(2,0) = z[0];  M(2,1) = z[1];  M(2,2) = z[2];  M(2,3) = 0.0;
	//   M(3,0) = 0.0;   M(3,1) = 0.0;   M(3,2) = 0.0;   M(3,3) = 1.0;
	//#undef M
//	 glMultMatrixd( m );

	   // Translate Eye to Origin 
	   //m=m*translation3d(vec3( -eyex, -eyey, -eyez) );
	//	m=m*translation3d(-eye);	
	//   return m;
	//}

}
*/