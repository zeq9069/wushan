#!/bin/bash
#计算cpu的数量和使用率

#cpu数量
cpu=`cat /proc/cpuinfo | grep "processor" | uniq | awk '{count+=1}; END{print count}'`
cpu_us=`vmstat | awk '{print $13}' | sed -n '$p'`
cpu_sy=`vmstat | awk '{print $14}' | sed -n '$p'`
cpu_sum=$(echo " $cpu_us + $cpu_sy "|bc)

echo "{\"cpu\":$cpu,\"cpu_sum\":$cpu_sum}"







