# IPv4 address counter
Counts unique ipv4 addresses passed to application as file or stdin.

## Build
```
 ./gradlew clean build
```
Application's jar will be located at  `build/libs/ip-addr-counter-1.0.jar`

## Launch with pipe
```
art4noir$ time cat ~/ips | java -Xmn10m -Xmx530m -jar build/libs/ip-addr-counter-1.0.jar 
1000000000

real    1m6.941s
user    0m55.239s
sys     0m20.588s
```
## Launch with file
```
art4noir$ time java -Xmn10m -Xmx530m -jar build/libs/ip-addr-counter-1.0.jar ~/ips
1000000000

real    1m0.370s
user    0m49.927s
sys     0m9.026s
```
## Memory usage
Main memory consumer is `IPv4Counter` which takes more than 512Mb of heap constantly.
All other allocations are insignificant - a dozen Mbs for young generation and old generation will be enough.

## About dataset and read speed used for tests above
```
art4noir$ time wc -l ~/ips
 1440000000 /Users/art4noir/ips

real    0m27.349s
user    0m15.000s
sys     0m9.273s
```
