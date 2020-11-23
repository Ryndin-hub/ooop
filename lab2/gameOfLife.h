#ifndef LAB2_GAMEOFLIFE_H
#define LAB2_GAMEOFLIFE_H

#include <iostream>
#include <fstream>

class GameOfLife {
private:
    int w;
    int h;
    int numberOfMoves = 0;
    bool **grid;
    bool **prevGrid;
    int numberOfAliveNeighbors(int x, int y) const;
    void updateCell(int x, int y);
public:
    GameOfLife(int width, int height);
    ~GameOfLife();
    friend std::ostream& operator<<(std::ostream& os, const GameOfLife& game);
    void update();
    void set(int x, int y);
    void clear(int x, int y);
    void back();
    void reset();
    void save(const std::string& fileName) const;
    void load(const std::string& fileName);
};

class GameHandler {
private:
    GameOfLife game = GameOfLife(10,10);
    int gameStatus = 0;
public:
    void runGame();
};

#endif //LAB2_GAMEOFLIFE_H
