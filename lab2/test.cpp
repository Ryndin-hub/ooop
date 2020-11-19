#include <gtest/gtest.h>
#include "gameOfLife.h"

namespace {
    class GameFixture : public testing::Test {
    public:
        GameHandler game;
        std::string output1 = "output1";
        std::string output2 = "output2";
        std::string output3 = "output3";
    };
}

bool compareTestFiles(std::string &path1, std::string &path2){
    std::ifstream file1,file2;
    file1.open(path1 + ".txt");
    file2.open(path2 + ".txt");
    return std::equal(std::istreambuf_iterator<char>(file1.rdbuf()),
                      std::istreambuf_iterator<char>(),
                      std::istreambuf_iterator<char>(file2.rdbuf()));
}

void runSimpleTest(std::istringstream &input,GameHandler &game){
    std::streambuf *backup1, *backup2;
    std::ofstream output;
    output.open("testOutput.txt");
    backup1 = std::cin.rdbuf();
    backup2 = std::cout.rdbuf();
    std::cout.rdbuf(output.rdbuf());
    std::cin.rdbuf(input.rdbuf());

    game.runGame();

    std::cin.rdbuf(backup1);
    std::cout.rdbuf(backup2);
}

TEST_F(GameFixture,set){
    std::istringstream input("set A0 "
                             "save " + output1 + " "
                             "reset "
                             "save " + output2 + " "
                             "end ");

    runSimpleTest(input,game);
    ASSERT_FALSE(compareTestFiles(output1,output2));
}

TEST_F(GameFixture,clear){
    std::istringstream input("set C4 "
                             "save " + output1 + " "
                             "clear C4 "
                             "save " + output2 + " "
                             "reset "
                             "save " + output3 + " "
                             "end ");

    runSimpleTest(input,game);
    EXPECT_FALSE(compareTestFiles(output1,output2));
    ASSERT_TRUE(compareTestFiles(output2,output3));
}

TEST_F(GameFixture,saveload){
    std::istringstream input("set A0 "
                             "save " + output1 + " "
                             "reset "
                             "load " + output1 + " "
                             "save " + output2 + " "
                             "end ");

    runSimpleTest(input,game);
    ASSERT_TRUE(compareTestFiles(output1,output2));
}

TEST_F(GameFixture,back){
    std::istringstream input("set B3 "
                             "set B4 "
                             "set B5 "
                             "save " + output1 + " "
                             "step 1 "
                             "save " + output2 + " "
                             "back "
                             "save " + output3 + " "
                             "end ");

    runSimpleTest(input,game);
    EXPECT_FALSE(compareTestFiles(output1,output2));
    ASSERT_TRUE(compareTestFiles(output1,output3));
}

TEST_F(GameFixture,glider){
    std::istringstream input("set A1 "
                             "set B2 "
                             "set C0 "
                             "set C1 "
                             "set C2 "
                             "save " + output1 + " "
                             "step " + std::to_string(4 * 10) + " "
                             "save " + output2 + " "
                             "step 1 "
                             "save " + output3 + " "
                             "end ");

    runSimpleTest(input,game);
    EXPECT_TRUE(compareTestFiles(output1,output2));
    ASSERT_FALSE(compareTestFiles(output1,output3));
}

TEST_F(GameFixture,stick){
    std::istringstream input("set C0 "
                             "set C1 "
                             "set C2 "
                             "save " + output1 + " "
                             "step 1000000 "
                             "save " + output2 + " "
                             "step 1 "
                             "save " + output3 + " "
                             "end ");

    runSimpleTest(input,game);
    EXPECT_TRUE(compareTestFiles(output1,output2));
    ASSERT_FALSE(compareTestFiles(output1,output3));
}
