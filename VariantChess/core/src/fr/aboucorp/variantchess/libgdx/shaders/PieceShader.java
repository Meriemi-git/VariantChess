package fr.aboucorp.variantchess.libgdx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import fr.aboucorp.variantchess.entities.ChessColor;

public class PieceShader implements Shader {

    private ShaderProgram program;
    private Camera camera;
    private RenderContext context;
    private int u_projTrans;
    private int u_worldTrans;
    private int u_color;
    private int u_lightPosition;
    private Vector3 lightPosition;

    public PieceShader() {
    }

    @Override
    public void dispose() {
        this.program.dispose();
    }

    @Override
    public void init() {
        String vert = Gdx.files.internal("data/shaders/test_vertex.glsl").readString();
        String frag = Gdx.files.internal("data/shaders/test_fragment.glsl").readString();
        this.program = new ShaderProgram(vert, frag);
        if (!this.program.isCompiled())
            throw new GdxRuntimeException(this.program.getLog());
        this.u_projTrans = this.program.getUniformLocation("u_projTrans");
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
        this.u_color = this.program.getUniformLocation("u_color");
        this.u_lightPosition = this.program.getUniformLocation("u_lightPosition");
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return instance.userData != null;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.program.setUniformMatrix(this.u_projTrans, camera.combined);
        this.program.setUniformf(this.u_lightPosition, new Vector3(3.5f, 20f, 3.5f));
        context.setDepthTest(GL30.GL_LEQUAL);
        context.setCullFace(GL30.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        this.program.setUniformMatrix(this.u_worldTrans, renderable.worldTransform);
        ChessColor color = (ChessColor) renderable.userData;
        if (color == ChessColor.WHITE) {
            Color colorU = Color.WHITE;
            this.program.setUniformf(this.u_color, colorU.r, colorU.g, colorU.b);
        } else {
            Color colorU = Color.GRAY;
            this.program.setUniformf(this.u_color, colorU.r, colorU.g, colorU.b);
        }

        renderable.meshPart.render(this.program);
    }

    @Override
    public void end() {
        this.program.end();
    }
}
