################################################################################
#
# Compute the sum of all values stored in one column
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $column, $destination) = @ARGV;

print_operation("compute_sum", $source, $column, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

# open source and destination file
my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">>$destination";
	select DESTINATION_FILE;
}

# compute average occurence in source file
my $total;
my $sum;
foreach(@source_lines) {
	$total++;
	my @values = split(/\s+/);
	if($column < 1 || $column > $#values+1) {
		die "the column number $column is not valid for the file $source";
	}
	$sum += $values[$column-1];
}
my $average = $sum/$total;
print "$source $total $sum $average\n";
    
#close files
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: compute_sum.pl <source> <column> [<destination>]

    Compute the sum over the values in a column and *append* the result to the
    destination files. The output is the source file name, the total number of
    lines t, the sum s, and the average s/t.

    <column> the number of the column. For instance, 1 will sum up the values
        stored in the first column.

    <source> the source file.

    <destination> the the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}
