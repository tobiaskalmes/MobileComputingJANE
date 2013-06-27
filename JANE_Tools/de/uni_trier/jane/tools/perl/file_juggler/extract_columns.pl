################################################################################
#
# Extract columns from a data file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $columns, $destination) = @ARGV;

print_operation("extract columns", $source, $columns, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# extract columns from source file
foreach(@source_lines) {
	my @values = get_row_values($columns, $_, $source);
	my $line = array_to_string(@values, " ");
	print "$line\n";
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

