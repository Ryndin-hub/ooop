#include <iostream>
#include <ctime>
#include "game.h"

int main(int argc, char* argv[]) {
    std::srand(std::time(nullptr));

    if (argc == 2) {
        Game game(argv[1]);
        game.run();
    } else {
        Game game;
        game.run();
    }

    return 0;
}
