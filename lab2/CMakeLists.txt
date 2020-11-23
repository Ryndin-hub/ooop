cmake_minimum_required(VERSION 3.15)
project(lab2)

set(CMAKE_CXX_STANDARD 14)
add_subdirectory(lib/googletest-master)
include_directories(lib/googletest-master/googletest/include)

add_executable(lab2 main.cpp gameOfLife.cpp gameOfLife.h test.cpp)
target_link_libraries(lab2 gtest gtest_main)

set(CMAKE_CXX_STANDARD_LIBRARIES "-static-libgcc -static-libstdc++ -lwsock32 -lws2_32 ${CMAKE_CXX_STANDARD_LIBRARIES}")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,-Bstatic,--whole-archive -lwinpthread -Wl,--no-whole-archive")
