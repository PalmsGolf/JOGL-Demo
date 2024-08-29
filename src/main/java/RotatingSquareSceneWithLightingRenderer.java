import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RotatingSquareSceneWithLightingRenderer implements GLEventListener {
    private int shaderProgram;
    private int vertexArray;
    private long startTime;
    private Texture texture;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = getGLInterface(drawable);  // Get GL context to access OpenGL infterface
        startTime = System.currentTimeMillis();

        // Vertex data for the cube (positions, normals, texture coordinates)
        float[] vertices = {
                // Positions          // Normals           // Texture Coords
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f
        };

        // Generate and bind Vertex Array Object (VAO)
        int[] vertexArray = new int[1];
        gl.glGenVertexArrays(1, vertexArray, 0);
        this.vertexArray = vertexArray[0];
        gl.glBindVertexArray(this.vertexArray);

        // Generate and bind Vertex Buffer Object (VBO)
        int[] vertexBuffer = new int[1];
        gl.glGenBuffers(1, vertexBuffer, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBuffer[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * 4, GLBuffers.newDirectFloatBuffer(vertices), GL.GL_STATIC_DRAW);

        // Position attribute
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 8 * 4, 0);
        gl.glEnableVertexAttribArray(0);

        // Normal attribute
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 8 * 4, 3 * 4);
        gl.glEnableVertexAttribArray(1);

        // Texture coordinate attribute
        gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, 8 * 4, 6 * 4);
        gl.glEnableVertexAttribArray(2);

        gl.glEnable(GL.GL_DEPTH_TEST); // Now needed

        // Load and compile vertex and fragment shaders
        shaderProgram = JoglUtils.createProgram(gl, "/vertex_shader_cube_light.glsl",
                "/fragment_shader_cube_light.glsl");


        texture = JoglUtils.loadTexture("glass.png");

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = getGLInterface(drawable);
        gl.glDeleteProgram(shaderProgram);
        gl.glDeleteVertexArrays(1, new int[]{vertexArray}, 0);

        if (texture != null) {
            texture.destroy(gl);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = getGLInterface(drawable);

        // Clear color and depth buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Use shader program
        gl.glUseProgram(shaderProgram);

        forwardShaderUniforms(gl);

        // Bind texture and draw cube
        if (texture != null) {
            texture.bind(gl);
        }

        gl.glBindVertexArray(vertexArray);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);  // 36 vertices. 6 faces and 2 triangles per face)
        gl.glBindVertexArray(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = getGLInterface(drawable);
        gl.glViewport(0, 0, width, height);
    }

    private GL3 getGLInterface(GLAutoDrawable drawable) {
        return drawable.getGL().getGL3();
    }

    private void forwardShaderUniforms(GL3 gl){
        // Transformation matrices
        float time = (System.currentTimeMillis() - startTime) / 1000.0f;
        float angle = time * 50.0f;

        // for rotate, move...
        Matrix4f model = new Matrix4f().identity().rotate((float) Math.toRadians(angle), 1.0f, 1.0f, 0.0f);

        // Camera view
        Matrix4f view = new Matrix4f().lookAt(new Vector3f(3.0f, 3.0f, 3.0f),
                                              new Vector3f(0.0f, 0.0f, 0.0f),
                                              new Vector3f(0.0f, 1.0f, 0.0f));
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.1f, 100.0f);

        int modelLoc = gl.glGetUniformLocation(shaderProgram, "model");
        int viewLoc = gl.glGetUniformLocation(shaderProgram, "view");
        int projLoc = gl.glGetUniformLocation(shaderProgram, "projection");

        gl.glUniformMatrix4fv(modelLoc, 1, false, model.get(new float[16]), 0);
        gl.glUniformMatrix4fv(viewLoc, 1, false, view.get(new float[16]), 0);
        gl.glUniformMatrix4fv(projLoc, 1, false, projection.get(new float[16]), 0);

        // Lighting. One pixel will be brighter than another one..
        int lightPosLoc = gl.glGetUniformLocation(shaderProgram, "lightPos");
        int viewPosLoc = gl.glGetUniformLocation(shaderProgram, "viewPos");
        int lightColorLoc = gl.glGetUniformLocation(shaderProgram, "lightColor");
        int objectColorLoc = gl.glGetUniformLocation(shaderProgram, "objectColor");

        gl.glUniform3f(lightPosLoc, 1.2f, 1.0f, 2.0f);
        gl.glUniform3f(viewPosLoc, 3.0f, 3.0f, 3.0f);
        gl.glUniform3f(lightColorLoc, 1.0f, 1.0f, 1.0f);
        gl.glUniform3f(objectColorLoc, 1.0f, 0.5f, 0.31f);  // Base color of your object
    }
}