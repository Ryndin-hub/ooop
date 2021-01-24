#include <iostream>
#include "CSVParser.h"

int main() {
    std::ifstream file("test.csv");
    try {
        CSVParser<double, int, std::string> parser(file, 0);
        for (auto tuple : parser) {
            std::cout << tuple << std::endl;
        }
    } catch (std::exception &e){
        std::cerr << e.what();
    }
    file.close();
    return 0;
}
