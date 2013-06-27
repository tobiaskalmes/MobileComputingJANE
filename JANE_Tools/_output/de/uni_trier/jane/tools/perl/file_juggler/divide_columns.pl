################################################################################
#
# Divide values of two columns
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $scolumn, $reference, $rcolumn, $destination) = @ARGV;

print_operation("divide_columns", $source, $scolumn, $reference, $rcolumn, $destination);

if($#ARGV < 3 || $#ARGV > 4) {
	usage();
}

my @source_lines = read_file($source);
my @reference_lines = read_file($reference);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# divide columns
foreach my $i (0..$#source_lines) {
	my @source_values = split(/\s+/, $source_lines[$i]);
	my @reference_values = split(/\s+/, $reference_lines[$i]);
	my $result = $source_values[$scolumn]/$reference_values[$rcolumn];
	print "$result\n";
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: extract_columns.pl <source> <columns> [<destination>]

    Extract columns from the source file and write them into the destination
    file. The values in each row are expected to be separated by white spaces.

    <source> the source file.

    <columns> a list of numbers used to extract the columns. For instance,
        "1 3 1 7" will extract the columns 1, 3, and 7 and write them in the
        order 1 3 1 7 into the destination file.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.        
USE
;
}

