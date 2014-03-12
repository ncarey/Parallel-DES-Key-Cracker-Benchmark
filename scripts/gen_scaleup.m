
data = dlmread('/home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/EC2/scaleup.txt',',')

yTicks = [100000,200000,400000,800000,1600000]
xTicks = [0,1,2,4,8,16,32,64]

scaleup = [0,1,2,3,4,5]
threads = data(:,1)
keyLen = data(:,2)
keySize = data(:,3)
time = data(:,4)
kps = data(:,5)

kps = kps ./ threads

plot(scaleup, kps)

xlabel('Scaleup Factor. Num Threads = 2\^(Scaleup Factor), Key-Space Size = (2\^20)\^(Scaleup Factor)')

ylabel('Throughput per Thread: Keys Examined per Second per Thread')

title('Throughput per Thread as a Function of Scaleup')

print -dpng /home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/graphs/ThruputScaleup.png

yTicks = [0,1,2,4,8,16,32,64,128,256]

plot(scaleup, time)

xlabel('Scaleup Factor. Num Threads = 2\^(Scaleup Factor), Key-Space Size = (2\^20)\^(Scaleup Factor)')

ylabel('Execution Time in Seconds')

title('Execution Time as a Function of Scaleup')

print -dpng /home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/graphs/TimeScaleup.png

