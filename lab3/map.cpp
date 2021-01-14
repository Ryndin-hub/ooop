#include "map.h"
#include <fstream>

Cell randomCell(){
    int a = (rand())%10 + 1;
    if (a > 4) return Empty;
    else return (Cell)a;
}

Map::Map() {
    grid = new Cell*[w];
    for(int i = 0; i < w; i++){
        grid[i] = new Cell[h];
    }
    for (int i = 0; i < w; i++){
        for (int j = 0; j < h; j++){
            grid[i][j] = randomCell();
        }
    }
}

Map::Map(char * path) {
    std::ifstream input(path);
    input >> w;
    input >> h;
    grid = new Cell*[w];
    for(int i = 0; i < w; i++){
        grid[i] = new Cell[h];
    }
    for (int i = 0; i < w; i++){
        for (int j = 0; j < h; j++){
            int tmp;
            input >> tmp;
            grid[i][j] = (Cell) tmp;
        }
    }
    input.close();
}

Map::~Map() {
    for (int i = 0; i < w; i++){
        delete [] grid[i];
    }
    delete [] grid;
}

std::ostream &operator<<(std::ostream &os, const Map &map) {
    for (int i = 0; i < map.h; i++){
        for (int j = 0; j < map.w; j++){
            switch (map.grid[i][j]){
                case Empty:
                    std::cout << ' ';
                    break;
                case Rock:
                    std::cout << '^';
                    break;
                case Bomb:
                    std::cout << 'B';
                    break;
                case Apple:
                    std::cout << 'A';
                    break;
                case Border:
                    std::cout << ' ';
                    break;
            }
            std::cout << ' ';
        }
        std::cout << "\n";
    }
    std::cout << "\n";
    return os;
}
