#include "game.h"

void Game::run() {
    while (1) {
        std::string input;
        getline(std::cin, input);
        std::string words[10];
        int wordsAmount = 0;
        for (auto x : input){
            if (x == ' '){
                wordsAmount++;
                if (wordsAmount == 9){
                    break;
                }
            } else {
                words[wordsAmount] += x;
            }
        }
        wordsAmount++;

        if (words[0] == "end"){
            return;
        }
        else if (words[0] == "summon") {
            if (words[1] == "collector") {
                collectors.emplace_back(new Collector(map));
                manual_modes.emplace_back(new ManualMode(collectors.back()));
                aliveRobots.emplace_back(std::make_pair(collectors.back(),manual_modes.back()));
                lastRobot++;
            } else if (words[1] == "defuser"){
                defusers.emplace_back(new Defuser(map));
                manual_modes.emplace_back(new ManualMode(defusers.back()));
                aliveRobots.emplace_back(std::make_pair(defusers.back(),manual_modes.back()));
                lastRobot++;
            } else {
                std::cout << "Wrong robot name" << std::endl;
                continue;
            }
        }
        else if (words[0] == "select") {
            int N = std::stoi(words[1]);
            currentRobot = N - 1;
        }
        else if (words[0] == "setmode") {
            if (words[1] == "manual") {
                manual_modes.emplace_back(new ManualMode(aliveRobots[currentRobot].first));
                aliveRobots[currentRobot].second = manual_modes.back();
            } else if (words[1] == "autoscan") {
                auto_scans.emplace_back(new AutoScan(aliveRobots[currentRobot].first));
                aliveRobots[currentRobot].second = auto_scans.back();
            } else if (words[1] == "autocollect") {
                auto_collects.emplace_back(new AutoCollect(aliveRobots[currentRobot].first));
                aliveRobots[currentRobot].second = auto_collects.back();
            } else if (words[1] == "autodefuse") {
                auto_defuses.emplace_back(new AutoDefuse(aliveRobots[currentRobot].first));
                aliveRobots[currentRobot].second = auto_defuses.back();
            } else std::cout << "Wrong mode name" << std::endl;
            continue;
        }
        else if (words[0] == "move"){
            if (typeid(*aliveRobots[currentRobot].second) == typeid(ManualMode)){
                if (words[1] == "u" || words[1] == "up") aliveRobots[currentRobot].first->move(Up);
                else if (words[1] == "r" || words[1] == "right") aliveRobots[currentRobot].first->move(Right);
                else if (words[1] == "d" || words[1] == "down") aliveRobots[currentRobot].first->move(Down);
                else if (words[1] == "l" || words[1] == "left") aliveRobots[currentRobot].first->move(Left);
            } else std::cout << "Mode is not manual" << std::endl;
        }
        else if (words[0] == "scan"){
            if (typeid(*aliveRobots[currentRobot].second) == typeid(ManualMode) && typeid(*aliveRobots[currentRobot].first) == typeid(Collector)){
                aliveRobots[currentRobot].first->scan();
            } else std::cout << "Mode is not manual or robot cant scan" << std::endl;
        }
        else if (words[0] == "grab"){
            if (typeid(*aliveRobots[currentRobot].second) == typeid(ManualMode) && typeid(*aliveRobots[currentRobot].first) == typeid(Collector)){
                aliveRobots[currentRobot].first->grab();
            } else std::cout << "Mode is not manual or robot cant grab" << std::endl;
        }
        else if (words[0] == "auto" && wordsAmount == 1) {
            std::vector<Mode*> active;
            for (auto it : aliveRobots){
                if (typeid(*it.second) == typeid(AutoScan)){
                    active.push_back(it.second);
                } else if (typeid(*it.second) == typeid(AutoCollect)){
                    active.push_back(it.second);
                } else if (typeid(*it.second) == typeid(AutoDefuse)){
                    active.push_back(it.second);
                }
            }
            int doneRobots = 0;
            while (doneRobots != active.size()){
                doneRobots = 0;
                for (auto it : active){
                    doneRobots += it->makeMove();
                }
            }
        }
        else if (words[0] == "auto"){
            int N = std::stoi(words[1]);
            std::vector<Mode*> active;
            for (auto it : aliveRobots){
                if (typeid(*it.second) == typeid(AutoScan)){
                    it.second->cooldown = 0;
                    active.push_back(it.second);
                } else if (typeid(*it.second) == typeid(AutoCollect)){
                    it.second->cooldown = 0;
                    active.push_back(it.second);
                } else if (typeid(*it.second) == typeid(AutoDefuse)){
                    it.second->cooldown = 0;
                    active.push_back(it.second);
                }
            }
            int doneRobots = 0;
            while (doneRobots != active.size() && N > 0){
                doneRobots = 0;
                for (auto it : active){
                    doneRobots += it->makeMove();
                }
                N--;
            }
        } else if (words[0] == "help") {
            std::cout << "summon collector/defuser" << std::endl;
            std::cout << "select (N)" << std::endl;
            std::cout << "setmode manual/autoscan/autocollect/autodefuse" << std::endl;
            std::cout << "move (direction)" << std::endl;
            std::cout << "scan" << std::endl;
            std::cout << "grab" << std::endl;
            std::cout << "auto" << std::endl;
            continue;
        } else {
            std::cout << "Wrong input" << std::endl;
            continue;
        }

        if (currentRobot > -1 && currentRobot <= lastRobot){
            std::cout << *aliveRobots[currentRobot].first;
        } else std::cout << "No robot selected" << std::endl;
    }
}

Game::~Game() {
    for (auto it : collectors) delete it;
    for (auto it : defusers) delete it;
    for (auto it : manual_modes) delete it;
    for (auto it : auto_scans) delete it;
    for (auto it : auto_collects) delete it;
    for (auto it : auto_defuses) delete it;
    delete map;
}
