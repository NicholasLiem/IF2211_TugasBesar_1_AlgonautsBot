@echo off
:: Game Runner 
cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES 1/starter-pack/runner-publish/
start "" dotnet GameRunner.dll

:: Game Engine 
cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES 1/starter-pack/engine-publish/
timeout /t 1
start "" dotnet Engine.dll

:: Game Logger 
cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES 1/starter-pack/logger-publish/
timeout /t 1
start "" dotnet Logger.dll

:: Bots 
::cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES 1/starter-pack/reference-bot-publish/
::start "" dotnet ReferenceBot.dll
::start "" dotnet ReferenceBot.dll
:: start "" java -jar Algonauts.jar
:: timeout /t 3
:: cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES 1/starter-pack/starter-bots/TUBES_ALGONAUTS/Tubes1_Algonauts/target/
:: cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES 1/starter-pack/starter-bots/JavaBot/target/
cd C:/Users/gitac/Desktop/GIts/STIMA/TUBES_ALGONAUTS/Tubes1_Algonauts/target/
start "" java -jar Algonauts.jar
timeout /t 3
start "" java -jar Algonauts.jar
timeout /t 3
start "" java -jar Algonauts.jar

cd ../

pause