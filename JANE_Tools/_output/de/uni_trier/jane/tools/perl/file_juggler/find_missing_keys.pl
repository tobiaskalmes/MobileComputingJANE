################################################################################
#
# Extract all rows which have a matching key
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $reference, $destination) = @ARGV;

print_operation("find_missing_keys", $source, $reference, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

my @key_lines = read_file($source);
my %source_key_map;
foreach(@key_lines) {
	$source_key_map{$_} = 1;
}
my @reference_key_lines = read_file($reference);

if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# print all keys missing in source
foreach(@reference_key_lines) {
	if(not exists $source_key_map{$_}) {
		print $_;
	}
}


select STDOUT;
close REFERENCE_FILE;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: find_missing_keys.pl <source> <reference> [<destination>]

    Find all keys in reference which are missing in source.

    <source> the source file.

    <reference> the reference file.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}

