################################################################################
#
# Apply a command on all possible tuples which can be created on a given domain.
#
# Author: Hannes Frey
#
################################################################################

use strict;

if($#ARGV < 1) {
	usage();
}

my $command = pop(@ARGV);

my @domain;
foreach(@ARGV) {
	my @axis = split(/\s+/);
	push(@domain, \@axis);
}

my @values;
visit_all_tuples(0, \@domain, \@values);


sub visit_all_tuples {
	my($i, $domain, $values) = @_;
	if($i <= $#$domain) {
		my $axis = @$domain[$i];
		my $value;
		foreach $value (@$axis) {
			push(@$values, $value);
			visit_all_tuples($i+1, $domain, $values);
			pop(@$values);
		}
	}
	else {
		my $line = substitute_tuple_values($command, @values);
		$line =~ s/'/"/g;
		system $line;
	}
}


sub substitute_tuple_values {
    my($string) = shift @_;
    my(@tuple) = @_;
    if($#tuple >= 9) {
        die "The tuple has more than 9 elements.";
    }
    foreach my $i (0..$#tuple) {
        my $key = "#".($i+1);
        my $value = $tuple[$i];
        $string =~ s/$key/$value/g;
    }
    return $string;
}


################################################################################

sub usage {
die<<USE
Usage: visit.pl <domain_1> ... <domain_n> <command>

    Execute a command for each possible tuple from the set <domain_1> x
    <domain_2> x ... x <domain_n>. The maximum number of domains is limited to
    9.

    <domain_i> the values of the ith components in the tuples. The values are
        expected to be separated by white space characters. For instance, "a1
        a2 a3" is the three element domain {a1, a2, a3}.

    <command> the command to be executed. Before the commend gets executed, the
        generated tuple components are inserted into this string. Each
        occurence of #i is replaced by the ith component of the current tuple.
        For instance, a tuple (a,b,c) and a command string "command #1 #2 #3"
        will result in execution of "command a b c". In addition, for
        compatility with MS DOS each occurence of an ' will be replaced by an ".
USE
}

