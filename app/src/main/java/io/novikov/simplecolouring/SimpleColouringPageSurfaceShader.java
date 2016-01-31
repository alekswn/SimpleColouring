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
        programId = Shader.prepareProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void draw(int texId) {
        //Util.LogDebug(TAG, "draw() started...");
        // clear Screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int resSLocation = GLES20.glGetUniformLocation(programId, "resS");
        GLES20.glUniform1f(resSLocation, 800);

        int resTLocation = GLES20.glGetUniformLocation(programId, "resT");
        GLES20.glUniform1f(resTLocation, 600);

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
}
