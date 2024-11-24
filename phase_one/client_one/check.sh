#!/bin/bash

# Log file
LOG_FILE="perm_change.log"

# Find files with 777 permissions
echo "Searching for files with 777 permissions..." | tee -a "$LOG_FILE"
find / -type f -perm 0777 2>/dev/null | while read -r file; do
    echo "Found: $file" | tee -a "$LOG_FILE"
    # Change permissions to 700
    chmod 700 "$file" && echo "Changed permissions for: $file" | tee -a "$LOG_FILE"
done

echo "Permission change process completed." | tee -a "$LOG_FILE"