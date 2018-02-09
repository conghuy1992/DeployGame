package com.conghuy.evade;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Android on 8/2/2018.
 */

public class Player extends AnimatedSprite {
    private String TAG = "Player";
    private Sprite lineBottomSprite;
    private boolean isGameOver = false;
    private GameOverCallBack callBack;

    public Player(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager,
                  PhysicsWorld mPhysicsWorld, Sprite lineBottomSprite, GameOverCallBack callBack) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        this.lineBottomSprite = lineBottomSprite;
        this.callBack = callBack;
        Body body = PhysicsFactory.createBoxBody(mPhysicsWorld, this, BodyDef.BodyType.DynamicBody, Statics.objectFixtureDef);

        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));

        this.animate(new long[]{200, 200}, 0, 1, true);
        this.setUserData(body);
    }

//    @Override
//    protected void onManagedUpdate(float pSecondsElapsed) {
//        super.onManagedUpdate(pSecondsElapsed);
//
//        if (this.collidesWith(lineBottomSprite)) {
//            Log.d(TAG, "collidesWith");
//            isGameOver = true;
//            this.setIgnoreUpdate(true);
//            callBack.onGameOver();
//        }
//    }

    public void jumpFace(float mGravityX, float mGravityY) {
        final Body faceBody = (Body) this.getUserData();

//        final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50, this.mGravityY * -50);
        final Vector2 velocity = Vector2Pool.obtain(mGravityX, mGravityY);
        faceBody.setLinearVelocity(velocity);
        Vector2Pool.recycle(velocity);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
