#include "gameOfLife.h"

void GameOfLife::draw() const{
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].draw();
        }
        std::cout << "\n";
    }
    std::cout << "Move number: " << numberOfMoves << "\n";
}

void GameOfLife::update() {
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].update1();
        }
    }
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].update2();
        }
    }
    numberOfMoves++;
}

GameOfLife::GameOfLife() {
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            Cell *neighbors[8];
            neighbors[0] = &cellGrid[(i-1+h)%h][(j-1+w)%w];
            neighbors[1] = &cellGrid[i][(j-1+w)%w];
            neighbors[2] = &cellGrid[(i+1)%h][(j-1+w)%w];
            neighbors[3] = &cellGrid[(i-1+h)%h][j];
            neighbors[4] = &cellGrid[(i+1)%h][j];
            neighbors[5] = &cellGrid[(i-1+h)%h][(j+1)%w];
            neighbors[6] = &cellGrid[i][(j+1)%w];
            neighbors[7] = &cellGrid[(i+1)%h][(j+1)%w];
            cellGrid[i][j].setNeighbors(neighbors);
        }
    }
}

void GameOfLife::set(int a, int b) {
    cellGrid[a][b].setState(1);
}

void GameOfLife::clear(int a, int b) {
    cellGrid[a][b].setState(0);
}

void GameOfLife::back() {
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].swapState();
        }
    }
}

void GameOfLife::reset() {
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].setState(0);
        }
    }
    numberOfMoves = 0;
}

void GameOfLife::save(const std::string& path) const{
    std::ofstream file(path + ".txt");
    file.clear();
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].save(file);
        }
        file << "\n";
    }
    file << std::endl;
    file.close();
}

void GameOfLife::load(const std::string& path){
    std::ifstream file(path + ".txt");
    for (int i = 0; i < h; i++){
        for (int j = 0; j < w; j++){
            cellGrid[i][j].load(file);
        }
    }
    file.close();
    numberOfMoves = 0;
}

void Cell::update1() {
    previousState = state;
}

void Cell::update2() {
    if (state == 0 && calculateNumberOfAliveNeighbors() == 3){
        state = 1;
        return;
    }
    if (state == 1 && calculateNumberOfAliveNeighbors() < 2){
        state = 0;
        return;
    }
    if (state == 1 && calculateNumberOfAliveNeighbors() > 3){
        state = 0;
        return;
    }
}

int Cell::calculateNumberOfAliveNeighbors() const{
    int num = 0;
    for (int i = 0; i < 8; i++){
        if (neighbors[i] != nullptr) num += neighbors[i]->previousState;
    }
    return num;
}

void Cell::draw() const{
    if (state == 0) std::cout << " ' ";
    else std::cout << " * ";
}

void Cell::setNeighbors(Cell *ptr[8]) {
    for (int i = 0; i < 8; i++){
        neighbors[i] = ptr[i];
    }
}

void Cell::setState(bool a) {
    state = a;
}

void Cell::swapState() {
    bool tmp = state;
    state = previousState;
    previousState = tmp;
}

void Cell::save(std::ofstream &file) const{
    file << state << ' ';
}

void Cell::load(std::ifstream &file){
    file >> state;
}

void GameHandler::runGame() {
    gameStatus = 1;
    while (gameStatus == 1){
        std::string input;
        std::cin >> input;
        if (input == "reset"){
            game.reset();
        } else if (input == "set"){
            std::string XY;
            std::cin >> XY;
            game.set(XY[0] - 'A',XY[1] - '0');
        } else if (input == "clear"){
            std::string XY;
            std::cin >> XY;
            game.clear(XY[0] - 'A',XY[1] - '0');
        } else if (input == "step"){
            std::string N;
            std::cin >> N;
            for (int i = 0; i < std::stoi(N); i++){
                game.update();
            }
        } else if (input == "back"){
            game.back();
        } else if (input == "save"){
            std::string path;
            std::cin >> path;
            game.save(path);
        } else if (input == "load"){
            std::string path;
            std::cin >> path;
            game.load(path);
        } else if (input == "end"){
            gameStatus = 0;
        }
        game.draw();
    }
}
