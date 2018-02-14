#!/bin/bash
# 计算总的机器内存和已使用的内存大小，单位是Mb

All_mem=`free -m | grep Mem | awk '{print $2}'`
Used=`free -m | awk '/buffers\// {print $NF}'`

echo "{\"all_size\":$All_mem,\"used_size\":$Used}"