package com.javarush.task.task35.task3513;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

public class Controller extends KeyAdapter {
    //Будет следить за нажатием клавиш во время игры.
    private Model model;
    private View view;
    private static int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        view = new View(this);
    }

    public Tile[][] getGameTiles(){
        
        return model.getGameTiles();
    }
    
    public int getScore(){
        return model.score;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //возможность обрабатывать пользовательский ввод
        int code = e.getKeyCode();
        //Если была нажата клавиша ESC - вызови метод resetGame (начало новой игры)
        if(code == VK_ESCAPE){
            resetGame();
        }
        //Если метод canMove модели возвращает false - установи флаг isGameLost в true.
        if(!model.canMove()){
            view.isGameLost = true;
        }
        // Если оба флага isGameLost и isGameWon равны false - обработай варианты движения:
        if(!view.isGameLost && !view.isGameWon){
            switch (code){
                case VK_LEFT: model.left(); //стелка влево ← (делает ход влево)
                    break;
                case VK_RIGHT: model.right(); // стрелка вправо → (делает ход вправо)
                    break;
                case VK_UP: model.up(); //стрелка вверх ↑ (делает ход вверх)
                    break;
                case VK_DOWN: model.down(); //стрелка вниз ↓ (делает ход вниз)
                    break;
                case VK_Z: model.rollback(); //нажатие клавиши Z (возвращает предыдущее состояние игры)
                    break;
                case VK_R: model.randomMove(); //нажатие клавиши R (делает рандомный ход)
                    break;
                case VK_A: model.autoMove(); //нажатие клавиши A (выбирает лучший из возможных ходов и выполнять его)
                    break;
            }
        }
        // Если поле maxTile у модели стало равно WINNING_TILE, установи флаг isGameWon в true.
        if(model.maxTile == WINNING_TILE){
            view.isGameWon = true;
        }
        view.repaint();
    }

    public void resetGame(){
        view.isGameWon = false;
        view.isGameLost = false;
        model.score = 0;
        model.resetGameTiles();
    }

    public View getView() {
        return view;
    }
}
