#!/bin/bash

echo "running . setup.sh"

. setup.sh

for fullpath in test/*.java;do
	echo "translating $fullpath ###########################"
	filename="${fullpath##*/}"                      # Strip longest match of */ from start
    dir="${fullpath:0:${#fullpath} - ${#filename}}" # Substring from 0 thru pos of filename
    base="${filename%.[^.]*}"                       # Strip shortest match of . plus at least one non-dot char from end
    ext="${filename:${#base} + 1}" 
	if java translator.Translator $fullpath;

	then 
		echo "making $filename ###########################"
		rm "*.o"
		rm "Main"
		make		

		echo "running $filename ###########################"
		
		
		echo "C++ output ###########################"
		./main

		echo "Java output ###########################"
		cd "test"
		javac "$filename"
		java "$base"
		cd ".."
	else  
		echo "making $filename ###########################"
		make
		echo "running $filename ###########################"

		echo "c++ output ###########################"
		./main

		echo "Java output ###########################"
		cd "test"
		javac "$filename"
		java "$base"
		cd ".."
	fi

	rm "test/*.class"
done

