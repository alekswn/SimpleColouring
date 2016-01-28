package io.novikov.simplecolouring;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PassthroughSurfaceShader extends Shader {
    private static final String TAG = "ShaderSimple";
    private static final int POSITION_COMPONENT_COUNT = 2;

    private  int programId;
    private  FloatBuffer vertexData;

    public void init(Context context) {
        init(context, -1f, -1f, 1f, 1f);
    }

    public void init(Context context, float x1, float y1, float x2, float y2) {
        final String vertexShaderCode = Util.readTextFileFromResource(context,
                                                                      R.raw.simple_vertex_shader);
        final String fragmentShaderCode = Util.readTextFileFromResource(context,
                                                                    R.raw.simple_fragment_shader);
        programId = Shader.prepareProgram(vertexShaderCode, fragmentShaderCode);

        float[] tableVerticesWithTriangles =
                {
                        //Triangle1
                        x1, y1,
                        x2, y2,
                        x1, y2,
                        //Triangle2
                        x1, y1,
                        x2, y1,
                        x2, y2
                };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void draw() {


        GLES20.glUseProgram(programId);

        final String U_COLOR = "u_Color";
        final String A_POSITION = "a_Position";
        int uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR);
        int aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);

        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, 0, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glFlush();
    }
}
