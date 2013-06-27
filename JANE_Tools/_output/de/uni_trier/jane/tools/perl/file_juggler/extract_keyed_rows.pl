################################################################################
#
# Extract all rows which have a matching key
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $keys, $key_columns, $destination) = @ARGV;

print_operation("extract_keyed_rows", $source, $keys, $destination);

if($#ARGV < 3 || $#ARGV > 4) {
	usage();
}

# just check for duplicates
my @key_lines = read_file($keys);
my %source_line_map = read_keyed_lines($source, $key_columns);

if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# print all matching lines
my %key_map;
foreach my $line (@key_lines) {
	my @row = get_row_values($key_columns, $line, $keys);
	my $key = array_to_string(@row, " ");
	if($key_map{$key}) {
		die "$key is duplicate in file $keys";
	}
	$key_map{$key} = 1;
	if(not $source_line_map{$key}) {
		die "$source does not contain a line with the key $key";
	}
	my $src_line = $source_line_map{$key};
	print $src_line;
}


select STDOUT;
close REFERENCE_FILE;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: extract_keyed_rows.pl <source> <keys> <kcolumns> [<destination>]

    Extract all rows from source file which have a matching key in the key
    file.

    <source> the source file.

    <keys> the file which contains the keys. The key values are extracted from
        this file by using the kcolumns parameter.
        
    <kcolumns> the columns which are used as key values.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}

