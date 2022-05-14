jar := target/school-segregation-1.0-SNAPSHOT-jar-with-dependencies.jar
exp_dir1 := experiments/school_segregation_evolution

build:
	mvn clean package

run-experiment-school-segregation-evolution:
	time java -classpath ${jar} com.ruloweb.abm.util.sweep.ParameterSweep \
		${exp_dir1}/rad_02.params \
		-p out=${exp_dir1}/rad_02.csv \
		&> ${exp_dir1}/rad_02.out

	time java -classpath ${jar} com.ruloweb.abm.util.sweep.ParameterSweep \
		${exp_dir1}/rad_05.params \
		-p out=${exp_dir1}/rad_05.csv \
		&> ${exp_dir1}/rad_05.out

	time java -classpath ${jar} com.ruloweb.abm.util.sweep.ParameterSweep \
		${exp_dir1}/rad_10.params \
		-p out=${exp_dir1}/rad_10.csv \
		&> ${exp_dir1}/rad_10.out
