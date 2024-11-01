#!/bin/bash

file_names=$(find ~/ -size +1M)
num_files=$(find ~/ -size +1M | wc -l)
echo "Files larger than 1M:" | tee -a ./bigfile
echo -e "$file_names\n" | tee -a ./bigfile
echo "Number of files found: $num_files" | tee -a ./bigfile

timestamp=$(date +"%Y-%m-%d %H:%M:%S")
echo -e "Search Date: $timestamp\n" | tee -a ./bigfile

# Check if bigfile is not empty
if [ -s ./bigfile ]; then
    # Prepare the email content
    mail_body="$(cat ./bigfile)"
    
    # Send the email to the system administrator
    echo "$mail_body" | mail -s "Large Files Report" unsecurea64@gmail.com
fi