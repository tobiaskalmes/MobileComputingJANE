################################################################################
#
# Extract rows (text lines) from a data file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $compare, $column, $destination) = @ARGV;

print_operation("extract_rows_less_than", $source, $compare, $column, $destination);

if($#ARGV < 2 || $#ARGV > 3) {
	usage();
}

my @source_lines = read_file($source);
my @compare_lines = read_file($compare);
if($#source_lines != $#compare_lines) {
	die "$source and $compare have a different number of lines";
}

if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# compute average occurence in source file
foreach my $i (0..$#source_lines) {
	my @source_values = split(/\s+/, $source_lines[$i]);
	my @compare_values = split(/\s+/, $compare_lines[$i]);
	if($column < 1 || $column > $#source_values+1 || $column > $#compare_values+1) {
		die "the column number $column is not valid for the files $source or $compare";
	}
	if($source_values[$column-1] < $compare_values[$column-1]) {
		print $source_lines[$i];
	}
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: extract_rows_less_than.pl <source> <compare> <column> [<destination>]

    Extract all lines form source whose value stored on column position is less
    than the value stored in compare at column position and write them into the
    destination file.

    <source> the source file.

    <compare> the file used to compare the values.
    
    <column> the column where the values are stored in each line.

    <destination> the destination file. If this parameter is empty, the lines
        are printed to standard out.
USE
;
}

