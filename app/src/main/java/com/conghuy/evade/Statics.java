package com.conghuy.evade;

import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsFactory;

/**
 * Created by Android on 16/1/2018.
 */

public interface Statics {
    String GFX = "gfx/";
    FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
    FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
}
