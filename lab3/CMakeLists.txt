cmake_minimum_required(VERSION 3.15)
project(lab3)

set(CMAKE_CXX_STANDARD 14)

add_executable(lab3 main.cpp robot.cpp robot.h map.cpp map.h Mode.h AutoMode.cpp AutoScan.cpp AutoCollect.cpp AutoDefuse.cpp game.cpp game.h)
