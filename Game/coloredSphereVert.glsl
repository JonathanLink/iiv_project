// #extension GL_EXT_gpu_shader4 : require
uniform mat4 transform;
attribute vec4 vertex;
varying vec4 fcolor;
 
void main() {

	gl_Position = transform * vertex;  
	//fcolor = vec4(gl_VertexID, 0,0, 0.0, 1.0);
	fcolor = vec4(clamp(gl_Position, 0.0, 0.5));
}