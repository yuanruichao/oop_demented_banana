all: Main

Main: java_lang.o out.o
	g++ java_lang.o out.o -o Main

java_lang.o: java_lang.cc java_lang.h
	g++ -c java_lang.cc

out.o: out.cpp out.h
	g++ -c out.cpp