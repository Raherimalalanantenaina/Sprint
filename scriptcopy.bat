@echo off
REM Set variables for directories

set working_dir=C:\Users\26134\Documents\S4\Mrnaina\sprint0_2777
set local=C:\Users\26134\Documents\S4\Mrnaina\sprint0_2777
set webapps=C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps
set OUT_LIB=C:\Users\26134\Documents\S4\Mrnaina\sprint0_2777\out
set name=Sprinttest
set OUT_JAR=FrontControllerServelet.jar


REM Supprimer le dossier temporaire s'il existe déjà
if exist "%local%\temp" (
    rmdir /s /q "%local%\temp"
)

REM Créer un nouveau dossier temporaire dans le dossier local
mkdir "%local%\temp"
mkdir "%local%\temp\WEB-INF"
mkdir "%local%\temp\WEB-INF\lib"

REM Copier les fichiers et répertoires depuis working_dir vers temp
xcopy /s /e /q "%working_dir%\web\*" "%local%\temp\WEB-INF\"
copy "%working_dir%\*.xml" "%local%\temp\WEB-INF"

REM Copier le contenu du répertoire lib vers temp/WEB-INF/lib
copy "%working_dir%\lib\*" "%local%\temp\WEB-INF\lib"

REM Copier le fichier JAR dans le répertoire temporaire
copy "%OUT_LIB%\%OUT_JAR%" "%local%\temp\WEB-INF\lib"

REM Vérifier si la copie a réussi
if %errorlevel% equ 0 (
    echo JAR file copied successfully to WEB-INF\lib directory.
) else (
    echo Failed to copy JAR file. Please check errors.
    exit /b 1
)


jar cvf "%local%\%name%.war" -C "%local%\temp" .
move "%local%\%name%.war" "%webapps%"
