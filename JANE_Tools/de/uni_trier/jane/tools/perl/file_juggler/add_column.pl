################################################################################
#
# Add a first column into a data file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $column, $destination) = @ARGV;
my(@column_values);


print_operation("add_column", $source, $column, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

@column_values = split(/\s+/, $column);

my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# add columns to source file
my $i=0;
foreach(@source_lines) {
	if($i > $#column_values) {
		die "The column '$column' has not enough values for $source.";
	}
	print $column_values[$i]." ".$_;
	$i++;
}
if($i <= $#column_values) {
	die "The column '$column' has to many values for $source.";
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: add_column.pl <source> <column> [<destination>]

    Add a column as the first one of the columns of the source files and write
    the result into the destination file. The values in each row are expected to
    be separated by white spaces.

    <source> the the source file.

    <column> a list of column entries to be inserted. For instance, "1 2 3 4"
        will add 1, 2, 3, 4 to the lines of the current source file and writes
        the extended lines into the destination file.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}
