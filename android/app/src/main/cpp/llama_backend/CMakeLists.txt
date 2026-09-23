# Kylow llama.cpp integration boundary.
#
# The upstream llama.cpp source is intentionally not copied into this repository
# without a pinned revision. The parent build enables this directory only when
# KYLOW_LLAMA_CPP_DIR points at a verified checkout.
cmake_minimum_required(VERSION 3.22.1)

if(NOT DEFINED KYLOW_LLAMA_CPP_DIR)
    message(FATAL_ERROR "KYLOW_LLAMA_CPP_DIR must point to a pinned llama.cpp checkout")
endif()

set(LLAMA_BUILD_COMMON ON CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
set(GGML_OPENMP OFF CACHE BOOL "" FORCE)

add_subdirectory("${KYLOW_LLAMA_CPP_DIR}" "${CMAKE_BINARY_DIR}/llama.cpp" EXCLUDE_FROM_ALL)
