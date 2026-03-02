#!/usr/bin/perl -w

use strict;
use warnings;
use FindBin qw($Bin);

my $knownSetsFile = "$Bin/known-sets.txt";
my $generatorScript = "$Bin/gen-list-unimplemented-cards-for-set.pl";

open(my $knownSetsHandle, '<', $knownSetsFile) || die "can't open $knownSetsFile";
my @setNames;
while (my $line = <$knownSetsHandle>) {
    chomp $line;
    next if $line =~ /^\s*$/;

    my ($setName) = split('\|', $line);
    next if !defined $setName || $setName eq '';
    push(@setNames, $setName);
}
close($knownSetsHandle);

chdir($Bin) || die "can't change directory to $Bin";

my $setsWithUnimplemented = 0;

foreach my $setName (@setNames) {
    my $pid = open(my $scriptOutput, '-|', $^X, '-X', $generatorScript, $setName);
    die "can't run $generatorScript for '$setName'" if !defined $pid;

    my $outputFile;
    my $unimplementedCards = 0;

    while (my $line = <$scriptOutput>) {
        chomp $line;
        if ($line =~ /^Issue tracker generated:\s+(.+)$/) {
            $outputFile = $1;
            next;
        }
        if ($line =~ /^Unimplemented cards:\s+(\d+)$/) {
            $unimplementedCards = $1;
        }
    }
    close($scriptOutput);

    if ($? != 0) {
        warn "failed to generate issue tracker for '$setName'\n";
        next;
    }

    if ($unimplementedCards > 0) {
        $setsWithUnimplemented++;
        my $fileName = defined $outputFile ? $outputFile : '(unknown output file)';
        print "$setName: $fileName ($unimplementedCards unimplemented cards)\n";
    }
    elsif (defined $outputFile && -e $outputFile) {
        unlink($outputFile) || warn "can't remove $outputFile: $!\n";
    }
}

if ($setsWithUnimplemented == 0) {
    print "All known sets are fully implemented.\n";
}
