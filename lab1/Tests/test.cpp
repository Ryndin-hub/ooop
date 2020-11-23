#include <gtest/gtest.h>
#include "../RNK/RNK.h"

namespace {
class RnkFixture : public testing::Test {
public:
    RNK rnk1,rnk2,rnk3;
};
}

TEST_F(RnkFixture,eq){
    rnk1[0] = RNK::A;
    ASSERT_EQ(rnk1[0],RNK::A);
    rnk1[0] = rnk2[1] = RNK::G;
    ASSERT_EQ(rnk1[0],RNK::G);
    ASSERT_EQ(rnk2[1],RNK::G);
}

TEST_F(RnkFixture,plus){
    for (int i = 0; i < 100; i++){
        rnk1[i] = RNK::G;
        rnk2[i] = RNK::A;
    }
    rnk3 = rnk1 + rnk2;
    for (int i = 0; i < 100; i++){
        ASSERT_EQ(rnk3[i],rnk1[i]);
        ASSERT_EQ(rnk3[i+100],rnk2[i]);
    }
}

TEST_F(RnkFixture,1mil){
    for (int i = 0; i < 1000000; i++){
        rnk1[i] = RNK::C;
    }
    for (int i = 91; i < 1000000; i+=20317){
        ASSERT_EQ(rnk1[i],RNK::C);
    }
}

TEST_F(RnkFixture,cpy){
    for (int i = 0; i < 1000; i+=3){
        rnk1[i] = RNK::G;
        rnk1[i+1] = RNK::A;
        rnk1[i+2] = RNK::C;
    }
    rnk2 = rnk1;
    ASSERT_EQ(rnk1,rnk2);
    for (int i = 0; i < 1000; i++){
        ASSERT_EQ(rnk1[i],rnk2[i]);
    }
}

TEST_F(RnkFixture,split){
    for (int i = 0; i < 1000; i+=3){
        rnk1[i] = RNK::G;
        rnk1[i+1] = RNK::A;
        rnk1[i+2] = RNK::C;
    }
    rnk2 = rnk1.split(100);
    for (int i = 0; i < 1000 - 100; i++){
        ASSERT_EQ(rnk1[i+100],rnk2[i]);
    }
}

TEST_F(RnkFixture,isComplementary){
    for (int i = 0; i < 1000; i+=3){
        rnk1[i] = RNK::G;
        rnk1[i+1] = RNK::A;
        rnk1[i+2] = RNK::C;
    }
    rnk2 = !rnk1;
    ASSERT_TRUE(rnk2.isComplementary(rnk1));
    rnk2[10] = RNK::T;
    rnk2[11] = RNK::T;
    ASSERT_FALSE(rnk2.isComplementary(rnk1));
}

TEST_F(RnkFixture,big_rad){
    rnk1[0] = RNK::C;
    rnk1[1000000000] = rnk1[0];
    ASSERT_EQ(rnk1[0],RNK::C);
    ASSERT_EQ(rnk1[1000000000],RNK::C);
}

TEST_F(RnkFixture,unavailibale_pos){
    for(int i = 0; i < 100000; i++){
        ASSERT_EQ(rnk1[i],NULL);
    }
}
