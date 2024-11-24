#!/bin/bash

# Ensuring the required commands are installed: ping, traceroute, route, and hostname
# we are only considering ubuntu / debian-based linux distributions and fedora / red-hat based linux distributions
# therefore we use apt for installing the packages first and then fallback to using dnf as the package manager if using
# apt fails
if ! command -v ping &> /dev/null; then
        echo "ping could not be found, installing ..."
        # using apt update to refresh the packages metadata however, this is not really needed for dnf since dnf install
        # does that implicity
        sudo apt update &> /dev/null
        sudo apt install -y iputils-ping &> /dev/null || sudo dnf install -y iputils-ping &> /dev/null
        echo "ping installed successfully."
fi
if ! command -v traceroute &> /dev/null; then
        echo "traceroute could not be found, installing ..."
        sudo apt update &> /dev/null
        sudo apt install -y traceroute &> /dev/null || sudo dnf install -y traceroute &> /dev/null
        echo "traceroute installed successfully."
fi
if ! command -v route &> /dev/null; then
        echo "route could not be found, installing ..."
        sudo apt update &> /dev/null
        sudo apt install -y net-tools  &> /dev/null || sudo dnf install -y net-tools &> /dev/null
        echo "route installed successfully."
fi
if ! command -v hostname &> /dev/null; then
        echo "hostname could not be found, installing ..."
        sudo apt update &> /dev/null
        sudo apt install -y hostname &> /dev/null || sudo dnf install -y hostname &> /dev/null
        echo "hostname installed successfully."
fi


if [[ $# -lt 1 ]]; then
    echo "Error: please pass the IP address of the target VM"
    exit 1
fi

target_ip=$1

log_file="./network.log"
timestamp=$(date +"%Y-%m-%d %H:%M:%S")

echo "$timestamp: Starting traceroute diagnostics for $target_ip" | tee -a "$log_file"

# we display the routing table for the provided ip
echo "$timestamp: Displaying routing table" | tee -a "$log_file"
route -n | tee -a "$log_file"

# Display hostname
echo "$timestamp: Displaying hostname" | tee -a "$log_file"
hostname | tee -a "$log_file"

# Testing the DNS Server, we find dns server name in resolv.conf, 
dns_server=$(grep "nameserver" /etc/resolv.conf | awk '{print $2}' | head -n 1)
if [[ -n "$dns_server" ]]; then
    echo "$timestamp: Testing local DNS Server: $dns_server" | tee -a "$log_file"
    if ping -c 3 -W 3 "$dns_server" &> /dev/null; then
        echo "$timestamp: DNS Server is reachable" | tee -a "$log_file"
    else
        echo "$timestamp: DNS Server is not reachable" | tee -a "$log_file"
    fi
else
    echo "$timestamp: No DNS Server configured" | tee -a "$log_file"
fi

# Tracrout google.com
echo "$timestamp: Tracing route to google.com" | tee -a "$log_file"
traceroute google.com | tee -a "$log_file"

# Pinging google.com to test connectivity
echo "$timestamp: Pinging google.com" | tee -a "$log_file"
if ping -c 3 -W 3 google.com &> /dev/null; then
    echo "$timestamp: Connectivity to google.com is ok" | tee -a "$log_file"
else
    echo "$timestamp: Cannot reach google.com" | tee -a "$log_file"
fi

# we Try to trace route to the target IP, ping it, and if there is no connection we reboot our machine
echo "$timestamp: Tracing route to $target_ip" | tee -a "$log_file"
traceroute "$target_ip" | tee -a "$log_file"
echo "$timestamp: Pinging $target_ip" | tee -a "$log_file"
if ping -c 3 -W 3 "$target_ip" &> /dev/null; then
    echo "$timestamp: Connectivity to $target_ip is ok" | tee -a "$log_file"
else
    echo "$timestamp: Cannot reach $target_ip. Rebooting the system..." | tee -a "$log_file"
    sudo reboot
fi