#include "robot.h"
#include <chrono>
#include <thread>

void Robot::increaseMapSize(Direction d) {
    Cell **newMap;
    switch (d) {
        case Up:
            newMap = new Cell *[maxX];
            for (int i = 0; i < maxX; i++) {
                newMap[i] = new Cell[maxY + 100];
            }
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    newMap[i][j] = scannedCells[i][j];
                }
            }
            for (int i = 0; i < maxX; i++) {
                for (int j = maxY; j < maxY + 100; j++) {
                    newMap[i][j] = Null;
                }
            }
            for (int i = 0; i < maxX; i++) {
                delete[] scannedCells[i];
            }
            delete[] scannedCells;
            maxY += 100;
            break;
        case Right:
            newMap = new Cell *[maxX + 100];
            for (int i = 0; i < maxX + 100; i++) {
                newMap[i] = new Cell[maxY];
            }
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    newMap[i][j] = scannedCells[i][j];
                }
            }
            for (int i = maxX; i < maxX + 100; i++) {
                for (int j = 0; j < maxY; j++) {
                    newMap[i][j] = Null;
                }
            }
            for (int i = 0; i < maxX; i++) {
                delete[] scannedCells[i];
            }
            delete[] scannedCells;
            maxX += 100;
            break;
        case Down:
            newMap = new Cell *[maxX];
            for (int i = 0; i < maxX; i++) {
                newMap[i] = new Cell[maxY + 100];
            }
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    newMap[i][j + 100] = scannedCells[i][j];
                }
            }
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < 100; j++) {
                    newMap[i][j] = Null;
                }
            }
            for (int i = 0; i < maxX; i++) {
                delete[] scannedCells[i];
            }
            delete[] scannedCells;
            y += 100;
            maxY += 100;
            break;
        case Left:
            newMap = new Cell *[maxX + 100];
            for (int i = 0; i < maxX + 100; i++) {
                newMap[i] = new Cell[maxY];
            }
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    newMap[i + 100][j] = scannedCells[i][j];
                }
            }
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < maxY; j++) {
                    newMap[i][j] = Null;
                }
            }
            for (int i = 0; i < maxX; i++) {
                delete[] scannedCells[i];
            }
            delete[] scannedCells;
            x += 100;
            maxX += 100;
            break;
    }
    scannedCells = newMap;
}

void Robot::move(Direction d) {
    int nextX = absoluteX;
    int nextY = absoluteY;
    switch (d){
        case Up:
            nextX = absoluteX;
            nextY = absoluteY + 1;
            if (y+1 >= maxY) increaseMapSize(d);
            break;
        case Right:
            nextX = absoluteX + 1;
            nextY = absoluteY;
            if (x+1 >= maxX) increaseMapSize(d);
            break;
        case Down:
            nextX = absoluteX;
            nextY = absoluteY - 1;
            if (y-1 < 0) increaseMapSize(d);
            break;
        case Left:
            nextX = absoluteX - 1;
            nextY = absoluteY;
            if (x-1 < 0) increaseMapSize(d);
            break;
    }
    if (nextX > map->w || nextX < 0 || nextY > map->h || nextY < 0) return;
    switch (map->grid[nextX][nextY]){
        case Empty:
            x += nextX - absoluteX;
            y += nextY - absoluteY;
            absoluteX = nextX;
            absoluteY = nextY;
            break;
        case Rock:
            break;
        case Bomb:
            if (canDefuse == 1) {
                x += nextX - absoluteX;
                y += nextY - absoluteY;
                absoluteX = nextX;
                absoluteY = nextY;
            }
            break;
        case Apple:
            x += nextX - absoluteX;
            y += nextY - absoluteY;
            absoluteX = nextX;
            absoluteY = nextY;
            break;
    }
}

std::ostream &operator<<(std::ostream &os, const Robot &robot) {
    int radius = 10;
    for (int i = robot.y + radius; i >= robot.y - radius; i--){
        for (int j = robot.x - radius; j <= robot.x + radius; j++){
            for (auto & it : robot.otherRobots) {
                if ((j == it->x) && (i == it->y)){
                    std::cout << 'R';
                    goto break_loop;
                }
            }
            if ((j == robot.x) && (i == robot.y)) {
                std::cout << 'R';
            } else if ((i < 0) || (j < 0) || (i >= robot.maxY) || (j >= robot.maxX)){
                std::cout << '?';
            } else {
            switch (robot.scannedCells[j][i]){
                case Empty:
                    std::cout << ' ';
                    break;
                case Rock:
                    std::cout << '^';
                    break;
                case Bomb:
                    std::cout << '*';
                    break;
                case Apple:
                    std::cout << '@';
                    break;
                case Border:
                    std::cout << '/';
                    break;
                case Null:
                    std::cout << '?';
                    break;
            }
            }
            break_loop:;
            std::cout << " ";
        }
        std::cout << "\n";
    }
    std::cout << "\n";
    return os;
}

Robot::Robot(Map *_map) {
    map = _map;
    absoluteX = rand() % map->w;
    absoluteY = rand() % map->h;
    while(map->grid[absoluteX][absoluteY] != Empty) {
        absoluteX = rand() % map->w;
        absoluteY = rand() % map->h;
    }
    for (auto it : map->robots){
        otherRobots.push_back(it);
    }
    for (auto it : map->robots){
        it->otherRobots.push_back(this);
    }
    if (_map->robots.size() == 0) {
        scannedCells = new Cell *[maxX];
        for (int i = 0; i < maxX; i++) {
            scannedCells[i] = new Cell[maxY];
        }
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                scannedCells[i][j] = Null;
            }
        }
    } else {
        Robot *firstRobot = _map->robots[0];
        std::pair<int,int> offset = getOffset(firstRobot);
        x = firstRobot->x + offset.first;
        y = firstRobot->y + offset.second;
        notifyAll(0,0,Empty);
        offset = getOffset(firstRobot);
        x = firstRobot->x + offset.first;
        y = firstRobot->y + offset.second;
        maxX = firstRobot->maxX;
        maxY = firstRobot->maxY;
        scannedCells = new Cell *[firstRobot->maxX];
        for (int i = 0; i < firstRobot->maxX; i++) {
            scannedCells[i] = new Cell[firstRobot->maxY];
        }
        for (int i = 0; i < firstRobot->maxX; i++) {
            for (int j = 0; j < firstRobot->maxY; j++) {
                scannedCells[i][j] = firstRobot->scannedCells[i][j];
            }
        }
    }
    scannedCells[x][y] = Empty;
    map->robots.push_back(this);
}

Robot::~Robot(){
    for (int i = 0; i < maxX; i++){
        delete [] scannedCells[i];
    }
    delete [] scannedCells;
}

void Collector::scan() {
    if (canDefuse == 1) return;
    Cell sensorU, sensorR, sensorD, sensorL;
    if (absoluteY + 1 >= map->h) sensorU = Border;
    else sensorU = map->grid[absoluteX][absoluteY+1];
    if (absoluteX + 1 >= map->w) sensorR = Border;
    else sensorR = map->grid[absoluteX+1][absoluteY];
    if (absoluteY - 1 < 0) sensorD = Border;
    else sensorD = map->grid[absoluteX][absoluteY-1];
    if (absoluteX - 1 < 0) sensorL = Border;
    else sensorL = map->grid[absoluteX-1][absoluteY];
    if (y+1 >= maxY) increaseMapSize(Up);
    if (x+1 >= maxX) increaseMapSize(Right);
    if (y-1 < 0) increaseMapSize(Down);
    if (x-1 < 0) increaseMapSize(Left);
    notifyAll(0,1,sensorU);
    notifyAll(1,0,sensorR);
    notifyAll(0,-1,sensorD);
    notifyAll(-1,0,sensorL);
    scannedCells[x][y+1] = sensorU;
    scannedCells[x+1][y] = sensorR;
    scannedCells[x][y-1] = sensorD;
    scannedCells[x-1][y] = sensorL;
}

void Collector::grab(){
    if (canDefuse == 1) return;
    if (map->grid[absoluteX][absoluteY] == Apple){
        map->grid[absoluteX][absoluteY] = Empty;
        scannedCells[x][y] = Empty;
        notifyAll(0,0,Empty);
        grabbedApples++;
    }
}

void Defuser::defuse(){
    if (canDefuse != 1) return;
    if (map->grid[absoluteX][absoluteY] == Bomb){
        map->grid[absoluteX][absoluteY] = Empty;
        scannedCells[x][y] = Empty;
        notifyAll(0,0,Empty);
    }
}

std::pair<int,int> Robot::getOffset(const Robot *r) const {
    return std::make_pair(absoluteX - r->absoluteX, absoluteY - r->absoluteY);
}

void Robot::notifyAll(int cellX,int cellY, Cell state){
    for (auto & it : otherRobots){
        std::pair<int,int> offset = getOffset(it);
        while (it->y + offset.second + cellY >= it->maxY) it->increaseMapSize(Up);
        while (it->x + offset.first + cellX >= it->maxX) it->increaseMapSize(Right);
        while (it->y + offset.second + cellY < 0) {
            it->increaseMapSize(Down);
        }
        while (it->x + offset.first + cellX < 0) {
            it->increaseMapSize(Left);
        }
        it->scannedCells[it->x + offset.first + cellX][it->y + offset.second + cellY] = state;
    }
}

