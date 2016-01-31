package io.novikov.simplecolouring;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLException;

public abstract class Shader {
    private static final String TAG = "Shader";
    protected static final int BYTES_PER_FLOAT = 4;

    public abstract void init(Context context);
    public abstract void draw(int texId);
    public abstract void resize(int width, int height);

    protected static int prepareProgram(String vertexShaderCode, String fragmentShaderCode)
                                                                                throws GLException{
        int vertexShader = 0;
        int fragmentShader = 0;
        int programId = 0;
        try {
            //compile program
            vertexShader = compile(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            fragmentShader = compile(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            //link program
            programId = link(vertexShader, fragmentShader);
            //validate program
            if (BuildConfig.DEBUG ) {
                validate(programId);
            }
        } catch (GLException e) {
            if (vertexShader != 0)   GLES20.glDeleteShader(vertexShader);
            if (fragmentShader != 0) GLES20.glDeleteShader(fragmentShader);
            if (programId != 0) GLES20.glDeleteProgram(programId);
            throw e;
        }
        return programId;
    }

    private static void validate(int programId) throws GLException {
        GLES20.glValidateProgram(programId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        if (validateStatus[0] == 0) {
            throw new GLException(GLES20.glGetError(), "Program is not valid: " +
                    GLES20.glGetProgramInfoLog(programId));
        }
    }

    private static int link(int vertexShaderId,int fragmentShaderId) throws GLException {
        final int programId = GLES20.glCreateProgram();
        if(programId == 0) {
            throw new GLException(GLES20.glGetError(), "Could not create a program." );
        }
        GLES20.glAttachShader(programId, vertexShaderId);
        GLES20.glAttachShader(programId, fragmentShaderId);
        GLES20.glLinkProgram(programId);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            final String info = GLES20.glGetProgramInfoLog(programId);
            GLES20.glDeleteProgram(programId);
            throw new GLException(GLES20.glGetError(), "Could not link the program: " + info);
        }

        return programId;
    }

    private static int compile(int type, String shaderCode) throws GLException {
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            throw new GLException(GLES20.glGetError(), "Could not create new shader." );
        }

        GLES20.glShaderSource(shaderObjectId, shaderCode);

        GLES20.glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            final String info = GLES20.glGetShaderInfoLog(shaderObjectId);
            GLES20.glDeleteShader(shaderObjectId);
            throw new GLException(GLES20.glGetError(), "Could not compile a shader: type:" + type
                                                        + "; info:"  + info);
        }
        return shaderObjectId;
    }
}
