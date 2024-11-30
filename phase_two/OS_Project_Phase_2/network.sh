#!/bin/bash

if [[ $# -lt 2 ]]; then
    echo "Error: please pass the ip addresses of the two client vms"
    exit 1
fi

# checking if the ping command is found on the system
if ! command -v ping &> /dev/null; then
    echo "ping could not be found, installing ..."
    sudo apt install -y iputils-ping &> /dev/null || sudo dnf install -y iputils &> /dev/null
    echo "ping installed successfully."
fi

# checking if the traceroute command is found on the system
if ! command -v traceroute &> /dev/null; then
    echo "traceroute could not be found, installing ..."
    sudo apt install -y traceroute &> /dev/null || sudo dnf install -y traceroute &> /dev/null
    echo "tracerouter installed successfully."
fi

client1_ip=$1
client2_ip=$2

for i in {1..3}; do
    echo "pinging client 1 ip: $client1_ip ..."
    if ping -c 3 -W 3 "$client1_ip"; then    #-c 3 means send 3 packets, -W means wait time is 3 seconds
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "$timestamp: Connectivity with $client1_ip is ok" | tee -a ./network.log  #takes input from previous command output and appends (-a) to network.log
    else
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "Connection with $client1_ip is down at $timestamp"
        echo "Connection with $client1_ip is down at $timestamp" | tee -a ./network.log
        ./traceroute.sh "$client1_ip"
        # Uncomment the next line if you want to reboot here, im not sure if its in traceroute or here
        # sudo reboot
    fi

    echo "pinging client 2 ip: $client2_ip ..."
    if ping -c 3 -W 3 "$client2_ip"; then     
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "$timestamp: Connectivity with $client2_ip is ok" | tee -a ./network.log
    else
        timestamp=$(date +"%Y-%m-%d %H:%M:%S")
        echo "Connection with $client2_ip is down at $timestamp"
        echo "Connection with $client2_ip is down at $timestamp" | tee -a ./network.log
        ./traceroute.sh "$client2_ip"
        # Uncomment the next line if you want to reboot here, im not sure if its in traceroute or here
        # sudo reboot
    fi
done
