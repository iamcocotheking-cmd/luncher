cmake_minimum_required(VERSION 3.22.1)
project(durbin_glfw_stub C)

add_library(glfw SHARED glfw_stub.c)

find_library(log-lib log)
find_library(android-lib android)

target_include_directories(glfw PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(glfw ${log-lib} ${android-lib})
