#!/bin/bash
cd '/home/salem/temp/aria/'

echo ${BASH_ARGV[@]} >> cmd.txt
#echo ${BASH_ARGC} ${BASH_ARGV[@]} >> cmd.txt
#echo "$0" >> cmd0.txt
#echo "$1" "$2" "$3" "$4" "$5"  >> cmd.txt

#args = "args3f"
#echo  ${args} >> cmd3.txt
#for ((i=0; i < ${BASH_ARGC}; i++)); do
#	echo "${BASH_ARGV[i]}" >> cmd3.txt
#	echo "${BASH_ARGV[i]} " >> ${args} 
#done

#echo ${args} >> cmd3.txt

java Index ${BASH_ARGV[@]}

