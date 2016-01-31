//uniform mat4 uMVPMatrix;
attribute vec2 aPosition;
attribute vec2 aTexCoord;

varying vec2 vTextureCoord;

void main(){
	gl_Position = vec4(aPosition.x, aPosition.y, 0.0, 1.0);
	vTextureCoord = aTexCoord;
}
