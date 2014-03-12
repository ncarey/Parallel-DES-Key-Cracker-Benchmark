
data = dlmread('/home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/EC2/speedup.txt',',')

yTicks = [100000,200000,400000,800000,1600000]
xTicks = [0,1,2,4,8,16,32,64]


threads = data(:,1)
keyLen = data(:,2)
keySize = data(:,3)
time = data(:,4)
kps = data(:,5)

plot(threads, kps)

set(gca, 'Xscale', 'log')
set(gca, 'Xtick', xTicks)
set(gca, 'Xticklabel', xTicks)
xlabel('Number of Parallel Threads Searching Key-Space (log scale)')

set(gca, 'Yscale', 'log')
set(gca, 'Ytick', yTicks)
set(gca, 'Yticklabel', yTicks)
ylabel('Throughput: Keys Examined Per Second (log scale)')

title('Throughput Speedup as a Function of Parallel Thread Count')

print -dpng /home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/graphs/ThruputSpeedup.png

yTicks = [0,1,2,4,8,16,32,64,128,256]

plot(threads, time)

set(gca, 'Xscale', 'log')
set(gca, 'Xtick', xTicks)
set(gca, 'Xticklabel', xTicks)
xlabel('Number of Parallel Threads Searching Key-Space (log scale)')

set(gca, 'Yscale', 'log')
set(gca, 'Ytick', yTicks)
set(gca, 'Yticklabel', yTicks)
ylabel('Execution Time in Seconds (log scale)')

title('Execution Time Speedup as a Function of Parallel Thread Count')

print -dpng /home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/graphs/TimeSpeedup.png

