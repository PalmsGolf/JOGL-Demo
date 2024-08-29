import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.Texture;

public class RotatingSquareSceneRenderer implements GLEventListener {
    private int shaderProgram;
    private int vertexArray;
    private long startTime;
    private Texture texture;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = getGLInterface(drawable);  // Get GL context to access OpenGL infterface
        startTime = System.currentTimeMillis();

        float[] vertices = {
                // Positions       |  // Texture Coords
                -0.5f, -0.5f, 0.0f,  0.0f, 0.0f,  // Bottom-left    [_
                0.5f, -0.5f, 0.0f,  1.0f, 0.0f,   // Bottom-right  _]
                0.5f, 0.5f, 0.0f,   1.0f, 1.0f,   // Top-right     ^]
                -0.5f, 0.5f, 0.0f,   0.0f, 1.0f   // Top-left       [^
        };

        // Index data for the square (two triangles)
        int[] indices = {
                0, 1, 2,  // First triangle
                2, 3, 0   // Second triangle
        };

        // Create and bind Vertex Array Object (VAO)
        int[] vertexArray = new int[1];
        gl.glGenVertexArrays(1, vertexArray, 0);
        this.vertexArray = vertexArray[0];
        gl.glBindVertexArray(this.vertexArray); // setting default value

        // Create and bind Vertex Buffer Object (VBO)
        int[] vertexBuffer = new int[1];
        gl.glGenBuffers(1, vertexBuffer, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBuffer[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * 4, GLBuffers.newDirectFloatBuffer(vertices),
                GL.GL_STATIC_DRAW);

        // Create and bind Element Buffer Object (EBO)
        int[] elementBuffer = new int[1];
        gl.glGenBuffers(1, elementBuffer, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBuffer[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, GLBuffers.newDirectIntBuffer(indices),
                GL.GL_STATIC_DRAW); // :) That's where vide card memory value.....

        // Load and compile vertex and fragment shaders
        shaderProgram = JoglUtils.createProgram(gl, "/vertex_shader_cube.glsl", "/fragment_shader_cube.glsl");

        // Specify the layout of the vertex data
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 5 * 4, 0);  //float 4 * 5 elements
        gl.glEnableVertexAttribArray(0);

        gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 5 * 4, 3 * 4);
        gl.glEnableVertexAttribArray(1);

        // Load texture
        texture = JoglUtils.loadTexture("glass.png");

        // Set texture parameters
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

        // Buffers needs to be deleted as well

        if (texture != null) {
            texture.destroy(gl);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = getGLInterface(drawable);

        // Clear the screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // DEPTH buffer clean is needed now
        gl.glClearColor(0.68f, 0.85f, 0.9f, 1.0f); // : GL.GL_COLOR_BUFFER_BIT

        // Use the shader program
        gl.glUseProgram(shaderProgram);

        forwardShaderUniforms(gl);
        // Bind texture
        if (texture != null) {
            texture.bind(gl);
        }

        // Draw the square using the EBO (indices)
        gl.glBindVertexArray(vertexArray);
        gl.glDrawElements(GL.GL_TRIANGLES, 6, GL.GL_UNSIGNED_INT, 0);

        // Unbind the VAO
        gl.glBindVertexArray(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = getGLInterface(drawable);
        gl.glViewport(0, 0, width, height);

        // Aspect Ratio...
    }

    private GL3 getGLInterface(GLAutoDrawable drawable) {
        return drawable.getGL().getGL3();
    }

    private void forwardShaderUniforms(GL3 gl){
        float angle = (System.currentTimeMillis() - startTime) / 1000.0f;
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);

        float[] rotationMatrix = {
                cos, -sin, 0.0f, 0.0f,
                sin,  cos, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        int transformLoc = gl.glGetUniformLocation(shaderProgram, "transform");
        gl.glUniformMatrix4fv(transformLoc, 1, false, rotationMatrix, 0);
    }
}