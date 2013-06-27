################################################################################
#
# Check if there exists a line for each key.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $keys, $key_columns, $destination) = @ARGV;

print_operation("check_order", $source, $keys, $key_columns, $destination);

if($#ARGV < 2 || $#ARGV > 3) {
	usage();
}

my @source_lines = read_file($source);
my @key_lines = read_file($keys);

if($destination) {
	open DESTINATION_FILE, ">>$destination";
	select DESTINATION_FILE;
}

my $result = "ok";
if($#source_lines == $#key_lines) {
	foreach my $i (0..$#source_lines) {
		my @source_row = get_row_values($key_columns, $source_lines[$i], $source);
		my $source_key = array_to_string(@source_row, " ");
		my $key = $key_lines[$i];
		if($key != $source_key) {
			$result = "error";
		}
	}
}
else {
	$result = "error";
}

print "$source $result\n";	


select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: extract_rows.pl <source> <regex> [<destination>]

    Extract all matching lines from the source file and write them into the
    destination file.

    <source> the source file.
    
    <regex> a regular expression the line has to match. For instance,
        ".*DELIVER.*" will include all lines containing the character sequence
        DELIVER.

    <destination> the destination file. If this parameter is empty, the lines
        are printed to standard out.
USE
;
}

