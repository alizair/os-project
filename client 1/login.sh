#!/bin/bash
#for security reasons we should only allow root to be able to read and write
SERVER_IP="10.0.2.7"
SFTP_USER="client1"
SFTP_PASS="1234"
INVALID_ATTEMPTS_LOG="invalid_attempts.log"
CLIENT_LOG="client_timestamp_invalid_attempts.log"
MAX_ATTEMPTS=3
ATTEMPT_COUNT=0

# Check and install dependencies 
if ! command -v ssh &> /dev/null; then
    echo "ssh not found. Installing..."
    sudo apt update
    sudo apt install -y ssh
fi

if ! command -v sshpass &> /dev/null; then
    echo "sshpass not found. Installing..."
    sudo apt update
    sudo apt install -y sshpass
fi

if ! command -v sftp &> /dev/null; then
    echo "sftp not found. Installing..."
    sudo apt update
    sudo apt install -y openssh-client
fi

# Request user credentials
read -p "Enter username: " username
read -s -p "Enter password: " password #-s makes it secret, doesn't display what the user inputs

log_invalid_attempt() {
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "$timestamp - Invalid login attempt for user: $username" >> $INVALID_ATTEMPTS_LOG
}

# To copy the log file to the server using SFTP
copy_log_to_server() {
    echo "Copying log file to server..."
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no -o BatchMade=no -o ConnectTimeout=5 $username@SERVER_IP "exit" 2>/dev/null
    sshpass -p "$SFTP_PASS" sftp -oBatchMode=no $SFTP_USER@$SERVER_IP <<< $"put $INVALID_ATTEMPTS_LOG"
}

# Attempt login and handle invalid attempts
while [ $ATTEMPT_COUNT -lt $MAX_ATTEMPTS ]; do 
    sshpass -p "$password" ssh $username@$SERVER_IP "exit" 2>/dev/null
    # Check if login was successful
    if [ $? -eq 0 ]; then     #check exit code 
        echo -e "\nLogin successful!"
        exit 0
    else
        echo -e "\nInvalid credentials. Please try again."
        ((ATTEMPT_COUNT++))
        log_invalid_attempt
        if [ $ATTEMPT_COUNT -lt $MAX_ATTEMPTS ]; then
            read -s -p "Enter password: " password
        fi
    fi
done

# Handle excessive invalid attempts
echo "Unauthorized user!"

# Copy log file to server
cp $INVALID_ATTEMPTS_LOG $CLIENT_LOG
copy_log_to_server

# Schedule logout after 30 seconds
echo "Logging out in 30 seconds..."
sleep 30 && pkill -KILL -u $USER

exit 1