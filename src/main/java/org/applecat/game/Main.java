package org.applecat.game;

import org.applecat.engine.GameEngine;
import org.applecat.engine.IGameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEngine = new GameEngine("GAME", 600, 480, vSync, gameLogic);
            new Thread(gameEngine).start();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
