package com.macbury.fabula.ui.ingame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.macbury.fabula.game_objects.components.TileMovementComponent;
import com.macbury.fabula.game_objects.system.PlayerSystem;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.screens.GamePlayScreen;
import com.macbury.fabula.utils.ActionTimer;
import com.macbury.fabula.utils.ActionTimer.TimerListener;

public class GamePlayUI extends Stage implements TimerListener {
  protected static final String TAG = "GamePlayUI";
  private Label statusLabel;
  private Table table;
  private Skin skin;
  private GamePlayScreen screen;
  private Button moveUpButton;
  private Button moveDownButton;
  private TextureAtlas guiAtlas;
  private Image centerTouchPad;
  private Button moveLeftButton;
  private Button moveRightButton;
  private ActionTimer debugTimer;

  public GamePlayUI(GamePlayScreen screen) {
    this.debugTimer = new ActionTimer(1.0f, this);
    this.debugTimer.start();
    this.screen   = screen;
    this.skin     = G.db.getUiSkin();
    this.guiAtlas = G.db.getAtlas("gui");
    this.table    = new Table();
    this.table.setFillParent(true);
    this.table.top().left();
    
    addActor(this.table);
    
    this.statusLabel = new Label("Loading...", skin);
    
    table.add(this.statusLabel).padLeft(10).padTop(10).top().left().colspan(4);
    table.row();
    
    table.add().colspan(3).expandY();
    table.add().expandX();
    table.row();
    
    WidgetGroup group = new WidgetGroup();
    
    this.moveUpButton      = new Button(new TextureRegionDrawable(guiAtlas.findRegion("up_button")));
    this.moveDownButton    = new Button(new TextureRegionDrawable(guiAtlas.findRegion("down_button")));
    this.moveLeftButton    = new Button(new TextureRegionDrawable(guiAtlas.findRegion("left_button")));
    this.moveRightButton   = new Button(new TextureRegionDrawable(guiAtlas.findRegion("right_button")));
    this.centerTouchPad    = new Image(new TextureRegionDrawable(guiAtlas.findRegion("center_button")));
    table.row();
    table.add(moveUpButton).colspan(3).padLeft(10).bottom().width(moveUpButton.getWidth());
    table.add().expandX();
    table.row();
    table.add(moveLeftButton).padLeft(10).right().width(moveLeftButton.getWidth());
    table.add(centerTouchPad).pad(0).width(centerTouchPad.getWidth()).height(centerTouchPad.getHeight()).center();
    table.add(moveRightButton).left().width(moveRightButton.getWidth());
    table.add().expandX();
    table.row();
    table.add(moveDownButton).padLeft(10).colspan(3).padBottom(10).top().width(moveDownButton.getWidth());
    table.add().expandX();
    table.row();
    
    this.moveUpButton.addListener(touchPadGestureListener);
    this.moveDownButton.addListener(touchPadGestureListener);
    this.moveLeftButton.addListener(touchPadGestureListener);
    this.moveRightButton.addListener(touchPadGestureListener);
    
    table.layout();
  }

  public void update(float delta) {
    debugTimer.update(delta);
    act(delta);
  }

  public void renderDebug() {
    table.debug();
    Table.drawDebug(this);
  }
  
  public void resize(float viewportWidth, float viewportHeight) {
    this.setViewport(viewportWidth, viewportHeight, true);
    table.layout();
  }

  private ActorGestureListener touchPadGestureListener = new ActorGestureListener() {
    @Override
    public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
      Scene scene = GamePlayUI.this.screen.getScene();
      
      if (scene != null) {
        PlayerSystem playerSystem = scene.getPlayerSystem();
        
        if (event.getTarget() == moveUpButton) {
          playerSystem.moveIn(TileMovementComponent.Direction.Up);
        } else if (event.getTarget() == moveDownButton) {
          playerSystem.moveIn(TileMovementComponent.Direction.Down);
        } else if (event.getTarget() == moveLeftButton) {
          playerSystem.moveIn(TileMovementComponent.Direction.Left);
        } else if (event.getTarget() == moveRightButton) {
          playerSystem.moveIn(TileMovementComponent.Direction.Right);
        }
      }
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
      Scene scene = GamePlayUI.this.screen.getScene();
      if (scene != null) {
        scene.getPlayerSystem().stopMove();
      }
    }
  };

  @Override
  public void onTimerTick(ActionTimer timer) {
    if (screen.getScene() == null) {
      this.statusLabel.setText("Loading... " + "FPS: " + Gdx.graphics.getFramesPerSecond());
    } else {
      this.statusLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + " Camera: " + screen.get3DCamera().position.y + " JAVA HEAP: " + (Gdx.app.getJavaHeap() / 1024) + " Kb NATIVE HEAP " + (Gdx.app.getNativeHeap() / 1024) + " Kb");
    }
  }
}
