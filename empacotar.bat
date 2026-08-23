@echo off
cd /d "%~dp0"

:: Encontra o jar.exe no seu computador
set "JAR_CMD=jar"
where jar >nul 2>&1
if errorlevel 1 (
    for /d %%D in ("C:\Program Files\Java\jdk*") do if exist "%%D\bin\jar.exe" set "JAR_CMD=%%D\bin\jar.exe"
)

:: Prepara as pastas e a biblioteca
if not exist dist mkdir dist
if not exist dist\lib mkdir dist\lib
copy /y "lib\AbsoluteLayout.jar" "dist\lib\" >nul

:: Cria o manifesto com a tela principal
(
  echo Manifest-Version: 1.0
  echo Main-Class: SimuladorClp
  echo Class-Path: lib/AbsoluteLayout.jar
  echo.
) > manifest.txt

:: Gera o JAR
"%JAR_CMD%" cvfm dist\SimuladorClp.jar manifest.txt -C src .
del /f /q manifest.txt >nul 2>&1

echo.
echo ============================================
echo [SUCESSO] JAR GERADO COM SUCESSO!
echo ============================================
pause