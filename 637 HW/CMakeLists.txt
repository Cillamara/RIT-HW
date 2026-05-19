cmake_minimum_required(VERSION 3.10)
project(HW)

set(CMAKE_CXX_STANDARD 14)

# 1. macOS Helper: Explicitly point to Homebrew's libomp
if(APPLE)
    set(OpenMP_ROOT "/opt/homebrew/opt/libomp")
    list(APPEND CMAKE_PREFIX_PATH "/opt/homebrew/opt/libomp")
    include_directories("/opt/homebrew/opt/libomp/include")
    link_directories("/opt/homebrew/opt/libomp/lib")
endif()

# 2. Key Fix: Find ONLY the C++ component (COMPONENTS CXX)
# This prevents the "Could NOT find OpenMP_C" error.
find_package(OpenMP REQUIRED COMPONENTS CXX)

add_executable(HW hw1/lr.cc)

# 3. Link the library
if(OpenMP_CXX_FOUND)
    target_link_libraries(HW PUBLIC OpenMP::OpenMP_CXX)
endif()