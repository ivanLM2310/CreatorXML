SET JAVA_HOME="C:\Program Files\Java\jdk1.8.0_162\bin"
SET PATH=%JAVA_HOME%;%PATH%
SET CLASSPATH=%JAVA_HOME%;
cd C:\Users\ivanl\Documents\GitHub\CreatorXML\src\AnalizadorFs\gdato
java -jar C:\jflex-1.6.1\lib\java-cup-11a.jar -parser sintacticoGdato -symbols symGdato sintacticoGdato.cup