
@echo off
title Air Canvas Launcher

cd /d "%~dp0"

echo ==========================
echo       AIR CANVAS
echo ==========================
echo.

echo [1/3] Checking Java...
where java >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java was not found in PATH.
    pause
    exit /b 1
)

echo [2/3] Checking Python environment...
if not exist "python\.venv\Scripts\python.exe" (
    echo ERROR: Python virtual environment was not found.
    echo Please create python\.venv first.
    pause
    exit /b 1
)

echo [3/3] Compiling Java...
cd /d "%~dp0java"

javac Main.java PointNode.java Stroke.java DrawingPanel.java UndoStack.java RedoStack.java GestureServer.java

if errorlevel 1 (
    echo ERROR: Java compilation failed.
    pause
    exit /b 1
)

echo.
echo Starting Java application...

start "Air Canvas Java" java Main

echo Waiting for Java socket server...
timeout /t 3 /nobreak >nul

echo Starting Python hand tracking...
cd /d "%~dp0python"

".venv\Scripts\python.exe" hand_tracking.py

echo.
echo Python application stopped.
echo Close the Java window if it is still open.
pause
