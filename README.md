# flatten-files
Flatten all files to a single directory

# Usage
mvn package exec:java -Dexec.args="FROM_DIR TO_DIR"
optional
mvn package exec:java -Dexec.args="FROM_DIR TO_DIR FILE_WILDCARD"


example:
mvn package exec:java -Dexec.args="C:\pictures\all c:\temp\flatten *.jpg"
will copy all *.jpg files from all subdirectories of C:\pictures\all to c:\temp\flatten

The file wildcard is useful to collect all files of a specific type in a huge directory structure.

In case you have spaces in your path (you should not) you can use single quotes inside the args or normal
quotes when starting via java -jar, see below.

# Alternative usage
mvn package
and then
java -jar target\flatten-files-2019.10.1-SNAPSHOT-jar-with-dependencies.jar FROM_DIR TO_DIR FILE_WILDCARD

# FAT32
You may exceed "Maximum number of files in a single folder"
convert e: /fs:ntfs