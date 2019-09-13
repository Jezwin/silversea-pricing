#! /bin/bash

set -e

CRX_HOST=$1
CRX_PORT=$2
CRX_TEST_PORT=$3
PKGS_DIR=$4
USER=$5
PASSWORD=$6

if [ ! -d "$PKGS_DIR" ]; then
    echo "Package directory: ${PKGS_DIR} does not exist. Aborting."
    exit 1;
fi

echo "Uploading all packages in ${PKGS_DIR} to ${CRX_URL}"

for filename in "$PKGS_DIR"/*; do
    echo "Uploading ${filename}"
    curl --fail -u "${USER}:${PASSWORD}" -F file=@"${filename}" -F name="${filename}" -F force=true -F install=true "http://${CRX_HOST}:${CRX_PORT}/crx/packmgr/service.jsp"
done

# echo "Checking packages installed correctly..."

# attempt_counter=0
# max_attempts=5

# until $(curl --fail -o installed-packages.txt -u "${USER}:${PASSWORD}" "http://${CRX_HOST}:${CRX_TEST_PORT}/crx/packmgr/service.jsp?cmd=ls"); do
#     if [ ${attempt_counter} -eq ${max_attempts} ];then
#       echo "Max attempts reached when trying to list installed packages"
#       exit 1
#     fi

#     printf '.'
#     attempt_counter=$(($attempt_counter+1))
#     sleep 5
# done

# for filename in "$PKGS_DIR"/*; do
#     echo "Checking ${filename} was correctly installed..."
#     pkg_file="$(basename "$filename")"
#     result=$(grep -c -A2 -B2 "<downloadName>${pkg_file}</downloadName>" installed-packages.txt) || :
#     if [ "${result}" -gt 0 ] 
#     then
#         echo "OK - ${pkg_file} was correctly installed..."
#     else
#         echo "${pkg_file} not found in package list after upload"
#         pkg_file_prefix=$(sed -E 's/[0-9].+//' <<< $pkg_file) 
#         echo "Searching instead for: $pkg_file_prefix"
#         result=$(grep "<downloadName>${pkg_file_prefix}" installed-packages.txt) || :
#         if [ -z ${result[0]} ]
#         then
#             echo "No match found for ${pkg_file_prefix} either."
#             echo "Outputting first 30 lines of response:"
#             echo $(head -30 installed-packages.txt)
#         else
#             echo "Found: ${result[0]}"
#         fi
#         exit 1
#     fi
# done