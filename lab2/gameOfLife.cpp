#include "gameOfLife.h"

int GameOfLife::numberOfAliveNeighbors(int x, int y) const{
    int sum = 0;
    sum += prevGrid[(x-1+h)%h][(y-1+w)%w];
    sum += prevGrid[x][(y-1+w)%w];
    sum += prevGrid[(x+1)%h][(y-1+w)%w];
    sum += prevGrid[(x-1+h)%h][y];
    sum += prevGrid[(x+1)%h][y];
    sum += prevGrid[(x-1+h)%h][(y+1)%w];
    sum += prevGrid[x][(y+1)%w];
    sum += prevGrid[(x+1)%h][(y+1)%w];
    return sum;
}

void GameOfLife::updateCell(int x, int y){
    if (prevGrid[x][y] == 0 && numberOfAliveNeighbors(x,y) == 3){
        grid[x][y] = 1;
    } else if (prevGrid[x][y] == 1 && numberOfAliveNeighbors(x,y) < 2){
        grid[x][y] = 0;
    } else if (prevGrid[x][y] == 1 && numberOfAliveNeighbors(x,y) > 3){
        grid[x][y] = 0;
    } else grid[x][y] = prevGrid[x][y];
}

GameOfLife::GameOfLife(int width, int height) {
    w = width;
    h = height;
    grid = new bool*[w];
    prevGrid = new bool*[w];
    for(int i = 0; i < h; i++){
        grid[i] = new bool[h];
        prevGrid[i] = new bool[h];
    }
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            grid[i][j] = 0;
        }
    }
}


GameOfLife::~GameOfLife() {
    for (int i = 0; i < h; i++){
        delete [] grid[i];
        delete [] prevGrid[i];
    }
    delete [] grid;
    delete [] prevGrid;
}

std::ostream& operator<<(std::ostream& os, const GameOfLife& game){
    for (int i = 0; i < game.h; i++){
        for (int j = 0; j < game.w; j++){
            std::cout << game.grid[i][j] << ' ';
        }
        std::cout << "\n";
    }
    std::cout << "Move number: " << game.numberOfMoves << "\n";
    return os;
}

void GameOfLife::update() {
    std::swap(prevGrid,grid);
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            updateCell(i,j);
        }
    }
    numberOfMoves++;
}

void GameOfLife::set(int x, int y) {
    grid[x][y] = 1;
}

void GameOfLife::clear(int x, int y) {
    grid[x][y] = 0;
}

void GameOfLife::back() {
    std::swap(grid,prevGrid);
}

void GameOfLife::reset() {
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            grid[i][j] = 0;
        }
    }
    numberOfMoves = 0;
}

void GameOfLife::save(const std::string& fileName) const{
    std::ofstream file(fileName + ".txt");
    file.clear();
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            file << grid[i][j] << ' ';
        }
        file << "\n";
    }
    file << std::endl;
    file.close();
}

void GameOfLife::load(const std::string& fileName){
    std::ifstream file(fileName + ".txt");
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            file >> grid[i][j];
        }
    }
    file.close();
    numberOfMoves = 0;
}

void GameHandler::runGame() {
    gameStatus = 1;
    while (gameStatus == 1){
        std::string input;
        getline(std::cin,input);
        if (input == "reset"){
            game.reset();
        } else if (input.substr(0,3) == "set"){
            game.set(input[4] - 'A',input[5] - '0');
        } else if (input.substr(0,5) == "clear"){
            game.clear(input[6] - 'A',input[7] - '0');
        } else if (input.substr(0,4) == "step"){
            if (input.length() == 4) game.update();
            else {
                int N = std::stoi(input.substr(5));
                for (int i = 0; i < N; i++){
                    game.update();
                }
            }
        } else if (input.substr(0,4) == "back"){
            game.back();
        } else if (input.substr(0,4) == "save"){
            std::string fileName = input.substr(5);
            game.save(fileName);
        } else if (input.substr(0,4) == "load"){
            std::string fileName = input.substr(5);
            game.load(fileName);
        } else if (input == "end"){
            gameStatus = 0;
        }
        std::cout << game;
    }
}
