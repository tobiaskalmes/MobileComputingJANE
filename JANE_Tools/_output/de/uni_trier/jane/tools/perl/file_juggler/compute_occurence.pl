################################################################################
#
# Compute the occurences of all lines matching a pattern
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;


my($source, $regex, $destination) = @ARGV;

print_operation("compute_occurence", $source, $regex, $destination);

if($#ARGV < 1 || $#ARGV > 2) {
	usage();
}

my @source_lines = read_file($source);
if($destination) {
	open DESTINATION_FILE, ">>$destination";
	select DESTINATION_FILE;
}

# compute average occurence in source file
my $total;
my $occurences = 0;
foreach(@source_lines) {
	$total++;
	if(/$regex/) {
		$occurences++;
	}
}
my $average = $occurences/$total;
print "$source $total $occurences $average\n";

select STDOUT;
close SOURCE_FILE;
close DESTINATION_FILE;


################################################################################

sub usage {
die<<USE
Usage: compute_occurence.pl <source> <regex> <destination>

    Compute the number of matching lines and *append* the result to the
    destination file. The output is the source file name, the total number of
    lines t, the number of matching lines m, and the average m/t.

    <source> the source file.
    
    <regex> a regular expression the line has to match. For instance,
        ".*DELIVER.*" will include all lines containing the character sequence
        DELIVER.

    <destination> the destination file. In case of no destination file the
        result is written to standard out.
USE
;
}
