#include "Mode.h"

bool AutoScan::filter(int x, int y) const {
    if (robot->scannedCells[x][y] == Empty || robot->scannedCells[x][y] == Apple){
        if (y + 1 < robot->maxY && robot->scannedCells[x][y+1] == Null) return 1;
        if (x + 1 < robot->maxX && robot->scannedCells[x+1][y] == Null) return 1;
        if (y - 1 >= 0 && robot->scannedCells[x][y-1] == Null) return 1;
        if (x - 1 >= 0 && robot->scannedCells[x-1][y] == Null) return 1;
    }
    return 0;
}

bool AutoScan::makeMove() {
    if (cooldown > 0) {
        cooldown--;
        return 1;
    }
    std::vector<std::pair<int,int>> checkedCells;
    std::pair<int,int> nextMove = std::make_pair(-1,-1);
    while (nextMove.first == -1 && nextMove.second == -1) {
        if (robot->targetX == -1 && robot->targetY == -1){
            if (checkedCells.size() > 5){
                cooldown = 1000;
                return 1;
            }
            if (findTarget(&checkedCells) == 1) return 1;
        }
        if (robot->x == robot->targetX && robot->y == robot->targetY) {
            robot->scan();
            robot->targetX = -1;
            robot->targetY = -1;
            return 0;
        }
        nextMove = Astar(blacklist,blacklistSize);
        checkedCells.emplace_back(std::make_pair(robot->targetX,robot->targetY));
        if (nextMove.first == -1 && nextMove.second == -1) {
            robot->targetX = -1;
            robot->targetY = -1;
        }
    }
    if (nextMove.second - robot->y == 1) robot->move(Up);
    else if (nextMove.first - robot->x == 1) robot->move(Right);
    else if (nextMove.second - robot->y == -1) robot->move(Down);
    else if (nextMove.first - robot->x == -1) robot->move(Left);
    if (robot->x == robot->targetX && robot->y == robot->targetY) {
        robot->scan();
        robot->targetX = -1;
        robot->targetY = -1;
    }
    return 0;
}
