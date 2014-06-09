#!/bin/bash
#set -x

conf="xfxwdnod25p:10.4.52.136p:/A3\nxfxwdnod22p:10.4.52.133p:/A3\nxfxwdnod19p:10.4.52.130p:/A3\nxfxwdnod16p:10.4.52.127p:/A3\nxfxwdnod11p:10.4.52.122p:/A3\nxfxwdnod6p:10.4.52.117p:/A3\nxfxwdnod1p:10.4.52.112p:/A3\nxfxwdnod23p:10.4.52.134p:/A4\nxfxwdnod20p:10.4.52.131p:/A4\nxfxwdnod17p:10.4.52.128p:/A4\nxfxwdnod12p:10.4.52.123p:/A4\nxfxwdnod7p:10.4.52.118p:/A4\nxfxwdnod2p:10.4.52.113p:/A4\nxfxwdnod24p:10.4.52.135p:/A5\nxfxwdnod21p:10.4.52.132p:/A5\nxfxwdnod18p:10.4.52.129p:/A5\nxfxwdnod13p:10.4.52.124p:/A5\nxfxwdnod8p:10.4.52.119p:/A5\nxfxwdnod3p:10.4.52.114p:/A5\nxfxwmager1p:10.4.52.104p:/A5\nxfxwdnod15p:10.4.52.126p:/A6\nxfxwdnod14p:10.4.52.125p:/A6\nxfxwdnod10p:10.4.52.121p:/A6\nxfxwdnod9p:10.4.52.120p:/A6\nxfxwdnod5p:10.4.52.116p:/A6\nxfxwdnod4p:10.4.52.115p:/A6\nxfxwmager2p:10.4.52.105p:/A6\nxfxwweb1p:10.4.52.108p:/A7\nxfxwweb2p:10.4.52.109p:/A7\n"

ip=$1
rack=`echo -e $conf|grep ${ip}p|awk -F':' '{print $3}'`
echo $rack
