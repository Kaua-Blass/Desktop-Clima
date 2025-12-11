@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM

@echo off
setlocal enabledelayedexpansion

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

cd /d "%DIRNAME%"

if not exist ".mvn\wrapper\maven-wrapper.jar" (
    echo Downloading maven-wrapper.jar...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar', '.mvn\wrapper\maven-wrapper.jar')"
)

if not exist ".mvn\wrapper\MavenWrapperDownloader.java" (
    echo Downloading MavenWrapperDownloader.java...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/apache/maven-wrapper/master/maven-wrapper/src/main/java/org/apache/maven/wrapper/MavenWrapperDownloader.java', '.mvn\wrapper\MavenWrapperDownloader.java')"
)

if not exist ".mvn\wrapper\maven-wrapper.properties" (
    echo Downloading maven-wrapper.properties...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/apache/maven-wrapper/master/maven-wrapper/src/main/resources/maven-wrapper.properties', '.mvn\wrapper\maven-wrapper.properties')"
)

for /f "tokens=1,2 delims==" %%A in (.mvn\wrapper\maven-wrapper.properties) do (
    if "%%A"=="distributionUrl" set DOWNLOAD_URL=%%B
)

java -cp .mvn\wrapper\maven-wrapper.jar org.apache.maven.wrapper.MavenWrapperDownloader "%DOWNLOAD_URL%" ".mvn\wrapper\maven-wrapper.jar"

for /f "tokens=*" %%i in ('dir /b /s ".mvn\wrapper\maven-wrapper.jar"') do set CLASSPATH=%%i

java %MAVEN_OPTS% -classpath %CLASSPATH% org.apache.maven.wrapper.MavenWrapperMain %*
