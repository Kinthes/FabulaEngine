package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.map.AsyncSceneLoader;
import com.macbury.fabula.map.AsyncSceneLoader.AsyncSceneLoaderListener;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.ui.ingame.GamePlayUI;
import com.macbury.fabula.utils.TopDownCamera;

public class GamePlayScreen extends BaseScreen implements AsyncSceneLoaderListener {
  
  private static final String TAG = "GamePlayScreen";
  private Scene scene;
  private TopDownCamera camera;
  private Terrain terrain;
  private Thread sceneLoadingThread;
  private GamePlayUI gamePlayUI;
  public GamePlayScreen(GameManager manager) {
    super(manager);
  }

  @Override
  public void show() {
    Gdx.app.log(TAG, "Show");
    
    G.shaders.createFB(Scene.MAIN_FRAME_BUFFER);
    
    this.gamePlayUI    = new GamePlayUI(this);
    this.camera        = new TopDownCamera();
    this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    
    Gdx.input.setInputProcessor(gamePlayUI);
    teleport(G.db.getPlayerStartPosition().getUUID(), 0,0);
  }
  
  public void teleport(String uuid, int tx, int ty) {
    if (scene != null) {
      scene.dispose();
      scene = null;
      terrain = null;
    }
    
    this.sceneLoadingThread = new Thread(new AsyncSceneLoader(G.db.getPlayerStartPosition().getFileHandler().file(), G.db.getPlayerStartPosition().getSpawnPosition(), this));
    this.sceneLoadingThread.start();
  }
  
  @Override
  public void dispose() {
    this.gamePlayUI.dispose();
    this.scene.dispose();
    camera           = null;
    scene            = null;
    terrain          = null;
  }
  
  @Override
  public void hide() {
    
  }
  
  @Override
  public void pause() {
    
  }
  
  @Override
  public void render(float delta) {
    camera.update();
    gamePlayUI.update(delta);
    
    if (scene != null) {
      scene.render(delta);
    }
    
    gamePlayUI.draw();
    //gamePlayUI.renderDebug();
  }
  
  @Override
  public void resize(int width, int height) {
    camera.viewportWidth  = width;
    camera.viewportHeight = height;
    this.camera.update(true);
    this.gamePlayUI.resize(Math.round(width / G.game.getScaledDensity()), Math.round(height / G.game.getScaledDensity()));

    Gdx.app.log(TAG, "Viewport: " + camera.viewportWidth + "x" + camera.viewportHeight + " for " + G.game.getScaledDensity());
    G.shaders.resize(width, height, true);
  }
  
  @Override
  public void resume() {
    
  }

  @Override
  public void onSceneDidLoad(Scene newScene, Vector2 spawnPosition) {
    Gdx.app.log(TAG, "Mounting scene");
    synchronized (newScene) {
      this.scene = newScene;
    }
    
    this.terrain = scene.getTerrain();
    this.camera.position.set(spawnPosition.x, 12, spawnPosition.y);
    this.camera.lookAt(spawnPosition.x, 0, spawnPosition.y-4);
    scene.setCamera(camera);
    
    this.scene.initialize();
    this.camera.update();
    this.scene.spawnOrMovePlayer(spawnPosition);
    this.sceneLoadingThread = null;
  }

  @Override
  public void onSceneLoadError(Exception e) {
    Gdx.app.error(TAG, "onSceneLoadError", e);
  }

  public Scene getScene() {
    return scene;
  }

  public PerspectiveCamera get3DCamera() {
    return this.camera;
  }
}
