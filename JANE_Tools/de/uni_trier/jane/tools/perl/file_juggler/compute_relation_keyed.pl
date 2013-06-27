################################################################################
#
# Relate the values of one file to those of another file
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $scolumn, $reference, $rcolumn, $keys, $key_columns,
    $destination) = @ARGV;

print_operation("compute_relation", $source, $scolumn, $reference, $rcolumn,
    $keys, $key_columns, $destination);

if($#ARGV < 5 || $#ARGV > 6) {
	usage();
}

my %key_line_map = read_keyed_lines($keys, $key_columns);
my %source_line_map = read_keyed_lines($source, $key_columns);
my %reference_line_map = read_keyed_lines($reference, $key_columns);

if($destination) {
	open DESTINATION_FILE, ">>$destination";
	select DESTINATION_FILE;
}

# compute the relation between source and destination
my $total = 0;
my $sum = 0;
foreach my $key (keys %key_line_map) {
	if(not $source_line_map{$key}) {
		die "$source does not contain a line with the key $key";
	}
	if(not $reference_line_map{$key}) {
		die "$reference does not contain a line with the key $key";
	}
	my $src_line = $source_line_map{$key};
	my $ref_line = $reference_line_map{$key};
	my @src_values = split(/\s+/, $src_line);
	my @ref_values = split(/\s+/, $ref_line);
	my $relation = $src_values[$scolumn - 1]/$ref_values[$rcolumn - 1];
	$sum += $relation;
	$total++;
}
my $average = $sum/$total;
print "$source $total $sum $average\n";


select STDOUT;
close REFERENCE_FILE;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: compute_relation_keyed.pl <source> <scolumn> <reference> <rcolumn>
    <keys> <kcolumns> [<destination>]

    Relate the values of source to those of reference and *append* the result
    to the destination file. The result is the total number of lines the sum
    over (source value/reference value) and the mean value. The relation is
    computed for each key value from the keys file. 

    <source> the source file.

    <scolumn> the column of the values from <source>.

    <reference> the file reference file.

    <rcolumn> the column of the values from <source> and <reference>.

    <keys> the file which contains the keys. The key values are extracted from
        this file by using the kcolumns parameter.
        
    <kcolumns> the columns which are used as key values.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}
