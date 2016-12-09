package scratch.box2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    Box2DDebugRenderer b2dr;
    World world;
    Body player, platform;
    final float PPM = 32;
    final float SCALE = 2;
    SpriteBatch batch;
    Texture texture;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    TiledMap map;

    @Override
    public void create() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width / SCALE, height / SCALE);

        world = new World(new Vector2(0, -9.8f), false);
        b2dr = new Box2DDebugRenderer();
        player = createBox(0, 8, 32, 32, false);
        platform = createBox(0, 0, 64, 32, true);

        batch = new SpriteBatch();
        texture = new Texture("geoDash.png");

        map = new TmxMapLoader().load("map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
    }

    @Override
    public void render() {
        update();

        // render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.render();

        batch.begin();
        batch.draw(texture, player.getPosition().x * PPM - 16, player.getPosition().y * PPM - 16);
        batch.end();
        b2dr.render(world, camera.combined.scl(PPM));
    }

    @Override
    public void dispose () {
        b2dr.dispose();
        batch.dispose();
        texture.dispose();
        world.dispose();
        tiledMapRenderer.dispose();
        map.dispose();
    }

    private void update() {
        world.step(1 / 60f, 6, 2);
        inputUpdate();
        updateCamera();
        tiledMapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    private void inputUpdate() {
        int horizontalforce = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontalforce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontalforce += 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.applyForceToCenter(0, 300, false);
        }
        player.setLinearVelocity(horizontalforce * 5, player.getLinearVelocity().y);
    }

    private Body createBox(float x, float y, int width, int height, boolean isStatic) {
        Body pBody;
        BodyDef def = new BodyDef();
        if (isStatic) {
            def.type = BodyDef.BodyType.StaticBody;
        } else {
            def.type = BodyDef.BodyType.DynamicBody;
        }
        def.position.set(x / PPM, y / PPM);
        pBody = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);
        pBody.createFixture(shape, 1.0f);
        shape.dispose();
        return pBody;
    }

    private void updateCamera() {
        Vector3 position = camera.position;
        position.x = player.getPosition().x * PPM;
        position.y = player.getPosition().y * PPM;
        camera.position.set(position);

        camera.update();
    }
}
