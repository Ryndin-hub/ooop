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

bool compareTestFiles(std::string &fileName1, std::string &fileName2){
    std::ifstream file1,file2;
    file1.open(fileName1 + ".txt");
    file2.open(fileName2 + ".txt");
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
    std::istringstream input("set A0\n"
                             "save " + output1 + "\n"
                             "reset\n"
                             "save " + output2 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    ASSERT_FALSE(compareTestFiles(output1,output2));
}

TEST_F(GameFixture,clear){
    std::istringstream input("set C4\n"
                             "save " + output1 + "\n"
                             "clear C4\n"
                             "save " + output2 + "\n"
                             "reset\n"
                             "save " + output3 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    EXPECT_FALSE(compareTestFiles(output1,output2));
    ASSERT_TRUE(compareTestFiles(output2,output3));
}

TEST_F(GameFixture,save_load){
    std::istringstream input("set A0\n"
                             "save " + output1 + "\n"
                             "reset\n"
                             "load " + output1 + "\n"
                             "save " + output2 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    ASSERT_TRUE(compareTestFiles(output1,output2));
}

TEST_F(GameFixture,back){
    std::istringstream input("set B3\n"
                             "set B4\n"
                             "set B5\n"
                             "save " + output1 + "\n"
                             "step 1\n"
                             "save " + output2 + "\n"
                             "back\n"
                             "save " + output3 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    EXPECT_FALSE(compareTestFiles(output1,output2));
    ASSERT_TRUE(compareTestFiles(output1,output3));
}

TEST_F(GameFixture,single_step){
    std::istringstream input("set A0\n"
                             "set A1\n"
                             "set B1\n"
                             "set C1\n"
                             "save " + output1 + "\n"
                             "step\n"
                             "save " + output2 + "\n"
                             "load " + output1 + "\n"
                             "step 1\n"
                             "save " + output3 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    ASSERT_FALSE(compareTestFiles(output1,output2));
    ASSERT_TRUE(compareTestFiles(output2,output3));
}

TEST_F(GameFixture,glider){
    std::istringstream input("set A1\n"
                             "set B2\n"
                             "set C0\n"
                             "set C1\n"
                             "set C2\n"
                             "save " + output1 + "\n"
                             "step 40\n"
                             "save " + output2 + "\n"
                             "step 1\n"
                             "save " + output3 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    EXPECT_TRUE(compareTestFiles(output1,output2));
    ASSERT_FALSE(compareTestFiles(output1,output3));
}

TEST_F(GameFixture,stick){
    std::istringstream input("set C0\n"
                             "set C1\n"
                             "set C2\n"
                             "save " + output1 + "\n"
                             "step 1000\n"
                             "save " + output2 + "\n"
                             "step 1\n"
                             "save " + output3 + "\n"
                             "end\n");

    runSimpleTest(input,game);
    EXPECT_TRUE(compareTestFiles(output1,output2));
    ASSERT_FALSE(compareTestFiles(output1,output3));
}
