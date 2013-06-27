################################################################################
#
# The functions defined in this module are intended as helper functions
# in order to create and evaluate a sequence of simulation runs.
#
# Author: Hannes Frey
#
################################################################################
package SimulationSequence;
use 5.006;
require Exporter;
use Carp;

@ISA = qw(Exporter);
@EXPORT = qw(visit_all_tuples create_domain print_general_domain_usage
	substitute_tuple_values print_general_substitution_usage add_trailing_zero
	read_file initialize_file print_operation get_row_values array_to_string
	read_keyed_lines);

use strict;

#
# visit_all_tuples(\@domain, \&function);
#
# This function will apply a function on all possible tuples which can be
# created on a given domain.
#
# $domain a reference to an array of arrays describing the domain of all
#     possible tuples. For instance, for [[a1, a2], [b1, b2, b3]] the
#     given function is called with the arguments (a1,b1), (a1,b2),
#     (a1,b3), (a2,b1), (a2,b2), (a2,b3).
# $function the function to be applied on each tuple. The tuple is passed
#     as an array.
#
sub visit_all_tuples {

    my($domain, $function) = @_;
    my @values;
    recursion(0, $domain, \@values, $function);

    sub recursion {
        my($i, $domain, $values, $func) = @_;
        if($i <= $#$domain) {
            my $axis = @$domain[$i];
            my $value;
            foreach $value (@$axis) {
                push(@$values, $value);
                recursion($i+1, $domain, $values, $func);
                pop(@$values);
            }
        }
        else {
            &$func(@$values);
        }
    }

}


# 
# @domain = create_domain($string);
#
# This function creates the domain which is required by the visit_all_tuples
# function. The passed string has to consist of a comma separated list of
# values strings. Each value string consists of a sequence of values which are
# separated by blanks. For instance the string "a1 a2, b1 b2 b3" will create the
# array of arrays [[a1, a2], [b1, b2, b3]].
#
# $string the string used to create the domain
#
sub create_domain {
    my($parameter) = @_;
    my @axis = split(/,\s*/, $parameter);
    my @result;
    foreach(@axis) {
        my @values = split(/\s+/, $_);
        push(@result, \@values);
    }
    return @result;
}


#
# print_general_domain_usage();
#
# This function can be used in order to print the required format of the string
# used by the create_domain function.
#
sub print_general_domain_usage {
print<<USE
        The passed string has to consist of a comma separated list of value
        strings. Each value string consists of a sequence of values which
        are separated by blanks. For instance, the string "a1 a2, b1 b2 b3"
        will create tuples in the following order (a1,b1), (a1,b2), (a1,b3),
        (a2,b1), (a2,b2), and (a2,b3).
USE
}


#
# $result = substitute_tuple_values($string, @tuple)
#
# This function places the passed tuple values into the string. The current tuple elements
# can be referred by #1,...,#9, i.e. #1 refers to the first tuple element, #2 to the second
# tuple element and so on. Each occurence of #i is replaced by the ith element of the tuple
# array. The function will exit with an error when the tuple array contains more than 9 values.
#
sub substitute_tuple_values {
    my($string) = shift @_;
    my(@tuple) = @_;
    if($#tuple >= 9) {
        croak "The tuple has more than 9 elements.";
    }
    foreach my $i (0..$#tuple) {
        my $key = "#".($i+1);
        my $value = $tuple[$i];
        $string =~ s/$key/$value/g;
    }
    return $string;
}


#
# print_general_substitution_usage();
#
# This function can be used in order to print the format of the string which is being passed
# to the substitute_tuple_values function.
#
sub print_general_substitution_usage {
print<<USE
        The generated domain tuple elements are inserted into this string.
        The tuple elements are referenced by #1,...,#9, i.e. #1 refers to
        the first tuple element, #2 to the second tuple element and so on.
        Each occurence is replaced by its assigned element of the tuple.
USE
}


#
# $string add_trailing_zero($number, $max)
#
# Use this function in order to add trailing zeros to a number.
#
# $number the number to be printed
# $max the maximum number of digits
#
sub add_trailing_zero {
    my($number, $max) = @_;
    my $len = length($number);
    foreach($len..$max-1) {
        $number = "0".$number;
    }
    return $number;
}


#
# @result read_file($name)
#
# Read the lines of the file into an array of strings.
#
# $name the filename
#
sub read_file {
	my($name) = @_;
	my @result;
    open FILE, "<$name" or croak "Can't read file $name";
    while(my $val = <FILE>) {
    	push(@result, $val);
    }
	close FILE;
	return @result;
}


#
# initialize_file($name, \%used_file_names)
#
# Make an existing file empty if it is used for the first time.
#
# $name the name of the file
# \%used_file_names a reference to a has which is used to store
#     all file names which have been used so far.
#
sub initialize_file {
	my($name, $used_file_names) = @_;
    if(not exists $used_file_names->{$name}) {
        $used_file_names->{$name} = 1;
        open FILE, ">$name";
        close FILE;
    }
}

#
# print_optional_to_destination($prefix, $destination)
#
# Print the operation optional destination to standard out
#
# $command the command
# $arguments the arguments
# $destination the target
#
sub print_operation {
	my($command) = shift @_;
	my($destination) = pop @_;
	print "$command(";
	my $first = 1;
	foreach(@_) {
		if(not $first) {
			print ",";
		}
		$first = 0;
		print "$_";
	}
	print ")";
    if($destination) {
	    print " -> $destination";
    }
    print "\n";
}


#
# get all column values from a line
#
sub get_row_values {
	my($columns, $line, $source) = @_;
	my @column_index = split(/\s+/, $columns);
	my @values = split(/\s+/, $line);
	my $vlen = $#values;
	my $clen = $#column_index;
	my @row;
	foreach my $i (0..$clen) {
		my $pos = $column_index[$i];
		if($pos < 1 || $pos > $vlen+1) {
			croak "$pos is not a valid column index for $source";
		}
		push(@row, $values[$pos-1]);
	}
	return @row;
}

#
# Concatenate an array to a string separated by the passed delimiter.
#
sub array_to_string {
	my($delimiter) = pop(@_);
    my $first = 1;
    my $result = "";
	foreach(@_) {
		if(not $first) {
			$result = $result.$delimiter;
		}
		$first = 0;
		$result = $result.$_;
	}
	return $result;
}


#
# %map read_keyed_lines($file, $key_columns)
#
# Read the lines of a file and store them into a map pointing from keys
# to lines. The keys are stored in the passed key columns.
#
sub read_keyed_lines {
	my($file, $key_columns) = @_;
	my %map;
	my @lines = read_file($file);
	foreach my $line (@lines) {
		my @row = get_row_values($key_columns, $line, $file);
		my $key = array_to_string(@row, " ");
		if($map{$key}) {
			croak "the key $key is duplicate in $file.";
		}
		$map{$key} = $line;
	}
	return %map;
}

################################################################################
