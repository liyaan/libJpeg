cmake_minimum_required(VERSION 3.10.2)

add_library( native-lib
             SHARED
             native-lib.cpp )

add_library(libjpeg STATIC IMPORTED)
#set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/x86/libturbojpeg.a)
set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libturbojpeg.a)
include_directories(include)
target_link_libraries(  native-lib
        libjpeg
        #jnigraphics是安卓NDK目录中直接有的
        jnigraphics
        log)