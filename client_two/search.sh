#!/bin/bash

file_names=$(find ~/ -size +1M)
num_files=$(find ~/ -size +1M | wc -l)
echo "Files larger than 1M:" | tee -a ./bigfile
echo -e "$file_names\n" | tee -a ./bigfile
echo "Number of files found: $num_files" | tee -a ./bigfile

timestamp=$(date +"%Y-%m-%d %H:%M:%S")
echo "Search Date: $timestamp" | tee -a ./bigfile