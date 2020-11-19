cmake_minimum_required(VERSION 3.15)
project(lab2)

set(CMAKE_CXX_STANDARD 14)
add_subdirectory(lib/googletest-master)
include_directories(lib/googletest-master/googletest/include)

add_executable(lab2 main.cpp gameOfLife.cpp gameOfLife.h test.cpp)
target_link_libraries(lab2 gtest gtest_main)