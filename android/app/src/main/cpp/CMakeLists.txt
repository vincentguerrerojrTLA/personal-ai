cmake_minimum_required(VERSION 3.22.1)
project(kylow_inference LANGUAGES C CXX)

option(KYLOW_ENABLE_LLAMA "Build Kylow with the pinned llama.cpp backend" ON)

include(llama_backend/PINNED_BACKEND.cmake)
if(KYLOW_ENABLE_LLAMA AND NOT DEFINED KYLOW_LLAMA_CPP_DIR)
    include(FetchContent)
    FetchContent_Declare(
        kylow_llama_cpp
        GIT_REPOSITORY ${KYLOW_LLAMA_CPP_REPOSITORY}
        GIT_TAG ${KYLOW_LLAMA_CPP_COMMIT}
        GIT_SHALLOW FALSE
        GIT_PROGRESS TRUE
    )
    FetchContent_GetProperties(kylow_llama_cpp)
    if(NOT kylow_llama_cpp_POPULATED)
        FetchContent_Populate(kylow_llama_cpp)
    endif()
    set(KYLOW_LLAMA_CPP_DIR "${kylow_llama_cpp_SOURCE_DIR}")
endif()

add_library(kylow_inference SHARED kylow_inference.cpp)
find_library(log-lib log)
target_compile_features(kylow_inference PRIVATE cxx_std_17)
target_compile_options(kylow_inference PRIVATE -Wall -Wextra -Werror)
target_link_libraries(kylow_inference ${log-lib})

if(KYLOW_ENABLE_LLAMA)
    add_subdirectory(llama_backend)
    target_link_libraries(kylow_inference llama)
    target_compile_definitions(kylow_inference PRIVATE KYLOW_HAS_LLAMA=1)
else()
    message(STATUS "Kylow: compiling fail-closed native bridge without llama.cpp backend")
endif()
