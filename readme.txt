Execute the problem by the following command:
java -Dlog4j.configuration=file:${log4j_config_file} -Dcalculation.properties=${properties_file} -jar MovingAverageCalculators-1.0.jar

For example:
java -Dlog4j.configuration=file:config/log4j.xml -Dcalculation.properties=config/calculator.properties -jar MovingAverageCalculators-1.0.jar

And the problem will ask for user's input for the type of moving averages.

