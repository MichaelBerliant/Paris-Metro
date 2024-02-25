#!/bin/zsh

#This is for if you use the program in the terminal and you do not want to delete the class files manually. To run it, enter: ./t.zsh

# Navigate to the current directory
cd "$(dirname "$0")" || exit

# Delete files with .class extension
rm -f *.class

echo "Deleted all files with .class extension in the current directory."
