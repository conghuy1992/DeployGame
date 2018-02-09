package com.conghuy.evade;

//MainActivity


import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 21:18:08 - 27.06.2010
 */
public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {
    // ===========================================================
    // Constants
    // ===========================================================

    private String TAG = "MainActivity";

    private int CAMERA_WIDTH = 240;
    private int CAMERA_HEIGHT = 400;

    // ===========================================================
    // Fields
    // ===========================================================

    private BitmapTextureAtlas mBitmapTextureAtlas;

    private TiledTextureRegion mBoxFaceTextureRegion;
    private TiledTextureRegion mCircleFaceTextureRegion;

    public ITextureRegion background, horizontal, line_bottom;

    private int mFaceCount = 0;

    private PhysicsWorld mPhysicsWorld;

    private float mGravityX;
    private float mGravityY;

    private Camera camera;

    private Scene mScene;

    private List<Target> targetList;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
//        Toast.makeText(this, "Touch the screen to add objects. Touch an object to shoot it up into the air.", Toast.LENGTH_LONG).show();

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources() {
        targetList = new ArrayList<>();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
        this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
        this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
        this.mBitmapTextureAtlas.load();

        this.background = Utils.getITextureRegion(this, "background.png");
        this.horizontal = Utils.getITextureRegion(this, "horizontal.png");
        this.line_bottom = Utils.getITextureRegion(this, "line_bottom.png");

    }

    private Sprite lineBottomSprite;
    private float bottom;
    private boolean isFrist = true;
    private float targetX, targetY;

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        bottom = CAMERA_HEIGHT - CAMERA_HEIGHT / 3f;
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        this.mScene = new Scene();
        // add background
        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0.0f,
                new Sprite(0, CAMERA_HEIGHT - this.background.getHeight(), this.background, getVertexBufferObjectManager())));
//        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-1.0f,
//                new Sprite(0, 50, this.cloud_2, getVertexBufferObjectManager())));
//        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-2.0f,
//                new Sprite(0, 150, this.cloud_1, getVertexBufferObjectManager())));
//        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-2.0f,
//                new Sprite(0, CAMERA_HEIGHT - this.bottom_icon.getHeight(), this.bottom_icon, getVertexBufferObjectManager())));
        mScene.setBackground(autoParallaxBackground);
//        this.mScene.setBackground(new Background(0, 0, 0));

        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

        lineBottomSprite = new Sprite(0, CAMERA_HEIGHT - line_bottom.getHeight() - 1, this.line_bottom, vertexBufferObjectManager);
        mScene.attachChild(lineBottomSprite);

        this.mScene.setOnSceneTouchListener(this);


        final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 1, CAMERA_WIDTH, 1, vertexBufferObjectManager);
        final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 1, vertexBufferObjectManager);
        final Rectangle left = new Rectangle(0, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);
        final Rectangle right = new Rectangle(CAMERA_WIDTH - 1, 0, 1, CAMERA_HEIGHT, vertexBufferObjectManager);

        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, Statics.wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, Statics.wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, Statics.wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, Statics.wallFixtureDef);

        this.mScene.attachChild(ground);
        this.mScene.attachChild(roof);
        this.mScene.attachChild(left);
        this.mScene.attachChild(right);

        startGame();

        this.mScene.registerUpdateHandler(this.mPhysicsWorld);

        this.mScene.setOnAreaTouchListener(this);

        return this.mScene;
    }

    private void createNextTarget() {
        initLocation();
        Target sprite = new Target(targetX, targetY, this.horizontal, getVertexBufferObjectManager(), this.mPhysicsWorld);
        this.mScene.attachChild(sprite);
        targetList.add(sprite);
    }

    private void startGame() {
        targetList.clear();

        isFrist = true;
        initLocation();

        Target sprite = new Target(targetX, targetY, this.horizontal, getVertexBufferObjectManager(), this.mPhysicsWorld);
        this.mScene.attachChild(sprite);
        targetList.add(sprite);

        this.addFace(targetX, targetY - mBoxFaceTextureRegion.getHeight());

        createNextTarget();

        mScene.registerUpdateHandler(detectCollides);
    }

    private void stopGame() {
        Utils.showMsg(MainActivity.this, "Game Over");
        mScene.unregisterUpdateHandler(detectCollides);
    }

    private void initLocation() {

        // flag:true -> object move right to left
        // flag:false -> object move left to right

        float objectWidth = horizontal.getWidth();

        float minX = objectWidth;
        float maxX = CAMERA_WIDTH / 2 - 10f - objectWidth; // 50f

        flag = flag ? false : true;

        if (flag) {
            minX = CAMERA_WIDTH / 2 + 30f;
            maxX = CAMERA_WIDTH - objectWidth * 2;
        }

        targetX = new Random().nextInt((int) (maxX - minX)) + minX;

        if (isFrist) {
            targetY = bottom;
            isFrist = false;
        } else {
            targetY = bottom - new Random().nextInt(80) - 20;
        }


    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown()) {
//            final AnimatedSprite face = (AnimatedSprite) pTouchArea;
//            this.jumpFace(face);

//            float pX = sprite.getX();
//            float pY = bottom;
//            sprite.setPosition(pX, pY);
//            sprite.getBody().setTransform(sprite.getBody().getPosition().x, (pY + sprite.getpTextureRegion().getHeight() / 2f) / 32f, sprite.getBody().getAngle());

//            float x = sprite.getBody().getPosition().x;
//            float y = sprite.getBody().getPosition().y;
//            Log.d(TAG, "X:" + x);
//            Log.d(TAG, "y:" + y);
//            Log.d(TAG, "getX:" + sprite.getX());
//            Log.d(TAG, "getY:" + sprite.getY());

            return true;
        }

        return false;
    }

    private float ratioScale = 0.01f;
    private float maxScale = 1f;
    private float minScale = 0.5f;
    private float maxJumpX = 20f;
    private float maxJumpY = 20f;
    private boolean flag = true;
    private IUpdateHandler iUpdateHandler = new IUpdateHandler() {
        @Override
        public void onUpdate(float pSecondsElapsed) {
            Log.d(TAG, "onUpdate");
            maxScale -= ratioScale;
            if (maxScale < minScale) maxScale = minScale;
            face.setScale(maxScale);
        }

        @Override
        public void reset() {

        }
    };
    private IUpdateHandler detectCollides = new IUpdateHandler() {
        @Override
        public void onUpdate(float pSecondsElapsed) {
//            Log.d(TAG,"detectCollides onUpdate");
            if (lineBottomSprite.collidesWith(face)) {
                // over game
                handlerMessage.obtainMessage(MSG_GAME_OVER, "").sendToTarget();
            }
//            Target obj = targetList.get(targetList.size()-1);
//            if (obj.collidesWith(face)) {
//                // change position
//                Log.d(TAG,"detectCollides face");
//                handlerMessage.obtainMessage(MSG_SEND, "").sendToTarget();
//            }

            for (int i = 0; i < targetList.size(); i++) {
                Log.d(TAG, "i:" + i);
                if (targetList.get(i).collidesWith(face)) {
                    // change position
                    Log.d(TAG, "detectCollides face");
                    handlerMessage.obtainMessage(MSG_SEND, "").sendToTarget();
                }
            }
        }

        @Override
        public void reset() {

        }
    };

    private void down() {
        mScene.registerUpdateHandler(iUpdateHandler);
    }

    private void up() {
        float index = 1f - maxScale;
        mGravityX = index * maxJumpX / 0.5f;
        mGravityY = index * maxJumpY / 0.5f;

        if (!flag) mGravityX = mGravityX * -1f;

        Log.d(TAG, "index:" + index);
        Log.d(TAG, "mGravityX:" + mGravityX);
        Log.d(TAG, "mGravityY:" + mGravityY);

        maxScale = 1f;
        face.setScale(maxScale);
        mScene.unregisterUpdateHandler(iUpdateHandler);
        face.jumpFace(mGravityX, mGravityY);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 3; i++) {
//                    try {
//                        // speed
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (i == 2) handlerMessage.obtainMessage(MSG_SEND, "").sendToTarget();
//                }
//            }
//        }).start();
    }

    private void updatePositionView() {

        if (face == null || face.isGameOver()) return;

        removeTarget(targetList.get(0));
        targetList.remove(0);

        Target sprite = targetList.get(targetList.size() - 1);
        float pX = sprite.getX();
        float pY = bottom;
        sprite.setPosition(pX, pY);
        sprite.getBody().setTransform(sprite.getBody().getPosition().x, (pY + sprite.getpTextureRegion().getHeight() / 2f) / 32f, sprite.getBody().getAngle());
        createNextTarget();
    }

    private final int MSG_SEND = 1;
    private final int MSG_GAME_OVER = 2;

    android.os.Handler handlerMessage = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_SEND:
                    updatePositionView();
//                    final String text = (String) msg.obj; // get contents
                    break;
                case MSG_GAME_OVER:
                    stopGame();
                    break;

            }
        }
    };

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
//        if(this.mPhysicsWorld != null) {
//            if(pSceneTouchEvent.isActionDown()) {
//                this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
//                return true;
//            }
//        }
//        return false;

        switch (pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                down();
                break;
//            case TouchEvent.ACTION_MOVE:
            case TouchEvent.ACTION_UP:
                up();
                break;
        }
        return true;
    }

    @Override
    public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(final AccelerationData pAccelerationData) {
//        this.mGravityX = pAccelerationData.getX();
//        this.mGravityY = pAccelerationData.getY();
//
//        final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
//        this.mPhysicsWorld.setGravity(gravity);
//        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        this.enableAccelerationSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();

        this.disableAccelerationSensor();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private Player face;

    private void addFace(final float pX, final float pY) {
//        this.mFaceCount++;
//
//        final Body body;
//
////        if(this.mFaceCount % 2 == 1){
//        face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
//        body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, Statics.objectFixtureDef);
////        } else {
////            face = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
////            body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
////        }
//
//        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
//
//        face.animate(new long[]{200, 200}, 0, 1, true);
//        face.setUserData(body);

        face = new Player(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager(),
                this.mPhysicsWorld, lineBottomSprite, new GameOverCallBack() {
            @Override
            public void onGameOver() {
                handlerMessage.obtainMessage(MSG_GAME_OVER, "").sendToTarget();
            }
        });
//        this.mScene.registerTouchArea(face);
        this.mScene.attachChild(face);
    }


    private void removeTarget(final Target face) {
        final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);

        this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
        this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());

        this.mScene.unregisterTouchArea(face);
        this.mScene.detachChild(face);

        System.gc();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
