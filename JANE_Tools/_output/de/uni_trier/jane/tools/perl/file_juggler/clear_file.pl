################################################################################
#
# Create a new file if it does not exist
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;


my($file) = @ARGV;

print_operation("clear_file", $file, "");

if($#ARGV != 0) {
	usage();
}

open FILE, ">$file";
close FILE;


################################################################################

sub usage {
die<<USE
Usage: touch_file.pl <file>

    Create a new file if it does not exist.

    <file> the file name.
USE
;
}
