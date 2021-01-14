#ifndef LAB3_MAP_H
#define LAB3_MAP_H

#include <iostream>
#include "vector"

class Robot;

enum Cell{
    Null,Empty,Rock,Bomb,Apple,Border
};

class Map {
public:
    int w = 100;
    int h = 100;
    Cell **grid;
    std::vector<Robot*> robots;
    Map();
    Map(char*);
    ~Map();
    friend std::ostream& operator<<(std::ostream& os, const Map& map);
};

#endif //LAB3_MAP_H
