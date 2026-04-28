SRC_MAIN=..\src\main\java
SRC_TEST=..\src\test\java
BIN_MAIN=..\bin\main
BIN_TEST=..\bin\test
LIB=..\lib

JUNIT_CP=$(LIB)\junit-jupiter-api-5.10.0.jar;$(LIB)\junit-jupiter-engine-5.10.0.jar;$(LIB)\junit-platform-console-standalone-1.10.0.jar;$(LIB)\apiguardian-api-1.1.2.jar

MAIN_SOURCES=$(shell dir /b /s "$(SRC_MAIN)\*.java")
TEST_SOURCES=$(shell dir /b /s "$(SRC_TEST)\*.java")

compile:
	if not exist $(BIN_MAIN) mkdir $(BIN_MAIN)
	javac -d $(BIN_MAIN) $(MAIN_SOURCES)

test-compile:
	if not exist $(BIN_TEST) mkdir $(BIN_TEST)
	javac -cp "$(BIN_MAIN);$(JUNIT_CP)" -d $(BIN_TEST) $(TEST_SOURCES)

run:
	java -cp $(BIN_MAIN) fairview.FairviewApp

test:
	java -cp "$(BIN_MAIN);$(BIN_TEST);$(JUNIT_CP)" org.junit.platform.console.ConsoleLauncher --scan-classpath

clean:
	if exist ..\bin rmdir /s /q ..\bin
	mkdir ..\bin
	mkdir ..\bin\main
	mkdir ..\bin\test