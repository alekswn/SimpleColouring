package io.novikov.simplecolouring;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * Created by al on 1/31/16.
 */
public class SimpleColouringPageSurfaceShader extends PassthroughSurfaceShader {

    private static final String TAG = "SimpleColouringPageSurfaceShader";

    @Override
    protected void initProgram(Context context) {
        final String vertexShaderCode = Util.readTextFileFromResource(context,
                R.raw.passthrough_vertex_shader);
        final String fragmentShaderCode = Util.readTextFileFromResource(context,
                R.raw.simple_colouring_page_fragment_shader);
        mProgram = Shader.prepareProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void draw(int texId) {
        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int resSLocation = GLES20.glGetUniformLocation(mProgram, "resS");
        GLES20.glUniform1f(resSLocation, mScreenWidth);

        int resTLocation = GLES20.glGetUniformLocation(mProgram, "resT");
        GLES20.glUniform1f(resTLocation, mScreenHeight);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(mProgram, "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, uvBuffer);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mtrxProjectionAndView, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(mProgram, "s_texture");

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);

        //Util.LogDebug(TAG, "draw() finished!!!");
    }
}
