#!/bin/bash 
#mount remote directory
#demo ./umountshell.sh /data/data8 /dev/sde1 192.168.1.173:/data/data8/.
echo "local directory: "$1
echo "dev path:"$2
#echo "scp source: "$3
echo "process now begin..."

echo "msg:umount "$1"..."
umount $1

echo "msg:mount "$2" "$1"..."
mount $2 $1

echo "msg:start edit /etc/fstab file..."
temp=`echo $1|sed 's/\//\\\\\//g'`
sed -i -r "s/(.*?$temp.*?)/#\1/" /etc/fstab
sed -i "\$a $2 $1 ext4  default 1 0" /etc/fstab

#echo "msg:rm -rf $1/* .."
#rm -rf $1/*

#echo "msg:scp -r "$3" "$1
#scp -r $3 $1
echo "end"
