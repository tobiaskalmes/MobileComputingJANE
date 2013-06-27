################################################################################
#
# Extract lines matching line numbers from a data file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $numbers, $destination) = @ARGV;

print_operation("extract_rows_numbered", $source, $numbers, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

my @source_lines = read_file($source);
my @number_lines = read_file($numbers);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# extract rows from source file
foreach(@number_lines) {
	print $source_lines[$_ - 1];
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: extract_rows_numbered.pl <source> <numbers> [<destination>]

    Extract all matching lines from the source file and write them into the
    destination file.

    <source> the source file.
    
    <numbers> a file containing a sequence of numbers which are separated by a
        newline. For instance, "12 <newline> 42 <newline> 8 <newline>" will
        extract the lines 12, 42, and 8 (in that order).

    <destination> the destination file. If this parameter is empty, the lines
        are printed to standard out.
USE
;
}

