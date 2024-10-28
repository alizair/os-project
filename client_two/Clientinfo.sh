#!/bin/bash

log_file="process_info.log"
echo "Process Information on $(date):" > $log_file

# Process tree of all currently running processes
echo -e "\nProcess Tree:" >> $log_file
ps -ef --forest >> $log_file

echo -e "\nZombie Processes:" >> $log_file
ps aux | awk '$8=="Z" {print $0}' >> $log_file

echo -e "\nCPU Usage:" >> $log_file
top -b -n 1 | grep "Cpu(s)" >> $log_file

echo -e "\nMemory Usage:" >> $log_file
free -h >> $log_file

echo -e "\nTop 5 Resource-Consuming Processes:" >> $log_file
ps -eo pid,ppid,cmd,%mem,%cpu --sort=-%mem | head -n 6 >> $log_file

# Securely copy the log file to VM2
scp "$file_name" username@VM2_IP:/path/to/destination/folder/
