################################################################################
#
# Extract rows (text lines) from a data file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $regex, $destination) = @ARGV;

print_operation("extract_rows", $source, $regex, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# extract rows from source file
foreach(@source_lines) {
	if(/$regex/) {
		print $_;
	}
}
    
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

