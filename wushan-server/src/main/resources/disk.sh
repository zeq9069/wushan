#!/bin/bash
# 计算磁盘总容量和已使用的容量，单位是Mb

All_size=`df -hm | awk '{if($1 ~ "/dev/") all+=$2 };END{ print all }'`
Used_size=`df -hm | awk '{if($1 ~ "/dev/") used+=$3 };END{ print used }'`

echo "{\"all_size\":$All_size,\"used_size\":$Used_size}"