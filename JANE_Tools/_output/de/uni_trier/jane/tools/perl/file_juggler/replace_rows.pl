################################################################################
#
# Replace rows in the source file with those from the replace file and store
# the result into the destination file.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $replace_file, $key_columns, $destination) = @ARGV;

print_operation("replace_rows", $source, $replace_file, $key_columns, $destination);

if($#ARGV < 2 || $#ARGV > 3) {
	usage();
}

my %replace_map = read_keyed_lines($replace_file, $key_columns);
my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

foreach my $line (@source_lines) {
	my @row = get_row_values($key_columns, $line, $source);
	my $key = array_to_string(@row, " ");
	if($replace_map{$key}) {
		print $replace_map{$key};
	}
	else {
		print $line;
	}
}
    
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: replace_rows.pl <source> <replace> <key_columns> [<destination>]

    Replace the lines of source with those from the replace file and store the
    result into the destination file.

    <source> the source file.
    
    <replace> the replace file.

    <key_columns> the columns used to create the keys.
        
    <destination> the destination file. If this parameter is empty, the lines
        are printed to standard out.
USE
;
}

