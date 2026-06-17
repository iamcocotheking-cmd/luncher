cmake_minimum_required(VERSION 3.22.1)

project("glfw")

add_library(glfw SHARED glfw_stub.c)

find_library(log_lib log)
find_library(android_lib android)

target_link_libraries(glfw ${log_lib} ${android_lib})
