SRC_DIR=../src
BIN_DIR=../bin

SOURCES=$(shell dir /b /s "$(SRC_DIR)\*.java")

compile:
	javac -d $(BIN_DIR) $(SOURCES)

run:
	java -cp $(BIN_DIR) fairview.FairviewApp

clean:
	del /q "$(BIN_DIR)\fairview\*.class"