#!/bin/bash

TRAIN="$1"
TEST="$2"
STOP_ACC="$3"
OUTPUT="$4"

# Decompress bz2 files if needed
if [[ "$TRAIN" == *.bz2 ]]; then
    TRAIN_DECOM="${TRAIN%.bz2}"
    if [ ! -f "$TRAIN_DECOM" ]; then
        bunzip2 -k "$TRAIN"
    fi
    TRAIN="$TRAIN_DECOM"
fi

if [[ "$TEST" == *.bz2 ]]; then
    TEST_DECOM="${TEST%.bz2}"
    if [ ! -f "$TEST_DECOM" ]; then
        bunzip2 -k "$TEST"
    fi
    TEST="$TEST_DECOM"
fi

./gdbt -data "$TRAIN" -data_test "$TEST" -J 48 -v 0.25 -iter 1000 -stop_acc "$STOP_ACC" -output_pred "$OUTPUT" -n_threads 8
