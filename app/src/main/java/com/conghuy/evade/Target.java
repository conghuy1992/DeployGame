package com.conghuy.evade;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Android on 8/2/2018.
 */

public class Target extends Sprite {
    private Body body;
    private ITextureRegion pTextureRegion;

    public Target(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager,
                  PhysicsWorld mPhysicsWorld) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.pTextureRegion = pTextureRegion;
//        body = PhysicsFactory.createBoxBody(mPhysicsWorld, this, BodyDef.BodyType.StaticBody, Statics.wallFixtureDef);
//        setBody(body);
        Body body = PhysicsFactory.createBoxBody(mPhysicsWorld, this, BodyDef.BodyType.StaticBody, Statics.wallFixtureDef);

        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));

//        this.animate(new long[]{200, 200}, 0, 1, true);
        this.setUserData(body);
        setBody(body);
    }

    public ITextureRegion getpTextureRegion() {
        return pTextureRegion;
    }

    public void setpTextureRegion(ITextureRegion pTextureRegion) {
        this.pTextureRegion = pTextureRegion;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
