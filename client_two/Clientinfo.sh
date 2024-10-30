#!/bin/bash

log_file="process_info.log"
username="client2"
server_ip="10.10.10.131"

if ! command -v ssh &>/dev/null || ! command -v scp &>/dev/null; then
    echo "ssh or scp not found. Installing..."
    sudo apt update
    sudo apt install -y ssh
fi

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

# Making sure the logs directory exists on the server
ssh $username@$server_ip "mkdir -p logs"

# Securely copy the log file to VM2 using SSH private key
scp "$log_file" $username@$server_ip:/home/client2/logs