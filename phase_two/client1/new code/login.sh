#!/bin/bash
SERVER_IP="127.0.0.1"
SFTP_USER="client1"
INVALID_ATTEMPTS_LOG="invalid_attempts.log"
CLIENT_LOG="client_timestamp_invalid_attempts.log"
MAX_ATTEMPTS=3
ATTEMPT_COUNT=0

# Check and install dependencies 
if ! command -v ssh &>/dev/null || ! command -v sftp &>/dev/null; then
    echo "ssh or sftp not found. Installing..."
    echo "$password" | sudo -S apt update
    echo "$password" | sudo -S apt install -y ssh
fi

if ! command -v sshpass &> /dev/null; then
    echo "sshpass not found. Installing..."
    echo "$password" | sudo -S apt update
    echo "$password" | sudo -S apt install -y sshpass
fi

# Request user credentials
read -p "Enter username: " username
read -s -p "Enter password: " password  # Secret input for password

echo "$password" | sudo -S apt update   # Make sure sudo reads the password from standard input


log_invalid_attempt() {
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "$timestamp - Invalid login attempt for user: $username" >> $INVALID_ATTEMPTS_LOG
}

# To copy the log file to the server using SFTP
copy_log_to_server() {
    echo "Copying log file to server..."
    ssh -o StrictHostKeyChecking=no $SFTP_USER@$SERVER_IP "mkdir -p logs"
    sftp -oBatchMode=no $SFTP_USER@$SERVER_IP <<< "put $INVALID_ATTEMPTS_LOG logs/"
}

# Attempt login and handle invalid attempts
while [ $ATTEMPT_COUNT -lt $MAX_ATTEMPTS ]; do # attempts less than max attempts allowed
    sshpass -p "$password" ssh -o PreferredAuthentications=password -o PubkeyAuthentication=no $username@$SERVER_IP "exit" 2>/dev/null
    # Check if login was successful
    if [ $? -eq 0 ]; then     #check exit code 
        echo -e "\nLogin successful!"
        exit 0
    else
        echo -e "\nInvalid credentials. Please try again."
        log_invalid_attempt
        ((ATTEMPT_COUNT++))
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
