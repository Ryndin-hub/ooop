#ifndef LAB2_GAMEOFLIFE_H
#define LAB2_GAMEOFLIFE_H

#include <iostream>
#include <fstream>

class Cell{
private:
    bool state = 0;
    bool previousState = 0;
    Cell *neighbors[8] = {nullptr,nullptr,nullptr,nullptr,nullptr,nullptr,nullptr,nullptr};
    int calculateNumberOfAliveNeighbors() const;
public:
    void setNeighbors(Cell *ptr[8]);
    void update1();
    void update2();
    void draw() const;
    void setState(bool a);
    void swapState();
    void save(std::ofstream &file) const;
    void load(std::ifstream &file);
};

class GameOfLife {
private:
    int w = 10;
    int h = 10;
    int numberOfMoves = 0;
    Cell cellGrid[10][10];
public:
    GameOfLife();
    void draw() const;
    void update();
    void set(int a, int b);
    void clear(int a, int b);
    void back();
    void reset();
    void save(const std::string& path) const;
    void load(const std::string& path);
};

class GameHandler {
private:
    GameOfLife game;
    int gameStatus = 0;
public:
    void runGame();
};

#endif //LAB2_GAMEOFLIFE_H
