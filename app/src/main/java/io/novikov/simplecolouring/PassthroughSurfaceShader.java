package io.novikov.simplecolouring;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PassthroughSurfaceShader extends Shader {
    private static final String TAG = "PassthroughSurfaceShader";
    protected static final int POSITION_COMPONENT_COUNT = 2;

    // Transformation matrix
    //private final float[] mtrxProjectionAndView = new float[16];

    protected  int programId;
    protected  FloatBuffer vertexData;
    protected  FloatBuffer uvBuffer;

    protected void initProgram(Context context) {
        final String vertexShaderCode = Util.readTextFileFromResource(context,
                R.raw.passthrough_vertex_shader);
        final String fragmentShaderCode = Util.readTextFileFromResource(context,
                R.raw.passthrough_fragment_shader);
        programId = Shader.prepareProgram(vertexShaderCode, fragmentShaderCode);
    }

    protected void initBuffers() {
        //prepare vertex data array
        float[] Vertices = { 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f };

        vertexData = ByteBuffer.allocateDirect(Vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(Vertices);
        vertexData.position(0);

        //prepare texture coordinates array
        float[] uvs =  { 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };

        // The texture buffer
        uvBuffer = ByteBuffer.allocateDirect(uvs.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);
    }

    public void init(Context context) {
        Util.LogDebug(TAG, "init()");
        initProgram(context);
        GLES20.glUseProgram(programId);

        initBuffers();
    }

    @Override
    public void draw(int texId) {
        //Util.LogDebug(TAG, "draw() started...");
        // clear Screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int aPositionLocation = GLES20.glGetAttribLocation(programId, "aPosition");
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, 0, vertexData);

        // texture coordinates
        int texCoordLoc = GLES20.glGetAttribLocation(programId, "aTexCoord" );
        GLES20.glEnableVertexAttribArray ( texCoordLoc );
        GLES20.glVertexAttribPointer ( texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

/*
        // Projection and view transformations
        int mtrxhandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mtrxProjectionAndView, 0);
*/
        //textures
        int samplerLoc = GLES20.glGetUniformLocation (programId, "sTexture" );
        GLES20.glUniform1i(samplerLoc, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFlush();
        // Disable attribs
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(texCoordLoc);
        //Util.LogDebug(TAG, "draw() finished!!!");
    }

    @Override
    public void resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
/*
        final float[] mtrxProjection = new float[16];
        final float[] mtrxView = new float[16];

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, width, 0.0f, height, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

        Util.LogDebug(TAG, "Projection matrix:" + Arrays.toString(mtrxProjectionAndView));
*/
    }
}
