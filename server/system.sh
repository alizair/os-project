#!/bin/bash

echo "Listing the files, directories, subdirectories in the user's home directory"
ls -lR ~ | tee -a ./disk_info.log

echo "Showing the disk space that is used and remaining"
df -h | tee -a ./disk_info.log

echo "----------------------------------"

# storing the cpu model in a variable
cpu_model=$(grep -m 1 'model name' /proc/cpuinfo)
# removing the 'model name' string from the output
cpu_model=${cpu_model:13}
echo "CPU Model:" $cpu_model | tee -a ./mem_cpu_info.log

# storing the number of logical cores in a variable
cores=$(nproc)
echo "Number of logical cores:" $cores |  tee -a ./mem_cpu_info.log