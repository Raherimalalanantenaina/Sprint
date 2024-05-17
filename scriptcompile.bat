@echo off

rem Répertoires
set TEMP_SRC=temp_src
set MY_CLASSES=classes

rem Création du répertoire temporaire pour les sources
if exist "%TEMP_SRC%" (
    echo Le répertoire de destination existe déjà. Suppression en cours...
    rmdir /s /q "%TEMP_SRC%"
    echo Répertoire de destination supprimé avec succès.
)
mkdir "%TEMP_SRC%"
echo Répertoire temporaire pour source créé

rem Création du répertoire temporaire pour les classes
if exist "%MY_CLASSES%" (
    echo Le répertoire de destination existe déjà. Suppression en cours...
    rmdir /s /q "%MY_CLASSES%"
    echo Répertoire de destination supprimé avec succès.
)
mkdir "%MY_CLASSES%"
echo Répertoire temporaire pour les .class créé

rem Copier les fichiers Java dans le répertoire temporaire
for /r src %%f in (*.java) do (
    copy "%%f" "%TEMP_SRC%"
)

rem Compilation des fichiers Java du répertoire source vers classes
javac -d "%MY_CLASSES%" "%TEMP_SRC%\*.java"
if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la compilation des fichiers Java.
    exit /b 1
)
echo Fichiers Java compilés dans le répertoire classes

rem Création du fichier jar
jar cf "lib\front_Servlet.jar" -C "%MY_CLASSES%" .
if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de la création du fichier jar.
    exit /b 1
)
echo Fichier jar créé avec succès.

rem Nettoyage des répertoires temporaires
rmdir /s /q "%TEMP_SRC%"
rmdir /s /q "%MY_CLASSES%"
echo Répertoires temporaires nettoyés
