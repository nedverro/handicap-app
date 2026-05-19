@echo off
chcp 65001 >nul
title UIR Handicap — Lancement

echo ============================================
echo     UIR Handicap - Lancement de l'app
echo ============================================
echo.

:: ── Trouver javac automatiquement ────────────────────────────
set JAVAC=
set JAVA=

:: Chercher dans les emplacements courants
for %%J in (
    "C:\Program Files\Java\jdk1.8.0_202\bin"
    "C:\Program Files\Java\jdk1.8.0_201\bin"
    "C:\Program Files\Java\jdk-11\bin"
    "C:\Program Files\Java\jdk-17\bin"
    "C:\Program Files\Java\jdk-21\bin"
    "C:\Program Files\Eclipse Adoptium\jdk-11.0.0.0-hotspot\bin"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.0.0-hotspot\bin"
    "C:\Program Files\Microsoft\jdk-11.0.0.0\bin"
    "C:\Program Files\Microsoft\jdk-17.0.0.0\bin"
) do (
    if exist "%%~J\javac.exe" (
        set JAVAC=%%~J\javac.exe
        set JAVA=%%~J\java.exe
    )
)

:: Si pas trouvé dans les emplacements fixes, chercher via JAVA_HOME
if "%JAVAC%"=="" (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set JAVAC=%JAVA_HOME%\bin\javac.exe
            set JAVA=%JAVA_HOME%\bin\java.exe
        )
    )
)

:: Si toujours pas trouvé, essayer directement depuis PATH
if "%JAVAC%"=="" (
    where javac >nul 2>&1
    if not errorlevel 1 (
        set JAVAC=javac
        set JAVA=java
    )
)

if "%JAVAC%"=="" (
    echo [ERREUR] Java JDK introuvable sur ce PC.
    echo.
    echo Veuillez installer le JDK Java depuis :
    echo https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

echo [OK] JDK trouve : %JAVAC%
echo.

:: ── Verifier que le JAR MySQL est present ────────────────────
if not exist "libs\mysql-connector-j-9.7.0.jar" (
    echo [ERREUR] Fichier manquant : libs\mysql-connector-j-9.7.0.jar
    echo.
    echo Telechargez le driver MySQL depuis :
    echo https://dev.mysql.com/downloads/connector/j/
    echo Choisir "Platform Independent" puis copiez le .jar dans le dossier libs\
    echo.
    pause
    exit /b 1
)

echo [OK] Driver MySQL trouve.
echo.

:: ── Verifier que XAMPP MySQL tourne ──────────────────────────
echo Verification de MySQL (XAMPP)...
tasklist /fi "imagename eq mysqld.exe" 2>nul | find /i "mysqld.exe" >nul
if errorlevel 1 (
    echo [ATTENTION] MySQL ne semble pas etre demarre.
    echo Lancez XAMPP Control Panel et demarrez MySQL avant de continuer.
    echo.
    pause
)

:: ── Compiler ──────────────────────────────────────────────────
echo Compilation en cours...
if not exist bin mkdir bin

"%JAVAC%" -encoding UTF-8 -cp "src;libs\mysql-connector-j-9.7.0.jar" -d bin ^
    src\*.java ^
    src\model\*.java ^
    src\dao\*.java ^
    src\controller\*.java ^
    src\view\*.java

if errorlevel 1 (
    echo.
    echo [ERREUR] La compilation a echoue. Verifiez les fichiers sources.
    pause
    exit /b 1
)

echo [OK] Compilation reussie.
echo.

:: ── Lancer ───────────────────────────────────────────────────
echo Lancement de l'application...
echo.
"%JAVA%" -cp "bin;libs\mysql-connector-j-9.7.0.jar" Main

pause