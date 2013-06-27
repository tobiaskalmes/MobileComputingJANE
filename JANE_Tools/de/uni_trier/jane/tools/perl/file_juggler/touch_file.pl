################################################################################
#
# Remove all content of a file
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;


my($file) = @ARGV;

print_operation("touch_file", $file, "");

if($#ARGV != 0) {
	usage();
}

open FILE, ">>$file";
close FILE;


################################################################################

sub usage {
die<<USE
Usage: clear_file.pl <file>

    Remove all content of a file.

    <file> the file name.
USE
;
}
