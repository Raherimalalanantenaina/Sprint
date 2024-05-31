@echo off

mkdir "out"

for /r "src" %%f in (*.java) do copy "%%f" "out"

@REM Compiler toute les classe en specifiant le classpath-
javac -cp "lib\*" -d "." "out\*.java"

jar cfe "lib\front-controller.jar" -c "mg"

if exist "out" (
    rmdir /s /q "out"
)
if exist "mg" (
    rmdir /s /q "mg"
)