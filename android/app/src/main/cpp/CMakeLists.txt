cmake_minimum_required(VERSION 3.22.1)
project(kylow_inference LANGUAGES C CXX)

add_library(kylow_inference SHARED kylow_inference.cpp)

find_library(log-lib log)
target_compile_features(kylow_inference PRIVATE cxx_std_17)
target_compile_options(kylow_inference PRIVATE -Wall -Wextra -Werror)
target_link_libraries(kylow_inference ${log-lib})
