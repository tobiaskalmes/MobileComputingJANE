################################################################################
#
# Append rows from source to destination file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $destination) = @ARGV;

print_operation("append_rows", $source, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">>$destination";
	select DESTINATION_FILE;
}

# extract rows from source file
foreach(@source_lines) {
	print $_;
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: append_rows.pl <source> [<destination>]

    Append the lines of source to the destination file.

    <source> the source file.
    
    <destination> the destination file. If this parameter is empty, the lines
        are printed to standard out.
USE
;
}

