package de.uni_trier.jane.basetypes;

/**
 * @author Klaus Sausen
 * vector implementation for matrix transformations
 */

public class Vector4D  {
  public double x,y,z,w;

  public Vector4D() {
    x=y=z=0.0;
    w=1.0;
  }

  public Vector4D(double d) {
  	x=y=z=d;
  	w=1.0;
  }
  public Vector4D(double x0, double y0, double z0, double w0) {
    x=x0;
    y=y0;
    z=z0;
    w=w0;
  }

  public Vector4D(double x0, double y0, double z0) {
    x=x0;
    y=y0;
    z=z0;
    w=1.0;
  }

  public Vector4D(Vector4D v) {
    x=v.x;
    y=v.y;
    z=v.z;
    w=v.w;
  }
  public void assign(Vector4D v) {
    x=v.x;
    y=v.y;
    z=v.z;
    w=v.w;
  }

  public Vector4D transform(Matrix a)
  //vec4<T> operator * (const matrix<T>& a, const vec4<T>& v)
  {
   return new Vector4D(
   a.v[0].x*x + a.v[0].y*y + a.v[0].z*z + a.v[0].w*w,
   a.v[1].x*x + a.v[1].y*y + a.v[1].z*z + a.v[1].w*w,
   a.v[2].x*x + a.v[2].y*y + a.v[2].z*z + a.v[2].w*w,
   a.v[3].x*x + a.v[3].y*y + a.v[3].z*z + a.v[3].w*w);
  }
  
  public void sub(Vector4D what) {
    x-=what.x;
    y-=what.y;
    z-=what.z;
  }

  public void normalize() {
    double len;
    //assume the vector to be homogenous (w to be 1.0)
    //x/=w;y/=w;z/=w;
    len=Math.sqrt(x*x+y*y+z*z);
    if (len==0.0)	//avoid division by zero 
    	return;
    len=1.0/len;
    x*=len;
    y*=len;
    z*=len;
    //w=1.0;
  }

  public void cross(Vector4D other) {
    double t0,t1;
    t0= y*other.z - z*other.y;
    t1= z*other.x - x*other.z;
    z = x*other.y - y*other.x;
    x=t0;
    y=t1;
  }
  
  public Vector4D copy() {
  	return new Vector4D(x,y,z,w);
  }
  
  public void print() {
  	System.out.println("("+x+","+y+","+z+","+w+")");
  }
}


