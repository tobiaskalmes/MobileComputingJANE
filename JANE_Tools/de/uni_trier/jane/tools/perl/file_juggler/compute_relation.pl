################################################################################
#
# Relate the values of one file to those of another file
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $scolumn, $reference, $rcolumn, $destination) = @ARGV;

print_operation("compute_relation", $source, $destination, $reference);

if($#ARGV < 3 || $#ARGV > 4) {
	usage();
}

# open reference, source and destination file
my @source_lines = read_file($source);
my @reference_lines = read_file($reference);
if($destination) {
	open DESTINATION_FILE, ">>$destination";
	select DESTINATION_FILE;
}

# extract rows from source file
if($#source_lines != $#reference_lines) {
	die "$source and $reference don't have the same number of lines.";
}
my $total = 0;
my $sum = 0;
foreach my $i (0..$#source_lines) {
	my $src_line = $source_lines[$i];
	my $ref_line = $reference_lines[$i];
	my @src_values = split(/\s+/, $src_line);
	my @ref_values = split(/\s+/, $ref_line);
	my $relation = $src_values[$scolumn - 1]/$ref_values[$rcolumn - 1];
	$sum += $relation;
	$total++;
}
my $average = $sum/$total;
print "$source $total $sum $average\n";

#close files
select STDOUT;
close REFERENCE_FILE;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: compute_relation.pl <source> <scolumn> <reference> <rcolumn>
    [<destination>]

    Relate the values of source to those of reference and *append* the result
    to the destination file. The result is the total number of lines the sum
    over (source value/reference value) and the mean value.

    <source> the source file.

    <scolumn> the column of the values from <source>.

    <reference> the file reference file.

    <rcolumn> the column of the values from <reference>.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}
