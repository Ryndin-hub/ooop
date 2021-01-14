#ifndef LAB3_ROBOT_H
#define LAB3_ROBOT_H

#include "map.h"
#include "vector"

class Map;

enum Direction{
    Up,Right,Down,Left
};

class Robot {
protected:
    void notifyAll(int,int,Cell);
    int canDefuse = 0;
    int absoluteX;
    int absoluteY;
    Map *map;
    void increaseMapSize(Direction);
public:
    std::vector<Robot*> otherRobots;
    std::pair<int,int> getOffset(const Robot*) const;
    int targetX = -1;
    int targetY = -1;
    int x = 10;
    int y = 10;
    int maxX = 20;
    int maxY = 20;
    Cell **scannedCells;
    Robot(Map*);
    ~Robot();
    int grabbedApples = 0;
public:
    void move(Direction);
    virtual void scan() {};
    virtual void grab() {};
    virtual void defuse() {};
    friend std::ostream& operator<<(std::ostream& os, const Robot& robot);
};

class Collector : public Robot {
public:
    using Robot::Robot;
    void scan() override;
    void grab() override;
};

class Defuser : public Robot {
public:
    Defuser(Map *_map) : Robot(_map) {canDefuse = 1;}
    using Robot::Robot;
    void defuse() override;
};

#endif //LAB3_ROBOT_H
