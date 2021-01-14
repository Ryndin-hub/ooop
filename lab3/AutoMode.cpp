#include "Mode.h"

bool AutoMode::findTarget(const std::vector<std::pair<int,int>> *checkedCells){
    int radius = 0;
    while(((robot->x + radius >= 0) && (robot->x + radius < robot->maxX)) || ((robot->y + radius >= 0) && (robot->y + radius < robot->maxY)) ||
            ((robot->x - radius >= 0) && (robot->x - radius < robot->maxX)) || ((robot->y - radius >= 0) && (robot->y - radius < robot->maxY))) {
        for (int i = -radius; i <= radius; i++) {
            for (auto & it : robot->otherRobots) {
                std::pair<int,int> offset = robot->getOffset(it);
                if (i == it->targetX + offset.first && radius - abs(i) == it->targetY + offset.second) goto break_loop;
                if (i == it->targetX + offset.first && -radius + abs(i) == it->targetY + offset.second) goto break_loop;
            }
            for (auto & ite : *checkedCells) {
                if (i + robot->x == ite.first && radius - abs(i) + robot->y == ite.second) goto break_loop;
                if (i + robot->x == ite.first && -radius + abs(i) + robot->y == ite.second) goto break_loop;
            }
            for (auto & it : robot->otherRobots) {
                if (i + robot->x == it->x && radius - abs(i) + robot->y == it->y) goto break_loop;
                if (i + robot->x == it->x && -radius + abs(i) + robot->y == it->y) goto break_loop;
            }
            if ((i + robot->x >= 0) && (i + robot->x < robot->maxX) &&
                (radius - abs(i) + robot->y >= 0) && (radius - abs(i) + robot->y < robot->maxY)) {
                if (filter(i + robot->x,radius - abs(i) + robot->y)) {
                    robot->targetX = i + robot->x;
                    robot->targetY = radius - abs(i) + robot->y;
                    return 0;
                }
            }
            if ((i + robot->x >= 0) && (i + robot->x < robot->maxX) &&
                (-radius + abs(i) + robot->y >= 0) && (-radius + abs(i) + robot->y < robot->maxY)) {
                if (filter(i + robot->x,-radius + abs(i) + robot->y)) {
                    robot->targetX = i + robot->x;
                    robot->targetY = -radius + abs(i) + robot->y;
                    return 0;
                }
            }
            break_loop:;
        }
        radius++;
    }
    return 1;
}

std::pair<int,int> AutoMode::Astar(const Cell *blacklist, const int blacklistSize){
    if (maxSize <= robot->maxX*robot->maxY) {
        delete [] searchedCells;
        searchedCells = new Aclass[2*robot->maxX*robot->maxY];
        maxSize = 2*robot->maxX*robot->maxY;
    }
    int searchedCellsSize = 0;
    int currentX = robot->x;
    int currentY = robot->y;
    int currentCell = 0;
    int newCell = 0;
    searchedCells[searchedCellsSize].x = currentX;
    searchedCells[searchedCellsSize].y = currentY;
    searchedCells[searchedCellsSize].state = 2;
    searchedCells[searchedCellsSize].len.first = 0;
    searchedCellsSize++;
    std::pair<int,int> oRobots[robot->otherRobots.size()];
    int robotsSize = 0;
    for (auto & it : robot->otherRobots) {
        oRobots[robotsSize] = std::make_pair(it->x,it->y);
        robotsSize++;
    }
    while(currentX != robot->targetX || currentY != robot->targetY){
        for (int i = 0; i < 4; i++) {
            int x = 0; int y = 0;
            if (i == 0){x = currentX; y = currentY + 1;}
            if (i == 1){x = currentX + 1; y = currentY;}
            if (i == 2){x = currentX; y = currentY - 1;}
            if (i == 3){x = currentX - 1; y = currentY;}
            if (x < 0 || x >= robot->maxX || y < 0 || y >= robot->maxY) {
                continue;
            }
            for (int j = 0; j < searchedCellsSize; j++){
                if (searchedCells[j].x == x && searchedCells[j].y == y){
                    newCell = j;
                    goto break_loop;
                }
            }
            searchedCells[searchedCellsSize].x = x;
            searchedCells[searchedCellsSize].y = y;
            searchedCells[searchedCellsSize].state = 0;
            newCell = searchedCellsSize;
            searchedCellsSize++;
            break_loop:;
            if (searchedCells[newCell].state == 2) continue;
            for (int j = 0; j < blacklistSize; j++){
                if (robot->scannedCells[x][y] == blacklist[j]){
                    searchedCells[newCell].state = 2;
                    continue;
                }
            }
            for (int j = 0; j < robotsSize; j++){
                if (x == oRobots[j].first && y == oRobots[j].second){
                    searchedCells[newCell].state = 2;
                    continue;
                }
            }
            if (searchedCells[newCell].state == 0){
                searchedCells[newCell].len.first = searchedCells[currentCell].len.first + 1;
                searchedCells[newCell].len.second = abs(x - robot->targetX) + abs(y - robot->targetY);
                searchedCells[newCell].prev.first = currentX;
                searchedCells[newCell].prev.second = currentY;
                searchedCells[newCell].state = 1;
                continue;
            }
            if (searchedCells[newCell].len.first > searchedCells[currentCell].len.first + 1) {
                searchedCells[newCell].len.first = searchedCells[currentCell].len.first + 1;
                searchedCells[newCell].prev.first = currentX;
                searchedCells[newCell].prev.second = currentY;
            }
        }
        int minLen = INT_MAX;
        int minX = -1;
        int minY = -1;
        for (int i = 0; i < searchedCellsSize; i++){
            if (searchedCells[i].state == 1){
                if (searchedCells[i].len.first + searchedCells[i].len.second < minLen){
                    minLen = searchedCells[i].len.first + searchedCells[i].len.second;
                    minX = searchedCells[i].x;
                    minY = searchedCells[i].y;
                }
            }
        }
        currentX = minX;
        currentY = minY;
        if (currentX == -1 && currentY == -1) return std::make_pair(-1,-1);
        if (searchedCellsSize > 1000) return std::make_pair(-1,-1);
        for (int i = 0; i < searchedCellsSize; i++){
            if (searchedCells[i].x == currentX && searchedCells[i].y == currentY){
                currentCell = i;
                break;
            }
        }
        searchedCells[currentCell].state = 2;
    }
    while(searchedCells[currentCell].prev.first != robot->x || searchedCells[currentCell].prev.second != robot->y){
        currentX = searchedCells[currentCell].prev.first;
        currentY = searchedCells[currentCell].prev.second;
        for (int i = 0; i < searchedCellsSize; i++){
            if (searchedCells[i].x == currentX && searchedCells[i].y == currentY){
                currentCell = i;
                break;
            }
        }
    }
    return std::make_pair(currentX,currentY);
}
