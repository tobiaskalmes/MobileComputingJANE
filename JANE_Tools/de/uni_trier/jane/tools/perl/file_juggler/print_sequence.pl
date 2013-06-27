################################################################################
#
# This is a command line tool in order to create a sequence of simulation runs.
#
# Author: Hannes Frey
#
################################################################################

use strict;
use SimulationSequence;

my($domain, $start, $end, $line, $file) = @ARGV;
my($total, $current,%used_files);

#
# print usage
#
if($#ARGV < 3 || $#ARGV > 4) {
print<<USE
Usage: print_sequence.pl <domain> <start> <end> <line> [<file>]

    Print <line> for each possible tuple from the <domain>. For each
    tuple printing is repeated <end> - <start> times.
    
    <domain> the domain used to create all tuples.
USE
;
print_general_domain_usage();
print<<USE

    <start> the start value of the counter used to replace #n or #m
    
    <end> the end value of the counter used to replace #n or #m

    <line> the pattern used to generate all lines.
USE
;
print_general_substitution_usage();
print<<USE
        In addition, each occurence of #n is replaced by the current
        counter value between <start> and <end>. Each occurence of #m
        is replaced by this value including trailing zeros. Furthermore,
        each occurence of #c is replaced by the current counter value
        between 1 and the total number of lines. Each occurence of #d
        is replaced by by the same counter including trailing zeros.
        Finally, each occurence of #t is replaced by the total number
        of lines.

    <file> this optinal parameter is the name of the file in order to
        write the lines. You may use a pattern including #1, ..., #9 in
        order to write the lines in different files. If no file name is
        passed the lines will be writen to standard out.
USE
;
    exit;
}


#
# print line for all tuples
#
my @d = create_domain($domain);
$total = 1 + $end - $start;
foreach(@d) {
    $total *= 1 + $#$_;
}
$current = 1;
visit_all_tuples(\@d, \&print_line);


#
# subroutine used to print the line
#
sub print_line {

    my(@tuple) = @_;
    
    # open the optional file
    if($file) {
      my $name = substitute_tuple_values($file, @tuple);
      if(exists $used_files{$name}) {
          open FILE, ">>$name";
      }
      else {
          $used_files{$name} = 1;
          open FILE, ">$name";
          print "$name\n";
      }
      select FILE;
    }
    
    # write all lines from start to end
    foreach($start..$end) {
        my $result = substitute_tuple_values($line, @tuple);
        $result =~ s/#t/$total/g;
        $result =~ s/#c/$current/g;
        my $tc = add_trailing_zero($current, length($total));
        $result =~ s/#d/$tc/g;
        $result =~ s/#n/$_/g;
        my $tn = add_trailing_zero($_, length($end));
        $result =~ s/#m/$tn/g;
        print $result."\n";
        $current++;
    }
    
    #close the optional file
    close FILE;
    select STDOUT;

}

