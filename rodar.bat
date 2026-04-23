@echo off
cd Lexico

echo Digite o caminho do arquivo:

REM Pergunta o caminho do arquivo
set /p file= 

echo.
echo Compilando...
javac lexicocode\*.java
if errorlevel 1 (
    echo Erro na compilacao.
    pause
    exit /b
)

echo Executando...
java lexicocode.LexicalCompiler "%file%"

echo.
pause