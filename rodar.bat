@echo off
echo ==========================================
echo    COMPILANDO E RODANDO O SIMULADOR
echo ==========================================

:: Garante que o terminal vai entrar na pasta src
cd src

:: 1. Apaga todos os arquivos .class antigos (limpa o cache)
echo [1/3] Limpando arquivos antigos...
del /S /Q *.class >nul 2>&1

:: 2. Compila todo o projeto com a biblioteca AbsoluteLayout
echo [2/3] Compilando arquivos do projeto...
javac -cp "..\lib\AbsoluteLayout.jar;." screens\ladder\*.java
javac -cp "..\lib\AbsoluteLayout.jar;." SimuladorClp.java

:: 3. Executa o simulador
echo [3/3] Iniciando o Simulador...
java -cp "..\lib\AbsoluteLayout.jar;." SimuladorClp

echo ==========================================
pause