# flatten-files
Flatten all files to a single directory

# Usage
mvn package exec:java -Dexec.args="FROM_DIR TO_DIR"

example:
mvn package exec:java -Dexec.args="C:\java\_prod c:\temp\flatten-test"

# Alternative usage
mvn package
and then
java -jar target\flatten-files-2019.10.1-SNAPSHOT-jar-with-dependencies.jar FROM_DIR TO_DIR
