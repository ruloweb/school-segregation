build:
	mvn clean package

run-simple-experiment:
	time java -classpath target/school-segregation-1.0-SNAPSHOT-jar-with-dependencies.jar com.ruloweb.abm.util.sweep.ParameterSweep experiments/simple_experiment_rad_02.params
	time java -classpath target/school-segregation-1.0-SNAPSHOT-jar-with-dependencies.jar com.ruloweb.abm.util.sweep.ParameterSweep experiments/simple_experiment_rad_05.params
	time java -classpath target/school-segregation-1.0-SNAPSHOT-jar-with-dependencies.jar com.ruloweb.abm.util.sweep.ParameterSweep experiments/simple_experiment_rad_10.params
