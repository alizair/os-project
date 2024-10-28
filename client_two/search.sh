#!/bin/bash

file_names=$(find ~/ -size +1M)
num_files=$(find ~/ -size +1M | wc -l)
echo "Files larger than 1M:" | tee -a ./bigfile
echo -e "$file_names\n" | tee -a ./bigfile
echo "Number of files found: $num_files" | tee -a ./bigfile

timestamp=$(date +"%Y-%m-%d %H:%M:%S")
echo "Search Date: $timestamp" | tee -a ./bigfile

#Check if 'bigfile' is not empty and send an email
if [ -s ./bigfile ]; then
    # Prepare the email content
    mail_body="Files larger than 1M found on $timestamp:\n$(cat ./bigfile)"
    
    # Send the email to the system administrator
    echo -e "$mail_body" | mail -s "Large Files Report" QUID@qu.edu.qa
fi