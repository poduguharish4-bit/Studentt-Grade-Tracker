@echo off
:: Compilation and execution script for Student Grade Tracker (Batch)
title CodeAlpha Student Grade Tracker

if not exist build (
    mkdir build
)

echo Compiling Java source files...
javac -d build -sourcepath src src\com\codealpha\gradetracker\Main.java

if %errorlevel% neq 0 (
    echo [Error] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Compilation successful!
echo Launching Student Grade Tracker...
java -cp build com.codealpha.gradetracker.Main %*
if %errorlevel% neq 0 (
    pause
)
