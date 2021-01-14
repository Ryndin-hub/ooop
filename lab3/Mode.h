#ifndef LAB3_MODE_H
#define LAB3_MODE_H

#include "robot.h"
#include <vector>

class Mode {
protected:
    Robot *robot;
public:
    int cooldown = 0;
    virtual bool makeMove() {return 1;}
    Mode(Robot *_robot) : robot(_robot) {};
};

class Aclass {
public:
    int x;
    int y;
    std::pair<int,int> len;
    std::pair<int,int> prev;
    int state;
};

class AutoMode : public Mode {
protected:
    virtual bool filter(int, int) const {return 0;};
    bool findTarget(const std::vector<std::pair<int,int>>*);
    std::pair<int,int> Astar(const Cell*, int);
    Aclass *searchedCells = new Aclass[10];
    int maxSize = 10;
    ~AutoMode() {delete [] searchedCells;}
public:
    using Mode::Mode;
};

class ManualMode : public Mode {
public:
    using Mode::Mode;
};

class AutoScan : public AutoMode{
private:
    bool filter(int, int) const override;
    const int blacklistSize = 4;
    const Cell blacklist[4] = {Null,Rock,Bomb,Border};
public:
    bool makeMove() override;
    using AutoMode::AutoMode;
};

class AutoCollect : public AutoMode{
private:
    bool filter(int, int) const override;
    const int blacklistSize = 4;
    const Cell blacklist[4] = {Null,Rock,Bomb,Border};
public:
    bool makeMove() override;
    using AutoMode::AutoMode;
};

class AutoDefuse : public AutoMode{
private:
    bool filter(int, int) const override;
    const int blacklistSize = 3;
    const Cell blacklist[4] = {Null,Rock,Border};
public:
    bool makeMove() override;
    using AutoMode::AutoMode;
};

#endif //LAB3_MODE_H
