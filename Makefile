all:
	mvn package

#	-Dwebdriver.chrome.driver=/path/to/chromedriver
drive:
	mvn clean install
	/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin/java -jar target/ebaykleinanzeigenautomator.jar

