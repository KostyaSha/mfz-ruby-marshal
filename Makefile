marshal:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.mfizz.ruby.demo.MarshalMain" -Dexec.args=""

unmarshal:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.mfizz.ruby.demo.UnmarshalMain" -Dexec.args=""
