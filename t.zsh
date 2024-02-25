#!/bin/zsh

# Navigate to the current directory
cd "$(dirname "$0")" || exit

# Delete files with .class extension
rm -f *.class

echo "Deleted all files with .class extension in the current directory."
