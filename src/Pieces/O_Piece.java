package Pieces;

import org.newdawn.slick.SlickException;

public class O_Piece extends BasicPiece {

    public O_Piece(boolean isEndLoc) throws SlickException {
        super(4);
        structure[0] = new Block(getCurrentColor(isEndLoc), 5, -1);
        structure[1] = new Block(getCurrentColor(isEndLoc), 6, -1);
        structure[2] = new Block(getCurrentColor(isEndLoc), 5, 0);
        structure[3] = new Block(getCurrentColor(isEndLoc), 6, 0);
        canRotate = false;


    }

    @Override
    public String getCurrentColor(boolean isEndLoc) throws SlickException{

        if(isEndLoc) return "images/EndLoc_Block.png";
        return  "images/green.png";
    }
}