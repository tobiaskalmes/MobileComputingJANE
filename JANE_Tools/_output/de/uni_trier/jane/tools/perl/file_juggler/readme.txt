-------------------------------------------------------------------------------
- Readme of de/uni_trier/jane/tools/perl/file_juggler
-
- Author: Hannes Frey
-------------------------------------------------------------------------------

This folder contains some perl scripts which are useful for creating a sequence
of simulation runs and for evaluating the result files. The following describes
a general strategy how to perform a simulation run and how the scripts will help
to do that.


1.) Generating a sequence of simulations
- - - - - - - - - - - - - - - - - - - - 

Use the print_sequence.pl script in order to create a sequence of simulation
run commands. The following is a simlpe example how to use this tool.

print_sequence.pl "algo1 algo2, 100 200 300" 1 4
    "java simulate algorithm=#1 seed=#2#m nodes=#2 result=res_#1_#2.dat"

The result will look as follows:

java simulate algorithm=algo1 seed=1001 nodes=100 result=res_algo1_100.dat
java simulate algorithm=algo1 seed=2001 nodes=200 result=res_algo1_200.dat
java simulate algorithm=algo1 seed=3001 nodes=300 result=res_algo1_300.dat
java simulate algorithm=algo1 seed=4001 nodes=400 result=res_algo1_400.dat
java simulate algorithm=algo1 seed=1002 nodes=100 result=res_algo1_100.dat
java simulate algorithm=algo1 seed=2002 nodes=200 result=res_algo1_200.dat
java simulate algorithm=algo1 seed=3002 nodes=300 result=res_algo1_300.dat
java simulate algorithm=algo1 seed=4002 nodes=400 result=res_algo1_400.dat
java simulate algorithm=algo1 seed=1003 nodes=100 result=res_algo1_100.dat
java simulate algorithm=algo1 seed=2003 nodes=200 result=res_algo1_200.dat
java simulate algorithm=algo1 seed=3003 nodes=300 result=res_algo1_300.dat
java simulate algorithm=algo1 seed=4003 nodes=400 result=res_algo1_400.dat
java simulate algorithm=algo1 seed=1004 nodes=100 result=res_algo1_100.dat
java simulate algorithm=algo1 seed=2004 nodes=200 result=res_algo1_200.dat
java simulate algorithm=algo1 seed=3004 nodes=300 result=res_algo1_300.dat
java simulate algorithm=algo1 seed=4004 nodes=400 result=res_algo1_400.dat
java simulate algorithm=algo2 seed=1001 nodes=100 result=res_algo2_100.dat
java simulate algorithm=algo2 seed=2001 nodes=200 result=res_algo2_200.dat
java simulate algorithm=algo2 seed=3001 nodes=300 result=res_algo2_300.dat
java simulate algorithm=algo2 seed=4001 nodes=400 result=res_algo2_400.dat
java simulate algorithm=algo2 seed=1002 nodes=100 result=res_algo2_100.dat
java simulate algorithm=algo2 seed=2002 nodes=200 result=res_algo2_200.dat
java simulate algorithm=algo2 seed=3002 nodes=300 result=res_algo2_300.dat
java simulate algorithm=algo2 seed=4002 nodes=400 result=res_algo2_400.dat
java simulate algorithm=algo2 seed=1003 nodes=100 result=res_algo2_100.dat
java simulate algorithm=algo2 seed=2003 nodes=200 result=res_algo2_200.dat
java simulate algorithm=algo2 seed=3003 nodes=300 result=res_algo2_300.dat
java simulate algorithm=algo2 seed=4003 nodes=400 result=res_algo2_400.dat
java simulate algorithm=algo2 seed=1004 nodes=100 result=res_algo2_100.dat
java simulate algorithm=algo2 seed=2004 nodes=200 result=res_algo2_200.dat
java simulate algorithm=algo2 seed=3004 nodes=300 result=res_algo2_300.dat
java simulate algorithm=algo2 seed=4004 nodes=400 result=res_algo2_400.dat


2.) Running the simulation
- - - - - - - - - - - - -

The result of print_sequence.pl can be stored in a batch file. Execute the file
as a batch process on your favorite OS :). The example from above may produce
the following result files, which contain four lines with a key and a measure
points each, for instance.

res_algo1_100.dat
res_algo1_200.dat
res_algo1_300.dat
res_algo1_400.dat
res_algo2_100.dat
res_algo2_200.dat
res_algo2_300.dat
res_algo2_400.dat


3.) Evaluating the result files
- - - - - - - - - - - - - - - -

Evaluating simulation simulation log files often requires visiting a sequence
of result file in a certain order. The most important tool to do this is the
visit.pl script which can be used to apply a command line for all possible
combinations of a parameter list. See visit.pl for a more detailed description.
For instance, to compute the average value of the second column of the values
stored in the above result files just apply visit.pl on the compute_sum.pl
script.

visit.pl "algo1 algo2" "100 200 300 400"
    "compute_sum.pl res_#1_#2.dat 2 final_#1"

The result will be stored in the following two files:

final_algo1.dat
final_algo2.dat

The remaining scripts in this folder are intended to be applied in combination
with visit.pl. For a detailed description call those scripts without an argument.

Have fun.


