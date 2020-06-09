package Sates;


import Main.Main;
import Drawables.Pieces.*;
import Drawables.PieceHolder;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import Drawables.*;

import java.util.HashMap;
import java.util.Random;

public class Game  extends BasicGameState  {
    public static final int ID = 1;
    private StateBasedGame game;
    private Grid grid;
    BasicPiece piece;
    BasicPiece endLocation;
    PieceHolder heldPiece;
    PieceHolder nextPiece;
    int timeSinceUpdate;
    int timeBetweenUpdate;
    Block[] pieceStructure;
    boolean canHold;
    Image background;
    int gameOverTimer;
    boolean isGameOver;
    HashMap<Integer, Float> keysHeld;
    float alphaChange;
    float valueChange;

    int top;
    Image foreground;

    @Override
    public int getID() {
        return ID;
    }
    /**
     * Init method is called once
     * @param container
     * @param game
     */
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        alphaChange = 0;
        valueChange = 1;
        isGameOver = false;
        canHold = true;

        grid = new Grid(Main.getScreenWidth()/2-(Main.getScreenWidth()*(50f/1920f))*5,  Main.getScreenHeight()* 10f/1080f, Main.getScreenWidth()*(50f/1920f), (Main.getScreenHeight() *50f)/1080f);
        grid.setTop(top);
        background = new Image("res/images/Background2.png");
        foreground = new Image(background.getResourceReference());
        foreground.setImageColor(1, 1, 1, 0);
        heldPiece = new PieceHolder(grid.startX - (3.5f*grid.blockWidth)-5, grid.startY + grid.blockHeight,   0.7f* grid.blockWidth, 0.7f*grid.blockHeight, "Hold");
        nextPiece = new PieceHolder(grid.startX+ grid.blockWidth*9+5, grid.startY + grid.blockHeight,    0.7f*grid.blockWidth, 0.7f*grid.blockHeight,  "Next");
        try {
            randomizePiece();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        keysHeld  = new HashMap<>();
    }
    /**
     * Render method is periodically called to render graphcics
     * @param container
     * @param game
     * @param g This is one way of rendering graphics
     */
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        background.draw(0, 0, 1920, 1080);

        grid.drawBackground(g);
        piece.draw(grid.startX, grid.startY, grid.blockWidth, grid.blockHeight);
        endLocation.draw(grid.startX, grid.startY, grid.blockWidth, grid.blockHeight);
        grid.draw(g);
        heldPiece.drawBackground(g);
        nextPiece.drawBackground(g);

        if(heldPiece.piece != null){
            heldPiece.draw(g);
        }
        nextPiece.draw(g);

        grid.drawStats(g);

        foreground.draw(0, 0, Main.getScreenWidth(), Main.getScreenHeight());

    }
    /**
     * Update method is periodically called
     * @param container
     * @param game
     * @param delta This is used for getting the amount of ms passed since this method has been called
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if(isGameOver){
            gameOverTimer += delta;
            goToGameOver();
        }else {
            keyHeld(delta);

            timeBetweenUpdate = (int) ((grid.tpl[grid.level] / 60.0) * 1000);
            pieceStructure = piece.getStructure();
            while (endLocation.moveDown(grid)) ;
            timeSinceUpdate += delta;
            if (timeSinceUpdate >= timeBetweenUpdate) {
                if (!piece.moveDown(grid)) {
                    placePiece();
                }
                timeSinceUpdate = 0;
            }
        }
    }

    public void keyHeld(int delta){
        boolean resetTime = false;
        keysHeld.replaceAll((key, time) -> Float.valueOf(time + delta));
        try {
            //Press S to move piece down
            if (keysHeld.containsKey(Input.KEY_S) && keysHeld.get(Input.KEY_S) > 75 ) {

                resetTime = piece.moveDown(grid);
                keysHeld.replace(Input.KEY_S, 0f);

            }else if (keysHeld.containsKey(Input.KEY_D) && keysHeld.get(Input.KEY_D) > 75) {

                piece.moveSideways(grid,1);
                endLocation.moveUp(grid, (int)(endLocation.getCenterY() - piece.getCenterY()));
                endLocation.moveSideways(grid, 1);
                keysHeld.replace(Input.KEY_D, 0f);

            }else if (keysHeld.containsKey(Input.KEY_A) && keysHeld.get(Input.KEY_A) > 75 ) {
                piece.moveSideways(grid, -1);
                endLocation.moveUp(grid, (int) (endLocation.getCenterY() - piece.getCenterY()));
                endLocation.moveSideways(grid, -1);
                keysHeld.replace(Input.KEY_A, 0f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(resetTime)
            timeSinceUpdate = 0;

    }
    @Override
    public void keyReleased(int key, char c) {
        keysHeld.remove(key);
    }
    /**
     * Keyboard input detection method is periodically
     * @param key This is the specific keyboard key that is pressed in numerical form
     * @param c this is specific keyboard key that is pressed in char form
     */

    @Override
    public void keyPressed(int key, char c) {
        keysHeld.put(key, -250f);
        boolean resetTime = false;
        try {
            //Press S to move piece down
            if (key == Input.KEY_S) {

                resetTime = piece.moveDown(grid);

            }else if (key == Input.KEY_D) {

                piece.moveSideways(grid,1);
                endLocation.moveUp(grid, (int)(endLocation.getCenterY() - piece.getCenterY()));
                endLocation.moveSideways(grid, 1);

            }else if (key == Input.KEY_A) {
                piece.moveSideways(grid, -1);
                endLocation.moveUp(grid, (int)(endLocation.getCenterY() - piece.getCenterY()));
                endLocation.moveSideways(grid, -1);
            }else if (key == Input.KEY_W) {
                resetTime = piece.rotate(grid);
                endLocation.moveUp(grid, (int)(endLocation.getCenterY() - piece.getCenterY()));
                endLocation.rotate(grid);
            }else if(key == Input.KEY_SPACE){
                piece.moveUp(grid, (int)(piece.getCenterY() - endLocation.getCenterY()));
                timeSinceUpdate = timeBetweenUpdate;
            }else if(key == Input.KEY_E && canHold){
                if(heldPiece.piece == null){
                    heldPiece.piece = piece;
                    randomizePiece();
                }else{
                    BasicPiece copy = heldPiece.piece;
                    heldPiece.piece = (BasicPiece)piece.clone();
                    piece = copy;
                    endLocation = (BasicPiece)piece.clone();
                    endLocation.init(true);
                    piece.init(false);

                }
                heldPiece.piece.init(false);
                heldPiece.piece.moveDown(grid);
                heldPiece.piece.moveDown(grid);


                canHold = false;
            }

        }  catch (Exception e) {
            e.printStackTrace();
        }
        if(resetTime)
            timeSinceUpdate = 0;
        super.keyPressed(key, c);
    }

    /**
     * Method to randomize the current piece being moved.
     * @throws SlickException
     */
    public void randomizePiece() throws SlickException, CloneNotSupportedException {
        Random rand1 = new Random();
        Random rand2 = new Random();
        int selection1 = rand1.nextInt(7);
        int selection2 = rand2.nextInt(7);
        if(selection1 == selection2){
            if(selection1 != 0){
                selection1--;
            }else{
                selection1++;
            }
        }
        BasicPiece[] pieceType = {new L_Piece(), new J_Piece(), new I_Piece(),
                new T_Piece(), new O_Piece(), new S_Piece(), new Z_Piece()};
        if(nextPiece.piece != null){
                piece = (BasicPiece)nextPiece.piece.clone();

        }else{
            piece = pieceType[selection1];
        }
        nextPiece.piece = pieceType[selection2];

        endLocation = (BasicPiece)piece.clone();

        nextPiece.piece.init(false);
        endLocation.init(true);
        piece.init(false);
        canHold = true;
        nextPiece.piece.moveDown(grid);
        nextPiece.piece.moveDown(grid);

    }
    /**
     * Method to place the current piece
     */
    public void placePiece(){
        //Puts piece into grid array
        for (Block block : pieceStructure) {
            try {
                grid.putBlockToGrid(block);
            }catch(java.lang.ArrayIndexOutOfBoundsException e){
                isGameOver = true;
            }
        }
        //check if there are any line clears
        grid.checkLines();

        //randomize piece
        try {
            randomizePiece();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goToGameOver(){
        if(gameOverTimer > 10) {
            foreground.setImageColor(valueChange, valueChange, valueChange, alphaChange);
            valueChange -=0.0125f;
            alphaChange += 0.02f;
            gameOverTimer = 0;
        }
        if( valueChange <= 0.2f)
            game.enterState(4);
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        if(top < grid.getTop()){
            top = grid.getTop();
        }
        init(container, game);



        super.leave(container, game);


    }
}