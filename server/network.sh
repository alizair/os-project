client1_ip="10.0.2.5"
client2_ip="10.0.2.6"
# ips for me are: c1: 10.0.2.5, c2: 10.0.2.6, s: 10.0.2.7
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