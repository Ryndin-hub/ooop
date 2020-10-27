#include <gtest/gtest.h>
#include "RNK.h"

namespace {
class ClassDeclaration : public testing::Test {
public:
    RNK rnk1,rnk2,rnk3;
};
}

TEST_F(ClassDeclaration,test_eq){
    rnk1[0] = RNK::A;
    EXPECT_EQ(rnk1[0],RNK::A);
    rnk1[0] = rnk2[1] = RNK::G;
    EXPECT_EQ(rnk1[0],RNK::G);
    EXPECT_EQ(rnk2[1],RNK::G);
}

TEST_F(ClassDeclaration,test_plus){
    for (int i = 0; i < 100; i++){
        rnk1[i] = RNK::G;
        rnk2[i] = RNK::A;
    }
    rnk3 = rnk1 + rnk2;
    for (int i = 0; i < 100; i++){
        EXPECT_EQ(rnk3[i],rnk1[i]);
        EXPECT_EQ(rnk3[i+100],rnk2[i]);
    }
}

TEST_F(ClassDeclaration,test_100mil){
    for (int i = 0; i < 100000000; i++){
        rnk1[i] = RNK::C;
    }
    for (int i = 91; i < 100000000; i+=20317){
        EXPECT_EQ(rnk1[i],RNK::C);
    }
}

TEST_F(ClassDeclaration,test_cpy){
    for (int i = 0; i < 1000; i+=3){
        rnk1[i] = RNK::G;
        rnk1[i+1] = RNK::A;
        rnk1[i+2] = RNK::C;
    }
    rnk2 = rnk1;
    EXPECT_EQ(rnk1,rnk2);
    for (int i = 0; i < 1000; i++){
        EXPECT_EQ(rnk1[i],rnk2[i]);
    }
}

TEST_F(ClassDeclaration,test_split){
    for (int i = 0; i < 1000; i+=3){
        rnk1[i] = RNK::G;
        rnk1[i+1] = RNK::A;
        rnk1[i+2] = RNK::C;
    }
    rnk2 = rnk1.split(100);
    for (int i = 0; i < 1000 - 100; i++){
        EXPECT_EQ(rnk1[i+100],rnk2[i]);
    }
}

TEST_F(ClassDeclaration,test_isComplementary){
    for (int i = 0; i < 1000; i+=3){
        rnk1[i] = RNK::G;
        rnk1[i+1] = RNK::A;
        rnk1[i+2] = RNK::C;
    }
    rnk2 = !rnk1;
    EXPECT_TRUE(rnk2.isComplementary(rnk1));
    rnk2[10] = RNK::T;
    rnk2[11] = RNK::T;
    EXPECT_FALSE(rnk2.isComplementary(rnk1));
}

TEST_F(ClassDeclaration,test_big_rad){
    rnk1[0] = RNK::C;
    rnk1[1000000000] = rnk1[0];
    EXPECT_EQ(rnk1[0],RNK::C);
    EXPECT_EQ(rnk1[1000000000],RNK::C);
}
