package fr.aboucorp.variantchess.libgdx.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class CustomShaderProvider extends DefaultShaderProvider {

    public CustomShaderProvider() {
        super();
    }

    @Override
    protected Shader createShader(final Renderable renderable) {
        if (renderable.userData != null)
            return new PieceShader();
        else
            return super.createShader(renderable);
    }
}
