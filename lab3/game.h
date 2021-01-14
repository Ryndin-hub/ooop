#ifndef LAB3_GAME_H
#define LAB3_GAME_H

#include "robot.h"
#include "Mode.h"
#include "map.h"
#include <list>

class Game {
private:
    Map *map;
    std::vector<Collector*> collectors;
    std::vector<Defuser*> defusers;
    std::vector<ManualMode*> manual_modes;
    std::vector<AutoScan*> auto_scans;
    std::vector<AutoCollect*> auto_collects;
    std::vector<AutoDefuse*> auto_defuses;
    int currentRobot = -1;
    int lastRobot = -1;
    std::vector<std::pair<Robot*,Mode*>> aliveRobots;
public:
    Game() {map = new Map();}
    Game(char* path) {map = new Map(path);}
    void run();
    ~Game();
};

#endif //LAB3_GAME_H
