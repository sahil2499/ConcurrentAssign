#!/bin/bash

java_file="Vacuum.java"
robot_file="robot.txt"
room_file="roomFile.txt"

# Compile the Java file
javac "$java_file"

# Check if the compilation was successful
if [ $? -eq 0 ]; then
    echo "Java compilation successful."
    
    # Run the Java program with inputs from the files
    java Vacuum "$robot_file" "$room_file"
    
    # Alternatively, if the Java program requires command-line arguments
    # java Vacuum arg1 arg2 "$robot_file" "$room_file"
else
    echo "Java compilation failed. Please check the code and try again."
fi