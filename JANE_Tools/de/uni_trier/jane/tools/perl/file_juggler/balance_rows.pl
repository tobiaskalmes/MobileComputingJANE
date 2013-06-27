################################################################################
#
# Balance rows from a set of data files.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($source, $reference, $columns, $destination) = @ARGV;
my(@column_index);


print_operation("balance_rows", $source, $reference, $columns, $destination);

if($#ARGV < 2 || $#ARGV > 3) {
	usage();
}

@column_index = split(/\s+/, $columns);

# read the key values
my %keys;
open REF_FILE, "<$reference"
	or die "Can't open reference file $reference";
while(<REF_FILE>) {
	my $key = create_key($_, $reference);
	$keys{$key} = 1;
}
close REF_FILE;

# open source and destination file
my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">$destination";
	select DESTINATION_FILE;
}

# extract rows from source file
foreach(@source_lines) {
	my $key = create_key($_, $source);
	if(exists $keys{$key}) {
		print $_;
	}
}

#close files
select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


#
# create the key by using the passed line and by using the
# global column index array.
#
sub create_key {
	my($line, $file_name) = @_;
	my @values = split(/\s+/, $line);
	my $vlen = $#values;
	my $clen = $#column_index;
	my $key = "";
	foreach my $i (0..$clen) {
		my $pos = $column_index[$i];
		if($pos < 1 || $pos > $vlen+1) {
			die "$pos is not a valid column index for $file_name";
		}
		$key = $key.$values[$pos-1];
		if($i < $clen) {
			$key = $key." ";
		}
	}
	return $key;
}


################################################################################

sub usage {
die<<USE
Usage: balance_rows.pl <source> <reference> <columns> [<destination>]

    Extract all lines from the source which have a key value from reference and
    write them into the destination files.

    <source> the source file.

    <reference> the file used to read all key values.

    <columns> the columns used to create the keys for each line. The parameter
        consists of column numbers separated by white space characters. The
        values of each line are combined to a key by separating them with a
        single blank. For instance, the values "42", "[0:1]" are combined to
        "42 [0:1]".

    <destination> the destination file. If this parameter is empty the result
        is printed to standard out.
USE
;
}
