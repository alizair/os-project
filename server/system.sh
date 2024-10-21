#!/bin/bash

echo "Listing the files, directories, subdirectories in the user's home directory"
ls -lR ~ | tee -a ./disk_info.log

echo "Showing the disk space that is used and remaining"
df -h | tee -a ./disk_info.log

echo "----------------------------------"

vmstat -s | awk '
/total memory/ {total=$1}
/used memory/ {used=$1}
/free memory/ {free=$1}
END {
	printf "Used Memory: %.2fGB (%.2f%%)\n", used/1024/1024, (used/total)*100;
	printf "Free Memory: %.2fGB (%.2f%%)\n", free/1024/1024, (free/total)*100;
}' | tee -a ./mem_cpu_info.log


# storing the cpu model in a variable
cpu_model=$(grep -m 1 'model name' /proc/cpuinfo)
# removing the 'model name' string from the output
cpu_model=${cpu_model:13}
echo "CPU Model:" $cpu_model | tee -a ./mem_cpu_info.log

# storing the number of logical cores in a variable
cores=$(grep -m 1 'cpu cores' /proc/cpuinfo)
cores=${cores:12}
echo "Number of physical cores:" $cores |  tee -a ./mem_cpu_info.log