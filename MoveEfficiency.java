package com.javarush.task.task35.task3513;

public class MoveEfficiency implements  Comparable<MoveEfficiency>{

    private int numberOfEmptyTiles; //количество пустых плиток
    private int score; //счет
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        //сравни количество пустых плиток (numberOfEmptyTiles), потом счет (score),
        //если количество пустых плиток равное. Если и счет окажется равным, будем считать
        //эффективность ходов равной и вернем ноль.

        if(numberOfEmptyTiles > o.numberOfEmptyTiles){
            return 1;
        }else if(numberOfEmptyTiles < o.numberOfEmptyTiles){
            return -1;
        }else{
            if(score > o.score) {
                return 1;
            }else if(score < o.score){
                return -1;
            }
        }

        return 0;
    }
}
