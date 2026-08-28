@echo off
cd /d "E:\Users\User\Desktop\tcc\Trabalho-Final-CLP-main"

echo [1/3] Limpando e compilando arquivos alterados...
cd src
del /S /Q *.class >nul 2>&1
dir /s /b *.java > sources.txt
javac --release 23 -cp "..\lib\AbsoluteLayout.jar;." @sources.txt
del sources.txt
cd ..

echo [2/3] Preparando bibliotecas...
if not exist dist mkdir dist
if not exist dist\lib mkdir dist\lib
copy /y "lib\AbsoluteLayout.jar" "dist\lib\" >nul

set "JAR_CMD=jar"
where jar >nul 2>&1
if errorlevel 1 (
    for /d %%D in ("C:\Program Files\Java\jdk*") do if exist "%%D\bin\jar.exe" set "JAR_CMD=%%D\bin\jar.exe"
)

(
  echo Manifest-Version: 1.0
  echo Main-Class: SimuladorClp
  echo Class-Path: lib/AbsoluteLayout.jar
  echo.
) > manifest.txt

echo [3/3] Empacotando SimuladorClp.jar...
"%JAR_CMD%" cvfm dist\SimuladorClp.jar manifest.txt -C src .
del /f /q manifest.txt >nul 2>&1

echo.
echo ============================================
echo [SUCESSO] NOVO JAR GERADO NA PASTA DIST!
echo ============================================
pause