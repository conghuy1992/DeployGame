package com.conghuy.evade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;


import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android on 16/1/2018.
 */

public class Utils {

    public static ITexture getITexture(final SimpleBaseGameActivity context, final String pathImage) {
        try {
            // 1 - Set up bitmap textures
            ITexture iTexture = new BitmapTexture(context.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return context.getAssets().open(Statics.GFX + pathImage);
                }
            });
            // 2 - Load bitmap textures into VRAM
            iTexture.load();
            return iTexture;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BuildableBitmapTextureAtlas getBuildableBitmapTextureAtlas(final SimpleBaseGameActivity context) {
        try {
            BuildableBitmapTextureAtlas gameTextureAtlas = new BuildableBitmapTextureAtlas(
                    context.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
            gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource,
                    BitmapTextureAtlas>(0, 1, 0));
            return gameTextureAtlas;
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ITiledTextureRegion getITiledTextureRegion(final SimpleBaseGameActivity context, String pathImage, int pTileColumns, int pTileRows) {
        BuildableBitmapTextureAtlas gameTextureAtlas = getBuildableBitmapTextureAtlas(context);
        ITiledTextureRegion iTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
                context, Statics.GFX + pathImage, pTileColumns, pTileRows);
        try {
            gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            gameTextureAtlas.load();
            return iTiledTextureRegion;
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ITextureRegion getITextureRegion(final SimpleBaseGameActivity context, String pathImage) {
        // 3 - Set up texture regions
        return TextureRegionFactory.extractFromTexture(Utils.getITexture(context, pathImage));
    }

    public static Font getFont(MainActivity context) {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(context.getTextureManager(), 256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Font font = FontFactory.createStrokeFromAsset(context.getFontManager(), mainFontTexture, context.getAssets(),
                "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        font.load();
        return font;
    }

    public static int getDimenInPx(Context context, int id) {
        return (int) context.getResources().getDimension(id);
    }

    public static String getMsg(Context context, int id) {
        return context.getResources().getString(id);
    }


    public static void showMsg(Context context, String msg) {
        if (context != null) {
            try {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showMsg(Context context, int id) {
        if (context != null) {
            try {
                Toast.makeText(context, getMsg(context, id), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getNumber(String index) {
        if (index.equals("1")) return R.drawable.number_1;
        if (index.equals("2")) return R.drawable.number_2;
        if (index.equals("3")) return R.drawable.number_3;
        if (index.equals("4")) return R.drawable.number_4;
        if (index.equals("5")) return R.drawable.number_5;
        if (index.equals("6")) return R.drawable.number_6;
        if (index.equals("7")) return R.drawable.number_7;
        if (index.equals("8")) return R.drawable.number_8;
        if (index.equals("9")) return R.drawable.number_9;
        else return R.drawable.number_0;
    }

    public static Sound getSound(MainActivity context, String fileName) {
        try {
            return SoundFactory.createSoundFromAsset(context.getEngine().getSoundManager(), context, "mfx/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void shareIntent(Context context, String EXTRA_TEXT) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, EXTRA_TEXT);
        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, Const.GOOGLE_PLAY);
        context.startActivity(Intent.createChooser(sendIntent, "Share via " + context.getResources().getString(R.string.app_name)));
    }


}