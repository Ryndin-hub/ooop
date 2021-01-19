#include <iostream>
#include "CSVParser.h"

int main() {
    std::ifstream file("test.csv");
    CSVParser<double, int, std::string> parser(file, 0);
    for(auto tuple : parser){
        std::cout << tuple << std::endl;
    }
    file.close();
    return 0;
}
