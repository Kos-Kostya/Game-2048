package com.javarush.task.task35.task3513;

import java.util.*;


public class Model {
    //содержит игровую логику и хранить игровое поле.
    //ответственен за все манипуляции производимые с игровым полем.

    private static final int FIELD_WIDTH = 4;
    protected int score; //хранит текущий счет
    protected int maxTile; //хранит максимальный вес плитки

    private Stack<Tile[][]> previousStates = new Stack<>();//стэк для хранения предыдущего состояния поля(для отмены хода)

    private Stack<Integer> previousScores = new Stack<>();//стэк хранения предыдущего значения счета(Score)

    private boolean isSaveNeeded = true;

    private Tile[][] gameTiles;

    public Model() {
        this.score = 0;
        this.maxTile = 0;
        resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        //возвращающий true в случае, если в текущей позиции
        //возможно сделать ход так, чтобы состояние игрового поля изменилось. Иначе - false.

        Tile[][] move = gameTiles;

        //проверям что в массиве есть нули
        if (getEmptyTiles().size() != 0) return true;

        //проверяем что ячейки можно сoеденить по горизонтали и вертикали
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++){

                Tile tile = move[i][j];
                if ((i + 1) != FIELD_WIDTH && tile.value == move[i+1][j].value || //по вертикали
                        (j + 1) != FIELD_WIDTH && tile.value == move[i][j+1].value) { //по горизонтали
                    return true;
                }
            }

        }
        return false;
    }

    void resetGameTiles(){
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i < FIELD_WIDTH; i++){
            for (int j = 0; j < FIELD_WIDTH; j++){
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private List<Tile> getEmptyTiles(){
        //должен возвращать список пустых плиток в массиве gameTiles
        List<Tile> spisokPustih = new ArrayList<>();

        for(Tile[] tile : gameTiles){
            for(Tile newTile : tile){
                if(newTile.isEmpty()){
                    spisokPustih.add(newTile);
                }
            }
        }

        return spisokPustih;
    }

    private void addTile(){
        //смотрет какие плитки пустуют и, если такие имеются,
        //меняет вес одной из них, выбранной случайным образом, на 2 или 4

                    //Список с пустыми ячейками
                    List<Tile> pustie = getEmptyTiles();
                    if(pustie.size() != 0) {
                        //получаем индекс случайной ячейки из списка.
                        int index = (int) (Math.random() * pustie.size());
                        //Получаем случайный объект из списка использовав следующее выражение:
                        Tile tile = pustie.get(index);

                        //Передаем в пустые ячейки  вес одной из них, выбранной случайным
                        //образом, на 2 или 4 (на 9 двоек должна приходиться 1 четверка).
                        tile.value = Math.random() < 0.9 ? 2 : 4;
                    }
    }

    private boolean compressTiles(Tile[] tiles){
        //Сжатие плиток, таким образом, чтобы все пустые плитки были справа,
        // т.е. ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}

        boolean changes = false;//возвращает true если вносились изменения
                               //перемещаем нулевые значения в конец

        int insertPosition = 0;
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    changes = true;
                }
                insertPosition++;
            }
        }
        return changes;
    }



    private boolean mergeTiles(Tile[] tiles){
        //Слияние плиток одного номинала, т.е. ряд {4, 4, 2, 0} становится рядом {8, 2, 0, 0}.
        //ряд {4, 4, 4, 4} превратится в {8, 8, 0, 0}, а {4, 4, 4, 0} в {8, 4, 0, 0}.

        boolean changes = false;//возвращает true если вносились изменения

        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].isEmpty()) {
                continue;
            }

            if (i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value) {
                int updatedValue = tiles[i].value * 2;
                if (updatedValue > maxTile) {
                    maxTile = updatedValue;
                }
                score += updatedValue;
                tilesList.addLast(new Tile(updatedValue));
                tiles[i + 1].value = 0;
                changes = true;
            } else {
                tilesList.addLast(new Tile(tiles[i].value));
            }
            tiles[i].value = 0;
        }

        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }

        return changes;
    }

    public void left(){
        if(isSaveNeeded) {
            //сохраняем текущее игровое поле
            saveState(gameTiles);
        }

        //для каждой строки массива gameTiles вызывает методы compressTiles и mergeTiles
        //добавлять одну плитку с помощью метода addTile в том случае, если это необходимо.

        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
            isSaveNeeded = true;
        }
    }

    private Tile[][] turn(Tile[][] tile){
        final int M = tile.length;
        final int N = tile[0].length;
        Tile[][] newArray = new Tile[M][N];

        for(int i = 0; i < M; i++){
            for(int j = N - 1; j >= 0; j--){
                newArray[j][N - 1 -i] = gameTiles[i][j];
            }
        }
        return newArray;
    }

    public void up(){
        //сохраняем текущее игровое поле
        saveState(gameTiles);

        //на 180 градусов по часовой стрелке, сдвинуть влево

        gameTiles = turn(gameTiles);
        gameTiles = turn(gameTiles);
        gameTiles = turn(gameTiles);
        left();
        gameTiles = turn(gameTiles);
    }

    public void right(){
        //сохраняем текущее игровое поле
        saveState(gameTiles);

        //Поворот массива на 90 градусов

        gameTiles = turn(gameTiles);
        gameTiles = turn(gameTiles);
        left();
        gameTiles = turn(gameTiles);
        gameTiles = turn(gameTiles);
    }

    public void down(){
        //сохраняем текущее игровое поле
        saveState(gameTiles);

        //Поворот массива на 270 градусов

        gameTiles = turn(gameTiles);
        left();
        gameTiles = turn(gameTiles);
        gameTiles = turn(gameTiles);
        gameTiles = turn(gameTiles);
    }

    private void saveState(Tile[][] tiles){
        //сохраняет текущее игровое состояние и счет в стеки с помощью метода
        //push и устанавливать флаг isSaveNeeded равным false.

        //перед сохранение в стек, создаем новый массив
        Tile[][] tile = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i < FIELD_WIDTH; i++){
            for(int j = 0; j < FIELD_WIDTH; j++){
                tile[i][j] = new Tile(tiles[i][j].value);
            }
        }
        
        previousStates.push(tile);
        isSaveNeeded = false;

        //сохраняет в стек текущий счет
        previousScores.push(score);

    }

    public void rollback(){
        //возвращает предыдущее состояние игры если оба стека не пусты

        if(!previousStates.empty() && !previousScores.empty()) {
            //возвращаем предыдущие значения
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    void randomMove(){
        //делает рандомный ход
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n){
            case 0: left();
                 break;
            case 1: right();
                break;
            case 2: up();
                break;
            case 3: down();
                break;
        }
    }

    boolean hasBoardChanged(){
        //возвращать true, в случае, если вес плиток в массиве gameTiles
        //отличается от веса плиток в верхнем массиве стека previousStates.

        for(int i = 0; i < FIELD_WIDTH; i++){
            for(int j = 0; j < FIELD_WIDTH; j++){
                if(gameTiles[i][j].value != previousStates.peek()[i][j].value) return true;
            }
        }

        return false;
    }

    MoveEfficiency getMoveEfficiency(Move move){
        //принимает один параметр типа move, и возвращает объект типа
        //MoveEfficiency описывающий эффективность переданного хода
        MoveEfficiency moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        move.move();

        //в случае, если ход не меняет состояние игрового поля,
        //количество пустых плиток и счет у объекта MoveEfficiency сделай равными -1 и 0 соответственно
        if(!hasBoardChanged()){
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }
        //вызывать метод rollback, чтобы восстановить корректное игровое состояние;
        rollback();
        return moveEfficiency;
    }

    public void autoMove(){
        //выбирает лучший из возможных ходов и выполнять его

        //Создает объект PriorityQueue с указанной начальной емкостью,
        //который упорядочивает свои элементы в соответствии с их естественным порядком
        //с параметром Collections.reverseOrder(), для того, чтобы вверху очереди всегда был максимальный элемент.
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<MoveEfficiency>(4, Collections.reverseOrder());

        //Заполним PriorityQueue четырьмя объектами типа MoveEfficiency (по одному на каждый вариант хода).
        priorityQueue.offer(getMoveEfficiency(new Move() {
            @Override
            public void move() {
                left();
            }
        }));

        priorityQueue.offer(getMoveEfficiency(new Move() {
            @Override
            public void move() {
                left();
            }
        }));

        priorityQueue.offer(getMoveEfficiency(new Move() {
            @Override
            public void move() {
                up();
            }
        }));

        priorityQueue.offer(getMoveEfficiency(new Move() {
            @Override
            public void move() {
                down();
            }
        }));

        MoveEfficiency moveEfficiency = priorityQueue.poll();
        moveEfficiency.getMove().move();
    }
}


