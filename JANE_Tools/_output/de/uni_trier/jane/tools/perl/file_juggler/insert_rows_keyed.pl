################################################################################
#
# Replace rows in the source file with those from the replace file and store
# the result into the destination file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $insert_file, $key_columns, $destination) = @ARGV;

print_operation("insert_rows_keyed", $source, $insert_file, $key_columns, $destination);

if($#ARGV < 2 || $#ARGV > 3) {
	usage();
}

my %insert_map = read_keyed_lines($insert_file, $key_columns);
my %source_map = read_keyed_lines($source, $key_columns);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# create merged map
my %merged_map;
foreach(keys %insert_map) {
	$merged_map{$_} = $insert_map{$_};
}
foreach(keys %source_map) {
	if(exists $merged_map{$_}) {
		die "Duplicate key in $source and $insert_file";
	}
	$merged_map{$_} = $source_map{$_};
}

# print merged map
foreach (sort {$a <=> $b} keys %merged_map) {
	print $merged_map{$_};
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: insert_rows_keyed.pl <source> <insert> <key_columns> [<destination>]

    Insert the lines from <insert> into <source> and store the result into the
    destination file.

    <source> the source file.
    
    <insert> the insert file.

    <key_columns> the columns used to create the keys.
        
    <destination> the destination file. If this parameter is empty, the lines
        are printed to standard out.
USE
;
}

